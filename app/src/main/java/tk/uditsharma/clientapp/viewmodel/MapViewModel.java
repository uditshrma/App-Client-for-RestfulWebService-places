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
import tk.uditsharma.clientapp.model.CommentResponse;
import tk.uditsharma.clientapp.repository.PlaceRepository;

public class MapViewModel extends ViewModel {

    private PlaceRepository placeRepository;
    private CompositeDisposable compositeDisposable;
    private final MutableLiveData<ApiResponse<List<CommentResponse>>> cResponse = new MutableLiveData<>();
    private final MutableLiveData<ApiResponse<ResponseBody>> postCommentRes = new MutableLiveData<>();
    private final MutableLiveData<ApiResponse<ResponseBody>> addPlaceRes = new MutableLiveData<>();
    private final MutableLiveData<ApiResponse<ResponseBody>> delCommRes = new MutableLiveData<>();
    private final MutableLiveData<ApiResponse<ResponseBody>> editCommRes = new MutableLiveData<>();

    @Inject
    public MapViewModel(PlaceRepository placeRepository) {
        this.placeRepository = placeRepository;
        compositeDisposable = new CompositeDisposable();
    }

    public void notifyPlaceUpdate(String placeId, String cDate) {
        ApiResponse<List<AllPlacesResponse>> temp = placeRepository.getPlaceLiveData().getValue();
        temp.getData().add(new AllPlacesResponse(cDate, placeId));
        placeRepository.getPlaceLiveData().postValue(temp);
    }

    public void notifyRemoveComment(int commentId){
        ApiResponse<List<CommentResponse>> temp = cResponse.getValue();
        Iterator<CommentResponse> it = temp.getData().iterator();
        while (it.hasNext()) {
            CommentResponse cItem = it.next();
            if (cItem.getId() == commentId) {
                it.remove();
                cResponse.postValue(temp);
                break;
            }
        }
    }

    public void notifyEditComment(int commentId, String txt){
        ApiResponse<List<CommentResponse>> temp = cResponse.getValue();
        Iterator<CommentResponse> it = temp.getData().iterator();
        while (it.hasNext()) {
            CommentResponse cItem = it.next();
            if (cItem.getId() == commentId) {
                cItem.setCommentText(txt);
                cResponse.postValue(temp);
                break;
            }
        }
    }

    public void notifyAddComment(CommentResponse comment){
        ApiResponse<List<CommentResponse>> temp = cResponse.getValue();
        temp.getData().add(comment);
        cResponse.postValue(temp);
    }

    public LiveData<ApiResponse<List<CommentResponse>>> fetchCommentList(String placeId) {
        compositeDisposable.add(placeRepository.getCommentData(placeId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getCommentObserver()));

        return cResponse;
    }

    public LiveData<ApiResponse<ResponseBody>> postComment(String uName, String cTxt, String placeId) {
        compositeDisposable.add(placeRepository.postComment(uName, cTxt, placeId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(postCommentObserver()));

        return postCommentRes;
    }

    public LiveData<ApiResponse<ResponseBody>> deleteComment(String uName, int cId) {
        compositeDisposable.add(placeRepository.deleteComment(uName, cId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(deleteCommentObserver()));

        return delCommRes;
    }

    public LiveData<ApiResponse<ResponseBody>> editComment(String uName, String cTxt, int cId) {
        compositeDisposable.add(placeRepository.editComment(uName, cTxt, cId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(editCommentObserver()));

        return editCommRes;
    }

    public LiveData<ApiResponse<ResponseBody>> addPlace(String uName, String placeId, String cDate) {
        compositeDisposable.add(placeRepository.addPlaceToDate(uName, placeId, cDate)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(addPlaceObserver()));

        return addPlaceRes;
    }

    private DisposableSingleObserver<List<CommentResponse>> getCommentObserver() {
        return new DisposableSingleObserver<List<CommentResponse>>() {
            @Override
            public void onSuccess(List<CommentResponse> value) {
                cResponse.postValue(new ApiResponse<>(value));
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                cResponse.postValue(new ApiResponse<>(e));
            }
        };
    }

    private DisposableSingleObserver<ResponseBody> postCommentObserver() {
        return new DisposableSingleObserver<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody value) {
                postCommentRes.postValue(new ApiResponse<>(value));
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                postCommentRes.postValue(new ApiResponse<>(e));
            }
        };
    }

    private DisposableSingleObserver<ResponseBody> addPlaceObserver() {
        return new DisposableSingleObserver<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody value) {
                addPlaceRes.postValue(new ApiResponse<>(value));
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                addPlaceRes.postValue(new ApiResponse<>(e));
            }
        };
    }

    private DisposableSingleObserver<ResponseBody> deleteCommentObserver() {
        return new DisposableSingleObserver<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody value) {
                delCommRes.postValue(new ApiResponse<>(value));
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                delCommRes.postValue(new ApiResponse<>(e));
            }
        };
    }

    private DisposableSingleObserver<ResponseBody> editCommentObserver() {
        return new DisposableSingleObserver<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody value) {
                editCommRes.postValue(new ApiResponse<>(value));
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                editCommRes.postValue(new ApiResponse<>(e));
            }
        };
    }

}
