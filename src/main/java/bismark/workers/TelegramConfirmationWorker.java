package bismark.workers;

import bismark.services.ConfigServiceImpl;
import bismark.services.TelegramServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.Map;

public class TelegramConfirmationWorker implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(TelegramConfirmationWorker.class);


    private String userName;

    private ApplicationContext ctx;

    public TelegramConfirmationWorker(String userName, ApplicationContext ctx) {
        this.userName = userName;
        this.ctx =ctx;
    }

    @Override
    public void run() {
        LOGGER.info("START #TelegramConfirmationWorker");
        TelegramServiceImpl telegramService = ctx.getBean(TelegramServiceImpl.class);
        ConfigServiceImpl configService = ctx.getBean(ConfigServiceImpl.class);
        Map<Long, String> recipients = configService.loadUsersForReport(); //TODO rewrite this part
        recipients.forEach((key, value)->{
            if (userName.equals(value)){
                telegramService.sendConfirmationToUserByUserId(key);
                return;
            }
        });

    }
}
