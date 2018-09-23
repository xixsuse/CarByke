package com.carbyke.carbyke;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.roger.catloadinglibrary.CatLoadingView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class PickUpLocation extends Fragment{

    private SharedPreferences sharedPreferencesStationDataOffline;
    private static final String STATION_DATA_PREF = "station_data_pref";
    private static final String STATION_NAME = "name";
    private static final String COUNT = "count";
    int station_count = 0;

    private RecyclerView recyclerView;
    private View view;

    // Creating RecyclerView.Adapter.
    private RecyclerView.Adapter adapter ;
    private List<DataForRecyclerView> list = new ArrayList<>();

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    private static final String PICK_UP_BASE = "pick_up_base";
    private CatLoadingView catLoadingView;
    public static Button continue_b;

    public PickUpLocation() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_pick_up_location, container, false);

        TextView search_et = view.findViewById(R.id.ul_search_et);
        continue_b = view.findViewById(R.id.ul_continue_b);

        recyclerView = view.findViewById(R.id.ul_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.isDuplicateParentStateEnabled();
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        // Setting RecyclerView layout as LinearLayout.
        recyclerView.setLayoutManager(mLayoutManager);
        catLoadingView = new CatLoadingView();

        try {
            sharedPreferencesStationDataOffline = getActivity().getSharedPreferences(STATION_DATA_PREF, Context.MODE_PRIVATE);
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }



        continue_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    getActivity().finish();
                }
                catch (NullPointerException e){
                    //
                }

            }
        });


//        on text change listener
        search_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                FilterSearch(s);
                // TODO Auto-generated method stub
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


        return view;
    }

//    on start
    public void onStart(){
        super.onStart();
        LoadStationDataFromPrefs();
       // LoadData();
    }
//    on start

//    filtered search
    private void FilterSearch(final CharSequence s) {
        databaseReference.child(PICK_UP_BASE).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(list!=null) {
                    list.clear();  // v v v v important (eliminate duplication of data)
                }

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    DataForRecyclerView data = postSnapshot.getValue(DataForRecyclerView.class);
                    // filtering
                    if (data != null && data.getStation().toLowerCase().contains(s.toString().toLowerCase())) {
                        list.add(data);
                    }

                }

                adapter = new PickUpLocationRecyclerViewAdapter(getContext(), list);
                recyclerView.setAdapter(adapter);

                if (list.isEmpty()){
                    Toast.makeText(getContext(), "No Station Available", Toast.LENGTH_SHORT).show();
                }
                //   showEmptyPage();
                //progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Hiding the progress dialog.
                // progressDialog.dismiss();
            }
        });
    }
    //    filtered search



    //check internet connection and load data
    private void LoadData() {
        FetchData();
        //catLoadingView.show(getActivity().getSupportFragmentManager(), "");
//        new CheckNetworkConnection(getActivity(), new CheckNetworkConnection.OnConnectionCallback() {
//            @Override
//            public void onConnectionSuccess() {
//
//                FetchData();
//            //    myLoadingCat.dismissCat();
//                //catLoadingView.dismiss();
//            }
//            @Override
//            public void onConnectionFail(String msg) {
//                //catLoadingView.dismiss();
//                NoInternetConnectionAlert noInternetConnectionAlert = new NoInternetConnectionAlert(getActivity());
//                noInternetConnectionAlert.DisplayNoInternetConnection();
//            }
//        }).execute();
   }

//    fetch data if internet is available
    private void FetchData() {
        databaseReference.child(PICK_UP_BASE).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(list!=null) {
                    list.clear();  // v v v v important (eliminate duplication of data)
                }

                SharedPreferences.Editor editor = sharedPreferencesStationDataOffline.edit();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    DataForRecyclerView data = postSnapshot.getValue(DataForRecyclerView.class);
                    list.add(data);

                    // storing data in shared pref for offline
                    try {
                        editor.putString(String.valueOf(station_count), data.getStation());
                        station_count++;
                    }
                    catch (NullPointerException e){
                        //
                    }

                }

                editor.putString(COUNT, String.valueOf(station_count));
                editor.apply();

                adapter = new PickUpLocationRecyclerViewAdapter(getContext(), list);
                recyclerView.setAdapter(adapter);

                if (list.isEmpty()){
                    Toast.makeText(getActivity(), "No pick up place available", Toast.LENGTH_SHORT).show();
                }
                 //   showEmptyPage();
                //progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Hiding the progress dialog.
               // progressDialog.dismiss();
            }
        });

    }//    fetch data if internet is available

//    loading data from
    private void LoadStationDataFromPrefs(){
        String station;
        String cs;
        int ci;
        cs = sharedPreferencesStationDataOffline.getString(COUNT, "");
        if (TextUtils.isEmpty(cs)){
            LoadData();
            return;
        }
        ci = Integer.parseInt(cs);
        for (int i=0 ; i<= ci ; i++){
            station = sharedPreferencesStationDataOffline.getString(String.valueOf(ci),"");
            DataForRecyclerView dataForRecyclerView = new DataForRecyclerView();
            dataForRecyclerView.setStation(station);
            list.add(dataForRecyclerView);
            }
        Toast.makeText(getContext(), "", Toast.LENGTH_SHORT).show();
        adapter = new PickUpLocationRecyclerViewAdapter(getContext(), list);
        recyclerView.setAdapter(adapter);
        LoadData();
    }

}//    end




