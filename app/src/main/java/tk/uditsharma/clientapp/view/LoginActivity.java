package tk.uditsharma.clientapp.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import tk.uditsharma.clientapp.util.Constants;
import tk.uditsharma.clientapp.R;
import tk.uditsharma.clientapp.RegisterActivity;
import tk.uditsharma.clientapp.model.UserDao;
import tk.uditsharma.clientapp.model.UserDataAPI;
import tk.uditsharma.clientapp.util.Utility;
import tk.uditsharma.clientapp.model.LoginData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends Activity {

    ProgressDialog prgDialog;
    TextView errorMsg;
    EditText emailET;
    EditText pwdET;

    private String cUser;
    private String credentials;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        errorMsg = (TextView)findViewById(R.id.login_error);
        emailET = (EditText)findViewById(R.id.loginEmail);
        pwdET = (EditText)findViewById(R.id.loginPassword);
        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Please wait...");
        prgDialog.setCancelable(false);
    }

    public void loginUser(View view){
        String email = emailET.getText().toString();
        this.cUser = email;
        String password = pwdET.getText().toString();
        if(Utility.isNotNull(email) && Utility.isNotNull(password)){
            Log.i(Constants.LOG_TAG,"inside null check");
            if(Utility.validate(email)){
                credentials = Credentials.basic(email, password);
                prgDialog.show();
                Controller controller = new Controller();
                controller.start();
            }
            else{
                Toast.makeText(getApplicationContext(), "Please enter valid email", Toast.LENGTH_SHORT).show();
            }
        } else{
            Toast.makeText(getApplicationContext(), "Please fill the form, don't leave any field blank", Toast.LENGTH_LONG).show();
        }

    }

    public void navigatetoHomeActivity(String uName){
        Intent homeIntent = new Intent(this, HomeActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        homeIntent.putExtra("current_user", uName);
        startActivity(homeIntent);
    }

    public void navigatetoRegisterActivity(View view){
        Intent loginIntent = new Intent(this, RegisterActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(loginIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        prgDialog.dismiss();
    }

    private class Controller implements Callback<LoginData> {

        public void start() {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();
            OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
                @Override
                public okhttp3.Response intercept(Chain chain) throws IOException {
                    Request originalRequest = chain.request();

                    Request.Builder builder = originalRequest.newBuilder().header("Authorization",
                            credentials);

                    Request newRequest = builder.build();
                    return chain.proceed(newRequest);
                }
            }).build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(UserDataAPI.BASE_URL).client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

            UserDataAPI userDataAPI = retrofit.create(UserDataAPI.class);

            Call<LoginData> call = userDataAPI.login();
            call.enqueue(this);

        }

        @Override
        public void onResponse(Call<LoginData> call, Response<LoginData> response) {
            if(response.isSuccessful()) {
                LoginData data = response.body();
                UserDao.setToken(data.getEncoded());
                UserDao.setCurrentUser(cUser);
                UserDao.setCurrentName(data.getName());
                navigatetoHomeActivity(data.getName());
            } else {
                prgDialog.hide();
                if(response.code() == 401){
                    Toast.makeText(LoginActivity.this, "Wrong username/password.", Toast.LENGTH_SHORT).show();
                }
                System.out.println(response.errorBody());
            }
        }

        @Override
        public void onFailure(Call<LoginData> call, Throwable t) {
            prgDialog.hide();
            t.printStackTrace();
        }
    }

}