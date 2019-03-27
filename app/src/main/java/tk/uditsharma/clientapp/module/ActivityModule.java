package tk.uditsharma.clientapp.module;

import android.app.Activity;
import android.content.Context;

import tk.uditsharma.clientapp.interfaces.PlacesApplicationScope;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public class ActivityModule {

    private final Context context;

    ActivityModule(Activity context){
        this.context = context;
    }

    @Named("activity_context")
    @PlacesApplicationScope
    @Provides
    public Context context(){ return context; }
}
