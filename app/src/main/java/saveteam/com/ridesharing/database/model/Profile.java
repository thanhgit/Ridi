package saveteam.com.ridesharing.database.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "profiles")
public class Profile {
    @PrimaryKey
    @NonNull
    private String uid;

    private String firstName;
    private String lastName;
    private String avatar;
    private String phone;
    /**
     * true -> male
     * false -> female
     */
    private boolean gender;

    public Profile() {

    }

    @Ignore
    public Profile(@NonNull String uid, String firstName, String lastName, String avatar, String phone, boolean gender) {
        this.uid = uid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.avatar = avatar;
        this.phone = phone;
        this.gender = gender;
    }

    @NonNull
    public String getUid() {
        return uid;
    }

    public void setUid(@NonNull String uid) {
        this.uid = uid;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isGender() {
        return gender;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }

    @Ignore
    public String getGenderString() {
        if (gender) {
            return "male";
        } else {
            return "female";
        }
    }
}
