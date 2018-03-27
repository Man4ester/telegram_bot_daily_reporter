package bismark.utils;

public class ConfigHolder {

    public static final String CONFIG_FILE_NAME = "application.properties";

    public static final String RECIPIENT_LBL = "recipientsId";

    public static final String REMINDER_MESSAGE_LBL = "reminderMessage";

    public static final String START_REMINDER_TIME_LBL = "startRemindTime";

    public static final String END_REMINDER_TIME_LBL = "endRemindTime";

    public static final String REPORTER_TIME_LBL = "reporterTime";

    public static final String RECIPIENTS_LBL ="recipientsMap";

    public static final String REMINDER_INTERVAL_LBL = "reminderMinuteInterval";

    public static final String ADMIN_IDL_LBL = "adminId";

    public static final String RECIPIENT_SPLITTER = ",";

    public static final String TIME_SPLITTER = ":";

    public static final String USERNAME_SPLITTER = ":";

    private ConfigHolder() {

    }


}
