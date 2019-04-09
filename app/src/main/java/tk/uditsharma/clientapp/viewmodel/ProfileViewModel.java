package tk.uditsharma.clientapp.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import tk.uditsharma.clientapp.model.AllPlacesResponse;
import tk.uditsharma.clientapp.model.ApiResponse;
import tk.uditsharma.clientapp.model.UserParcel;
import tk.uditsharma.clientapp.repository.PlaceRepository;

public class ProfileViewModel extends ViewModel {

    private PlaceRepository placeRepository;
    private CompositeDisposable compositeDisposable;
    private final MutableLiveData<ApiResponse<List<AllPlacesResponse>>> pResponse = new MutableLiveData<>();
    private final MutableLiveData<ApiResponse<ResponseBody>> dResponse = new MutableLiveData<>();
    private UserParcel selectedUser;

    public UserParcel getSelectedUser() {
        return selectedUser;
    }

    public void setSelectedUser(UserParcel selectedUser) {
        this.selectedUser = selectedUser;
    }

    @Inject
    public ProfileViewModel(PlaceRepository placeRepository) {
        this.placeRepository = placeRepository;
        compositeDisposable = new CompositeDisposable();
    }

    public LiveData<ApiResponse<List<AllPlacesResponse>>> fetchPlaceList() {
        compositeDisposable.add(placeRepository.getPlacesApiData(this.selectedUser.userName())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getPlaceListObserver()));

        return pResponse;
    }

    public ApiResponse<List<AllPlacesResponse>> getPlaceList() {
        return pResponse.getValue();
    }

    public void notifyRemovePlace(String date, String placeId){
        ApiResponse<List<AllPlacesResponse>> temp = pResponse.getValue();
        Iterator<AllPlacesResponse> it = temp.getData().iterator();
        while (it.hasNext()) {
            AllPlacesResponse placeItem = it.next();
            if ((placeItem.getPlaceId().equals(placeId)) && (placeItem.getDate().equals(date))) {
                it.remove();
                pResponse.postValue(temp);
                break;
            }
        }
    }

    public LiveData<ApiResponse<ResponseBody>> deletePlace(String uName, String placeId, String cDate) {
        compositeDisposable.add(placeRepository.deletePlacesFromWebService(uName, placeId, cDate)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(deletePlaceObserver()));
        return dResponse;
    }

    private DisposableSingleObserver<List<AllPlacesResponse>> getPlaceListObserver() {
        return new DisposableSingleObserver<List<AllPlacesResponse>>() {
            @Override
            public void onSuccess(List<AllPlacesResponse> value) {
                pResponse.postValue(new ApiResponse<>(value));
                placeRepository.setPlaceLiveData(pResponse);
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                pResponse.postValue(new ApiResponse<>(e));
            }
        };
    }

    private DisposableSingleObserver<ResponseBody> deletePlaceObserver() {
        return new DisposableSingleObserver<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody value) {
                dResponse.postValue(new ApiResponse<>(value));
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                dResponse.postValue(new ApiResponse<>(e));
            }
        };
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (compositeDisposable != null) {
            compositeDisposable.clear();
            compositeDisposable = null;
        }
    }

}
