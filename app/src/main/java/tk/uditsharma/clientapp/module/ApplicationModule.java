package tk.uditsharma.clientapp.module;

import android.app.Application;
import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.sql.Timestamp;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import tk.uditsharma.clientapp.model.UserDao;
import tk.uditsharma.clientapp.model.UserDataAPI;

@Module
public class ApplicationModule {

    private final Application application;

    private String encodedString = null;

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
    @Provides
    public UserDataAPI provideUserDataApi(Retrofit retrofit) {
        return retrofit.create(UserDataAPI.class);
    }

    @Singleton
    @Provides
    public OkHttpClient provideOkHttpClient(Interceptor interceptor){
        return new OkHttpClient.Builder().addInterceptor(interceptor).build();
    }

    @Singleton
    @Provides
    public Interceptor provideInterceptor(){

        try {
            encodedString = Base64.encodeToString(
                    UserDao.getToken().getBytes("UTF-8"),Base64.NO_WRAP);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request originalRequest = chain.request();

                Request.Builder builder = originalRequest.newBuilder().header("Authorization",
                        "Bearer " + encodedString);

                Request newRequest = builder.build();
                return chain.proceed(newRequest);
            }
        };
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
        return new GsonBuilder();
    }

}
