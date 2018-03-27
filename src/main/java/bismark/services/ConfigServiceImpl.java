package bismark.services;

import bismark.models.TimeReminder;
import bismark.services.interfaces.IConfigService;
import bismark.utils.ConfigHolder;
import org.joda.time.DateTime;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class ConfigServiceImpl implements IConfigService {

    @Override
    public Properties loadProperties() {
        Properties prop = new Properties();
        InputStream input = null;

        try {
            String fileName = ConfigHolder.CONFIG_FILE_NAME;
            ClassLoader classLoader = new ConfigServiceImpl().getClass().getClassLoader();

            input = new FileInputStream(classLoader.getResource(fileName).getFile());
            prop.load(input);

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return prop;
    }

    @Override
    public List<Long> loadRecipients(Properties properties) {
        String[] ids = properties.getProperty(ConfigHolder.RECIPIENT_LBL).split(ConfigHolder.RECIPIENT_SPLITTER);
        List<Long> recipients = new ArrayList<>();

        for (int i = 0; i < ids.length; i++) {
            recipients.add(Long.parseLong(ids[i]));
        }

        return recipients;
    }

    @Override
    public String getReminderMessage(Properties properties) {
        return properties.getProperty(ConfigHolder.REMINDER_MESSAGE_LBL);
    }

    @Override
    public DateTime getStartRemindDate(Properties properties) {
        TimeReminder timeReminder = getFromString(properties.getProperty(ConfigHolder.START_REMINDER_TIME_LBL));
        return loadFromTimeReminder(timeReminder);
    }

    @Override
    public DateTime getEndRemindDate(Properties properties) {
        TimeReminder timeReminder = getFromString(properties.getProperty(ConfigHolder.END_REMINDER_TIME_LBL));
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
    public long getReporterMinutesDelay(Properties properties, DateTime currentTime) {
        TimeReminder tr = getFromString(properties.getProperty(ConfigHolder.REPORTER_TIME_LBL));
        DateTime dt = loadFromTimeReminder(tr);
        return TimeUnit.MILLISECONDS.toMinutes(dt.getMillis() - currentTime.getMillis());
    }

    @Override
    public long getReminderInterval(Properties properties) {
        return Long.parseLong(properties.getProperty(ConfigHolder.REMINDER_INTERVAL_LBL));
    }

    @Override
    public long getAdminTelegramId(Properties properties) {
        return Long.parseLong(properties.getProperty(ConfigHolder.ADMIN_IDL_LBL));
    }

    @Override
    public Map<Long, String> loadUsersForReport(Properties properties) {

        Map<Long, String> result = new HashMap<>();
        String[] parts = properties.getProperty(ConfigHolder.RECIPIENTS_LBL).split(ConfigHolder.RECIPIENT_SPLITTER);
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
