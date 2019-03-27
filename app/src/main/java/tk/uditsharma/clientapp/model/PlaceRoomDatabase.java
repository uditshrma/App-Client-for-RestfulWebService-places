package tk.uditsharma.clientapp.model;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {PlaceEntry.class}, version = 1, exportSchema = false)
public abstract class PlaceRoomDatabase extends RoomDatabase {

    public abstract PlaceEntryDao placeEntryDao();

    private static volatile PlaceRoomDatabase INSTANCE;

    /*public static PlaceRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (PlaceRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            PlaceRoomDatabase.class, "place_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }*/
}
