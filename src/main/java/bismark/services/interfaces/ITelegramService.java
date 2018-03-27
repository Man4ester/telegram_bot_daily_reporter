package bismark.services.interfaces;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.List;

public interface ITelegramService {

    HttpResponse callToTelegramAPI(String url, List<NameValuePair> urlParameters);

    String getResponseFromTelegramAPI(String url, List<NameValuePair> urlParameters);

    String getBaseURL();

    String getUrlForSendMessage();

    String getUrlForReadMessage();

    void sendReminderByUserId(long userId, String message);

    JSONObject readUpdatesForBot();

    void sendMessageToAdminAfterReportGenerated(long adminId);

}
