package bismark.workers;

import bismark.services.TelegramServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

public class TelegramSenderWorker implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(TelegramSenderWorker.class);

    private long userId;

    private String message;

    private ApplicationContext ctx;

    public TelegramSenderWorker(long userId, String message, ApplicationContext ctx) {
        this.userId = userId;
        this.message = message;
        this.ctx = ctx;
    }

    @Override
    public void run() {
        LOGGER.info("start");
        TelegramServiceImpl telegramService = ctx.getBean(TelegramServiceImpl.class);
        telegramService.sendReminderByUserId(userId, message);
    }
}
