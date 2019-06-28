package tk.uditsharma.clientapp.view;

import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;

import android.util.Log;

import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import java.util.List;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import tk.uditsharma.clientapp.model.User;
import tk.uditsharma.clientapp.util.Constants;
import tk.uditsharma.clientapp.R;
import tk.uditsharma.clientapp.view.adapter.RecyclerViewAdapter;
import tk.uditsharma.clientapp.model.ApiResponse;
import tk.uditsharma.clientapp.util.DaggerViewModelFactory;
import tk.uditsharma.clientapp.viewmodel.UserViewModel;


public class UserListActivity extends AppCompatActivity {

    @Inject
    DaggerViewModelFactory viewModelFactory;
    private UserViewModel uViewModel;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    RecyclerViewAdapter recyclerViewAdapter;
    ProgressDialog prgDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_list);
        findViews();
        initToolbar("All Users List");
        setAdapter();
        setupViewModel();
    }

    private void findViews() {
        toolbar = (Toolbar) findViewById(R.id.user_list_toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Please wait...");
        prgDialog.setCancelable(false);
        prgDialog.show();
    }

    public void initToolbar(String title) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);
    }

    private void setAdapter() {
        Log.d(Constants.LOG_TAG,"inside setAdapter");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAdapter = new RecyclerViewAdapter(this, prgDialog);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    private void setupViewModel() {
        uViewModel = ViewModelProviders.of(this,viewModelFactory).get(UserViewModel.class);
        uViewModel.getUserListResponse().observe(this, new Observer<ApiResponse<List<User>>>() {
            @Override
            public void onChanged(@Nullable ApiResponse<List<User>> userResponse) {
                if (userResponse == null) {
                    Toast.makeText(UserListActivity.this, "Error Occurred", Toast.LENGTH_SHORT).show();
                    prgDialog.dismiss();
                    return;
                }
                if (userResponse.getError() == null) {
                    recyclerViewAdapter.updateList(userResponse.getData());
                    prgDialog.dismiss();
                } else {
                    Throwable e = userResponse.getError();
                    prgDialog.dismiss();
                    Toast.makeText(UserListActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(Constants.LOG_TAG, "Error is " + e.getLocalizedMessage());

                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        prgDialog.dismiss();
    }

}
