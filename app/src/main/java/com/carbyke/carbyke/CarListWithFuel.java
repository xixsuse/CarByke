package com.carbyke.carbyke;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import im.delight.android.location.SimpleLocation;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;


/**
 * A simple {@link Fragment} subclass.
 */
public class CarListWithFuel extends Fragment implements EasyPermissions.PermissionCallbacks {

    private RecyclerView recyclerView;
    private View view;

    // Creating RecyclerView.Adapter.
    private RecyclerView.Adapter adapter ;
    private List<DataForRecyclerView> list = new ArrayList<>();


    private final static String VEHICLE_DETAILS = "vehicle_details";
    private final static String CARS = "cars";
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(VEHICLE_DETAILS).child(CARS);

    private static final int DEVICE_LOCATION = 1;
    SpinKitView loading;

    public CarListWithFuel() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_car_list_with_fuel, container, false);

        loading = view.findViewById(R.id.ww_spin);

        recyclerView = view.findViewById(R.id.ww_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.isDuplicateParentStateEnabled();
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        // Setting RecyclerView layout as LinearLayout.
        recyclerView.setLayoutManager(mLayoutManager);

        show();
        new CheckNetworkConnection(getActivity(), new CheckNetworkConnection.OnConnectionCallback() {
            @Override
            public void onConnectionSuccess() {
                grantPermission();

            }
            @Override
            public void onConnectionFail(String msg) {
                NoInternetConnectionAlert noInternetConnectionAlert = new NoInternetConnectionAlert(getActivity());
                noInternetConnectionAlert.DisplayNoInternetConnection();
                dismiss();
            }
        }).execute();


        return view;
    }

    //    on start
    public void onStart(){
        super.onStart();


    }
//    on start

    @AfterPermissionGranted(DEVICE_LOCATION)
    private void grantPermission() {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (EasyPermissions.hasPermissions(Objects.requireNonNull(getActivity()), perms)) {
            if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
            else{
                final MySharedPrefs mySharedPrefs = new MySharedPrefs(getActivity());
                try {
                    // googleMap.setMyLocationEnabled(true);
                    String latitude = String.valueOf(getLatitude());
                    String longitude = String.valueOf(getLongitude());

                    mySharedPrefs.setUserLatLog(latitude, longitude);

                    final String key = mySharedPrefs.getPickLocationKey();
                    final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("pick_up_base");
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String latitude, longitude, map_location;
                            dataSnapshot = dataSnapshot.child(key);
                            latitude = dataSnapshot.child("latitude").getValue(String.class);
                            longitude = dataSnapshot.child("longitude").getValue(String.class);
                            map_location = dataSnapshot.child("map_location").getValue(String.class);
                            mySharedPrefs.setPickLocationData(latitude, longitude, map_location);
                            //FetchDataOnline();
                            getListOfAvailableCars();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            //FetchDataOnline();
                            getListOfAvailableCars();
                        }
                    });



                } catch (IndexOutOfBoundsException | NullPointerException e) {
                    e.printStackTrace();
                    String key = mySharedPrefs.getPickLocationKey();
                    final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("pick_up_base").child(key);
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String latitude, longitude, map_location;
                            latitude = dataSnapshot.child("latitude").getValue(String.class);
                            longitude = dataSnapshot.child("longitude").getValue(String.class);
                            map_location = dataSnapshot.child("map_location").getValue(String.class);
                            mySharedPrefs.setPickLocationData(latitude, longitude, map_location);
                            //FetchDataOnline();
                            getListOfAvailableCars();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            //FetchDataOnline();
                            getListOfAvailableCars();
                        }
                    });
                    dismiss();
                }
            }
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "Grant Permission to Access Device Location.",
                    DEVICE_LOCATION, perms);
            dismiss();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        //Toast.makeText(this, "Some permissions have been granted!", Toast.LENGTH_SHORT).show();
        // Some permissions have been granted
        // ...
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        dismiss();
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (EasyPermissions.somePermissionPermanentlyDenied(this, Arrays.asList(perms))) {
            new MaterialDialog.Builder(Objects.requireNonNull(getActivity()))
                    .title("Grant Permission")
                    .titleColor(getResources().getColor(R.color.black))
                    .content("Please grant permission to access your location. Otherwise, We can not proceed further.")
                    .contentColor(getResources().getColor(R.color.black))
                    .icon(getResources().getDrawable(R.drawable.ic_error))
                    .backgroundColor(getResources().getColor(R.color.white))
                    .positiveText("Grant Permission")
                    .positiveColor(getResources().getColor(R.color.lightGreen))
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            new AppSettingsDialog.Builder(getActivity()).build().show();
                        }
                    })
                    .negativeText("Decline")
                    .negativeColor(getResources().getColor(R.color.red))
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
            //new AppSettingsDialog.Builder(this).build().show();
        }

    }


    //    getting list of available cars for booking
    private void getListOfAvailableCars() {
        final long start_millis;

        start_millis = Objects.requireNonNull(getActivity()).getIntent().getLongExtra("start_date", 0);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("book_vehicle").child("cars");
        databaseReference
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String general_vehicle_key = null;
                        ArrayList<String> arrayList = new ArrayList<>();
                        HashMap<String, String> hashMap = new HashMap<>();

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){ // for 1
                            general_vehicle_key = null;

                            for (DataSnapshot postshot : snapshot.getChildren()){ // for 2

                                final long booked_end_millis;
                                long booked_end_millis1;
                                final String end_date, number_plate;

                                end_date = postshot.child("end_date").getValue(String.class);
                                number_plate = postshot.child("number_plate").getValue(String.class);
                                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("EEEE MMM dd yyyy hh:mm a");
                                try {
                                    Date mDate = sdf.parse(end_date);
                                    booked_end_millis1 = mDate.getTime();
                                } catch (ParseException e) {
                                    booked_end_millis1 = 0;
                                    e.printStackTrace();
                                }
                                booked_end_millis = booked_end_millis1;


                                long t = booked_end_millis + 3600000;
                                if (start_millis >= t){ // if vehicle is not booked in that time (+3600000 for 1 hour addition)
                                    general_vehicle_key = snapshot.getKey();
                                    arrayList.add(postshot.getKey());
                                    hashMap.put(postshot.getKey(), number_plate);
//
                                }
                            } // for 2
                             setData(general_vehicle_key, hashMap);
                             hashMap.clear();
                             arrayList.clear();
                        } //for 1

                    } // on data change

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.w("sd5", "error "+databaseError);
                    }
                });
    }
    //    getting list of available cars for booking

    private void setData(final String general_vehicle_key, final HashMap<String, String> hashMap){
        if (TextUtils.isEmpty(general_vehicle_key)) return;

      //  final ArrayList<String> k = new ArrayList<>(arrayList);
        final HashMap<String, String > k = new HashMap<>(hashMap);

        databaseReference.child(general_vehicle_key)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (Object o : k.entrySet()) {
                            Map.Entry pair = (Map.Entry) o;
                            DataForRecyclerView data = dataSnapshot.getValue(DataForRecyclerView.class);
                            data.setGeneral_vehicle_key(general_vehicle_key);
                            data.setNumber_plate(pair.getValue().toString());
                            data.setNumber_plate_key(pair.getKey().toString());
                            list.add(data);
                        }

//                        for (String item: k) {
//                            Log.w("sd5", "error "+general_vehicle_key+" "+item);
//                            DataForRecyclerView data = dataSnapshot.getValue(DataForRecyclerView.class);
//                            data.setGeneral_vehicle_key(general_vehicle_key);
//                            data.setNumber_plate_key(item);
//                            list.add(data);
//                        }
                        adapter = new CarListWithFuelRecyclerViewAdapter(getContext(), list);
                        recyclerView.setAdapter(adapter);

                        if (list.isEmpty()){
                         //   Toast.makeText(getActivity(), "No Cars Available", Toast.LENGTH_SHORT).show();
                        }
                        dismiss();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getActivity(), "  "+databaseError, Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                });
    }


