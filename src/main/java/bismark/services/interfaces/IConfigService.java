package bismark.services.interfaces;

import bismark.models.TimeReminder;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Map;
import java.util.Properties;

public interface IConfigService {

    /**
     * Load all Properties
     * @return {@link Properties}
     */
    Properties loadProperties();

    /**
     * Load list with ID's of recipients
     * @param properties - source for reading data
     * @return - list with ID's
     */
    List<Long> loadRecipients(Properties properties);

    /**
     * Load message from setting what will be used to remind
     * @param properties - source for data
     * @return - message from config
     */
    String getReminderMessage(Properties properties);

    /**
     * Load start date for reminder.
     * @param properties - source for reading data
     * @return - {@link DateTime} with date
     */
    DateTime getStartRemindDate(Properties properties);

    /**
     * Load end date for reminder.
     * @param properties - source for reading data
     * @return {@link DateTime} with date
     */
    DateTime getEndRemindDate(Properties properties);

    /**
     * Convert {@link String} to {@link TimeReminder} to get more easy way to work with date
     * @param str - source
     * @return - {@link TimeReminder}
     */
    TimeReminder getFromString(String str);

    /**
     * Simple way to read delay
     * @param properties
     * @param currentTime
     * @return delay for worker
     */
    long getReporterMinutesDelay(Properties properties, DateTime currentTime);

    /**
     * Simple way to read interval
     * @param properties
     * @return interval for reminder
     */
    long getReminderInterval(Properties properties);

    /**
     * Simple way to read admin ID from settings
     * @param properties
     * @return id for admin
     */
    long getAdminTelegramId(Properties properties);

    /**
     * Load map with id <=> username for report
     * @param properties
     * @return map with correct format
     */
    Map<Long, String> loadUsersForReport(Properties properties);
}
