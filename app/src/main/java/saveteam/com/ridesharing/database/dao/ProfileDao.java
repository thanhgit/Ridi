package saveteam.com.ridesharing.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import saveteam.com.ridesharing.database.model.Profile;

@Dao
public interface ProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertProfiles(Profile... profiles);

    @Update
    void updateProfiles(Profile... Profiles);

    @Delete
    void deleteProfiles(Profile... Profiles);

    @Query("SELECT * FROM profiles")
    List<Profile> loadAllProfiles();

    @Query("SELECT * FROM profiles WHERE uid = :uid")
    List<Profile> loadProfileBy(String uid);
}
