package com.carbyke.carbyke;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class BookVehicle extends AppCompatActivity {

    private String car_image_url, car_name, number_plate
            , general_key, number_plate_key, duration, latitude, longitude, pick_up_location_name, payable_amount;
    private ImageView car_image_iv;
    private TextView car_name_tv, payable_amount_tv, duration_tv
            , start_date_tv, end_date_tv, location_name_tv, location_name;
    private String days, hours, minutes;
    private GoogleMap googleMap;
    private MapView mMapView;
    private ImageButton back_ib;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_vehicle);

        car_image_iv = findViewById(R.id.bv_car_image);
        car_name_tv = findViewById(R.id.bv_car_name);
        payable_amount_tv = findViewById(R.id.bv_price);
        duration_tv = findViewById(R.id.bv_duration);
        start_date_tv = findViewById(R.id.bv_start_date_tv);
        end_date_tv = findViewById(R.id.bv_end_date_tv);
        mMapView= findViewById(R.id.bv_mapview);
        location_name_tv= findViewById(R.id.bv_location_name);
        back_ib = findViewById(R.id.bv_back_ib);
        location_name_tv = findViewById(R.id.bv_location_name);
        Toolbar toolbar = findViewById(R.id.bv_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BookVehicle.this.finish();
            }
        });


        back_ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BookVehicle.this.finish();
            }
        });


        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout ctl = findViewById(R.id.bv_collapsing_toolbar);
        ctl.setTitle("CHECKOUT");


        getSetData();

        latitude = getIntent().getStringExtra("latitude");
        longitude = getIntent().getStringExtra("longitude");
        pick_up_location_name = getIntent().getStringExtra("pick_up_location_name");
        location_name_tv.setText(pick_up_location_name);
        initiateMap(savedInstanceState);

        location_name_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(latitude) || TextUtils.isEmpty(longitude)){
                    Toast.makeText(BookVehicle.this, "unable to fetch location", Toast.LENGTH_SHORT).show();
                    return;
                }
                startActivity(new Intent(BookVehicle.this, StationMapLocation.class));
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void getSetData(){
        MySharedPrefs mySharedPrefs = new MySharedPrefs(BookVehicle.this);
        car_image_url = getIntent().getStringExtra("car_image_url");
        car_name = getIntent().getStringExtra("car_name");
        number_plate = getIntent().getStringExtra("number_plate");
        number_plate_key = getIntent().getStringExtra("number_plate_key");
        general_key = getIntent().getStringExtra("general_vehicle_key");
        duration = mySharedPrefs.getDuration();

        setCatImage();
        setCarNamePriceDuration();
        setStartEndDate();
    }

    //  initializing map
    private void initiateMap(Bundle savedInstanceState){
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // needed to get the map to display immediately
        MapsInitializer.initialize(this);
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                Double lat, log;
                lat = Double.valueOf(latitude);
                log = Double.valueOf(longitude);
                googleMap = mMap;
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(new LatLng(lat, log));
                markerOptions.title(pick_up_location_name);
                CircleOptions circleOptions = new CircleOptions()
                        .center(new LatLng(lat, log))
                        .clickable(false)
                        .strokeWidth(1)
                        .fillColor(R.color.lightGreen30)
                        .strokeColor(R.color.lightGreen30)
                        .radius(2000);
                googleMap.addCircle(circleOptions);
                googleMap.addMarker(markerOptions);
                CameraPosition cameraPosition = new CameraPosition.Builder().target(
                        new LatLng(lat, log)).zoom(13).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                googleMap.getUiSettings().setZoomControlsEnabled(false);
                googleMap.getUiSettings().setRotateGesturesEnabled(true);
                googleMap.getUiSettings().setCompassEnabled(false);
                googleMap.getUiSettings().setScrollGesturesEnabled(false);
            }
        });
    }
    //  initializing map


    private void setStartEndDate() {
        MySharedPrefs mySharedPrefs = new MySharedPrefs(BookVehicle.this);
        start_date_tv.setText(mySharedPrefs.getStartDateTime());
        end_date_tv.setText(mySharedPrefs.getEndDateTime());
    }

    @SuppressLint("SetTextI18n")
    private void setCarNamePriceDuration() {
        car_name_tv.setText(car_name+"\n"+number_plate);

        MySharedPrefs mySharedPrefs = new MySharedPrefs(BookVehicle.this);
        payable_amount = mySharedPrefs.getSelectedPrice();
        try {
            NumberFormat formatter = new DecimalFormat("#,###");
            payable_amount_tv.setText(formatter.format(Integer.parseInt(payable_amount)));
        }
        catch (Exception e){
            payable_amount_tv.setText(payable_amount);
        }

        SetCalculatedDaysOrHours();

        String d,h,m;
        if (TextUtils.equals(days, "1")) d = "DAY";
        else d = "DAYS";
        if (TextUtils.equals(hours, "1")) h = "HOUR";
        else h = "HOURS";
        if (TextUtils.equals(minutes, "1")) m = "MINUTE";
        else m = "MINUTES";

        String msg = "";
        if (!TextUtils.isEmpty(days) && !TextUtils.isEmpty(hours) && !TextUtils.isEmpty(minutes)){
            msg = "For "+days+" "+d+" "+hours+" "+h+" "+minutes+" "+m;
        }
        else if (!TextUtils.isEmpty(days) && TextUtils.isEmpty(hours) && TextUtils.isEmpty(minutes)){
            msg = "For "+days+" "+d;
        }
        else if (TextUtils.isEmpty(days) && !TextUtils.isEmpty(hours) && TextUtils.isEmpty(minutes)){
            msg = "For "+hours+" "+h;
        }
        else if (TextUtils.isEmpty(days) && TextUtils.isEmpty(hours) && !TextUtils.isEmpty(minutes)){
            msg = "For "+minutes+" "+m;
        }
        else if (!TextUtils.isEmpty(days) && !TextUtils.isEmpty(hours) && TextUtils.isEmpty(minutes)){
            msg = "For "+days+" "+d+" "+hours+" "+h;
        }
        else if (TextUtils.isEmpty(days) && !TextUtils.isEmpty(hours) && !TextUtils.isEmpty(minutes)){
            msg = "For "+hours+" "+h+" "+minutes+" "+m;
        }
        duration_tv.setText(msg);
    }

    @SuppressLint("SetTextI18n")
    private void SetCalculatedDaysOrHours(){
        MySharedPrefs mySharedPrefs = new MySharedPrefs(BookVehicle.this);
        try {
            int time;
            Date d1 = new Date(mySharedPrefs.getStartDateTime());
            Date d2 = new Date(mySharedPrefs.getEndDateTime());

            time = (int) TimeUnit.MINUTES.convert(d2.getTime() - d1.getTime(), TimeUnit.MILLISECONDS);
            days = String.valueOf(time/(24*60));
            hours = String.valueOf((time%(24*60)) / 60);
            minutes = String.valueOf((time%(24*60)) % 60);
            if (TextUtils.equals(days, "0")) days = null;
            if (TextUtils.equals(hours, "0")) hours = null;
            if (TextUtils.equals(minutes, "0")) minutes = null;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //    set image
    private void setCatImage() {
        Glide.with(BookVehicle.this)
                .load(car_image_url)
                .into(car_image_iv);
    }
//    set image

}