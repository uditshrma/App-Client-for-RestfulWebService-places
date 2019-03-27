package tk.uditsharma.clientapp.repository;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Single;
import tk.uditsharma.clientapp.model.User;
import tk.uditsharma.clientapp.model.UserDataAPI;

@Singleton
public class UserRepository {

    private UserDataAPI userService;

    @Inject
    public UserRepository(UserDataAPI userService) {
        this.userService = userService;
    }

    public Single<List<User>> getUsers() {
        return userService.getUserList();
    }

}
