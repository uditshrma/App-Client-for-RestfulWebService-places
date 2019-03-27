package tk.uditsharma.clientapp.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import tk.uditsharma.clientapp.model.User;
import tk.uditsharma.clientapp.model.ApiResponse;
import tk.uditsharma.clientapp.repository.UserRepository;

public class UserViewModel extends ViewModel {

    private UserRepository userRepository;
    private CompositeDisposable compositeDisposable;
    private final MutableLiveData<ApiResponse<List<User>>> uResponse = new MutableLiveData<>();

    @Inject
    public UserViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
        compositeDisposable = new CompositeDisposable();
        fetchUserList();
    }

    public LiveData<ApiResponse<List<User>>> getUserListResponse() {
        return uResponse;
    }


    public void fetchUserList() {
        compositeDisposable.add(userRepository.getUsers()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getUserListObserver()));
    }

    private DisposableSingleObserver<List<User>> getUserListObserver() {
        return new DisposableSingleObserver<List<User>>() {
            @Override
            public void onSuccess(List<User> value) {
                uResponse.postValue(new ApiResponse<>(value));
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                uResponse.postValue(new ApiResponse<>(e));
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
