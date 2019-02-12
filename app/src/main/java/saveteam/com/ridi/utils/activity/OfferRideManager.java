package saveteam.com.ridi.utils.activity;

import saveteam.com.ridi.firebase.model.ProfileFB;
import saveteam.com.ridi.firebase.model.TripFB;

public class OfferRideManager {
    private static OfferRideManager instance;

    /**
     * Pre - offer
     */
    private ProfileFB profile;
    private TripFB tripFB;

    /**
     * confirm
     */

    public static OfferRideManager getInstance() {
        if (instance == null) {
            instance = new OfferRideManager();
        }

        return instance;
    }
}
