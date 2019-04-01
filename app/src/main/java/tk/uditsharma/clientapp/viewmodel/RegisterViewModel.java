package tk.uditsharma.clientapp.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import javax.inject.Inject;

import tk.uditsharma.clientapp.model.ApiResponse;
import tk.uditsharma.clientapp.model.RegData;
import tk.uditsharma.clientapp.repository.UserRepository;

public class RegisterViewModel extends ViewModel {
    private UserRepository userRepository;

    @Inject
    public RegisterViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public LiveData<ApiResponse<RegData>> registerUser(String name,String uName,String pwd){
        return userRepository.regUser(name, uName, pwd);
    }

}
