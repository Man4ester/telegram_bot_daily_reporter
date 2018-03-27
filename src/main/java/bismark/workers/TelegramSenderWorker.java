package bismark.workers;

import bismark.services.TelegramServiceImpl;
import org.springframework.context.ApplicationContext;

public class TelegramSenderWorker implements Runnable {

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
        TelegramServiceImpl telegramService = ctx.getBean(TelegramServiceImpl.class);
        telegramService.sendReminderByUserId(userId, message);
    }
}
