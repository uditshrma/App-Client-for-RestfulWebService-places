package tk.uditsharma.clientapp.module;

import android.app.Application;
import android.arch.persistence.room.Room;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import tk.uditsharma.clientapp.model.PlaceEntryDao;
import tk.uditsharma.clientapp.model.PlaceRoomDatabase;

@Module(includes = ApplicationModule.class)
public class RoomModule {

    @Provides
    @Singleton
    public PlaceRoomDatabase provideDatabase(Application mApp){
        //return PlaceRoomDatabase.getDatabase(mApp);
        return Room.databaseBuilder(mApp, PlaceRoomDatabase.class, "place_database").build();
    }

    @Singleton
    @Provides
    public PlaceEntryDao provideProductDao(PlaceRoomDatabase pDatabase) {
        return pDatabase.placeEntryDao();
    }

}
