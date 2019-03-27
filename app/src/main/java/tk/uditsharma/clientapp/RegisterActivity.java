package tk.uditsharma.clientapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import tk.uditsharma.clientapp.util.Constants;
import tk.uditsharma.clientapp.model.RegData;
import tk.uditsharma.clientapp.model.UserDataAPI;
import tk.uditsharma.clientapp.util.Utility;
import tk.uditsharma.clientapp.view.LoginActivity;
import tk.uditsharma.clientapp.view.MapsActivity;
import tk.uditsharma.clientapp.view.UserProfileActivity;

public class RegisterActivity extends Activity {
    ProgressDialog prgDialog;
    TextView errorMsg;
    EditText nameET;
    EditText emailET;
    EditText pwdET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        errorMsg = (TextView)findViewById(R.id.register_error);
        nameET = (EditText)findViewById(R.id.registerName);
        emailET = (EditText)findViewById(R.id.registerEmail);
        pwdET = (EditText)findViewById(R.id.registerPassword);
        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Please wait...");
        prgDialog.setCancelable(false);
    }

    public void registerUser(View view){
        String name = nameET.getText().toString();
        String email = emailET.getText().toString();
        String password = pwdET.getText().toString();
        if(Utility.isNotNull(name) && Utility.isNotNull(email) && Utility.isNotNull(password)){
            if(Utility.validate(email)){
                prgDialog.show();
                Controller controller = new Controller();
                controller.start(name, email, password);
            }
            else{
                Toast.makeText(getApplicationContext(), "Please enter valid email", Toast.LENGTH_LONG).show();
            }
        }
        else{
            Toast.makeText(getApplicationContext(), "Please fill the form, don't leave any field blank", Toast.LENGTH_LONG).show();
        }

    }

    public void navigateToLoginActivity(View view){
        Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(loginIntent);
    }

    public void testActivity(View view){
        Intent testIntent = new Intent(this, UserProfileActivity.class);
        startActivity(testIntent);
    }

    public void toMapActivity(View view){
        prgDialog.show();
        Intent mIntent = new Intent(this, MapsActivity.class);
        startActivity(mIntent);
    }

    public void setDefaultValues(){
        nameET.setText("");
        emailET.setText("");
        pwdET.setText("");
    }

    private class Controller implements Callback<RegData> {

        public void start(String name, String uName, String pwd) {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(UserDataAPI.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

            UserDataAPI userDataAPI = retrofit.create(UserDataAPI.class);

            Call<RegData> call = userDataAPI.register(name, uName, pwd);
            call.enqueue(this);

        }

        @Override
        public void onResponse(Call<RegData> call, Response<RegData> response) {
            if(response.isSuccessful()) {
                RegData data = response.body();
                prgDialog.hide();
                if(data.getStatus()){
                    setDefaultValues();
                    Toast.makeText(getApplicationContext(), "You are successfully registered!", Toast.LENGTH_LONG).show();
                }
                else{
                    errorMsg.setText(data.getError_msg());
                    Toast.makeText(getApplicationContext(), data.getError_msg(), Toast.LENGTH_LONG).show();
                }
            } else {
                prgDialog.hide();
                switch (response.code()) {
                    case 404:
                        Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                        break;
                    case 500:
                        Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                        break;
                    default :
                        Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                }
                Log.d(Constants.LOG_REG,"Code: " + response.code() + " Message: " + response.message());
            }
        }

        @Override
        public void onFailure(Call<RegData> call, Throwable t) {
            prgDialog.dismiss();
            t.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        prgDialog.dismiss();
    }

    @Override
    protected void onResume() {
        super.onResume();
        prgDialog.hide();
    }
}