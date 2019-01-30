package saveteam.com.ridesharing.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import saveteam.com.ridesharing.firebase.model.ProfileFB;

@Dao
public interface ProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertProfiles(ProfileFB... profiles);

    @Update
    void updateProfiles(ProfileFB... Profiles);

    @Delete
    void deleteProfiles(ProfileFB... Profiles);

    @Query("SELECT * FROM profiles")
    List<ProfileFB> loadAllProfiles();

    @Query("SELECT * FROM profiles WHERE uid = :uid")
    List<ProfileFB> loadProfileBy(String uid);
}
