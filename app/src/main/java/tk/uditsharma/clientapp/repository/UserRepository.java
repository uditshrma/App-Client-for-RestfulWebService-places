package tk.uditsharma.clientapp.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tk.uditsharma.clientapp.model.AllPlacesResponse;
import tk.uditsharma.clientapp.model.ApiResponse;
import tk.uditsharma.clientapp.model.LoginData;
import tk.uditsharma.clientapp.model.RegData;
import tk.uditsharma.clientapp.model.User;
import tk.uditsharma.clientapp.model.UserDataAPI;
import tk.uditsharma.clientapp.util.Constants;

@Singleton
public class UserRepository {

    private UserDataAPI userService;
    private UserDataAPI regService;


    @Inject
    public UserRepository(@Named("user_service") UserDataAPI userService,@Named("register_api") UserDataAPI regService) {
        this.userService = userService;
        this.regService = regService;
    }

    public Single<List<User>> getUsers() {
        return userService.getUserList();
    }

    public LiveData<ApiResponse<LoginData>> logIn(){
        final MutableLiveData<ApiResponse<LoginData>> loginResponse = new MutableLiveData<>();
        Call<LoginData> call = userService.login();
        call.enqueue(new Callback<LoginData>() {
            @Override
            public void onResponse(@NonNull Call<LoginData> call, @NonNull Response<LoginData> response) {
                if (response.isSuccessful()) {
                    loginResponse.postValue(new ApiResponse<>(response.body(),response.code()));
                } else{
                    loginResponse.postValue(new ApiResponse<>(response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginData> call, @NonNull Throwable t) {
                loginResponse.postValue(new ApiResponse<>(t));
            }
        });
        return loginResponse;
    }

    public LiveData<ApiResponse<RegData>> regUser(String name,String email,String password){
        final MutableLiveData<ApiResponse<RegData>> regResponse = new MutableLiveData<>();
        Call<RegData> call = regService.register(name, email, password);
        call.enqueue(new Callback<RegData>() {
            @Override
            public void onResponse(@NonNull Call<RegData> call, @NonNull Response<RegData> response) {
                if (response.isSuccessful()) {
                    regResponse.postValue(new ApiResponse<>(response.body(),response.code()));
                    Log.i(Constants.LOG_TAG,"Response Code: " + response.code());
                } else{
                    regResponse.postValue(new ApiResponse<>(response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<RegData> call, @NonNull Throwable t) {
                regResponse.postValue(new ApiResponse<>(t));
            }
        });
        return regResponse;
    }

}
