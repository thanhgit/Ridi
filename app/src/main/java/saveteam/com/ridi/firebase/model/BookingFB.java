package saveteam.com.ridi.firebase.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;


import java.io.Serializable;

@Entity(tableName = "bookings")
public class BookingFB implements Serializable {
    @PrimaryKey
    @NonNull
    private String uid;

    /**
     * Find ride
     */
    private String findRideId;
    private String findRideName;

    private String findRideFromPlace;
    private String findRideToPlace;

    private String findRideFromTime;
    private String findRideToTime;

    /**
     * Offer ride
     */
    private String offerRideId;
    private String offerRideName;

    private String offerRideFromPlace;
    private String offerRideToPlace;

    private String offerRideFromTime;
    private String offerRideToTime;

    /**
     * common
     */
    private double distance;
    private double cost;

    private String licensePlates;

    private String status;

    private String getOnPlace;
    private String getOffPlace;

    private String getOnTime;
    private String getOffTime;

    public BookingFB() {
    }

    @Ignore
    public BookingFB(@NonNull String uid, String findRideId, String findRideName, String findRideFromPlace, String findRideToPlace, String offerRideId, String offerRideName, String offerRideFromPlace, String offerRideToPlace, double distance, double cost, String getOnPlace, String getOffPlace, String getOnTime, String getOffTime, String licensePlates, String status) {
        this.uid = uid;
        this.findRideId = findRideId;
        this.findRideName = findRideName;
        this.findRideFromPlace = findRideFromPlace;
        this.findRideToPlace = findRideToPlace;
        this.offerRideId = offerRideId;
        this.offerRideName = offerRideName;
        this.offerRideFromPlace = offerRideFromPlace;
        this.offerRideToPlace = offerRideToPlace;
        this.distance = distance;
        this.cost = cost;
        this.getOnPlace = getOnPlace;
        this.getOffPlace = getOffPlace;
        this.getOnTime = getOnTime;
        this.getOffTime = getOffTime;
        this.licensePlates = licensePlates;
        this.status = status;
    }

    @Ignore
    public BookingFB(@NonNull String uid, String findRideId, String findRideName, String findRideFromPlace, String findRideToPlace, String offerRideId, String offerRideName, String offerRideFromPlace, String offerRideToPlace, double distance, double cost) {
        this.uid = uid;
        this.findRideId = findRideId;
        this.findRideName = findRideName;
        this.findRideFromPlace = findRideFromPlace;
        this.findRideToPlace = findRideToPlace;
        this.offerRideId = offerRideId;
        this.offerRideName = offerRideName;
        this.offerRideFromPlace = offerRideFromPlace;
        this.offerRideToPlace = offerRideToPlace;
        this.distance = distance;
        this.cost = cost;
        this.getOnPlace = "";
        this.getOffPlace = "";
        this.getOnTime = "";
        this.getOffTime = "";
        this.licensePlates = "";
        this.status = "none";
    }

    @NonNull
    public String getUid() {
        return uid;
    }

    public void setUid(@NonNull String uid) {
        this.uid = uid;
    }

    public String getFindRideId() {
        return findRideId;
    }

    public void setFindRideId(String findRideId) {
        this.findRideId = findRideId;
    }

    public String getFindRideName() {
        return findRideName;
    }

    public void setFindRideName(String findRideName) {
        this.findRideName = findRideName;
    }

    public String getOfferRideId() {
        return offerRideId;
    }

    public void setOfferRideId(String offerRideId) {
        this.offerRideId = offerRideId;
    }

    public String getOfferRideName() {
        return offerRideName;
    }

    public void setOfferRideName(String offerRideName) {
        this.offerRideName = offerRideName;
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

    public String getGetOnPlace() {
        return getOnPlace;
    }

    public void setGetOnPlace(String getOnPlace) {
        this.getOnPlace = getOnPlace;
    }

    public String getGetOffPlace() {
        return getOffPlace;
    }

    public void setGetOffPlace(String getOffPlace) {
        this.getOffPlace = getOffPlace;
    }

    public String getGetOnTime() {
        return getOnTime;
    }

    public void setGetOnTime(String getOnTime) {
        this.getOnTime = getOnTime;
    }

    public String getGetOffTime() {
        return getOffTime;
    }

    public void setGetOffTime(String getOffTime) {
        this.getOffTime = getOffTime;
    }

    public String getLicensePlates() {
        return licensePlates;
    }

    public void setLicensePlates(String licensePlates) {
        this.licensePlates = licensePlates;
    }

    public String getFindRideFromPlace() {
        return findRideFromPlace;
    }

    public void setFindRideFromPlace(String findRideFromPlace) {
        this.findRideFromPlace = findRideFromPlace;
    }

    public String getFindRideToPlace() {
        return findRideToPlace;
    }

    public void setFindRideToPlace(String findRideToPlace) {
        this.findRideToPlace = findRideToPlace;
    }

    public String getOfferRideFromPlace() {
        return offerRideFromPlace;
    }

    public void setOfferRideFromPlace(String offerRideFromPlace) {
        this.offerRideFromPlace = offerRideFromPlace;
    }

    public String getOfferRideToPlace() {
        return offerRideToPlace;
    }

    public void setOfferRideToPlace(String offerRideToPlace) {
        this.offerRideToPlace = offerRideToPlace;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOfferRideFromTime() {
        return offerRideFromTime;
    }

    public void setOfferRideFromTime(String offerRideFromTime) {
        this.offerRideFromTime = offerRideFromTime;
    }

    public String getOfferRideToTime() {
        return offerRideToTime;
    }

    public void setOfferRideToTime(String offerRideToTime) {
        this.offerRideToTime = offerRideToTime;
    }

    public String getFindRideFromTime() {
        return findRideFromTime;
    }

    public void setFindRideFromTime(String findRideFromTime) {
        this.findRideFromTime = findRideFromTime;
    }

    public String getFindRideToTime() {
        return findRideToTime;
    }

    public void setFindRideToTime(String findRideToTime) {
        this.findRideToTime = findRideToTime;
    }
}
