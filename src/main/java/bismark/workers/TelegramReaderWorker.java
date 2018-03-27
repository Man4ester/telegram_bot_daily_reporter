package bismark.workers;

import bismark.models.Message;
import bismark.services.ConfigServiceImpl;
import bismark.services.MessageServiceImpl;
import bismark.services.ReporterServiceImpl;
import bismark.services.TelegramServiceImpl;
import bismark.services.interfaces.IConfigService;
import org.json.JSONObject;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TelegramReaderWorker implements Runnable {


    private ApplicationContext ctx;

    private Properties properties;

    public TelegramReaderWorker(ApplicationContext ctx, Properties properties) {

        this.ctx = ctx;
        this.properties = properties;
    }

    @Override
    public void run() {
        TelegramServiceImpl telegramService = ctx.getBean(TelegramServiceImpl.class);
        ReporterServiceImpl reporterService = ctx.getBean(ReporterServiceImpl.class);
        MessageServiceImpl messageService = ctx.getBean(MessageServiceImpl.class);
        IConfigService configService = new ConfigServiceImpl();

        JSONObject json = telegramService.readUpdatesForBot();

        List<Message> lst = messageService.readMessagesFromJSON(json);
        reporterService.storeReportFromMessages(lst, configService.loadUsersForReport(properties));

        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.submit(new ReporterWorker(ctx, properties));
        executor.shutdown();

    }
}
