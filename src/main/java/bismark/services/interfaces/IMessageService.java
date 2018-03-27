package bismark.services.interfaces;

import bismark.models.Message;
import org.json.JSONObject;

import java.util.List;

public interface IMessageService {

    List<Message> readMessagesFromJSON(JSONObject json);

    Message getMessageFromJSONObject(JSONObject jsonObject);

    boolean isForToday(Message message);

    boolean isNeedToStore(Message message);

}
