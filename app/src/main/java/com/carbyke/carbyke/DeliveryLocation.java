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
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import im.delight.android.location.SimpleLocation;

import static android.app.Activity.RESULT_CANCELED;


/**
 * A simple {@link Fragment} subclass.
 */
public class DeliveryLocation extends Fragment {

    private static final int RESULT_OK = -1;
    private GoogleMap googleMap;
    MapView mMapView;
    private TextView search_tv;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private Button continue_b;
    private ImageButton current_location_ib, sat_ib, ter_ib;
    SharedPreferences sharedPreferencesLocation;
    private static final String LOCATION = "location";
    private static final String STATION = "station";
    private static final String TYPE = "type";

    public DeliveryLocation() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_delivery_location, container, false);

        search_tv = view.findViewById(R.id.dl_search_et);
        continue_b = view.findViewById(R.id.dl_continue_b);
        current_location_ib = view.findViewById(R.id.dl_current_location_ib);
        sat_ib = view.findViewById(R.id.dl_satellite_ib);
        ter_ib = view.findViewById(R.id.dl_terrain_ib);
        mMapView = view.findViewById(R.id.dl_mapview);

//        initiating google map
        initiateMap(savedInstanceState);
        //    register button click responses
        clickResponse();

        return view;
    } //on create


//    register button click responses
    private void clickResponse(){
        //         current location image button on click
        current_location_ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search_tv.setHint("Fetching your gps location...");
                GetUserLocationPermissionThenSetLocation(); //RRC
            }
        });

//        search edit text on click
        search_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .build(Objects.requireNonNull(getActivity()));
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    Toast.makeText(getActivity(), "e "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    // TODO: Handle the error.
                }
            }
        });

//        continue button
        continue_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        //        on text change listener
        search_tv.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)){
                    continue_b.setEnabled(true);
                    continue_b.setBackgroundResource(R.color.lightGreen);
                }
                else continue_b.setEnabled(false);
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });

        sat_ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            }
        });


        ter_ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }
        });

        continue_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String address = search_tv.getText().toString().trim();
                new MaterialDialog.Builder(Objects.requireNonNull(getActivity()))
                        .title("Selected Location")
                        .content(""+address)
                        .contentColorRes(R.color.black)
                        .titleColor(getResources().getColor(R.color.black))
                        .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS)
                        .positiveText("Confirm")
                        .positiveColorRes(R.color.green)
                        .negativeText("Cancel")
                        .negativeColorRes(R.color.googleRed)
                        .backgroundColor(getResources().getColor(R.color.white))
                        .icon(getResources().getDrawable(R.drawable.ic_location))
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                                sharedPreferencesLocation = getActivity().getSharedPreferences(LOCATION, Context.MODE_PRIVATE);
//                                SharedPreferences.Editor editor = sharedPreferencesLocation.edit();
//                                editor.putString(STATION, address);
//                                editor.putString(TYPE, "DELIVERY");
//                                editor.apply();
                                MySharedPrefs mySharedPrefs = new MySharedPrefs(getActivity());
                                mySharedPrefs.setDeliveryLocation(address, "DELIVERY");
                                getActivity().finish();
                            }
                        })
                        .show();
            }
        });

    }
//    register button click responses


//  initializing map
    private void initiateMap(Bundle savedInstanceState){
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // needed to get the map to display immediately
        MapsInitializer.initialize(Objects.requireNonNull(getActivity()).getApplicationContext());
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

//                set selected location on map on click
                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(final LatLng point) {
                        search_tv.setHint("Fetching...");
                        setSelectedLocation(point);  //  RRC
                    }
                });



                CameraPosition cameraPosition = new CameraPosition.Builder().target(
                        new LatLng(31.3260, 75.5762)).zoom(15).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                googleMap.getUiSettings().setZoomControlsEnabled(false);
                googleMap.getUiSettings().setRotateGesturesEnabled(true);
                googleMap.getUiSettings().setCompassEnabled(true);
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }
        });
    }
    //  initializing map


//    set selected location on map and edit text
    private void setSelectedLocation(LatLng point) {
       // Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(getResources().getResourceName(R.drawable.tracker), "drawable", getActivity().getPackageName()));
      //  Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, 38, 38, false);


        try {
            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
            String gps_address="";
            List<Address> addresses = null;
            addresses = geocoder.getFromLocation(point.latitude,point.longitude, 1);
            gps_address = addresses.get(0).getAddressLine(0);
            search_tv.setText(gps_address);

//                            setting location at google map
            googleMap.clear();
            googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(point.latitude, point.longitude))
                    .anchor(0.5f, 0.1f)
                    .title(gps_address)
                    .snippet(""));
                    //.icon(BitmapDescriptorFactory.fromBitmap(resizedBitmap)));

        } catch (IOException | ArrayIndexOutOfBoundsException e) {
            Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }
    //    set selected location on map and edit text



    //    permission to get user location, if permitted then get user location from gps
    private void GetUserLocationPermissionThenSetLocation() {
        if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        else{

            try {
               // googleMap.setMyLocationEnabled(true);
                String latitude = String.valueOf(getLatitude());
                String longitude = String.valueOf(getLongitude());

                Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                String gps_address="";
                Double lat = Double.valueOf(latitude);
                Double log = Double.valueOf(longitude);
                List<Address> addresses = null;
                addresses = geocoder.getFromLocation(getLatitude(),getLongitude(), 1);
                gps_address = addresses.get(0).getAddressLine(0);
                search_tv.setText(gps_address);
                googleMap.clear();
                googleMap.addMarker(new MarkerOptions().position(new LatLng(lat, log)).title(gps_address));
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lat, log)));
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, log), 18.0f));
            } catch (IOException | IndexOutOfBoundsException | NullPointerException e) {
                e.printStackTrace();
               // Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }
//    permission to get user location

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(Objects.requireNonNull(getActivity()), data);
                search_tv.setText(place.getAddress());
                googleMap.clear();
                googleMap.addMarker(new MarkerOptions().position(place.getLatLng()).title(place.getName().toString()));
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 15.0f));


             //   Log.i(TAG, "Place: " + place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(Objects.requireNonNull(getActivity()), data);
                Toast.makeText(getActivity(), ""+status.getStatusMessage(), Toast.LENGTH_SHORT).show();
                // TODO: Handle the error.
              //  Log.i(TAG, status.getStatusMessage());

            }
        }
    }


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



//end
}
