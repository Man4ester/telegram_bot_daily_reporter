package bismark.workers;

import bismark.services.ConfigServiceImpl;
import bismark.services.interfaces.IConfigService;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.Date;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ReminderWorker implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReminderWorker.class);

    private long localTimeoutMinutes;
    private DateTime start;
    private DateTime end;


    IConfigService configService;

    private ApplicationContext ctx;

    public ReminderWorker(long timeout, Date dateFrom, Date dateTo, ApplicationContext ctx) {
        this.localTimeoutMinutes = timeout;
        this.start = new DateTime(dateFrom);
        this.end = new DateTime(dateTo);
        this.ctx = ctx;
    }

    @Override
    public void run() {
        LOGGER.info("START");
        configService = ctx.getBean(ConfigServiceImpl.class);

        boolean active = true;

        while (active) {

            try {
                if (isTimeToStop()) {
                    active = false;
                    LOGGER.info("TIME TO STOP REMIND");
                    break;
                }

                if (isTimeToRemind()) {
                    sendMessageToRecipients();
                }

                TimeUnit.MINUTES.sleep(localTimeoutMinutes);
            } catch (InterruptedException e) {
                LOGGER.error(e.getMessage());
            }
        }
    }

    private void sendMessageToRecipients() {
        LOGGER.info("sendMessageToRecipients");
        for (Long recipientId : configService.loadRecipients()) {
            LOGGER.info("WILL SEND REMIND TO: {}", recipientId);
            ExecutorService executor = Executors.newFixedThreadPool(1);
            executor.submit(new TelegramSenderWorker(recipientId, configService.getReminderMessage(), ctx));
            executor.shutdown();
        }
    }

    private boolean isTimeToRemind() {
        DateTime now = new DateTime();
        return (now.isAfter(start) && now.isBefore(end));
    }

    private boolean isTimeToStop() {
        return (new DateTime()).isAfter(end);
    }
}
