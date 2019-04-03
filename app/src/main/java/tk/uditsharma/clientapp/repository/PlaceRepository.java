package tk.uditsharma.clientapp.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.Call;
import tk.uditsharma.clientapp.model.AllPlacesResponse;
import tk.uditsharma.clientapp.model.ApiResponse;
import tk.uditsharma.clientapp.model.PlaceEntry;
import tk.uditsharma.clientapp.model.PlaceEntryDao;
import tk.uditsharma.clientapp.model.UserDao;
import tk.uditsharma.clientapp.model.UserDataAPI;
import tk.uditsharma.clientapp.util.Constants;

@Singleton
public class PlaceRepository {
    private PlaceEntryDao mPlaceDao;
    private LiveData<List<PlaceEntry>> uAllPlaces;
    private UserDataAPI userService;

    @Inject
    PlaceRepository(PlaceEntryDao placeDao, @Named("user_service") UserDataAPI userService) {
        this.mPlaceDao = placeDao;
        this.userService = userService;
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

    public Single<List<AllPlacesResponse>> getPlacesApiData(String uName) {
        return userService.getPlaces(uName);
    }

    public Single<ResponseBody> deletePlacesFromWebService(String uName, String pId, String date) {
        return userService.deletePlace(uName, pId, date);
    }

}
