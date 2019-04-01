package tk.uditsharma.clientapp.module;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import tk.uditsharma.clientapp.model.UserDataAPI;

@Module
public class RegisterModule {

    @Named("register_retrofit")
    @Provides
    public Retrofit provideRetrofit(OkHttpClient httpClient,@Named("register_gson_builder") GsonBuilder builder) {
        Gson gson = builder
                .setLenient()
                .create();

        return new Retrofit.Builder()
                .baseUrl(UserDataAPI.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    @Named("register_api")
    @Provides
    public UserDataAPI provideUserDataApi(@Named("register_retrofit") Retrofit retrofit) {
        return retrofit.create(UserDataAPI.class);
    }

    @Named("register_gson_builder")
    @Provides
    public GsonBuilder provideGsonBuilder() {
        return new GsonBuilder();
    }

}
