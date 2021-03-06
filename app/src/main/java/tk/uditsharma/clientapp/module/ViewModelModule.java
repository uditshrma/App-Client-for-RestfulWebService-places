package tk.uditsharma.clientapp.module;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;
import tk.uditsharma.clientapp.interfaces.ViewModelKey;
import tk.uditsharma.clientapp.util.DaggerViewModelFactory;
import tk.uditsharma.clientapp.viewmodel.LoginViewModel;
import tk.uditsharma.clientapp.viewmodel.MapViewModel;
import tk.uditsharma.clientapp.viewmodel.PlaceViewModel;
import tk.uditsharma.clientapp.viewmodel.ProfileViewModel;
import tk.uditsharma.clientapp.viewmodel.RegisterViewModel;
import tk.uditsharma.clientapp.viewmodel.UserViewModel;

@Module
public abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(PlaceViewModel.class)
    abstract ViewModel providePlaceViewModel(PlaceViewModel placeViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(UserViewModel.class)
    abstract ViewModel provideUserViewModel(UserViewModel userViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel.class)
    abstract ViewModel provideLoginViewModel(LoginViewModel loginViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(RegisterViewModel.class)
    abstract ViewModel provideRegisterViewModel(RegisterViewModel registerViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ProfileViewModel.class)
    abstract ViewModel provideProfileViewModel(ProfileViewModel profileViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(MapViewModel.class)
    abstract ViewModel provideMapViewModel(MapViewModel mapViewModel);

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(DaggerViewModelFactory factory);

}
