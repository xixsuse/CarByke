package com.carbyke.carbyke;


import android.Manifest;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
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

                try {
                    // googleMap.setMyLocationEnabled(true);
                    String latitude = String.valueOf(getLatitude());
                    String longitude = String.valueOf(getLongitude());
                    MySharedPrefs mySharedPrefs = new MySharedPrefs(getActivity());
                    mySharedPrefs.setUserLatLog(latitude, longitude);

                    FetchDataOnline();

                } catch (IndexOutOfBoundsException | NullPointerException e) {
                    e.printStackTrace();
                    FetchDataOnline();
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
