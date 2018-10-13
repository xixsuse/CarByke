package com.carbyke.carbyke;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.eralp.circleprogressview.CircleProgressView;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import mehdi.sakout.fancybuttons.FancyButton;

public class ProfileVerification extends AppCompatActivity implements View.OnClickListener {

    private FancyButton upload_license_fb, upload_adhar_fb, upload_live_photo_fb;
    private SpinKitView loading;
    private CircleProgressView profile_verification_cpv;

    private final static String USER_PROFILES = "user_profiles";
    private final static String IMAGES = "images";
    private final static String DRIVING_LICENSE = "driving_license";
    private final static String FACE_IMAGE = "face_image";
    private final static String AADHAR_IMAGE = "aadhar_image";
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(USER_PROFILES);
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_verification);

        upload_adhar_fb = findViewById(R.id.pv_upload_adhar_fb);
        upload_license_fb = findViewById(R.id.pv_upload_driving_license_fb);
        upload_live_photo_fb = findViewById(R.id.pv_upload_live_photo_fb);
        loading = findViewById(R.id.pv_spin_kit);
        profile_verification_cpv = findViewById(R.id.pv_profile_verification_cpv);
        profile_verification_cpv.setInterpolator(new AccelerateDecelerateInterpolator());

        upload_adhar_fb.setOnClickListener(this);
        upload_license_fb.setOnClickListener(this);
        upload_live_photo_fb.setOnClickListener(this);
    }

    public void onStart(){
        super.onStart();
        checkPercentageComplete();
    }


//    check % profile completion
    private void checkPercentageComplete() {
        final String uid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        databaseReference.child(uid).child(IMAGES).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = 0;
                if (dataSnapshot.hasChild(DRIVING_LICENSE)) count++;
                if (dataSnapshot.hasChild(AADHAR_IMAGE)) count++;
                if (dataSnapshot.hasChild(FACE_IMAGE)) count++;

                if (count == 0){
                    profile_verification_cpv.setProgress(0);
                }
                else if (count == 1){
                    profile_verification_cpv.setProgressWithAnimation(25, 2000);
                }
                else if (count == 2){
                    profile_verification_cpv.setProgressWithAnimation(50, 2000);
                }
                else if (count == 3){
                    profile_verification_cpv.setProgressWithAnimation(80, 2000);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
//    check % profile completion

    //    onclick
    @Override
    public void onClick(View view) {
        show();
        int id = view.getId();
        switch (id){
            case R.id.pv_upload_adhar_fb:
                new CheckNetworkConnection(ProfileVerification.this, new CheckNetworkConnection.OnConnectionCallback() {
                    @Override
                    public void onConnectionSuccess() {
                        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container_profile_verification, new UploadAadharCard()).addToBackStack("aadhar_card").commit();
                        dismiss();
                    }
                    @Override
                    public void onConnectionFail(String msg) {
                        NoInternetConnectionAlert noInternetConnectionAlert = new NoInternetConnectionAlert(ProfileVerification.this);
                        noInternetConnectionAlert.DisplayNoInternetConnection();
                        dismiss();
                    }
                }).execute();
                break;
            case R.id.pv_upload_driving_license_fb:
                new CheckNetworkConnection(ProfileVerification.this, new CheckNetworkConnection.OnConnectionCallback() {
                    @Override
                    public void onConnectionSuccess() {
                        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container_profile_verification, new UploadDrivingLicense()).addToBackStack("upload_license").commit();
                        dismiss();
                    }
                    @Override
                    public void onConnectionFail(String msg) {
                        NoInternetConnectionAlert noInternetConnectionAlert = new NoInternetConnectionAlert(ProfileVerification.this);
                        noInternetConnectionAlert.DisplayNoInternetConnection();
                        dismiss();
                    }
                }).execute();
                break;
            case R.id.pv_upload_live_photo_fb:
                new CheckNetworkConnection(ProfileVerification.this, new CheckNetworkConnection.OnConnectionCallback() {
                    @Override
                    public void onConnectionSuccess() {
                        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container_profile_verification, new UploadLivePhoto()).addToBackStack("live_photo").commit();
                        dismiss();
                    }
                    @Override
                    public void onConnectionFail(String msg) {
                        NoInternetConnectionAlert noInternetConnectionAlert = new NoInternetConnectionAlert(ProfileVerification.this);
                        noInternetConnectionAlert.DisplayNoInternetConnection();
                        dismiss();
                    }
                }).execute();
                break;

        }

    }
//    onclick

    private void show(){
        loading.setVisibility(View.VISIBLE);
    }

    private void dismiss(){
        loading.setVisibility(View.GONE);
    }

//    end
}
