package tk.uditsharma.clientapp.view;

import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.util.List;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import tk.uditsharma.clientapp.model.PlaceEntry;
import tk.uditsharma.clientapp.util.Constants;
import tk.uditsharma.clientapp.util.DaggerViewModelFactory;
import tk.uditsharma.clientapp.R;
import tk.uditsharma.clientapp.view.adapter.PlaceListAdapter;
import tk.uditsharma.clientapp.viewmodel.PlaceViewModel;

public class WishListActivity extends AppCompatActivity implements ActionMode.Callback {

    @Inject
    DaggerViewModelFactory viewModelFactory;
    private PlaceViewModel mPlaceViewModel;
    View cView;
    PlaceEntry selectedPlace;
    ProgressDialog prgDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.place_list_layout);
        RecyclerView recyclerView = findViewById(R.id.wishlist_recycler_view);
        final PlaceListAdapter adapter = new PlaceListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mPlaceViewModel = ViewModelProviders.of(this,viewModelFactory).get(PlaceViewModel.class);
        prgDialog = new ProgressDialog(this);
        // Set Progress Dialog Text
        prgDialog.setMessage("Please wait...");
        // Set Cancelable as False
        prgDialog.setCancelable(false);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab2);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prgDialog.show();
                Intent intent = new Intent(WishListActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });
        mPlaceViewModel.getAllPlaces().observe(this, new Observer<List<PlaceEntry>>() {
            @Override
            public void onChanged(@Nullable final List<PlaceEntry> mAllPlaces) {
                Log.d(Constants.LOG_TAG,"inside onChanged");
                adapter.setPlaces(mAllPlaces);
            }
        });
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        // Inflate a menu resource providing context menu items
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.wish_list_actionmode, menu);
        return true;
    }

    public void goToActionMode(View item, PlaceEntry pResp){
        this.startActionMode(this);
        this.cView = item;
        this.selectedPlace = pResp;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        cView.setSelected(false);
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_place_entry:
                mPlaceViewModel.delete(selectedPlace);
                mode.finish();
                return true;
            default:
                return false;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        prgDialog.hide();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        prgDialog.dismiss();
    }

    /*public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NEW_PLACE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            PlaceEntry place = new PlaceEntry(UserDao.getCurrentUser(), data.getStringExtra(MapsActivity.ID_REPLY), data.getStringExtra(MapsActivity.NAME_REPLY));
            mPlaceViewModel.insert(place);
        } else {
            Toast.makeText(this, "Place not saved because empty.", Toast.LENGTH_SHORT).show();
        }
    }*/

}