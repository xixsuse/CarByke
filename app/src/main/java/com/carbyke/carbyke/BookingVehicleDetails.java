package com.carbyke.carbyke;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;
import me.ydcool.lib.qrmodule.encoding.QrGenerator;

public class BookingVehicleDetails extends AppCompatActivity implements View.OnClickListener {

    private String car_image_url, car_name, number_plate
            , general_key, number_plate_key, duration, latitude
            , longitude, pick_up_location_name, booking_key, station_key, pollution, knee, rc, insurance;
    private ImageView car_image_iv;
    private TextView car_name_tv
            , payable_amount_tv, duration_tv, start_date_tv, end_date_tv
            , location_name_tv, ending_kilometers_tv;
    private GoogleMap googleMap;
    private MapView mMapView;
    private ImageButton back_ib;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private EditText start_kilometers_et, ending_kilometers_et;
    private CheckBox insurance_c, rc_c, pollution_c, knee_c, payment_c;
    private RelativeLayout status_booked_rl;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private ImageView qr_image;

    private DatabaseReference databaseReferenceSTATION = FirebaseDatabase.getInstance().getReference().child("pick_up_base");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_vehicle_details);

        car_image_iv = findViewById(R.id.vd_car_image);
        car_name_tv = findViewById(R.id.vd_car_name);
        payable_amount_tv = findViewById(R.id.vd_price);
        duration_tv = findViewById(R.id.vd_duration);
        start_date_tv = findViewById(R.id.vd_start_date_tv);
        end_date_tv = findViewById(R.id.vd_end_date_tv);
        mMapView = findViewById(R.id.vd_mapview);
        location_name_tv = findViewById(R.id.vd_location_name);
        back_ib = findViewById(R.id.vd_back_ib);
        location_name_tv = findViewById(R.id.vd_location_name);
        start_kilometers_et = findViewById(R.id.vd_starting_km_et);
        ending_kilometers_et = findViewById(R.id.vd_ending_km_et);
        ending_kilometers_tv = findViewById(R.id.vd_ending_km_tv);
        status_booked_rl = findViewById(R.id.vd_status_booked_rl);
        qr_image = findViewById(R.id.vd_qr_generated);

        insurance_c = findViewById(R.id.vd_insurance_c);
        rc_c = findViewById(R.id.vd_rc_c);
        pollution_c = findViewById(R.id.vd_pollution_c);
        knee_c = findViewById(R.id.vd_knee_c);

        Toolbar toolbar = findViewById(R.id.vd_toolbar);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        firebaseAuth = FirebaseAuth.getInstance();


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BookingVehicleDetails.this.finish();
            }
        });

        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        collapsingToolbarLayout = findViewById(R.id.vd_collapsing_toolbar);

        mMapView.setEnabled(false);


        getAndSetData();

        latitude = getIntent().getStringExtra("latitude");
        longitude = getIntent().getStringExtra("longitude");
        pick_up_location_name = getIntent().getStringExtra("station_name");
        location_name_tv.setText(pick_up_location_name);
        initiateMap(savedInstanceState);

        location_name_tv.setOnClickListener(this);

        try {
            Bitmap qrCode = new QrGenerator.Builder()
                    .content("https://github.com/Ydcool/QrModule")
                    .qrSize(300)
                    .margin(2)
                    .color(Color.BLACK)
                    .bgColor(Color.WHITE)
                    .ecc(ErrorCorrectionLevel.H)
                    .overlaySize(100)
                    .overlayAlpha(255)
                    .overlayXfermode(PorterDuff.Mode.SRC_ATOP)
                    .encode();
            qr_image.setImageBitmap(qrCode);
        } catch (WriterException e) {
            Toast.makeText(this, "err "+e, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @SuppressLint("SetTextI18n")
    public void getAndSetData() {

        setLayoutVisibility();

        if (TextUtils.equals(getIntent().getStringExtra("booking_status"), "BOOKED")){
            collapsingToolbarLayout.setTitle("BOOKED");
        } else if (TextUtils.equals(getIntent().getStringExtra("booking_status"), "ACTIVE")){
            collapsingToolbarLayout.setTitle("ACTIVE");
        } else if (TextUtils.equals(getIntent().getStringExtra("booking_status"), "COMPLETED")){
            collapsingToolbarLayout.setTitle("COMPLETED");
        } else if (TextUtils.equals(getIntent().getStringExtra("booking_status"), "CANCELLED")){
            collapsingToolbarLayout.setTitle("CANCELLED");
        }

        car_name = getIntent().getStringExtra("car_name");
        number_plate = getIntent().getStringExtra("number_plate");
        number_plate_key = getIntent().getStringExtra("number_plate_key");
        general_key = getIntent().getStringExtra("general_vehicle_key");
        booking_key = getIntent().getStringExtra("booking_key");
        duration = getIntent().getStringExtra("booking_day_hour_minutes");
        start_kilometers_et.setText(getIntent().getStringExtra("starting_kilometers"));
        ending_kilometers_et.setText(getIntent().getStringExtra("ending_kilometers"));

        payable_amount_tv.setText(""+getIntent().getLongExtra("total_payable_amount", 0));

        setCatImage(general_key);
        setCarNamePriceDuration();
        setStartEndDate();
        setAccessories();
    }


//    setting layout visibilities
    private void setLayoutVisibility() {
        if (TextUtils.equals(getIntent().getStringExtra("booking_status"), "BOOKED") || TextUtils.equals(getIntent().getStringExtra("booking_status"), "CANCELLED") ){
            status_booked_rl.setVisibility(View.GONE);
        } else if (TextUtils.equals(getIntent().getStringExtra("booking_status"), "ACTIVE") || TextUtils.equals(getIntent().getStringExtra("booking_status"), "CANCELLED") ){
            ending_kilometers_et.setVisibility(View.GONE);
            ending_kilometers_tv.setVisibility(View.GONE);
        }

    }
    //    setting layout visibilities


    //    set accessories
    private void setAccessories() {
        insurance_c.setChecked(getIntent().getBooleanExtra("insurance", false));
        knee_c.setChecked(getIntent().getBooleanExtra("knee", false));
        pollution_c.setChecked(getIntent().getBooleanExtra("pollution", false));
        rc_c.setChecked(getIntent().getBooleanExtra("rc", false));
    }
//    set accessories

    //  initializing map
    private void initiateMap(Bundle savedInstanceState) {
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // needed to get the map to display immediately
        MapsInitializer.initialize(this);
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                double lat, log;
                lat = Double.parseDouble(latitude);
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
                googleMap.getUiSettings().setAllGesturesEnabled(false);
            }
        });
    }
    //  initializing map


    private void setStartEndDate() {
        String start_date = getIntent().getStringExtra("start_date");
        String end_date = getIntent().getStringExtra("end_date");

        start_date_tv.setText(start_date.substring(start_date.length() - 20));
        end_date_tv.setText(end_date.substring(end_date.length() - 20));
    }

    @SuppressLint("SetTextI18n")
    private void setCarNamePriceDuration() {
        car_name_tv.setText(car_name + "\n" + number_plate);
        duration_tv.setText("For "+duration);
    }


    //    set image
    private void setCatImage(String general_key) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("vehicle_details").child("cars").child(general_key);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Glide.with(BookingVehicleDetails.this)
                        .load(dataSnapshot.child("image_url").getValue(String.class))
                        .into(car_image_iv);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
    //    set image


    //    on click
    @Override
    public void onClick(View view) {
        int id = view.getId();

//        map location of station
        if (id == R.id.vd_location_name) {
            if (TextUtils.isEmpty(latitude) || TextUtils.isEmpty(longitude)) {
                Toast.makeText(BookingVehicleDetails.this, "unable to fetch location", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(BookingVehicleDetails.this, StationMapLocation.class);
            intent.putExtra("latitude", latitude);
            intent.putExtra("longitude", longitude);
            intent.putExtra("location_name", pick_up_location_name);
            startActivity(intent);
        }
    }
    //    on click


//    end
}
