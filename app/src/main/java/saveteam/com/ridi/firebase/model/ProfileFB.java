package saveteam.com.ridi.firebase.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
@Entity(tableName = "profiles")
public class ProfileFB implements Serializable {
    public static final String DB_IN_FB = "profilesv1";

    @PrimaryKey
    @NonNull
    private String uid;

    private String firstName;
    private String lastName;
    private String avatar;
    private String phone;
    private String mode;
    private String birthday;
    private String homePlace;
    private String officePlace;
    private String startTime;
    private String leaveOfficeTime;
    private String vehicles;
    private float rating;
    /**
     * true -> male
     * false -> female
     */
    private boolean gender;

    public ProfileFB() {
    }

    @Ignore
    public ProfileFB(@NonNull String uid, String firstName, String lastName, String avatar, String phone, String mode, String birthday, String homePlace, String officePlace, String startTime, String leaveOfficeTime,String vehicles,int rating, boolean gender) {
        this.uid = uid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.avatar = avatar;
        this.phone = phone;
        this.mode = mode;
        this.birthday = birthday;
        this.homePlace = homePlace;
        this.officePlace = officePlace;
        this.startTime = startTime;
        this.leaveOfficeTime = leaveOfficeTime;
        this.vehicles = vehicles;
        this.rating = rating;
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

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getHomePlace() {
        return homePlace;
    }

    public void setHomePlace(String homePlace) {
        this.homePlace = homePlace;
    }

    public String getOfficePlace() {
        return officePlace;
    }

    public void setOfficePlace(String officePlace) {
        this.officePlace = officePlace;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getLeaveOfficeTime() {
        return leaveOfficeTime;
    }

    public void setLeaveOfficeTime(String leaveOfficeTime) {
        this.leaveOfficeTime = leaveOfficeTime;
    }

    public boolean isGender() {
        return gender;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }

    public String getVehicles() {
        return vehicles;
    }

    public void setVehicles(String vehicles) {
        this.vehicles = vehicles;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
