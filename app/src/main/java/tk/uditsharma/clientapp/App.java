package tk.uditsharma.clientapp;

import android.app.Activity;
import android.app.Application;

import com.google.android.gms.maps.MapsInitializer;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import tk.uditsharma.clientapp.component.DaggerSingletonComponent;
import tk.uditsharma.clientapp.component.SingletonComponent;
import tk.uditsharma.clientapp.module.ApplicationModule;

public class App extends Application implements HasActivityInjector {

    @Inject
    DispatchingAndroidInjector<Activity> dispatchingAndroidInjector;

    //private SingletonComponent singletonApplicationComponent;

    public static App get(Activity activity){
        return (App) activity.getApplication();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MapsInitializer.initialize(this);
        SingletonComponent singletonApplicationComponent = DaggerSingletonComponent.builder()
                .application(this)
                .applicationModule(new ApplicationModule(this))
                .build();

        singletonApplicationComponent.inject(this);
    }

    /*public SingletonComponent getSingletonApplicationComponent(){
        return singletonApplicationComponent;
    }*/

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return dispatchingAndroidInjector;
    }

}
