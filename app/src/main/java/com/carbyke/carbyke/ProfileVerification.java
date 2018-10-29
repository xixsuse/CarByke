package com.carbyke.carbyke;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.eralp.circleprogressview.CircleProgressView;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.tasks.OnFailureListener;
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
    private Button submit_b;
    private SpinKitView loading;
    private CircleProgressView profile_verification_cpv;
    private ImageButton back_ib;
    private TextView incomplete_tv, verification_failed_tv;
    private ImageView driving_license_iv, upload_adhar_iv, live_photo__iv;

    private final static String USER_VERIFICATIONS = "user_verifications";
    private final static String DRIVING_LICENSE = "driving_license";
    private final static String FACE_IMAGE = "face_image";
    private final static String AADHAR_IMAGE = "aadhar_image";
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(USER_VERIFICATIONS);
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    int count1 = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_verification);

        upload_adhar_fb = findViewById(R.id.pv_upload_adhar_fb);
        upload_license_fb = findViewById(R.id.pv_upload_driving_license_fb);
        upload_live_photo_fb = findViewById(R.id.pv_upload_live_photo_fb);
        submit_b = findViewById(R.id.pv_submit_b);
        back_ib = findViewById(R.id.pv_back_ib);
        loading = findViewById(R.id.pv_spin_kit);
        incomplete_tv = findViewById(R.id.pv_incomplete_tv);
        driving_license_iv = findViewById(R.id.pv_driving_license_iv);
        upload_adhar_iv = findViewById(R.id.pv_upload_adhar_iv);
        live_photo__iv = findViewById(R.id.pv_live_photo__iv);
        verification_failed_tv = findViewById(R.id.pv_verification_failed_tv);


        profile_verification_cpv = findViewById(R.id.pv_profile_verification_cpv);
        profile_verification_cpv.setInterpolator(new AccelerateDecelerateInterpolator());

        upload_adhar_fb.setOnClickListener(this);
        upload_license_fb.setOnClickListener(this);
        upload_live_photo_fb.setOnClickListener(this);
        submit_b.setOnClickListener(this);
        back_ib.setOnClickListener(this);
    }

    public void onStart(){
        super.onStart();

        CheckIfUserVerified();
    }

