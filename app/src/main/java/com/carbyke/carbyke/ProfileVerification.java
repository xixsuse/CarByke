package com.carbyke.carbyke;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import mehdi.sakout.fancybuttons.FancyButton;

public class ProfileVerification extends AppCompatActivity implements View.OnClickListener {

    private FancyButton upload_license_fb, upload_adhar_fb, upload_live_photo_fb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_verification);

        upload_adhar_fb = findViewById(R.id.pv_upload_adhar_fb);
        upload_license_fb = findViewById(R.id.pv_upload_driving_license_fb);
        upload_live_photo_fb = findViewById(R.id.pv_upload_live_photo_fb);

        upload_adhar_fb.setOnClickListener(this);
        upload_license_fb.setOnClickListener(this);
        upload_live_photo_fb.setOnClickListener(this);
    }

//    onclick
    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id){
            case R.id.pv_upload_adhar_fb:

                break;
            case R.id.pv_upload_driving_license_fb:
                getSupportFragmentManager().beginTransaction().add(R.id.fragment_container_profile_verification, new UploadDrivingLicense()).addToBackStack("upload_license").commit();
                break;
            case R.id.pv_upload_live_photo_fb:

                break;

        }

    }
//    onclick


//    end
}
