package saveteam.com.ridesharing.firebase.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.List;

@IgnoreExtraProperties
public class ConfirmListFB implements Serializable {
    public static final String DB_IN_FB = "confirmv1";

    private String uid;
    private String userName;
    private List<ConfirmFB> confirms;
    private List<ProfileFB> profiles;

    public ConfirmListFB() {
    }

    public ConfirmListFB(String uid, String userName, List<ConfirmFB> confirms, List<ProfileFB> profiles) {
        this.uid = uid;
        this.userName = userName;
        this.confirms = confirms;
        this.profiles = profiles;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<ConfirmFB> getConfirms() {
        return confirms;
    }

    public void setConfirms(List<ConfirmFB> confirms) {
        this.confirms = confirms;
    }

    public List<ProfileFB> getProfiles() {
        return profiles;
    }

    public void setProfiles(List<ProfileFB> profiles) {
        this.profiles = profiles;
    }
}
