package tk.uditsharma.clientapp.module;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;
import tk.uditsharma.clientapp.interfaces.ViewModelKey;
import tk.uditsharma.clientapp.util.DaggerViewModelFactory;
import tk.uditsharma.clientapp.viewmodel.PlaceViewModel;
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
    abstract ViewModelProvider.Factory bindViewModelFactory(DaggerViewModelFactory factory);

}
