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
import tk.uditsharma.clientapp.model.ApiResponse;
import tk.uditsharma.clientapp.util.Constants;
import tk.uditsharma.clientapp.model.RegData;
import tk.uditsharma.clientapp.util.DaggerViewModelFactory;
import tk.uditsharma.clientapp.util.Utility;
import tk.uditsharma.clientapp.view.LoginActivity;
import tk.uditsharma.clientapp.viewmodel.RegisterViewModel;

public class RegisterActivity extends AppCompatActivity {

    @Inject
    DaggerViewModelFactory viewModelFactory;
    private RegisterViewModel rViewModel;
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
        errorMsg = (TextView)findViewById(R.id.register_error);
        nameET = (EditText)findViewById(R.id.registerName);
        emailET = (EditText)findViewById(R.id.registerEmail);
        pwdET = (EditText)findViewById(R.id.registerPassword);
        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Please wait...");
        prgDialog.setCancelable(false);
        rViewModel = ViewModelProviders.of(this,viewModelFactory).get(RegisterViewModel.class);
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

    /*public void testActivity(View view){
        Intent testIntent = new Intent(this, UserProfileActivity.class);
        startActivity(testIntent);
    }*/

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