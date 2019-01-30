package saveteam.com.ridesharing.firebase.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;


import java.io.Serializable;

@Entity(tableName = "confirms")
public class ConfirmFB implements Serializable {
    @PrimaryKey
    @NonNull
    private String uid;

    private String findRideId;
    private String findRideName;

    private String offerRideId;
    private String offerRideName;

    private double distance;
    private double cost;

    private String getOnPlace;
    private String getOffPlace;

    private String getOnTime;
    private String getOffTime;

    private String licensePlates;

    private String status;

    public ConfirmFB() {
    }

    @Ignore
    public ConfirmFB(@NonNull String uid, String findRideId, String findRideName, String offerRideId, String offerRideName, double distance, double cost, String getOnPlace, String getOffPlace, String getOnTime, String getOffTime, String licensePlates) {
        this.uid = uid;
        this.findRideId = findRideId;
        this.findRideName = findRideName;
        this.offerRideId = offerRideId;
        this.offerRideName = offerRideName;
        this.distance = distance;
        this.cost = cost;
        this.getOnPlace = getOnPlace;
        this.getOffPlace = getOffPlace;
        this.getOnTime = getOnTime;
        this.getOffTime = getOffTime;
        this.licensePlates = licensePlates;

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
}