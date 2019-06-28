package tk.uditsharma.clientapp.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import tk.uditsharma.clientapp.R;
import tk.uditsharma.clientapp.RegisterActivity;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_layout);
        TextView welcomeMsg = (TextView) findViewById(R.id.un_text);
        welcomeMsg.setText(getIntent().getExtras().getString("current_user"));
    }

    public void showAllUsers(View view){
        Intent listIntent = new Intent(this,UserListActivity.class);
        startActivity(listIntent);
    }

    public void goToWishList(View view){
        Intent listIntent = new Intent(this, WishListActivity.class);
        startActivity(listIntent);
    }

    @Override
    public void onBackPressed() {
        Intent regIntent = new Intent(this, RegisterActivity.class);
        regIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(regIntent);
    }
}