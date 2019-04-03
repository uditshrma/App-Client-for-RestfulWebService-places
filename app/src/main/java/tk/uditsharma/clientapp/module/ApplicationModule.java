package tk.uditsharma.clientapp.module;

import android.app.Application;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.sql.Timestamp;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import tk.uditsharma.clientapp.model.UserDataAPI;
import tk.uditsharma.clientapp.util.RequestAuthenticationInterceptor;

@Module
public class ApplicationModule {

    private final Application application;

    public ApplicationModule(Application pApp){
        this.application = pApp;
    }

    @Singleton
    @Provides
    public Application app(){ return application; }

    @Singleton
    @Provides
    public Retrofit provideRetrofit(OkHttpClient httpClient,JsonDeserializer deserializer, GsonBuilder builder) {

        // Register an adapter to manage the date types as long values
        builder.registerTypeAdapter(Timestamp.class, deserializer);

        Gson gson = builder.create();

        return new Retrofit.Builder()
                .baseUrl(UserDataAPI.BASE_URL)
                .client(httpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    @Singleton
    @Named("user_service")
    @Provides
    public UserDataAPI provideUserDataApi(Retrofit retrofit) {
        return retrofit.create(UserDataAPI.class);
    }

    @Singleton
    @Provides
    public OkHttpClient provideOkHttpClient(RequestAuthenticationInterceptor interceptor){
        return new OkHttpClient.Builder().addInterceptor(interceptor).build();
    }

    @Singleton
    @Provides
    public RequestAuthenticationInterceptor provideInterceptor(){
        return new RequestAuthenticationInterceptor();
    }

    @Singleton
    @Provides
    public JsonDeserializer provideJsonDeserializer() {
        return new JsonDeserializer() {
            public Timestamp deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                return new Timestamp(json.getAsJsonPrimitive().getAsLong());
            }
        };
    }

    @Singleton
    @Provides
    public GsonBuilder provideGsonBuilder() {
        return new GsonBuilder().setLenient();
    }

}
