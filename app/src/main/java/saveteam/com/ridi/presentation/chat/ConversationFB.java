package saveteam.com.ridi.presentation.chat;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@IgnoreExtraProperties
public class ConversationFB implements Serializable {
    public static final String DB_IN_FB = "chanels";
    private String roomid;
    private List<MessageFB> messages;
    private String createdDate;

    public ConversationFB() {
        messages = new ArrayList<>();
    }

    public ConversationFB(String roomid, List<MessageFB> messages, String createdDate) {
        this.roomid = roomid;
        this.messages = messages;
        this.createdDate = createdDate;
    }

    public String getRoomid() {
        return roomid;
    }

    public void setRoomid(String roomid) {
        this.roomid = roomid;
    }

    public List<MessageFB> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageFB> messages) {
        this.messages = messages;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
}
