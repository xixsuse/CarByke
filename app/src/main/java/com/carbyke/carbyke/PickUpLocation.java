package com.carbyke.carbyke;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
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

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


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

    FragmentActivity context;
    // Creating RecyclerView.Adapter.
    private RecyclerView.Adapter adapter ;
    private List<DataForRecyclerView> list = new ArrayList<>();

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    private static final String PICK_UP_BASE = "pick_up_base";
    @SuppressLint("StaticFieldLeak")
    public static Button continue_b;

    SpinKitView sv;

    public PickUpLocation() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity){
            this.context = (FragmentActivity) context;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pick_up_location, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView search_et = view.findViewById(R.id.ul_search_et);
        continue_b = view.findViewById(R.id.ul_continue_b);
        sv = view.findViewById(R.id.dl_spin);

        recyclerView = view.findViewById(R.id.ul_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.isDuplicateParentStateEnabled();
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
        // Setting RecyclerView layout as LinearLayout.
        recyclerView.setLayoutManager(mLayoutManager);


//  continue button
        continue_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    MySharedPrefs mySharedPrefs = new MySharedPrefs(context);
                    String value = mySharedPrefs.getCameFromSOrSl();
                    if (TextUtils.equals(value, "search")){
                        Objects.requireNonNull(context).finish();
                    }
                    else if (TextUtils.equals(value, "searched_list_car")){
                        Objects.requireNonNull(context).finish();
                        startActivity(new Intent(context, SearchedCarList.class));
                    }

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

        CheckConnection();
        FetchDataOnline();
    }

//    on start
    public void onStart(){
        super.onStart();

      //  LoadStationDataFromPrefs();
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
                    DataForRecyclerView data = new DataForRecyclerView();
                    // filtering
                    if (!TextUtils.isEmpty(data.getStation())) {
                        if (data.getStation().toLowerCase().contains(s.toString().toLowerCase())) {

                            data.setStation(postSnapshot.child("station").getValue(String.class));
                            data.setPickUpLocationKey(postSnapshot.getKey());
                            list.add(data);
                        }
                    }
                }

                adapter = new PickUpLocationRecyclerViewAdapter(context, list);
                recyclerView.setAdapter(adapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    //    filtered search



    //check internet connection and load data
    private void CheckConnection() {
//        new CheckNetworkConnection(context, new CheckNetworkConnection.OnConnectionCallback() {
//            @Override
//            public void onConnectionSuccess() {
//
//            }
//            @Override
//            public void onConnectionFail(String msg) {
//                showNoInternetToast();
//            }
//        }).execute();
   }

//    fetch data if internet is available
    private void FetchDataOnline() {
        show();
        databaseReference.child(PICK_UP_BASE).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(list!=null) {
                    list.clear();  // v v v v important (eliminate duplication of data)
                }
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    DataForRecyclerView data = new DataForRecyclerView();
                    data.setStation(postSnapshot.child("station").getValue(String.class));
                    data.setLatitude(postSnapshot.child("latitude").getValue(String.class));
                    data.setLongitude(postSnapshot.child("longitude").getValue(String.class));
                    data.setMap_location(postSnapshot.child("map_location").getValue(String.class));
                    data.setPickUpLocationKey(postSnapshot.getKey());
                    list.add(data);
                }


                adapter = new PickUpLocationRecyclerViewAdapter(context, list);
                recyclerView.setAdapter(adapter);

                if (list.isEmpty()){
                    Toast.makeText(context, "No pick up place available", Toast.LENGTH_SHORT).show();
                }
            dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
              dismiss();
            }
        });

    }//    fetch data if internet is available

    @Override
    public void onDetach() {
        super.onDetach();
        this.context = null;
    }

//        spin kit loading methods
        private void show(){
        sv.setVisibility(View.VISIBLE);
        }
        private void dismiss(){
        sv.setVisibility(View.GONE);
        }

}//    end




