package tk.uditsharma.clientapp.component;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.support.AndroidSupportInjectionModule;
import tk.uditsharma.clientapp.App;
import tk.uditsharma.clientapp.module.ActivityBuilder;
import tk.uditsharma.clientapp.module.ApplicationModule;
import tk.uditsharma.clientapp.module.RoomModule;
import tk.uditsharma.clientapp.module.ViewModelModule;

@Singleton
@Component(modules = {AndroidSupportInjectionModule.class, RoomModule.class, ActivityBuilder.class, ViewModelModule.class})
public interface SingletonComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(App application);
        Builder applicationModule(ApplicationModule appModule);
        SingletonComponent build();
    }

    void inject(App application);
    /*void injectWishListActivity(WishListActivity wActivity);

    void injectMapActivity(MapsActivity mActivity);*/
}
