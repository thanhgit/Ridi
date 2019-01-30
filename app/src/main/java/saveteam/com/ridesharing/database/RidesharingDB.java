package saveteam.com.ridesharing.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import saveteam.com.ridesharing.database.dao.ProfileDao;
import saveteam.com.ridesharing.database.dao.SearchPlaceHistoryDao;
import saveteam.com.ridesharing.database.dao.UserDao;
import saveteam.com.ridesharing.database.model.Converters;
import saveteam.com.ridesharing.database.model.SearchPlaceHistory;
import saveteam.com.ridesharing.database.model.User;
import saveteam.com.ridesharing.firebase.model.ProfileFB;

@Database(entities = {User.class, ProfileFB.class, SearchPlaceHistory.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class RidesharingDB extends RoomDatabase {
    private static final String DATABASE_NAME = "ridesharing_db";

    public abstract UserDao getUserDao();
    public abstract ProfileDao getProfileDao();
    public abstract SearchPlaceHistoryDao getSearchPlaceHistoryDao();

    private static volatile RidesharingDB instance;

    public static RidesharingDB getInstance(Context context) {
        if (instance == null) {
            synchronized (RidesharingDB.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            RidesharingDB.class,
                            DATABASE_NAME)
                            .fallbackToDestructiveMigration().build();
                }
            }
        }

        return instance;
    }
}
