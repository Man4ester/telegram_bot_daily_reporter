package bismark.workers;

import bismark.services.ConfigServiceImpl;
import bismark.services.ReporterServiceImpl;
import bismark.services.TelegramServiceImpl;
import bismark.services.interfaces.IConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReporterWorker implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReporterWorker.class);

    private ApplicationContext ctx;


    private IConfigService configService;

    public ReporterWorker(ApplicationContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void run() {
        LOGGER.info("START");
        configService = ctx.getBean(ConfigServiceImpl.class);

        ReporterServiceImpl reporterService = ctx.getBean(ReporterServiceImpl.class);
        List<String> userNames = reporterService.generateHTMLReport();


        ExecutorService executor = Executors.newFixedThreadPool(1);

        for (String userName : userNames) {
            executor.submit(new TelegramConfirmationWorker(userName, ctx));
        }

        executor.shutdown();

        TelegramServiceImpl telegramService = ctx.getBean(TelegramServiceImpl.class);
        telegramService.sendMessageToAdminAfterReportGenerated(configService.getAdminTelegramId());

    }
}
