package bismark.services.interfaces;

import bismark.models.Message;
import org.json.JSONObject;

import java.util.List;

public interface IMessageService {

    /**
     * Parse {@link JSONObject} to list with messages {@link Message}
     * @param json
     * @return list with messages
     */
    List<Message> readMessagesFromJSON(JSONObject json);

    /**
     * Parse {@link JSONObject} to single message {@link Message}
     * @param jsonObject
     * @return single message
     */
    Message getMessageFromJSONObject(JSONObject jsonObject);

    /**
     * To check is it day to report
     * @param message
     * @return true in ase of time for report otherwise - false
     */
    boolean isForToday(Message message);

    /**
     * Need to check is it message need to store. <br />
     * We need to prevent storing spam messages
     * @param message
     * @return true - need to store otherwise false
     */
    boolean isNeedToStore(Message message);

}
