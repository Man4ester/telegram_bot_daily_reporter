package bismark.workers;

import bismark.models.Message;
import bismark.services.ConfigServiceImpl;
import bismark.services.MessageServiceImpl;
import bismark.services.ReporterServiceImpl;
import bismark.services.TelegramServiceImpl;
import bismark.services.interfaces.IConfigService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TelegramReaderWorker implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(TelegramReaderWorker.class);

    private ApplicationContext ctx;

    public TelegramReaderWorker(ApplicationContext ctx) {

        this.ctx = ctx;
    }

    @Override
    public void run() {
        LOGGER.info("START");

        TelegramServiceImpl telegramService = ctx.getBean(TelegramServiceImpl.class);
        ReporterServiceImpl reporterService = ctx.getBean(ReporterServiceImpl.class);
        MessageServiceImpl messageService = ctx.getBean(MessageServiceImpl.class);
        IConfigService configService = ctx.getBean(ConfigServiceImpl.class);

        JSONObject json = telegramService.readUpdatesForBot();

        List<Message> lst = messageService.readMessagesFromJSON(json);
        LOGGER.info("TOTAL MESSAGES: {}", lst.size());
        reporterService.storeReportFromMessages(lst, configService.loadUsersForReport());

        LOGGER.info("START Report Worker");
        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.submit(new ReporterWorker(ctx));
        executor.shutdown();

    }
}
