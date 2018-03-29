package bismark.services;

import bismark.models.TimeReminder;
import bismark.services.interfaces.IConfigService;
import bismark.utils.ConfigHolder;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Qualifier("configServiceImpl")
public class ConfigServiceImpl implements IConfigService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigServiceImpl.class);

    @Value("${startRemindTime}")
    private String startRemindTime;

    @Value("${recipientsId}")
    private String recipientsId;

    @Value("${reminderMessage}")
    private String reminderMessage;

    @Value("${reporterTime}")
    private String reporterTime;

    @Value("${endRemindTime}")
    private String endRemindTime;

    @Value("${reminderMinuteInterval}")
    private String reminderMinuteInterval;

    @Value("${adminId}")
    private String adminId;

    @Value("${recipientsMap}")
    private String recipientsMap;

    @Override
    public List<Long> loadRecipients() {
        String[] ids = recipientsId.split(ConfigHolder.RECIPIENT_SPLITTER);
        List<Long> recipients = new ArrayList<>();

        for (int i = 0; i < ids.length; i++) {
            recipients.add(Long.parseLong(ids[i]));
        }

        LOGGER.info("TOTAL USERS FOR REPORTING {}", recipients.size());
        return recipients;
    }

    @Override
    public String getReminderMessage() {
        return reminderMessage;
    }

    @Override
    public DateTime getStartRemindDate() {
        TimeReminder timeReminder = getFromString(startRemindTime);
        return loadFromTimeReminder(timeReminder);
    }

    @Override
    public DateTime getEndRemindDate() {
        TimeReminder timeReminder = getFromString(endRemindTime);
        return loadFromTimeReminder(timeReminder);
    }

    @Override
    public TimeReminder getFromString(String str) {
        String[] parts = str.split(ConfigHolder.TIME_SPLITTER);
        TimeReminder timeReminder = new TimeReminder();
        timeReminder.setHour(Integer.parseInt(parts[0]));
        timeReminder.setMinutes(Integer.parseInt(parts[1]));
        return timeReminder;
    }

    @Override
    public long getReporterMinutesDelay(DateTime currentTime) {
        TimeReminder tr = getFromString(reporterTime);
        DateTime dt = loadFromTimeReminder(tr);
        return TimeUnit.MILLISECONDS.toMinutes(dt.getMillis() - currentTime.getMillis());
    }

    @Override
    public long getReminderInterval() {
        return Long.parseLong(reminderMinuteInterval);
    }

    @Override
    public long getAdminTelegramId() {
        return Long.parseLong(adminId);
    }

    @Override
    public Map<Long, String> loadUsersForReport() {

        Map<Long, String> result = new HashMap<>();
        String[] parts = recipientsMap.split(ConfigHolder.RECIPIENT_SPLITTER);
        for (int i = 0; i < parts.length; i++) {
            String[] subParts = parts[i].split(ConfigHolder.USERNAME_SPLITTER);
            Long id = Long.parseLong(subParts[0]);
            String userName = subParts[1];
            result.put(id, userName);

        }
        return result;
    }

    private DateTime loadFromTimeReminder(TimeReminder timeReminder) {
        DateTime dateTime = new DateTime();
        dateTime = dateTime.withHourOfDay(timeReminder.getHour());
        dateTime = dateTime.withMinuteOfHour(timeReminder.getMinutes());

        return dateTime;
    }


}
