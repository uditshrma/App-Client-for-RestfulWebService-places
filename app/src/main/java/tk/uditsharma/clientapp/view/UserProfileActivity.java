package tk.uditsharma.clientapp.view;

import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import okhttp3.ResponseBody;
import tk.uditsharma.clientapp.model.ApiResponse;
import tk.uditsharma.clientapp.util.Constants;
import tk.uditsharma.clientapp.R;
import tk.uditsharma.clientapp.model.UserDao;
import tk.uditsharma.clientapp.model.UserParcel;
import tk.uditsharma.clientapp.model.AllPlacesResponse;
import tk.uditsharma.clientapp.util.DaggerViewModelFactory;
import tk.uditsharma.clientapp.viewmodel.ProfileViewModel;


public class UserProfileActivity extends AppCompatActivity {

    @Inject
    DaggerViewModelFactory viewModelFactory;
    private ProfileViewModel pViewModel;
    MaterialCalendarView calendarView;
    TextView dName;
    TextView userName;
    TextView uPassword;
    TextView regDate;
    ImageView divider;
    TextView info;
    Button toMapsButton;
    ProgressBar pBar;
    Toolbar tb;
    PlacesClient placesClient;
    String selectedPlaceId = null;
    String selectedDate = null;
    UserParcel cUser;
    ProgressDialog prgDialog;
    private MenuItem deleteOption;
    EventDecorator eDecor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);
        findViews();
        setSupportActionBar(tb);
        setValues();
        getPlacesData();
        createPlacesClient();
        setupCalendar();
    }

    private void findViews() {
        calendarView = findViewById(R.id.calendarView);
        dName = (TextView)findViewById(R.id.display_name);
        userName = (TextView)findViewById(R.id.p_user_id);
        uPassword = (TextView)findViewById(R.id.user_pwd);
        regDate = (TextView)findViewById(R.id.p_reg_date);
        divider = (ImageView) findViewById(R.id.divider_grd);
        info = (TextView)findViewById(R.id.date_place_info);
        toMapsButton = (Button) findViewById(R.id.to_maps);
        pBar = (ProgressBar) findViewById(R.id.progressBar);
        tb = (Toolbar) findViewById(R.id.profile_toolbar);
        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Please wait...");
        prgDialog.setCancelable(false);
        prgDialog.show();
        pBar.setVisibility(View.GONE);
        divider.setVisibility(View.GONE);
        info.setVisibility(View.GONE);
        toMapsButton.setVisibility(View.GONE);
    }

    private void setValues() {
        pViewModel = ViewModelProviders.of(this,viewModelFactory).get(ProfileViewModel.class);
        pViewModel.setSelectedUser(getIntent().getExtras().getParcelable("selected_user"));
        cUser = pViewModel.getSelectedUser();
        dName.setText(cUser.displayName());
        userName.setText(cUser.userName());
        uPassword.setText(cUser.password());
        regDate.setText(cUser.regDate());
    }

    private void createPlacesClient() {
        if (!Places.isInitialized()) {
            //Key from values/google_maps_api.xml
            Places.initialize(getApplicationContext(), getResources().getString(R.string.google_maps_key));
        }
        placesClient = Places.createClient(this);
    }

    private void setupCalendar() {
        calendarView.addDecorator(new TodayDecorator(this));
        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget,
                                       @NonNull CalendarDay date,
                                       boolean selected) {

                toMapsButton.setVisibility(View.GONE);
                info.setVisibility(View.INVISIBLE);
                pBar.setVisibility(View.VISIBLE);
                selectedDate = new SimpleDateFormat("yyyy-M-d").format(date.getDate());
                boolean placeAvailable = false;
                List<AllPlacesResponse> placeList = new ArrayList<>(pViewModel.getPlaceList().getData());
                if(!placeList.isEmpty()){
                    for (AllPlacesResponse place : placeList) {
                        placeAvailable = selectedDate.equals(place.getDate());
                        if(placeAvailable) {
                            selectedPlaceId = place.getPlaceId();
                            break;
                        }
                    }
                }
                if(placeAvailable){
                    FetchPlaceRequest request = FetchPlaceRequest.builder(selectedPlaceId, Arrays.asList(Place.Field.NAME)).build();
                    placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
                        //Log.i(Constants.LOG_TAG, "Place found: " + response.getPlace().getName());
                        String uName = cUser.displayName();
                        String pName = response.getPlace().getName();
                        final SpannableStringBuilder sb = new SpannableStringBuilder(uName + " visited " + pName + " on this day.");
                        final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
                        sb.setSpan(bss, uName.length()+9,uName.length()+9+pName.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                        info.setText(sb);
                        pBar.setVisibility(View.GONE);
                        info.setVisibility(View.VISIBLE);
                        toMapsButton.setVisibility(View.VISIBLE);
                        if(cUser.userName().equals(UserDao.getCurrentUser())){
                            deleteOption.setVisible(true);
                        }

                    }).addOnFailureListener((exception) -> {
                        if (exception instanceof ApiException) {
                            ApiException apiException = (ApiException) exception;
                            int statusCode = apiException.getStatusCode();
                            Log.e(Constants.LOG_TAG, "Place not found: " + exception.getMessage() + " Status: " + statusCode);
                        }
                    });
                    toMapsButton.setText("Check");
                    divider.setVisibility(View.VISIBLE);
                } else {
                    info.setText(cUser.displayName() + " hasn't visited any place on this day.");
                    deleteOption.setVisible(false);
                    divider.setVisibility(View.VISIBLE);
                    pBar.setVisibility(View.GONE);
                    info.setVisibility(View.VISIBLE);
                    toMapsButton.setVisibility(View.VISIBLE);
                    selectedPlaceId = null;
                    if(cUser.userName().equals(UserDao.getCurrentUser())){
                        toMapsButton.setText("Update");
                    } else {
                        toMapsButton.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        deleteOption = menu.findItem(R.id.delete_place);
        deleteOption.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.delete_place:
                pViewModel.deletePlace(UserDao.getCurrentUser(),selectedPlaceId,selectedDate).observe(this, new Observer<ApiResponse<ResponseBody>>() {
                    @Override
                    public void onChanged(@Nullable ApiResponse<ResponseBody> deleteResponse) {
                        if (deleteResponse == null) {
                            Toast.makeText(UserProfileActivity.this, "Error Occurred", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (deleteResponse.getError() == null) {
                            ResponseBody value = deleteResponse.getData();
                            try {
                                JSONObject jObj = new JSONObject(value.string());
                                if(jObj.getString("status").equals("Success")){
                                    Toast.makeText(UserProfileActivity.this, "Place Deleted", Toast.LENGTH_SHORT).show();
                                    pViewModel.notifyRemovePlace(selectedDate, selectedPlaceId);
                                } else {
                                    Toast.makeText(UserProfileActivity.this, "Could not deleted place", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Throwable e = deleteResponse.getError();
                            Toast.makeText(UserProfileActivity.this, "Error in deleting: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e(Constants.LOG_TAG, "Error is " + e.getLocalizedMessage());
                        }
                    }
                });
                return true;
            case R.id.reload_activity:
                finish();
                startActivity(getIntent());
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getPlacesData() {
        pViewModel.fetchPlaceList().observe(this, new Observer<ApiResponse<List<AllPlacesResponse>>>() {
            @Override
            public void onChanged(@Nullable ApiResponse<List<AllPlacesResponse>> placesResponse) {

                if (placesResponse == null) {
                    Toast.makeText(UserProfileActivity.this, "Error Occurred", Toast.LENGTH_SHORT).show();
                    prgDialog.dismiss();
                    return;
                }
                if (placesResponse.getError() == null) {
                    List<CalendarDay> mDayList = new ArrayList<>();
                    if (eDecor != null){
                        calendarView.removeDecorator(eDecor);
                        calendarView.clearSelection();
                    }
                    if (!placesResponse.getData().isEmpty()){
                        List<AllPlacesResponse> placeList = new ArrayList<>(placesResponse.getData());
                        for (AllPlacesResponse place : placeList) {
                            final StringTokenizer tokenizer = new StringTokenizer(
                                    place.getDate(), "-");
                            int y = Integer.parseInt(tokenizer.nextToken());
                            int m = (Integer.parseInt(tokenizer.nextToken()) - 1);
                            int d = Integer.parseInt(tokenizer.nextToken());
                            mDayList.add(CalendarDay.from(y, m, d));
                        }
                        eDecor = new EventDecorator(Color.WHITE, mDayList);
                        calendarView.addDecorator(eDecor);
                        prgDialog.dismiss();
                    } else{
                        prgDialog.dismiss();
                        Toast.makeText(UserProfileActivity.this, "No data Available", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Throwable e = placesResponse.getError();
                    prgDialog.dismiss();
                    Toast.makeText(UserProfileActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(Constants.LOG_TAG, "Error is " + e.getLocalizedMessage());
                }
            }
        });
    }

    public void toMapsActivity(View view) {
        prgDialog.show();
        Intent mapIntent = new Intent(this, MapsActivity.class);
        mapIntent.putExtra("place_id", selectedPlaceId);
        mapIntent.putExtra("selected_date", selectedDate);
        startActivity(mapIntent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        prgDialog.dismiss();
    }
}