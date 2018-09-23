package com.carbyke.carbyke;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.GeoDataApi;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import static android.app.Activity.RESULT_CANCELED;


/**
 * A simple {@link Fragment} subclass.
 */
public class DeliveryLocation extends Fragment {

    private static final int RESULT_OK = -1;
    private View view;
    private GoogleMap googleMap;
    MapView mMapView;
    private TextView button;

    private static final int PLACE_PICKER_REQUEST = 1000;
   // private PlaceAutocompleteFragment placeAutocompleteFragment;
    private GoogleApiClient mClient;
    private EditText search_et;

    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    public DeliveryLocation() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_delivery_location, container, false);

        search_et = view.findViewById(R.id.dl_search_et);
        button = view.findViewById(R.id.dl_search_btn);

        search_et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                    .build(getActivity());
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    Toast.makeText(getActivity(), "e "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    Toast.makeText(getActivity(), "e "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    // TODO: Handle the error.
                }
            }
        });

//        PlaceAutocompleteFragment placeAutocompleteFragment = (PlaceAutocompleteFragment) getActivity().getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
//        //AutocompleteFilter autocompleteFilter = new AutocompleteFilter.Builder().setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES).build();
//      //  placeAutocompleteFragment.setFilter(autocompleteFilter);
//        placeAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
//            @Override
//            public void onPlaceSelected(Place place) {
//                Toast.makeText(getActivity(),place.getName().toString(),Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onError(Status status) {
//                Toast.makeText(getActivity(),status.toString(),Toast.LENGTH_SHORT).show();
//                Log.w("e323", status.toString());
//
//            }
//        });

        mMapView = view.findViewById(R.id.dl_mapview);

        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                // For showing a move to my location button
                //googleMap.setMyLocationEnabled(true);

                // For dropping a marker at a point on the Map
                LatLng jalandhar = new LatLng(31.3260, 75.5762);
               // googleMap.addMarker(new MarkerOptions().position(jalandhar).title("Marker Title").snippet("Marker Description"));

                // For zooming automatically to the location of the marker
             //   CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(12).build();
               // googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                CameraPosition cameraPosition = new CameraPosition.Builder().target(
                        new LatLng(31.3260, 75.5762)).zoom(10).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                googleMap.getUiSettings().setZoomControlsEnabled(false);
                googleMap.getUiSettings().setRotateGesturesEnabled(true);
            }
        });
//

        return view;
    } //on create


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(getActivity(), "hit", Toast.LENGTH_SHORT).show();
                Place place = PlaceAutocomplete.getPlace(getActivity(), data);
                search_et.setText(place.getAddress());
             //   Log.i(TAG, "Place: " + place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getActivity(), data);
                // TODO: Handle the error.
              //  Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }


//end
}
