package bismark.services;

import bismark.models.Message;
import bismark.models.Sender;
import bismark.services.interfaces.IMessageService;
import bismark.utils.MessageHolder;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@Qualifier("messageServiceImpl")
public class MessageServiceImpl implements IMessageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageServiceImpl.class);

    @Override
    public List<Message> readMessagesFromJSON(JSONObject json) {
        LOGGER.info("readMessagesFromJSON");
        List<Message> messages = new ArrayList<>();

        if (null == json) {
            return messages;
        }

        if (!json.has(MessageHolder.RESULT)) {
            return messages;
        }

        JSONArray allMessages = json.getJSONArray(MessageHolder.RESULT);
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
        LOGGER.info("getMessageFromJSONObject");
        Message message = new Message();
        try {
            Sender sender = new Sender();

            if (null == jsonObject) {
                return null;
            }

            if (!jsonObject.has(MessageHolder.MESSAGE)) {
                return null;
            }

            JSONObject jsonMessage = jsonObject.getJSONObject(MessageHolder.MESSAGE);
            JSONObject jsonSender = jsonMessage.getJSONObject(MessageHolder.FROM);

            sender.setId(jsonSender.getLong(MessageHolder.ID));
            sender.setFirstName(jsonSender.getString(MessageHolder.FIRST_NAME));
            sender.setLastName(jsonMessage.has(MessageHolder.LAST_NAME) ? jsonSender.getString(MessageHolder.LAST_NAME) : "");

            message.setUpdateId(jsonObject.getLong(MessageHolder.UPDATE_ID));
            message.setCreatedDate(jsonMessage.getLong(MessageHolder.DATE));
            if (!jsonMessage.has(MessageHolder.TEXT)) {
                return null;
            }

            message.setText(formatText(jsonMessage.getString(MessageHolder.TEXT)));

            message.setId(jsonMessage.getLong(MessageHolder.MESSAGE_ID));
            message.setSender(sender);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }

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

        if (!message.getText().contains(MessageHolder.JIRA)) {
            return false;
        }
        return true;
    }

    private String formatText(String txt) {
        String text = txt.replaceAll(MessageHolder.HTTPS, MessageHolder.HTTPS_NORMAL);

        return text.substring(1, text.length());
    }
}
