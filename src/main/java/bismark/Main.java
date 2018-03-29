package bismark;

import bismark.services.ConfigServiceImpl;
import bismark.services.interfaces.IConfigService;
import bismark.workers.ReminderWorker;
import bismark.workers.TelegramReaderWorker;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Configuration
public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private static final DateFormat DF = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public static void main(String[] args) {

        ApplicationContext ctx =
                new AnnotationConfigApplicationContext(AppConfig.class);

        ConfigServiceImpl configService = ctx.getBean(ConfigServiceImpl.class);
        startDailyReportReminder(configService, ctx);
        startReporter(configService, ctx);


    }

    private static void startDailyReportReminder(IConfigService configService, ApplicationContext ctx) {
        LOGGER.info("startDailyReportReminder");
        Date start = configService.getStartRemindDate().toDate();
        Date end = configService.getEndRemindDate().toDate();
        Long interval = configService.getReminderInterval();

        LOGGER.info("Will send remind from {} to {} with interval {}", DF.format(start), DF.format(end), interval);

        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.submit(new ReminderWorker(interval, start, end, ctx));
        executor.shutdown();
    }

    private static void startReporter(IConfigService configService, ApplicationContext ctx) {
        LOGGER.info("startReporter");
        long delayMinutes = configService.getReporterMinutesDelay(new DateTime());

        LOGGER.info("Will use delay: {} minutes", delayMinutes);
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.schedule(new TelegramReaderWorker(ctx), delayMinutes, TimeUnit.MINUTES);
        executor.shutdown();
    }

}
