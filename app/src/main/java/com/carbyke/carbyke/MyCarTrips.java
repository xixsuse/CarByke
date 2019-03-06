package com.carbyke.carbyke;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyCarTrips extends Fragment {

    private TextView finish_tv;

    private DatabaseReference databaseReferenceBOOKINGKEYS = FirebaseDatabase.getInstance().getReference()
            .child("users_booking_records").child("online");

    private DatabaseReference databaseReferenceBOOKINRecord = FirebaseDatabase.getInstance().getReference()
            .child("record_of_car_bookings");

    private FragmentActivity context;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter ;
    private List<DataForRecyclerView> list = new ArrayList<>();
    private ArrayList<String> booking_keys_al = new ArrayList<>();
    private SpinKitView spinKitView;
    private RelativeLayout emptyRelativeLayout;

    public MyCarTrips() {
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
        return inflater.inflate(R.layout.fragment_my_car_trips, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.ct_recycler_view);
        finish_tv = view.findViewById(R.id.ct_finish_tv);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);

        emptyRelativeLayout = view.findViewById(R.id.ct_empty_rl);

        spinKitView = view.findViewById(R.id.ct_spin);

        finish_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, SearchCar.class));
                context.finish();
            }
        });

        getBookingKeys();

    }

//    getting booking keys
    private void getBookingKeys() {
        show();
        MySharedPrefs mySharedPrefs = new MySharedPrefs(context);
        String uid = mySharedPrefs.getUID();

        databaseReferenceBOOKINGKEYS.child(uid).child("cars")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        emptyRelativeLayout.setVisibility(View.GONE);
                        Log.w("fdf3", "snap: "+dataSnapshot);
                        if (dataSnapshot.getChildrenCount() == 0){
                            getBookingData();
                            dismiss();
                            return;
                        }
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            booking_keys_al.add(snapshot.getKey());
                        }
                        Log.w("fdf3", "keys: "+booking_keys_al);
                        getBookingData();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(context, "1 "+databaseError, Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                });
    }
    //    getting booking keys

//    get booking data
    private void getBookingData() {
        if (booking_keys_al.isEmpty()){
            emptyRelativeLayout.setVisibility(View.VISIBLE);
            YoYo.with(Techniques.Landing)
                    .repeat(0)
                    .duration(700)
                    .playOn(emptyRelativeLayout);
            dismiss();
        }

        for (final String booking_key : booking_keys_al) { // for 1

            databaseReferenceBOOKINRecord.child(booking_key)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            DataForRecyclerView dataForRecyclerView = new DataForRecyclerView();
                            dataForRecyclerView.setPayable_amount(dataSnapshot.child("total_payable_amount").getValue(Long.class));
                            dataForRecyclerView.setCar_name(dataSnapshot.child("car_name").getValue(String.class));
                            dataForRecyclerView.setNumber_plate(dataSnapshot.child("number_plate").getValue(String.class));
                            dataForRecyclerView.setGeneral_vehicle_key(dataSnapshot.child("general_vehicle_key").getValue(String.class));
                            dataForRecyclerView.setStart_date(dataSnapshot.child("start_date").getValue(String.class));
                            dataForRecyclerView.setEnd_date(dataSnapshot.child("end_date").getValue(String.class));
                            dataForRecyclerView.setTrip_status(dataSnapshot.child("trip_status").getValue(String.class));
                            dataForRecyclerView.setBooking_key(booking_key);
                            list.add(dataForRecyclerView);

                            adapter = new MyCarTripsRecyclerViewAdapter(context, list);
                            recyclerView.setAdapter(adapter);
                            dismiss();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(context, ""+databaseError, Toast.LENGTH_SHORT).show();
                            dismiss();
                        }
                    });

        } // for 1


    }
//    get booking data

    private void show(){
        spinKitView.setVisibility(View.VISIBLE);
    }
    private void dismiss(){
        spinKitView.setVisibility(View.GONE);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.context = null;
    }

}
