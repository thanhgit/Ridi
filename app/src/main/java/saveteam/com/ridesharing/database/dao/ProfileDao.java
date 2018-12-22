package saveteam.com.ridesharing.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import saveteam.com.ridesharing.database.model.Profile;

@Dao
public interface ProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertProfiles(Profile... profiles);

    @Update
    public void updateProfiles(Profile... Profiles);

    @Delete
    public void deleteProfiles(Profile... Profiles);

    @Query("SELECT * FROM profiles")
    public Profile[] loadAllProfiles();

    @Query("SELECT * FROM profiles WHERE uid = :uid")
    public Profile[] loadProfileBy(String uid);
}
