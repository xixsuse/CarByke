package com.carbyke.carbyke;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
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
    FragmentActivity context;

    SpinKitView loading;

    public VerifiedProfile() {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_verified_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        back_ib = view.findViewById(R.id.ap_back_ib);
        license_iv = view.findViewById(R.id.ap_license_iv);
        aadhar_iv = view.findViewById(R.id.ap_aadhar_iv);
        photo_iv = view.findViewById(R.id.ap_photo_iv);
        loading = view.findViewById(R.id.ap_spin_kit);

        back_ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = Objects.requireNonNull(context).getSupportFragmentManager().findFragmentByTag("verified_profile");
                if(fragment != null)
                    context.getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        getData();
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

                        Glide.with(context)
                                .load(license)
                                .apply(new RequestOptions().error(R.drawable.ic_error))
                                .into(license_iv);

                        Glide.with(context)
                                .load(aadhar)
                                .apply(new RequestOptions().error(R.drawable.ic_error))
                                .into(aadhar_iv);

                        Glide.with(context)
                                .load(photo)
                                .apply(new RequestOptions().error(R.drawable.ic_error))
                                .into(photo_iv);
                        dismiss();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        dismiss();

                    }
                });
    }


    @Override
    public void onDetach() {
        super.onDetach();
        this.context = null;
    }


    public void show(){
        loading.setVisibility(View.VISIBLE);
    }
    public void dismiss(){
        loading.setVisibility(View.GONE);
    }

}
