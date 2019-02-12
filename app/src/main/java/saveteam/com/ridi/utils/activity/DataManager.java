package saveteam.com.ridi.utils.activity;

import saveteam.com.ridi.firebase.model.ConfirmListFB;
import saveteam.com.ridi.firebase.model.ProfileFB;
import saveteam.com.ridi.firebase.model.TripFB;

public class DataManager {
    private static DataManager instance;

    private ProfileFB profile;
    private ProfileFB profileMatching;
    private ConfirmListFB confirm;
    private TripFB findRideTrip;
    private TripFB offerRideTrip;

    private double distance;
    private double cost;

    public static DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }

        return  instance;
    }

    public ProfileFB getProfile() {
        return profile;
    }

    public void setProfile(ProfileFB profile) {
        this.profile = profile;
    }

    public ConfirmListFB getConfirm() {
        return confirm;
    }

    public void setConfirm(ConfirmListFB confirm) {
        this.confirm = confirm;
    }

    public TripFB getFindRideTrip() {
        return findRideTrip;
    }

    public void setFindRideTrip(TripFB findRideTrip) {
        this.findRideTrip = findRideTrip;
    }

    public TripFB getOfferRideTrip() {
        return offerRideTrip;
    }

    public void setOfferRideTrip(TripFB offerRideTrip) {
        this.offerRideTrip = offerRideTrip;
    }

    public ProfileFB getProfileMatching() {
        return profileMatching;
    }

    public void setProfileMatching(ProfileFB profileMatching) {
        this.profileMatching = profileMatching;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }
}
