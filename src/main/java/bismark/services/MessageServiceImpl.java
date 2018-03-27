package bismark.services;

import bismark.models.Message;
import bismark.models.Sender;
import bismark.services.interfaces.IMessageService;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@Qualifier("messageServiceImpl")
public class MessageServiceImpl implements IMessageService {

    @Override
    public List<Message> readMessagesFromJSON(JSONObject json) {
        List<Message> messages = new ArrayList<>();

        if (null == json) {
            return messages;
        }

        if (!json.has("result")) {
            return messages;
        }

        JSONArray allMessages = json.getJSONArray("result");
        for (int i = 0; i < allMessages.length(); i++) {
            JSONObject singleJsonMessage = allMessages.getJSONObject(i);
            Message message = getMessageFromJSONObject(singleJsonMessage);
            if (isNeedToStore(message)) {
                messages.add(message);
            }
        }
        return messages;
    }

    @Override
    public Message getMessageFromJSONObject(JSONObject jsonObject) {
        Message message = new Message();
        Sender sender = new Sender();

        if (null == jsonObject) {
            return null;
        }

        JSONObject jsonMessage = jsonObject.getJSONObject("message");
        JSONObject jsonSender = jsonMessage.getJSONObject("from");

        sender.setId(jsonSender.getLong("id"));
        sender.setFirstName(jsonSender.getString("first_name"));
        sender.setLastName(jsonMessage.has("last_name") ? jsonSender.getString("last_name") : "");

        message.setUpdateId(jsonObject.getLong("update_id"));
        message.setCreatedDate(jsonMessage.getLong("date"));
        if (!jsonMessage.has("text")) {
            return null;
        }

        message.setText(formatText(jsonMessage.getString("text")));

        message.setId(jsonMessage.getLong("message_id"));
        message.setSender(sender);

        return message;
    }

    @Override
    public boolean isForToday(Message message) {
        DateTime today = new DateTime();
        today = today.withMinuteOfHour(0);
        today = today.withSecondOfMinute(0);
        today = today.withMillisOfDay(0);
        DateTime createdMessage = new DateTime(message.getCreatedDate() * 1000);
        return createdMessage.isAfter(today);
    }

    @Override
    public boolean isNeedToStore(Message message) {
        if (null == message) {
            return false;
        }

        if (!isForToday(message)) {
            return false;
        }

        if (!message.getText().contains("jira")) {
            return false;
        }
        return true;
    }

    private String formatText(String txt) {
        String text = txt.replaceAll("https", "\nhttps");

        return text.substring(1, text.length());
    }
}
