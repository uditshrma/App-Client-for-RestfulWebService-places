package tk.uditsharma.clientapp.view;

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

import dagger.android.AndroidInjection;
import tk.uditsharma.clientapp.model.ApiResponse;
import tk.uditsharma.clientapp.util.Constants;
import tk.uditsharma.clientapp.R;
import tk.uditsharma.clientapp.RegisterActivity;
import tk.uditsharma.clientapp.model.UserDao;
import tk.uditsharma.clientapp.util.DaggerViewModelFactory;
import tk.uditsharma.clientapp.util.Utility;
import tk.uditsharma.clientapp.model.LoginData;

import javax.inject.Inject;

import okhttp3.Credentials;
import tk.uditsharma.clientapp.viewmodel.LoginViewModel;

public class LoginActivity extends AppCompatActivity {

    @Inject
    DaggerViewModelFactory viewModelFactory;
    private LoginViewModel uViewModel;
    ProgressDialog prgDialog;
    TextView errorMsg;
    EditText emailET;
    EditText pwdET;
    private String cUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        errorMsg = (TextView)findViewById(R.id.login_error);
        emailET = (EditText)findViewById(R.id.loginEmail);
        pwdET = (EditText)findViewById(R.id.loginPassword);
        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Please wait...");
        prgDialog.setCancelable(false);
        uViewModel = ViewModelProviders.of(this,viewModelFactory).get(LoginViewModel.class);
    }

    public void loginUser(View view){
        String email = emailET.getText().toString();
        this.cUser = email;
        String password = pwdET.getText().toString();
        if(Utility.isNotNull(email) && Utility.isNotNull(password)){
            Log.i(Constants.LOG_TAG,"inside null check");
            if(Utility.validate(email)){
                uViewModel.addCredentials(Credentials.basic(email, password));
                prgDialog.show();
                uViewModel.logInUser().observe(this, new Observer<ApiResponse<LoginData>>() {
                    @Override
                    public void onChanged(@Nullable ApiResponse<LoginData> lResponse) {
                        if (lResponse == null) {
                            Toast.makeText(LoginActivity.this, "Error Occurred", Toast.LENGTH_SHORT).show();
                            prgDialog.dismiss();
                            return;
                        }
                        if (lResponse.getError() == null) {
                            if (lResponse.getCode() != 200) {
                                prgDialog.hide();
                                if (lResponse.getCode() == 401) {
                                    Toast.makeText(LoginActivity.this, "Unauthorized.", Toast.LENGTH_SHORT).show();
                                }
                            }else {
                                uViewModel.addToken(lResponse.getData().getEncoded());
                                UserDao.setToken(lResponse.getData().getEncoded());
                                UserDao.setCurrentUser(cUser);
                                UserDao.setCurrentName(lResponse.getData().getName());
                                navigateToHomeActivity(lResponse.getData().getName());
                            }
                        } else {
                            Throwable e = lResponse.getError();
                            prgDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Error is " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            e.printStackTrace();

                        }
                    }
                });
            }
            else{
                Toast.makeText(getApplicationContext(), "Please enter valid email", Toast.LENGTH_SHORT).show();
            }
        } else{
            Toast.makeText(getApplicationContext(), "Please fill the form, don't leave any field blank", Toast.LENGTH_LONG).show();
        }

    }

    public void navigateToHomeActivity(String uName){
        Intent homeIntent = new Intent(this, HomeActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        homeIntent.putExtra("current_user", uName);
        startActivity(homeIntent);
    }

    public void navigateToRegisterActivity(View view){
        Intent loginIntent = new Intent(this, RegisterActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(loginIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        prgDialog.dismiss();
    }

}