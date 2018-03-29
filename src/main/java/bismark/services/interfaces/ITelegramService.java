package bismark.services.interfaces;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.List;

public interface ITelegramService {

    /**
     * Make http call to Telegram API
     * @param url
     * @param urlParameters
     * @return response {@link HttpResponse}
     */
    HttpResponse callToTelegramAPI(String url, List<NameValuePair> urlParameters);

    /**
     * Make call by URL
     * @param url
     * @param urlParameters
     * @return
     */
    String getResponseFromTelegramAPI(String url, List<NameValuePair> urlParameters);

    /**
     * Base url to Telegram API
     * @return base URL
     */
    String getBaseURL();

    /**
     * Url for Send message method to Telegram API
     * @return
     */
    String getUrlForSendMessage();

    /**
     *Url for get Updates method to Telegram API
     * @return
     */
    String getUrlForReadMessage();

    /**
     * Simple reminder sending
     * @param userId
     * @param message
     */
    void sendReminderByUserId(long userId, String message);

    /**
     *
     * @return
     */
    JSONObject readUpdatesForBot();

    /**
     *
     * @param adminId
     */
    void sendMessageToAdminAfterReportGenerated(long adminId);

}
