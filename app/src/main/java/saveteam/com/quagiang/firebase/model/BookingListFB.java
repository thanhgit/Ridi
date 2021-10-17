package saveteam.com.quagiang.firebase.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@IgnoreExtraProperties
public class BookingListFB implements Serializable {
    public static final String DB_IN_FB = "bookingsv1";

    private String uid;
    private String userName;
    private List<BookingFB> bookings = new ArrayList<>();
    private List<ProfileFB> profiles = new ArrayList<>();

    public BookingListFB() {
    }

    public BookingListFB(String uid, String userName, List<BookingFB> bookings, List<ProfileFB> profiles) {
        this.uid = uid;
        this.userName = userName;
        this.bookings = bookings;
        this.profiles = profiles;
    }

    public List<BookingFB> getBookings() {
        return bookings;
    }

    public void setBookings(List<BookingFB> bookings) {
        this.bookings = bookings;
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

    public List<ProfileFB> getProfiles() {
        return profiles;
    }

    public void setProfiles(List<ProfileFB> profiles) {
        this.profiles = profiles;
    }
}
