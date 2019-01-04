package saveteam.com.ridesharing.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.support.annotation.NonNull;

import saveteam.com.ridesharing.database.dao.ProfileDao;
import saveteam.com.ridesharing.database.dao.SearchPlaceHistoryDao;
import saveteam.com.ridesharing.database.dao.UserDao;
import saveteam.com.ridesharing.database.model.Converters;
import saveteam.com.ridesharing.database.model.Profile;
import saveteam.com.ridesharing.database.model.SearchPlaceHistory;
import saveteam.com.ridesharing.database.model.User;

@Database(entities = {User.class, Profile.class, SearchPlaceHistory.class}, version = 4, exportSchema = false)
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
