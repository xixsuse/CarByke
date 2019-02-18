package com.carbyke.carbyke;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyCarTrips extends Fragment {

    private DatabaseReference databaseReferenceBOOKINGKEYS = FirebaseDatabase.getInstance().getReference()
            .child("users_booking_records").child("online");

    private DatabaseReference databaseReferenceBOOKINRecord = FirebaseDatabase.getInstance().getReference()
            .child("record_of_car_bookings");

    private FragmentActivity context;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter ;
    private List<DataForRecyclerView> list = new ArrayList<>();
    private ArrayList<String> booking_keys_al;

    public MyCarTrips() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_car_trips, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.ww_recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(mLayoutManager);

        getBookingKeys();

    }

//    getting booking keys
    private void getBookingKeys() {
        MySharedPrefs mySharedPrefs = new MySharedPrefs(context);
        String uid = mySharedPrefs.getUID();

        databaseReferenceBOOKINGKEYS.child(uid).child("cars")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount() == 0){
                            getBookingData();
                            return;
                        }
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            booking_keys_al.add(snapshot.getKey());
                        }
                        getBookingData();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
    //    getting booking keys

//    get booking data
    private void getBookingData() {
        if (booking_keys_al.isEmpty()){
            Toast.makeText(context, "no data", Toast.LENGTH_SHORT).show();
        }



    }
//    get booking data


    @Override
    public void onDetach() {
        super.onDetach();
        this.context = null;
    }

}
