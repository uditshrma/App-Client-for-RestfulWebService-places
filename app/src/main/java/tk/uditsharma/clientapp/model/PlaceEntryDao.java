package tk.uditsharma.clientapp.model;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface PlaceEntryDao {
    @Insert
    void insert(PlaceEntry place);

    @Query("DELETE FROM place_table")
    void deleteAll();

    @Query("SELECT * from place_table")
    LiveData<List<PlaceEntry>> getAllPlaces();

    @Query("SELECT * from place_table WHERE user_id = :uId ORDER BY place_name")
    LiveData<List<PlaceEntry>> getAllPlacesByUser(String uId);

    @Delete
    void deletePlaces(PlaceEntry... users);

}
