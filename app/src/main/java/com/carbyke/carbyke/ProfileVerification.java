package com.carbyke.carbyke;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.github.ybq.android.spinkit.SpinKitView;

import mehdi.sakout.fancybuttons.FancyButton;

public class ProfileVerification extends AppCompatActivity implements View.OnClickListener {

    private FancyButton upload_license_fb, upload_adhar_fb, upload_live_photo_fb;
    private SpinKitView loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_verification);

        upload_adhar_fb = findViewById(R.id.pv_upload_adhar_fb);
        upload_license_fb = findViewById(R.id.pv_upload_driving_license_fb);
        upload_live_photo_fb = findViewById(R.id.pv_upload_live_photo_fb);
        loading = findViewById(R.id.pv_spin_kit);

        upload_adhar_fb.setOnClickListener(this);
        upload_license_fb.setOnClickListener(this);
        upload_live_photo_fb.setOnClickListener(this);
    }

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
