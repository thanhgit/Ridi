package saveteam.com.quagiang.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import saveteam.com.quagiang.database.model.SearchPlaceHistory;

@Dao
public interface SearchPlaceHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSearchPlaceHistorys(SearchPlaceHistory... SearchPlaceHistorys);

    @Update
    void updateSearchPlaceHistorys(SearchPlaceHistory... SearchPlaceHistorys);

    @Delete
    void deleteSearchPlaceHistorys(SearchPlaceHistory... SearchPlaceHistorys);

    @Query("SELECT * FROM searchplacehistories")
    List<SearchPlaceHistory> loadAllSearchPlaceHistorys();

    @Query("SELECT * FROM searchplacehistories WHERE title = :title")
    List<SearchPlaceHistory> loadSearchPlaceHistoryBy(String title);
}
