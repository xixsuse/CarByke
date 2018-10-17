package com.carbyke.carbyke;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
public class CarListWithFuel extends Fragment {

    private RecyclerView recyclerView;
    private View view;

    // Creating RecyclerView.Adapter.
    private RecyclerView.Adapter adapter ;
    private List<DataForRecyclerView> list = new ArrayList<>();

    private final static String VEHICLE_DETAILS = "vehicle_details";
    private final static String CARS = "cars";
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(VEHICLE_DETAILS).child(CARS);

    SpinKitView loading;

    public CarListWithFuel() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
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

        return view;
    }

    //    on start
    public void onStart(){
        super.onStart();

        show();
        new CheckNetworkConnection(getActivity(), new CheckNetworkConnection.OnConnectionCallback() {
            @Override
            public void onConnectionSuccess() {
                FetchDataOnline();
            }
            @Override
            public void onConnectionFail(String msg) {
                NoInternetConnectionAlert noInternetConnectionAlert = new NoInternetConnectionAlert(getActivity());
                noInternetConnectionAlert.DisplayNoInternetConnection();
                dismiss();
            }
        }).execute();

    }
//    on start


//    fetch data if internet is available
    private void FetchDataOnline() {

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(list!=null) {
                    list.clear();  // v v v v important (eliminate duplication of data)
                }

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    DataForRecyclerView data = postSnapshot.getValue(DataForRecyclerView.class);
                    list.add(data);
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
                Toast.makeText(getActivity(), "c "+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }//    fetch data if internet is available

    private void show(){
        loading.setVisibility(View.VISIBLE);
    }
    private void dismiss(){
        loading.setVisibility(View.GONE);
    }

//    end
}
