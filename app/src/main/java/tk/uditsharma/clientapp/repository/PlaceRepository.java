package tk.uditsharma.clientapp.repository;

import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import tk.uditsharma.clientapp.model.PlaceEntry;
import tk.uditsharma.clientapp.model.PlaceEntryDao;
import tk.uditsharma.clientapp.model.UserDao;

@Singleton
public class PlaceRepository {
    private PlaceEntryDao mPlaceDao;
    private LiveData<List<PlaceEntry>> uAllPlaces;

    @Inject
    PlaceRepository(PlaceEntryDao placeDao) {
        //PlaceRoomDatabase db = PlaceRoomDatabase.getDatabase(application);
        //mPlaceDao = db.placeEntryDao();
        this.mPlaceDao = placeDao;
        uAllPlaces = mPlaceDao.getAllPlacesByUser(UserDao.getCurrentUser());
    }

    public LiveData<List<PlaceEntry>> getAllPlaces() {
        return uAllPlaces;
    }

    public void insert (PlaceEntry place) {
        new insertAsyncTask(mPlaceDao).execute(place);
    }

    public void delete (PlaceEntry place) {
        new deleteAsyncTask(mPlaceDao).execute(place);
    }

    private static class insertAsyncTask extends AsyncTask<PlaceEntry, Void, Void> {

        private PlaceEntryDao mAsyncTaskDao;

        insertAsyncTask(PlaceEntryDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final PlaceEntry... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

    private static class deleteAsyncTask extends AsyncTask<PlaceEntry, Void, Void> {

        private PlaceEntryDao mAsyncTaskDao;

        deleteAsyncTask(PlaceEntryDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final PlaceEntry... params) {
            mAsyncTaskDao.deletePlaces(params[0]);
            return null;
        }
    }

}
