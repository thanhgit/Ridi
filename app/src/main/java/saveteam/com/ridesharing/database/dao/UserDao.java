package saveteam.com.ridesharing.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import saveteam.com.ridesharing.database.model.User;

@Dao
public interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertUsers(User... users);

    @Update
    public void updateUsers(User... users);

    @Delete
    public void deleteUsers(User... users);

    @Query("SELECT * FROM users")
    public User[] loadAllUsers();

    @Query("SELECT * FROM users WHERE uid = :uid")
    public User[] loadUserBy(String uid);
}
