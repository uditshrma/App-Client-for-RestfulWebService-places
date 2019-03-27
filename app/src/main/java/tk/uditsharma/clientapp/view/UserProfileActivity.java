package tk.uditsharma.clientapp.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.Base64;
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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import tk.uditsharma.clientapp.util.Constants;
import tk.uditsharma.clientapp.R;
import tk.uditsharma.clientapp.model.UserDao;
import tk.uditsharma.clientapp.model.UserDataAPI;
import tk.uditsharma.clientapp.model.UserParcel;
import tk.uditsharma.clientapp.model.AllPlacesResponse;


public class UserProfileActivity extends AppCompatActivity {
    MaterialCalendarView calendarView;
    TextView dName;
    TextView userName;
    TextView uPassword;
    TextView regDate;
    ImageView divider;
    TextView info;
    Button toMapsButton;
    ProgressBar pBar;
    UserParcel cUser;
    String encodedString = null;
    UserDataAPI userdataAPI;
    PlacesClient placesClient;
    String selectedPlaceId = null;
    String selectedDate = null;
    List<AllPlacesResponse> placeList = new ArrayList<>();
    List<CalendarDay> mDayList = new ArrayList<>();
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    ProgressDialog prgDialog;
    private MenuItem deleteOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);
        calendarView = findViewById(R.id.calendarView);
        dName = (TextView)findViewById(R.id.display_name);
        userName = (TextView)findViewById(R.id.p_user_id);
        uPassword = (TextView)findViewById(R.id.user_pwd);
        regDate = (TextView)findViewById(R.id.p_reg_date);
        divider = (ImageView) findViewById(R.id.divider_grd);
        info = (TextView)findViewById(R.id.date_place_info);
        toMapsButton = (Button) findViewById(R.id.to_maps);
        pBar = (ProgressBar) findViewById(R.id.progressBar);
        Toolbar tb = (Toolbar) findViewById(R.id.profile_toolbar);
        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Please wait...");
        prgDialog.setCancelable(false);
        pBar.setVisibility(View.GONE);
        divider.setVisibility(View.GONE);
        info.setVisibility(View.GONE);
        toMapsButton.setVisibility(View.GONE);
        setSupportActionBar(tb);
        cUser =  getIntent().getExtras().getParcelable("selected_user");

        dName.setText(cUser.displayName());
        userName.setText(cUser.userName());
        uPassword.setText(cUser.password());
        regDate.setText(cUser.regDate());

        getPlacesData();

        if (!Places.isInitialized()) {
            //Key from values/google_maps_api.xml
            Places.initialize(getApplicationContext(), getResources().getString(R.string.google_maps_key));
        }
        placesClient = Places.createClient(this);
        calendarView.addDecorator(new TodayDecorator(this));

        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget,
                                       @NonNull CalendarDay date,
                                       boolean selected) {

                toMapsButton.setVisibility(View.GONE);
                info.setVisibility(View.INVISIBLE);
                pBar.setVisibility(View.VISIBLE);
                //String msg = selected ? date.getDate().toString() : "No Selection";
                selectedDate = new SimpleDateFormat("yyyy-M-d").format(date.getDate());
                boolean placeAvailable = false;
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
                            // Handle error with given status code.
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
                //Toast.makeText(this, "Action Delete Place selected", Toast.LENGTH_SHORT).show();
                compositeDisposable.add(userdataAPI.deletePlace(UserDao.getCurrentUser(),selectedPlaceId,selectedDate)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(deletePlaceObserver()));
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
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Log.d(Constants.LOG_TAG,"inside createUserDataAPI of userProfile");

        try {
            encodedString = Base64.encodeToString(
                    UserDao.getToken().getBytes("UTF-8"),Base64.NO_WRAP);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request originalRequest = chain.request();

                Request.Builder builder = originalRequest.newBuilder().header("Authorization",
                        "Bearer " + encodedString);
                Log.d(Constants.LOG_TAG,"inside okHttpClient of userProfile");

                Request newRequest = builder.build();
                return chain.proceed(newRequest);
            }
        }).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UserDataAPI.BASE_URL)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        userdataAPI = retrofit.create(UserDataAPI.class);
        compositeDisposable.add(userdataAPI.getPlaces(cUser.userName())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getPlaceListObserver()));
    }

    private DisposableSingleObserver<ResponseBody> deletePlaceObserver() {
        return new DisposableSingleObserver<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody value) {
                try {
                    JSONObject jObj = new JSONObject(value.string());
                    if(jObj.getString("status").equals("Success")){
                        Toast.makeText(UserProfileActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(UserProfileActivity.this, "Could not deleted place", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                Toast.makeText(UserProfileActivity.this, "Error occurred in deleting place.", Toast.LENGTH_SHORT).show();
            }
        };
    }

    private DisposableSingleObserver<List<AllPlacesResponse>> getPlaceListObserver() {
        return new DisposableSingleObserver<List<AllPlacesResponse>>() {
            @Override
            public void onSuccess(List<AllPlacesResponse> value) {
                if (!value.isEmpty()) {
                    placeList.addAll(value);
                    for (AllPlacesResponse place : placeList) {
                        final StringTokenizer tokenizer = new StringTokenizer(
                                place.getDate(), "-");
                        int y = Integer.parseInt(tokenizer.nextToken());
                        int m = (Integer.parseInt(tokenizer.nextToken()) - 1);
                        int d = Integer.parseInt(tokenizer.nextToken());
                        mDayList.add(CalendarDay.from(y, m, d));
                    }
                    calendarView.addDecorator(new EventDecorator(Color.WHITE, mDayList));
                    //prgDialog.dismiss();

                } else {
                    //prgDialog.dismiss();
                }
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                Toast.makeText(UserProfileActivity.this, "Arror occured", Toast.LENGTH_SHORT).show();
            }
        };
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
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
        prgDialog.dismiss();
    }
}