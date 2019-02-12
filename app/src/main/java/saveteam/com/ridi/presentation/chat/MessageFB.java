package saveteam.com.ridi.presentation.chat;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
public class MessageFB implements Serializable {
    private String uid;
    private String roomid;
    private String message;
    private String createdDate;

    public MessageFB() {
    }

    public MessageFB(String uid, String roomid, String message, String createdDate) {
        this.uid = uid;
        this.roomid = roomid;
        this.message = message;
        this.createdDate = createdDate;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getRoomid() {
        return roomid;
    }

    public void setRoomid(String roomid) {
        this.roomid = roomid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
}
