package bismark.services.interfaces;

import bismark.models.TimeReminder;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Map;

public interface IConfigService {

    /**
     * Load list with ID's of recipients
     *
     * @return - list with ID's
     */
    List<Long> loadRecipients();

    /**
     * Load message from setting what will be used to remind
     *
     * @return - message from config
     */
    String getReminderMessage();

    /**
     * Load start date for reminder.
     *
     * @return - {@link DateTime} with date
     */
    DateTime getStartRemindDate();

    /**
     * Load end date for reminder.
     *
     * @return {@link DateTime} with date
     */
    DateTime getEndRemindDate();

    /**
     * Convert {@link String} to {@link TimeReminder} to get more easy way to work with date
     *
     * @param str - source
     * @return - {@link TimeReminder}
     */
    TimeReminder getFromString(String str);

    /**
     * Simple way to read delay
     *
     * @param currentTime
     * @return delay for worker
     */
    long getReporterMinutesDelay(DateTime currentTime);

    /**
     * Simple way to read interval
     *
     * @return interval for reminder
     */
    long getReminderInterval();

    /**
     * Simple way to read admin ID from settings
     *
     * @return id for admin
     */
    long getAdminTelegramId();

    /**
     * Load map with id <=> username for report
     *
     * @return map with correct format
     */
    Map<Long, String> loadUsersForReport();
}
