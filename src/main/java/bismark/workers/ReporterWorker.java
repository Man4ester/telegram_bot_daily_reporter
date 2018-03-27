package bismark.workers;

import bismark.services.ConfigServiceImpl;
import bismark.services.ReporterServiceImpl;
import bismark.services.TelegramServiceImpl;
import bismark.services.interfaces.IConfigService;
import org.springframework.context.ApplicationContext;

import java.util.Properties;

public class ReporterWorker implements Runnable {


    private ApplicationContext ctx;

    private Properties properties;

    private IConfigService configService = new ConfigServiceImpl();

    public ReporterWorker(ApplicationContext ctx, Properties properties) {
        this.ctx = ctx;
        this.properties = properties;
    }

    @Override
    public void run() {

        ReporterServiceImpl reporterService = ctx.getBean(ReporterServiceImpl.class);
        reporterService.generateHTMLReport();


        TelegramServiceImpl telegramService = ctx.getBean(TelegramServiceImpl.class);
        telegramService.sendMessageToAdminAfterReportGenerated(configService.getAdminTelegramId(properties));

    }
}
