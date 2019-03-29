package tk.uditsharma.clientapp.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import javax.inject.Inject;

import tk.uditsharma.clientapp.model.ApiResponse;
import tk.uditsharma.clientapp.model.LoginData;
import tk.uditsharma.clientapp.repository.UserRepository;
import tk.uditsharma.clientapp.util.RequestAuthenticationInterceptor;

public class LoginViewModel extends ViewModel {
    private UserRepository userRepository;
    private RequestAuthenticationInterceptor rInterceptor;

    @Inject
    public LoginViewModel(UserRepository userRepository, RequestAuthenticationInterceptor rInterceptor) {
        this.userRepository = userRepository;
        this.rInterceptor = rInterceptor;
    }

    public LiveData<ApiResponse<LoginData>> logInUser(){
        return userRepository.logIn();
    }

    public void addCredentials(String credentials){
        rInterceptor.setCredentials(credentials);
    }

    public void addToken(String token){
        rInterceptor.setToken(token);
    }

}
