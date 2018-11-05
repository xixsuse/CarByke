package com.carbyke.carbyke;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jsibbold.zoomage.ZoomageView;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import co.ceryle.radiorealbutton.RadioRealButtonGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class VerifiedProfile extends Fragment {

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("user_verifications");


    ZoomageView license_iv, aadhar_iv, photo_iv;
    private ImageButton back_ib;
    FirebaseAuth firebaseAuth;


    SpinKitView loading;

    public VerifiedProfile() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_verified_profile, container, false);


        back_ib = view.findViewById(R.id.ap_back_ib);
        license_iv = view.findViewById(R.id.ap_license_iv);
        aadhar_iv = view.findViewById(R.id.ap_aadhar_iv);
        photo_iv = view.findViewById(R.id.ap_photo_iv);
        loading = view.findViewById(R.id.ap_spin_kit);

        back_ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = Objects.requireNonNull(getActivity()).getSupportFragmentManager().findFragmentByTag("verified_profile");
                if(fragment != null)
                    getActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        getData();
        return view;
    }

    private void getData() {
        show();
        databaseReference.child(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String license, aadhar, photo;
                        license = dataSnapshot.child("driving_license").getValue(String.class);
                        aadhar = dataSnapshot.child("aadhar_image").getValue(String.class);
                        photo = dataSnapshot.child("face_image").getValue(String.class);

                        Picasso.with(getActivity())
                                .load(license)
                                .error(R.drawable.ic_warning)
                                .into(license_iv);

                        Picasso.with(getActivity())
                                .load(aadhar)
                                .error(R.drawable.ic_warning)
                                .into(aadhar_iv);

                        Picasso.with(getActivity())
                                .load(photo)
                                .error(R.drawable.ic_warning)
                                .into(photo_iv);
                        dismiss();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        dismiss();

                    }
                });
    }

    public void show(){
        loading.setVisibility(View.VISIBLE);
    }
    public void dismiss(){
        loading.setVisibility(View.GONE);
    }

}
