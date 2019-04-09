package tk.uditsharma.clientapp.view;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import okhttp3.ResponseBody;
import tk.uditsharma.clientapp.model.ApiResponse;
import tk.uditsharma.clientapp.view.adapter.CommentViewAdapter;
import tk.uditsharma.clientapp.util.Constants;
import tk.uditsharma.clientapp.R;
import tk.uditsharma.clientapp.model.UserDao;
import tk.uditsharma.clientapp.model.CommentResponse;
import tk.uditsharma.clientapp.util.DaggerViewModelFactory;
import tk.uditsharma.clientapp.model.PlaceEntry;
import tk.uditsharma.clientapp.viewmodel.MapViewModel;
import tk.uditsharma.clientapp.viewmodel.PlaceViewModel;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, ActionMode.Callback {

    private GoogleMap mMap;
    private NestedScrollView mScrollView;
    private RecyclerView recyclerView;
    CommentViewAdapter cAdapter;
    EditText commentText;
    TextView dateText;
    String cPlaceId = null;
    String cPlaceName = null;
    String cDate = null;
    ProgressDialog prgDialog;
    View cView;
    CommentResponse selectedComment;
    EditText input;
    AlertDialog.Builder alertBuilder;
    AlertDialog alert;
    @Inject
    DaggerViewModelFactory viewModelFactory;
    private PlaceViewModel mPlaceViewModel;
    private MapViewModel mMapViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        findViews();
        mPlaceViewModel = ViewModelProviders.of(this,viewModelFactory).get(PlaceViewModel.class);
        mMapViewModel = ViewModelProviders.of(this,viewModelFactory).get(MapViewModel.class);
        startRecyclerView();
        getCommentData();
        setMapFragment();
    }

    private void findViews(){
        commentText = (EditText)findViewById(R.id.commentBox);
        mScrollView = (NestedScrollView) findViewById(R.id.main_scrollview);
        dateText = (TextView) findViewById(R.id.selected_date_info);
        Toolbar tb = (Toolbar) findViewById(R.id.map_toolbar);
        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Please wait...");
        prgDialog.setCancelable(false);
        prgDialog.show();
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            if (extras.getString("place_id") != null) {
                cPlaceId = extras.getString("place_id");
            }
            cDate = extras.getString("selected_date");
            dateText.setText("Add this place as visited on "+ cDate);
        }
        setSupportActionBar(tb);
        alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle("Edit Comment");
        input = new EditText(this);
        alertBuilder.setView(input);
        alertBuilder.setCancelable(true);
        alertBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String editText = input.getText().toString();
                if (!editText.isEmpty()) {
                    mMapViewModel.editComment(UserDao.getCurrentUser(),editText,selectedComment.getId()).observe(MapsActivity.this, new Observer<ApiResponse<ResponseBody>>() {
                        @Override
                        public void onChanged(@Nullable ApiResponse<ResponseBody> editResponse) {

                            if (editResponse == null) {
                                Toast.makeText(MapsActivity.this, "Error Occurred", Toast.LENGTH_SHORT).show();
                                prgDialog.dismiss();
                                return;
                            }
                            if (editResponse.getError() == null) {
                                String eText = input.getText().toString();
                                try {
                                    JSONObject jObj = new JSONObject(editResponse.getData().string());
                                    if(jObj.getString("status").equals("Success")){
                                        mMapViewModel.notifyEditComment(selectedComment.getId(), eText);
                                        commentText.getText().clear();
                                        Toast.makeText(MapsActivity.this, "Comment updated", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(MapsActivity.this, "Could not edit comment.", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }catch (IOException e) {
                                    e.printStackTrace();
                                }

                            } else {
                                Throwable e = editResponse.getError();
                                prgDialog.dismiss();
                                Toast.makeText(MapsActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.e(Constants.LOG_TAG, "Error is " + e.getLocalizedMessage());
                            }
                        }
                    });
                } else {
                    Toast.makeText(MapsActivity.this, "Please enter a comment.", Toast.LENGTH_LONG).show();
                }
            }
        });

        alertBuilder.setNegativeButton("Cancel", null);
        alert = alertBuilder.create();
        /*alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });*/
    }

    private void setMapFragment(){
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (ScrollGoogleMap) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        ((ScrollGoogleMap) getSupportFragmentManager()
                .findFragmentById(R.id.map))
                .setListener(new ScrollGoogleMap.OnTouchListener() {
                    @Override
                    public void onTouch() {
                        mScrollView.requestDisallowInterceptTouchEvent(true);
                    }
                });
    }

    public void goToActionMode(View item, CommentResponse cResp){
            this.startActionMode(this);
            this.cView = item;
            this.selectedComment = cResp;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.actionmode, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_comment:
                input.setText(selectedComment.getCommentText());
                alert.show();
                mode.finish();
                return true;
            case R.id.delete_comment:
                mMapViewModel.deleteComment(UserDao.getCurrentUser(), selectedComment.getId()).observe(this, new Observer<ApiResponse<ResponseBody>>() {
                    @Override
                    public void onChanged(@Nullable ApiResponse<ResponseBody> deleteResponse) {

                        if (deleteResponse == null) {
                            Toast.makeText(MapsActivity.this, "Error Occurred", Toast.LENGTH_SHORT).show();
                            prgDialog.dismiss();
                            return;
                        }
                        if (deleteResponse.getError() == null) {
                            try {
                                JSONObject jObj = new JSONObject(deleteResponse.getData().string());
                                if(jObj.getString("status").equals("Success")){
                                    mMapViewModel.notifyRemoveComment(selectedComment.getId());
                                    Toast.makeText(MapsActivity.this, "Comment Deleted", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(MapsActivity.this, "Could not delete Comment", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }catch (IOException e) {
                                e.printStackTrace();
                            }

                        } else {
                            Throwable e = deleteResponse.getError();
                            prgDialog.dismiss();
                            Toast.makeText(MapsActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e(Constants.LOG_TAG, "Error is " + e.getLocalizedMessage());
                        }
                    }
                });
                mode.finish();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        cView.setSelected(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.date_picker:
                int mYear;
                int mMonth;
                int mDay;
                if(cDate != null){
                    final StringTokenizer tokenizer = new StringTokenizer(
                            cDate, "-");
                    mYear = Integer.parseInt(tokenizer.nextToken());
                    mMonth = (Integer.parseInt(tokenizer.nextToken()) - 1);
                    mDay = Integer.parseInt(tokenizer.nextToken());
                } else {
                    final Calendar c = Calendar.getInstance();
                    mYear = c.get(Calendar.YEAR);
                    mMonth = c.get(Calendar.MONTH);
                    mDay = c.get(Calendar.DAY_OF_MONTH);
                }

                DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                cDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                                dateText.setText("Add this place as visited on "+ cDate);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
                return true;
            case R.id.fav:
                if (cPlaceId == null) {
                    Toast.makeText(this, "Please Select a Place", Toast.LENGTH_SHORT).show();
                } else {
                    PlaceEntry place = new PlaceEntry(UserDao.getCurrentUser(), cPlaceId, cPlaceName);
                    mPlaceViewModel.insert(place);
                    Toast.makeText(this, "Added to wish list", Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startRecyclerView() {

        recyclerView = (RecyclerView) findViewById(R.id.comments_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cAdapter = new CommentViewAdapter(MapsActivity.this);
        recyclerView.setAdapter(cAdapter);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (!Places.isInitialized()) {
            //Key from values/google_maps_api.xml
            Places.initialize(getApplicationContext(), getResources().getString(R.string.google_maps_key));
        }
        PlacesClient placesClient = Places.createClient(this);
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                cPlaceId = place.getId();
                cPlaceName = place.getName();
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title(place.getName()));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 14), 500, null);
            }

            @Override
            public void onError(Status status) {
                Log.i(Constants.LOG_TAG, "An error occurred: " + status.toString());
            }
        });

        if (cPlaceId != null) {
            FetchPlaceRequest request = FetchPlaceRequest.builder(cPlaceId, Arrays.asList(Place.Field.NAME, Place.Field.LAT_LNG)).build();
            placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
                Place mPlace = response.getPlace();
                cPlaceName = mPlace.getName();
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(mPlace.getLatLng()).title(mPlace.getName()));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mPlace.getLatLng(), 14), 500, null);
            }).addOnFailureListener((exception) -> {
                if (exception instanceof ApiException) {
                    ApiException apiException = (ApiException) exception;
                    int statusCode = apiException.getStatusCode();
                    Log.e(Constants.LOG_TAG, "Place not found: " + exception.getMessage() + " Status: " + statusCode);
                }
            });
        }
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 14), 500, null);
                marker.showInfoWindow();
                return true;
            }
        });
    }

    private void getCommentData() {
        if (cPlaceId != null) {
            mMapViewModel.fetchCommentList(cPlaceId).observe(this, new Observer<ApiResponse<List<CommentResponse>>>() {
                @Override
                public void onChanged(@Nullable ApiResponse<List<CommentResponse>> commentResponse) {

                    if (commentResponse == null) {
                        Toast.makeText(MapsActivity.this, "Error Occurred", Toast.LENGTH_SHORT).show();
                        prgDialog.dismiss();
                        return;
                    }
                    if (commentResponse.getError() == null) {
                        List<CommentResponse> commentList = new ArrayList<>();
                        commentList = commentResponse.getData();
                        cAdapter.setCommentList(commentList);
                        prgDialog.dismiss();

                    } else {
                        Throwable e = commentResponse.getError();
                        prgDialog.dismiss();
                        Toast.makeText(MapsActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e(Constants.LOG_TAG, "Error is " + e.getLocalizedMessage());
                    }
                }
            });
        } else {
            prgDialog.dismiss();
            Toast.makeText(MapsActivity.this, "No place is selected.", Toast.LENGTH_LONG).show();
        }

    }

    public void onClickComment(View view){
        String cText = commentText.getText().toString();
        if (cPlaceId != null) {
            if (!cText.isEmpty()) {
                mMapViewModel.postComment(UserDao.getCurrentUser(),cText,cPlaceId).observe(this, new Observer<ApiResponse<ResponseBody>>() {
                    @Override
                    public void onChanged(@Nullable ApiResponse<ResponseBody> addResponse) {

                        if (addResponse == null) {
                            Toast.makeText(MapsActivity.this, "Error Occurred", Toast.LENGTH_SHORT).show();
                            prgDialog.dismiss();
                            return;
                        }
                        if (addResponse.getError() == null) {
                            String cText = commentText.getText().toString();
                            try {
                                JSONObject jObj = new JSONObject(addResponse.getData().string());
                                if(jObj.getString("status").equals("Success")){
                                    mMapViewModel.notifyAddComment(new CommentResponse(jObj.getInt("comment_id"),
                                            UserDao.getCurrentName(), UserDao.getCurrentUser(), cText));
                                    commentText.getText().clear();
                                    Toast.makeText(MapsActivity.this, "Comment created", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(MapsActivity.this, "Could not create comment.", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }catch (IOException e) {
                                e.printStackTrace();
                            }

                        } else {
                            Throwable e = addResponse.getError();
                            prgDialog.dismiss();
                            Toast.makeText(MapsActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e(Constants.LOG_TAG, "Error is " + e.getLocalizedMessage());
                        }
                    }
                });
            } else {
                Toast.makeText(MapsActivity.this, "Please enter a comment.", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(MapsActivity.this, "Please select a place.", Toast.LENGTH_LONG).show();
        }
    }

    public void onClickAdd(View view){
        if (cPlaceId != null) {
            mMapViewModel.addPlace(UserDao.getCurrentUser(),cPlaceId,cDate).observe(this, new Observer<ApiResponse<ResponseBody>>() {
                @Override
                public void onChanged(@Nullable ApiResponse<ResponseBody> placesResponse) {

                    if (placesResponse == null) {
                        Toast.makeText(MapsActivity.this, "Error Occurred", Toast.LENGTH_SHORT).show();
                        prgDialog.dismiss();
                        return;
                    }
                    if (placesResponse.getError() == null) {
                        try {
                            JSONObject jObj = new JSONObject(placesResponse.getData().string());
                            if(jObj.getString("status").equals("Success")){
                                mMapViewModel.notifyPlaceUpdate(cPlaceId, cDate);
                                Toast.makeText(MapsActivity.this, "Place Added.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MapsActivity.this, "Could not add place", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }catch (IOException e) {
                            e.printStackTrace();
                        }

                    } else {
                        Throwable e = placesResponse.getError();
                        prgDialog.dismiss();
                        Toast.makeText(MapsActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e(Constants.LOG_TAG, "Error is " + e.getLocalizedMessage());
                    }
                }
            });
        }else {
            Toast.makeText(MapsActivity.this, "Please select a place.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        prgDialog.dismiss();
    }

}
