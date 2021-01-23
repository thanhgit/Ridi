package saveteam.com.ridi.firebase.model;

import android.arch.persistence.room.Ignore;
import android.support.annotation.NonNull;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
public class FeedbackFB implements Serializable {
    public static final String DB_IN_FB = "feedbackv1";

    @NonNull
    private String uid;
    private String email;
    private String title;
    private String description;
    private int rating;

    public FeedbackFB() {

    }

    @Ignore
    public FeedbackFB(@NonNull String uid, String email, String title, String description, int rating) {
        this.uid = uid;
        this.email = email;
        this.title = title;
        this.description = description;
        this.rating = rating;
    }

    @NonNull
    public String getUid() {
        return uid;
    }

    public void setUid(@NonNull String uid) {
        this.uid = uid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

