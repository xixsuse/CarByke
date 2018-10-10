package com.carbyke.carbyke;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.auth.api.Auth;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Objects;

import co.ceryle.radiorealbutton.RadioRealButtonGroup;
import de.hdodenhof.circleimageview.CircleImageView;
import mehdi.sakout.fancybuttons.FancyButton;

public class EditProfile extends AppCompatActivity implements View.OnClickListener {

    private CircleImageView profile_image_iv;
    private TextView name_tv, email_tv, phone_tv;
    RadioRealButtonGroup gender_radio;

    private final static String USER_PROFILES = "user_profiles";
    private final static String EMAIL = "email";
    private final static String NAME = "name";
    private final static String PHONE_NUMBER = "phone_number";
    private final static String GENDER = "gender";

    private ImageButton back_ib, logout_ib;

    private FancyButton update_fb;

    private SharedPreferences sharedPreferences;
    private final static String PROFILE_DATA = "profile_data";

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(USER_PROFILES);
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        profile_image_iv = findViewById(R.id.ep_profile_image_iv);
        name_tv = findViewById(R.id.ep_name_tv);
        email_tv = findViewById(R.id.ep_email_tv);
        phone_tv = findViewById(R.id.ep_phone_tv);
        back_ib = findViewById(R.id.ep_back_ib);
        logout_ib = findViewById(R.id.ep_logout_ib);
        gender_radio = findViewById(R.id.ep_radio_gender);
        update_fb = findViewById(R.id.ep_save_b);

        sharedPreferences = getSharedPreferences(PROFILE_DATA, MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();

        back_ib.setOnClickListener(this);
        logout_ib.setOnClickListener(this);
        update_fb.setOnClickListener(this);


    }


    //    on start
    public void onStart(){
        super.onStart();
        getProfileDataFromSharedPref();
    }


    public void getProfileDataFromSharedPref() {
        String name, email, phone, gender;
        name = sharedPreferences.getString(NAME, "");
        email = sharedPreferences.getString(EMAIL, "");
        phone = sharedPreferences.getString(PHONE_NUMBER, "");
        gender = sharedPreferences.getString(GENDER, "");

        name_tv.setText(name);
        if (!TextUtils.equals(email, "link google account!")){
            email_tv.setText(email);
            email_tv.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_ep_email),null,getResources().getDrawable(R.drawable.ic_locked),null);
        }
        else{
            email_tv.setHint("link google account!");
        }

        if (!TextUtils.equals(phone, "link phone number!")){
            phone_tv.setText(phone);
            phone_tv.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_ep_phone),null,getResources().getDrawable(R.drawable.ic_verified),null);
        }
        else{
            phone_tv.setHint("link phone number!");
        }

        if (TextUtils.equals(gender,"male")){
            gender_radio.setPosition(0);
        }
        else if (TextUtils.equals(gender,"female")){
            gender_radio.setPosition(1);
        }
        else gender_radio.setPosition(0);

        // now load data from database
        setData();

    }


//    setting user data
    private void setData() {
        String uid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        databaseReference.child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String name, email, phone, gender;
                        Uri profile_image_url;
                        name = dataSnapshot.child(NAME).getValue(String.class);
                        email = mAuth.getCurrentUser().getEmail();
                        phone = mAuth.getCurrentUser().getPhoneNumber();
                        gender = dataSnapshot.child(GENDER).getValue(String.class);
                        profile_image_url = mAuth.getCurrentUser().getPhotoUrl();

                        name_tv.setText(name);
                        if (!TextUtils.isEmpty(email)){
                            email_tv.setText(email);
                            email_tv.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_ep_email),null,getResources().getDrawable(R.drawable.ic_locked),null);
                            }
                        else{
                            email_tv.setHint("link google account!");
                            email = "link google account!";
                            email_tv.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_ep_email),null,null,null);

                        }

                        if (!TextUtils.isEmpty(phone)){
                            phone_tv.setText(phone);
                            phone_tv.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_ep_phone),null,getResources().getDrawable(R.drawable.ic_verified),null);
                        }
                        else{
                            phone_tv.setHint("link phone number!");
                            phone_tv.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_ep_phone),null,null,null);
                            phone = "link phone number!";
                        }

                        if (TextUtils.equals(gender,"male")){
                            gender_radio.setPosition(0);
                        }
                        else if (TextUtils.equals(gender,"female")){
                            gender_radio.setPosition(1);
                        }
                        else {
                            gender_radio.setPosition(0);
                        }

                        Picasso.with(EditProfile.this)
                                .load(profile_image_url)
                                .placeholder(R.drawable.ic_placeholder_profile_pic)
                                .into(profile_image_iv);

                        saveFetchedDataInSharedPrefs(name, email, phone, gender);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
    //    setting user data

//    saving data in shared pref
    private void saveFetchedDataInSharedPrefs(String name, String email, String phone, String gender) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(NAME, name);
        editor.putString(EMAIL, email);
        editor.putString(PHONE_NUMBER, phone);
        editor.putString(GENDER, gender);
        editor.apply();
    }
    //    saving data in shared pref

    //    saving data in shared pref
    private void saveFetchedDataInSharedPrefsAfterUpdate(String name, String gender) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(NAME, name);
        editor.putString(GENDER, gender);
        editor.apply();
    }
    //    saving data in shared pref

//    on click
    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id){
//            back button
            case R.id.ep_back_ib:
                EditProfile.this.finish();
                break;
//                update profile
            case R.id.ep_save_b:
                String name, gender = null;
                name = name_tv.getText().toString().trim();
                int position = gender_radio.getPosition();

                if (position == 0) gender = "male";
                else if (position == 1) gender = "female";

                final String uid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                databaseReference.child(uid).child(GENDER).setValue(gender);
                databaseReference.child(uid).child(NAME).setValue(name);
                saveFetchedDataInSharedPrefsAfterUpdate(name, gender);
                Toast.makeText(this, "profile updated", Toast.LENGTH_SHORT).show();
                break;

//                logout
            case R.id.ep_logout_ib:
                new MaterialDialog.Builder(this)
                        .title("Logout")
                        .titleColor(Color.BLACK)
                        .content("Are You Sure to Logout?")
                        .contentColor(Color.BLACK)
                        .positiveText("Yes")
                        .positiveColor(getResources().getColor(R.color.red))
                        .negativeText("No")
                        .icon(getResources().getDrawable(R.drawable.ic_logout_black))
                        .backgroundColor(getResources().getColor(R.color.white))
                        .negativeColor(getResources().getColor(R.color.lightGreen))
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull final MaterialDialog dialog, @NonNull DialogAction which) {

                                SharedPreferences sharedPreferencesLogin = getSharedPreferences("login", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferencesLogin.edit();
                                editor.putString("logged_in", "false");
                                editor.apply();
                                mAuth = FirebaseAuth.getInstance();
                                mAuth.signOut();


                               // EditProfile.this.finish();
                               // finishAffinity();
                               // startActivity(new Intent(EditProfile.this, Home.class));

                                // TODO
                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                // TODO
                            }
                        }) .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        // TODO
                    }
                })
                        .show();
                break;

        }//switch
    }
//    on click


//    end
}
