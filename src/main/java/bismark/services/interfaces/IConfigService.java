package bismark.services.interfaces;

import bismark.models.TimeReminder;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Map;
import java.util.Properties;

public interface IConfigService {

    Properties loadProperties();

    List<Long> loadRecipients(Properties properties);

    String getReminderMessage(Properties properties);

    DateTime getStartRemindDate(Properties properties);

    DateTime getEndRemindDate(Properties properties);

    TimeReminder getFromString(String str);

    long getReporterMinutesDelay(Properties properties, DateTime currentTime);

    long getReminderInterval(Properties properties);

    long getAdminTelegramId(Properties properties);

    Map<Long, String> loadUsersForReport(Properties properties);
}
