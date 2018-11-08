package com.carbyke.carbyke;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class StationMapLocation extends AppCompatActivity {

    private GoogleMap googleMap;
    private MapView mMapView;
    private TextView textView;
    private Double latitude, longitude;
    private MySharedPrefs mySharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_map_location);

        mySharedPrefs = new MySharedPrefs(StationMapLocation.this);
        latitude = Double.valueOf(mySharedPrefs.getPickLocationLat());
        longitude = Double.valueOf(mySharedPrefs.getPickLocationLong());


        mMapView= findViewById(R.id.ml_mapview);
        textView= findViewById(R.id.ml_text_view);
        textView.setText(mySharedPrefs.getPickLocationMapLocation());
        initiateMap(savedInstanceState);
    }


    //  initializing map
    private void initiateMap(Bundle savedInstanceState){
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // needed to get the map to display immediately
        MapsInitializer.initialize(this);
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(new LatLng(latitude, longitude));
                markerOptions.title(mySharedPrefs.getPickLocationMapLocation());
                googleMap.addMarker(markerOptions);
                CameraPosition cameraPosition = new CameraPosition.Builder().target(
                        new LatLng(latitude, longitude)).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                googleMap.getUiSettings().setZoomControlsEnabled(true);
                googleMap.getUiSettings().setRotateGesturesEnabled(true);
                googleMap.getUiSettings().setCompassEnabled(false);
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            }
        });
    }
    //  initializing map


//    end
}
