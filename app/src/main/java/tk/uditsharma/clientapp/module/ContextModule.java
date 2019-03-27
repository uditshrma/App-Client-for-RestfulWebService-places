package tk.uditsharma.clientapp.module;

import android.content.Context;

import tk.uditsharma.clientapp.interfaces.PlacesApplicationScope;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public class ContextModule {

    Context context;

    public ContextModule(Context context){
        this.context = context;
    }

    @Named("application_context")
    @PlacesApplicationScope
    @Provides
    public Context context(){ return context.getApplicationContext(); }
}
