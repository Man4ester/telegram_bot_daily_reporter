package bismark.models;

import java.util.Date;

public class Message {

    private long id;

    private long updateId;

    private Sender sender;

    private String text;

    private long createdDate;

    private Date normalizedDate;

    public Date getNormalizedDate() {
        return normalizedDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUpdateId() {
        return updateId;
    }

    public void setUpdateId(long updateId) {
        this.updateId = updateId;
    }

    public Sender getSender() {
        return sender;
    }

    public void setSender(Sender sender) {
        this.sender = sender;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(long createdDate) {
        this.normalizedDate = new Date(createdDate*1000);
        this.createdDate = createdDate;
    }
}