//    fetch data if internet is available
    private void FetchDataOnline() {

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(list!=null) {
                    list.clear();  // v v v v important (eliminate duplication of data)
                }

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    String enabled = postSnapshot.child("enabled").getValue(String.class);
                    if (TextUtils.equals("true", enabled) || TextUtils.isEmpty(enabled)){
                        DataForRecyclerView data = postSnapshot.getValue(DataForRecyclerView.class);
                        list.add(data);
                      }
                    }

                adapter = new CarListWithFuelRecyclerViewAdapter(getContext(), list);
                recyclerView.setAdapter(adapter);

                if (list.isEmpty()){
                    Toast.makeText(getActivity(), "No Cars Available", Toast.LENGTH_SHORT).show();
                }
                dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                dismiss();
            }
        });

    }//    fetch data if internet is available


    //    getting user location
    public double getLatitude(){

        // construct a new instance of SimpleLocation
        SimpleLocation location = new SimpleLocation(Objects.requireNonNull(getActivity()));
        // if we can't access the location yet
        if (!location.hasLocationEnabled()) {
            // ask the user to enable location access
            SimpleLocation.openSettings(getActivity());
        }
        return location.getLatitude();


    }
    public double getLongitude(){
        // construct a new instance of SimpleLocation
        SimpleLocation  location = new SimpleLocation(Objects.requireNonNull(getActivity()));
        // if we can't access the location yet
        if (!location.hasLocationEnabled()) {
            // ask the user to enable location access
            SimpleLocation.openSettings(getActivity());
        }
        return location.getLongitude();
    }


    private void show(){
        loading.setVisibility(View.VISIBLE);
    }
    private void dismiss(){
        loading.setVisibility(View.GONE);
    }

//    end
}
