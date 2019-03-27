package tk.uditsharma.clientapp.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "place_table", indices = {@Index(value = {"user_id", "place_id"},
        unique = true)})
public class PlaceEntry {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int entryId;
    @NonNull
    @ColumnInfo(name = "user_id")
    private  String userId;
    @NonNull
    @ColumnInfo(name = "place_id")
    private String placeId;

    @NonNull
    @ColumnInfo(name = "place_name")
    private String placeName;

    @NonNull
    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(@NonNull String placeName) {
        this.placeName = placeName;
    }

    public int getEntryId() {
        return entryId;
    }

    public void setEntryId(int mId) {
        this.entryId = mId;
    }

    @NonNull
    public String getUserId() {
        return userId;
    }

    @NonNull
    public String getPlaceId() {
        return placeId;
    }

    public PlaceEntry(@NonNull String userId, @NonNull String placeId, @NonNull String placeName) {
        this.userId = userId;
        this.placeId = placeId;
        this.placeName = placeName;
    }
}