//    checking if user verified
    private void CheckIfUserVerified() {
        final String uid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("verification_status");
        databaseReference.child(uid).child("verification").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        String code = dataSnapshot.child("code").getValue(String.class);
                        if (!TextUtils.isEmpty(code)){
                            final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("verification_status");
                            databaseReference.child(uid).child("approval")
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @SuppressLint("SetTextI18n")
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            count1 = 2;
                                            upload_license_fb.setEnabled(false);
                                            upload_adhar_fb.setEnabled(false);
                                            upload_live_photo_fb.setEnabled(false);
                                            String license = dataSnapshot.child("license").getValue(String.class);
                                            String aadhar = dataSnapshot.child("aadhar").getValue(String.class);
                                            String photo = dataSnapshot.child("photo").getValue(String.class);

                                            driving_license_iv.setVisibility(View.VISIBLE);
                                            upload_adhar_iv.setVisibility(View.VISIBLE);
                                            live_photo__iv.setVisibility(View.VISIBLE);

                                            if (TextUtils.equals("verified", license) && !TextUtils.isEmpty(license)) {
                                                driving_license_iv.setBackground(getResources().getDrawable(R.drawable.success));
                                            } else if (TextUtils.equals("not_verified", license) && !TextUtils.isEmpty(license)) {
                                                driving_license_iv.setBackground(getResources().getDrawable(R.drawable.ic_error));
                                                upload_license_fb.setEnabled(true);
                                            }

                                            if (TextUtils.equals("verified", aadhar) && !TextUtils.isEmpty(aadhar)) {
                                                upload_adhar_iv.setBackground(getResources().getDrawable(R.drawable.success));
                                            } else if (TextUtils.equals("not_verified", aadhar) && !TextUtils.isEmpty(aadhar)) {
                                                upload_adhar_iv.setBackground(getResources().getDrawable(R.drawable.ic_error));
                                                upload_adhar_fb.setEnabled(true);
                                            }

                                            if (TextUtils.equals("verified", photo) && !TextUtils.isEmpty(photo)) {
                                                live_photo__iv.setBackground(getResources().getDrawable(R.drawable.success));
                                            } else if (TextUtils.equals("not_verified", photo) && !TextUtils.isEmpty(photo)) {
                                                live_photo__iv.setBackground(getResources().getDrawable(R.drawable.ic_error));
                                                upload_live_photo_fb.setEnabled(true);
                                            }
                                            String message = dataSnapshot.child("message").getValue(String.class);
                                            if (!TextUtils.isEmpty(message)){
                                                verification_failed_tv.setText("Verification Failed: \n"+message+"\n\nNote: No need to re-submit profile for verification. Just re-upload the disapproved image");
                                            }


                                            checkPercentageComplete();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                        } //if
                        else {
                            checkPercentageComplete();
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(ProfileVerification.this, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


    }
//    checking if user verified

    //    check % profile completion
    private void checkPercentageComplete() {
        final String uid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        databaseReference.child(uid).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = 0;
                if (dataSnapshot.hasChild(DRIVING_LICENSE)) {
                    count++;
                    upload_license_fb.setText("uploaded");
                } else{
                    upload_license_fb.setText("upload");
                }

                if (dataSnapshot.hasChild(AADHAR_IMAGE)){
                    count++;
                    upload_adhar_fb.setText("uploaded");
                } else{
                    upload_adhar_fb.setText("upload");
                }

                if (dataSnapshot.hasChild(FACE_IMAGE)){
                    count++;
                    upload_live_photo_fb.setText("uploaded");
                } else{
                    upload_live_photo_fb.setText("upload");
                }


                submit_b.setEnabled(false);
                submit_b.setBackgroundColor(getResources().getColor(R.color.buttonDisabledColor));
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
                    submit_b.setEnabled(true); // enable
                    submit_b.setBackgroundColor(getResources().getColor(R.color.lightGreen));
                }
                if (count1 == 2){
                    incomplete_tv.setText("Verification Pending");
                    profile_verification_cpv.setProgressWithAnimation(90, 2000);
                    submit_b.setEnabled(false); // enable
                    submit_b.setBackgroundColor(getResources().getColor(R.color.buttonDisabledColor));
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
                        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container_profile_verification, new UploadAadharCard(), "aadhar_card").addToBackStack("aadhar_card").commit();
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
                        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container_profile_verification, new UploadDrivingLicense(), "upload_license").addToBackStack("upload_license").commit();
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
                        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container_profile_verification, new UploadLivePhoto(), "live_photo").addToBackStack("live_photo").commit();
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
//                back button
            case R.id.pv_back_ib:
                ProfileVerification.this.finish();
                break;
            case R.id.pv_submit_b:
                new MaterialDialog.Builder(this)
                        .title("Confirm")
                        .titleColor(Color.BLACK)
                        .content("Are You Sure to Send Your Profile For Verification?")
                        .contentColor(Color.BLACK)
                        .positiveText("Yes")
                        .positiveColor(getResources().getColor(R.color.red))
                        .negativeText("No")
                        .icon(getResources().getDrawable(R.drawable.ic_adhar_card))
                        .backgroundColor(getResources().getColor(R.color.white))
                        .negativeColor(getResources().getColor(R.color.lightGreen))
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull final MaterialDialog dialog, @NonNull DialogAction which) {
                                new CheckNetworkConnection(ProfileVerification.this, new CheckNetworkConnection.OnConnectionCallback() {
                                    @Override
                                    public void onConnectionSuccess() {
                                        submitForVerification();
                                    }
                                    @Override
                                    public void onConnectionFail(String msg) {
                                        NoInternetConnectionAlert noInternetConnectionAlert = new NoInternetConnectionAlert(ProfileVerification.this);
                                        noInternetConnectionAlert.DisplayNoInternetConnection();
                                        dismiss();
                                    }
                                }).execute();
                            }
                        })
                        .show();
                break;

        }

    }
    //    onclick


    private void submitForVerification() {
        final String uid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("verification_status");
        databaseReference.child(uid).child("verification").child("code").setValue("sent");
        dismiss();
        Toast.makeText(this, "Profile Sent For Verification", Toast.LENGTH_SHORT).show();

    }


    private void show(){
        loading.setVisibility(View.VISIBLE);
    }

    private void dismiss(){
        loading.setVisibility(View.GONE);
    }

//    end
}
