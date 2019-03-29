package tk.uditsharma.clientapp.util;

import android.util.Base64;
import android.util.Log;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import okhttp3.Interceptor;
import okhttp3.Request;
import tk.uditsharma.clientapp.model.UserDao;

public class RequestAuthenticationInterceptor implements Interceptor {

    private String authCredentials = null;

    public void setCredentials(String creds){
        this.authCredentials = creds;
    }

    public void setToken(String token){
        try {
            this.authCredentials = "Bearer " + Base64.encodeToString(
                    token.getBytes("UTF-8"), Base64.NO_WRAP);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public okhttp3.Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        Request.Builder builder = originalRequest.newBuilder().header("Authorization",
                authCredentials);
        Request newRequest = builder.build();
        return chain.proceed(newRequest);
    }

}
