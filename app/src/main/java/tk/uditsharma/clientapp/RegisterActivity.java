package tk.uditsharma.clientapp;

import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import okhttp3.Credentials;
import tk.uditsharma.clientapp.model.ApiResponse;
import tk.uditsharma.clientapp.model.LoginData;
import tk.uditsharma.clientapp.model.UserDao;
import tk.uditsharma.clientapp.util.Constants;
import tk.uditsharma.clientapp.model.RegData;
import tk.uditsharma.clientapp.util.DaggerViewModelFactory;
import tk.uditsharma.clientapp.util.Utility;
import tk.uditsharma.clientapp.view.HomeActivity;
import tk.uditsharma.clientapp.view.LoginActivity;
import tk.uditsharma.clientapp.viewmodel.LoginViewModel;
import tk.uditsharma.clientapp.viewmodel.RegisterViewModel;

public class RegisterActivity extends AppCompatActivity {

    @Inject
    DaggerViewModelFactory viewModelFactory;
    private RegisterViewModel rViewModel;
    private LoginViewModel lViewModel;
    ProgressDialog prgDialog;
    TextView errorMsg;
    EditText nameET;
    EditText emailET;
    EditText pwdET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        findViews();
        rViewModel = ViewModelProviders.of(this,viewModelFactory).get(RegisterViewModel.class);
        lViewModel = ViewModelProviders.of(this,viewModelFactory).get(LoginViewModel.class);
    }

    private void findViews() {
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
                rViewModel.registerUser(name, email, password).observe(this, new Observer<ApiResponse<RegData>>() {
                    @Override
                    public void onChanged(@Nullable ApiResponse<RegData> rResponse) {
                        if (rResponse == null) {
                            Toast.makeText(RegisterActivity.this, "Error Occurred", Toast.LENGTH_SHORT).show();
                            prgDialog.dismiss();
                            return;
                        }
                        if (rResponse.getError() == null) {
                            if (rResponse.getCode() != 200) {
                                prgDialog.hide();
                                prgDialog.hide();
                                switch (rResponse.getCode()) {
                                    case 404:
                                        Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                                        break;
                                    case 500:
                                        Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                                        break;
                                    default :
                                        Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                                }
                                Log.d(Constants.LOG_REG,"Code: " + rResponse.getCode());
                            }else {
                                RegData data = rResponse.getData();
                                prgDialog.hide();
                                if(data.getStatus()){
                                    setDefaultValues();
                                    Toast.makeText(getApplicationContext(), "You are successfully registered!", Toast.LENGTH_LONG).show();
                                }
                                else{
                                    errorMsg.setText(data.getError_msg());
                                    Toast.makeText(getApplicationContext(), data.getError_msg(), Toast.LENGTH_LONG).show();
                                }
                            }
                        } else {
                            Throwable e = rResponse.getError();
                            prgDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "Error is " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            e.printStackTrace();

                        }
                    }
                });
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

    public void anonymousLogin(View view){
        lViewModel.addCredentials(Credentials.basic("user@mail.com", "text"));
        prgDialog.show();
        lViewModel.logInUser().observe(this, new Observer<ApiResponse<LoginData>>() {
            @Override
            public void onChanged(@Nullable ApiResponse<LoginData> lResponse) {
                if (lResponse == null) {
                    Toast.makeText(RegisterActivity.this, "Error Occurred", Toast.LENGTH_SHORT).show();
                    prgDialog.dismiss();
                    return;
                }
                if (lResponse.getError() == null) {
                    if (lResponse.getCode() != 200) {
                        prgDialog.hide();
                        if (lResponse.getCode() == 401) {
                            Toast.makeText(RegisterActivity.this, "Unauthorized.", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        lViewModel.addToken(lResponse.getData().getEncoded());
                        UserDao.setToken(lResponse.getData().getEncoded());
                        UserDao.setCurrentUser("user@mail.com");
                        UserDao.setCurrentName(lResponse.getData().getName());
                        navigateToHomeActivity(lResponse.getData().getName());
                    }
                } else {
                    Throwable e = lResponse.getError();
                    prgDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "Error is " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();

                }
            }
        });
    }

    public void navigateToHomeActivity(String uName){
        Intent homeIntent = new Intent(this, HomeActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        homeIntent.putExtra("current_user", uName);
        startActivity(homeIntent);
    }

    public void setDefaultValues(){
        nameET.setText("");
        emailET.setText("");
        pwdET.setText("");
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

}