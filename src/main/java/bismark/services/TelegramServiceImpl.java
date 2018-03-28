package bismark.services;

import bismark.services.interfaces.ITelegramService;
import bismark.utils.TelegramHolder;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Service
@Qualifier("telegramServiceImpl")
public class TelegramServiceImpl implements ITelegramService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TelegramServiceImpl.class);

    @Value("${telegramURL}")
    private String telegramURL;

    @Value("${userAgent}")
    private String userAgent;

    @Value("${reportGeneratedMessage}")
    private String reportGeneratedMessage;


    @Override
    public String getUrlForSendMessage() {
        return String.format(telegramURL, TelegramHolder.SEND_MESSAGE);
    }

    @Override
    public String getUrlForReadMessage() {
        return String.format(telegramURL, TelegramHolder.GET_UPDATES);
    }

    @Override
    public String getBaseURL() {
        return telegramURL;
    }

    @Override
    public void sendReminderByUserId(long userId, String message) {
        String url = getUrlForSendMessage();
        List<NameValuePair> urlParameters = addVariablesForRequest(userId, message);
        callToTelegramAPI(url, urlParameters);
    }

    @Override
    public JSONObject readUpdatesForBot() {
        String url = getUrlForReadMessage();

        return new JSONObject(getResponseFromTelegramAPI(url, Collections.EMPTY_LIST));
    }

    @Override
    public void sendMessageToAdminAfterReportGenerated(long adminId) {
        String url = getUrlForSendMessage();
        List<NameValuePair> urlParameters = addVariablesForRequest(adminId, reportGeneratedMessage);
        callToTelegramAPI(url, urlParameters);
    }

    @Override
    public HttpResponse callToTelegramAPI(String url, List<NameValuePair> urlParameters) {
        LOGGER.info("callToTelegramAPI");
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(url);
            post.setHeader(TelegramHolder.USER_AGENT, userAgent);

            if (!urlParameters.isEmpty()) {
                post.setEntity(new UrlEncodedFormEntity(urlParameters));
            }

            return httpclient.execute(post);

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    @Override
    public String getResponseFromTelegramAPI(String url, List<NameValuePair> urlParameters) {
        LOGGER.info("getResponseFromTelegramAPI");
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(url);
            post.setHeader(TelegramHolder.USER_AGENT, userAgent);

            if (!urlParameters.isEmpty()) {
                post.setEntity(new UrlEncodedFormEntity(urlParameters));
            }

            HttpResponse response = httpclient.execute(post);

            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));

            StringBuffer result = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            return result.toString();


        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    private List<NameValuePair> addVariablesForRequest(long userId, String message) {
        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add(new BasicNameValuePair(TelegramHolder.CHAT_ID, String.valueOf(userId)));
        urlParameters.add(new BasicNameValuePair(TelegramHolder.TEXT, message));

        return urlParameters;
    }
}
