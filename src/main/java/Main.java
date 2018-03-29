import bismark.AppConfig;
import bismark.services.ConfigServiceImpl;
import bismark.services.interfaces.IConfigService;
import bismark.workers.ReminderWorker;
import bismark.workers.TelegramReaderWorker;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private static final DateFormat DF = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public static void main(String[] args) {

        IConfigService configService = new ConfigServiceImpl();
        ApplicationContext ctx =
                new AnnotationConfigApplicationContext(AppConfig.class);

        startDailyReportReminder(configService, ctx);
        startReporter(configService, ctx);


    }

    private static void startDailyReportReminder(IConfigService configService, ApplicationContext ctx) {
        LOGGER.info("startDailyReportReminder");
        Properties properties = configService.loadProperties();
        Date start = configService.getStartRemindDate(properties).toDate();
        Date end = configService.getEndRemindDate(properties).toDate();
        Long interval = configService.getReminderInterval(properties);

        LOGGER.info("Will send remind from {} to {} with interval {}", DF.format(start), DF.format(end), interval);

        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.submit(new ReminderWorker(properties, interval, start, end, ctx));
        executor.shutdown();
    }

    private static void startReporter(IConfigService configService, ApplicationContext ctx) {
        LOGGER.info("startReporter");
        Properties properties = configService.loadProperties();
        long delayMinutes = configService.getReporterMinutesDelay(properties, new DateTime());

        LOGGER.info("Will use delay: {} minutes", delayMinutes);
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.schedule(new TelegramReaderWorker(ctx, properties), delayMinutes, TimeUnit.MINUTES);
        executor.shutdown();
    }

}
