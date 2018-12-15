package saveteam.com.ridesharing.database;

import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.support.annotation.NonNull;

import saveteam.com.ridesharing.database.dao.UserDao;
import saveteam.com.ridesharing.database.model.Converters;
import saveteam.com.ridesharing.database.model.User;

@Database(entities = {User.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class RidesharingDB extends RoomDatabase {
    private static final String DATABASE_NAME = "ridesharing_db";

    public abstract UserDao getUserDao();

    private static volatile RidesharingDB instance;

    public static RidesharingDB getInstance(Context context) {
        if (instance == null) {
            synchronized (RidesharingDB.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            RidesharingDB.class,
                            DATABASE_NAME).build();
                }
            }
        }

        return instance;
    }
    @NonNull
    @Override
    protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration config) {
        return null;
    }

    @NonNull
    @Override
    protected InvalidationTracker createInvalidationTracker() {
        return null;
    }

    @Override
    public void clearAllTables() {

    }
}
