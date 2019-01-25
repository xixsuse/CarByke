package com.carbyke.carbyke;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {


    private SharedPreferences sharedPreferencesLogin;
    private final static String LOGIN = "login";
    private final static String LOGGED_IN_OR_NOT = "logged_in";

    private ImageView background_iv;
    CircleImageView profile_image_iv;
    private ImageButton car_ib, bike_ib;
    private TextView login_sign_tv, name_tv, email_tv, phone_tv;
    private RelativeLayout not_signed_in_rl, signed_in_rl;

    private final static String USER_PROFILES ="user_profiles";
    private final static String PROFILE = "profile";
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(USER_PROFILES);
    private final String PHONE_NUMBER = "phone_number";
    private final String EMAIL = "email";
    private final String NAME = "name";
    private final String PROFILE_IMAGE_URL = "profile_image_url";
    private FirebaseAuth mAuth;

    private SharedPreferences sharedPreferences;
    private final static String PROFILE_DATA = "profile_data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        background_iv = findViewById(R.id.ch_background_iv);
        car_ib = findViewById(R.id.ch_car_ib);
        bike_ib = findViewById(R.id.ch_bike_ib);


        Toolbar toolbar =  findViewById(R.id.toolbar);
        toolbar.setTitle(""); // setting title to null
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        login_sign_tv = navigationView.getHeaderView(0).findViewById(R.id.hh_login_sign);
        not_signed_in_rl = navigationView.getHeaderView(0).findViewById(R.id.hh_not_sign_in_rl);
        signed_in_rl = navigationView.getHeaderView(0).findViewById(R.id.hh_signed_in_rl);
        profile_image_iv = navigationView.getHeaderView(0).findViewById(R.id.hh_profile_image_iv);
        name_tv = navigationView.getHeaderView(0).findViewById(R.id.hh_name_tv);
        email_tv = navigationView.getHeaderView(0).findViewById(R.id.hh_email_tv);
        phone_tv = navigationView.getHeaderView(0).findViewById(R.id.hh_phone_tv);

//        getting id of nav bar
        mAuth = FirebaseAuth.getInstance();


        car_ib.setOnClickListener(this);
        bike_ib.setOnClickListener(this);
        login_sign_tv.setOnClickListener(this);
        signed_in_rl.setOnClickListener(this);

        new CheckNetworkConnection(Home.this, new CheckNetworkConnection.OnConnectionCallback() {
            @Override
            public void onConnectionSuccess() {

            }
            @Override
            public void onConnectionFail(String msg) {
                NoInternetConnectionAlert noInternetConnectionAlert = new NoInternetConnectionAlert(Home.this);
                noInternetConnectionAlert.DisplayNoInternetConnection();
            }
        }).execute();

    }
    //    on start
    public void onStart(){
        super.onStart();
        MySharedPrefs mySharedPrefs = new MySharedPrefs(Home.this);
        mySharedPrefs.initiateProfileData();;
        checkIfSignedIn();

        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://us-central1-carbyke-a3301.cloudfunctions.net/pickBase?text=-LMzea0bkxgwJROhBVPE";

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                       // Toast.makeText(Home.this, ""+response, Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
             //   Toast.makeText(Home.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                //mTextView.setText("That didn't work!");
            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);

    }
    //    on Start


//    if signed in then display profile data in profile
    private void checkIfSignedIn() {
//        sharedPreferencesLogin = getSharedPreferences(LOGIN, MODE_PRIVATE);
//        String val = sharedPreferencesLogin.getString(LOGGED_IN_OR_NOT, "");
        MySharedPrefs mySharedPrefs = new MySharedPrefs(Home.this);
        String val = mySharedPrefs.getLoggedInOrNot();
        if (TextUtils.equals(val, "true")){
            not_signed_in_rl.setVisibility(View.GONE);
            signed_in_rl.setVisibility(View.VISIBLE);
            getProfileDataFromSharedPref();
           // setUserData();
        }

    }
    //    if signed in then display profile data in profile

//    get user profile saved data from shared pref
    public void getProfileDataFromSharedPref() {
       // sharedPreferences = getSharedPreferences(PROFILE_DATA, MODE_PRIVATE);
        String name, email, phone;
        MySharedPrefs mySharedPrefs = new MySharedPrefs(Home.this);
        mySharedPrefs.initiateProfileData();
        name = mySharedPrefs.getProfileName();
        email = mySharedPrefs.getProfileEmail();
        phone = mySharedPrefs.getProfilePhoneNumber();

//        name = sharedPreferences.getString(NAME, "");
//        email = sharedPreferences.getString(EMAIL, "");
//        phone = sharedPreferences.getString(PHONE_NUMBER, "");

        if (TextUtils.isEmpty(name)){
            name_tv.setVisibility(View.GONE);
        }else {
            name_tv.setText(name);
        }

//        email
        if (!TextUtils.isEmpty(email) && !TextUtils.equals(email,"Link google account!")){
            email_tv.setText(email);
        }
        else {
            email_tv.setVisibility(View.GONE);
        }
//        phone
        if (!TextUtils.isEmpty(phone)  && !TextUtils.equals(phone,"Link phone number!")){
            phone_tv.setText(phone);
        }
        else {
            phone_tv.setVisibility(View.GONE);
        }

        // now load data from database
        setUserDataFromFirebase();
    }
//    get user profile saved data from shared pref



    //    setting user data
    private void setUserDataFromFirebase() {
        mAuth = FirebaseAuth.getInstance();
        String uid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        databaseReference.child(uid).child(PROFILE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String name;
                        String email;
                        String phone;
                        Uri profile_image_url;
                        name = dataSnapshot.child(NAME).getValue(String.class);
                        email = mAuth.getCurrentUser().getEmail();
                        phone = mAuth.getCurrentUser().getPhoneNumber();
                        profile_image_url = mAuth.getCurrentUser().getPhotoUrl();



                        if (TextUtils.isEmpty(name)){
                            name_tv.setVisibility(View.GONE);
                        }else {
                            name_tv.setText(name);
                        }
//        email
                        if (!TextUtils.isEmpty(email)){
                            email_tv.setText(email);
                        }
                        else {
                            email_tv.setVisibility(View.GONE);
                            email = "Link google account!";
                        }
//        phone
                        if (!TextUtils.isEmpty(phone)){
                            phone_tv.setText(phone);
                        }
                        else {
                            phone_tv.setVisibility(View.GONE);
                            phone = "Link phone number!";
                        }

                        Picasso.with(Home.this)
                                .load(profile_image_url)
                                .placeholder(R.drawable.ic_placeholder_profile_pic)
                                .into(profile_image_iv);

                        saveFetchedDataInSharedPrefs(name, email, phone);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
//    setting user data


//    saving data in shared pref
private void saveFetchedDataInSharedPrefs(String name, String email, String phone) {
//    SharedPreferences.Editor editor = sharedPreferences.edit();
//    editor.putString(NAME, name);
//    editor.putString(EMAIL, email);
//    editor.putString(PHONE_NUMBER, phone);
//    editor.apply();
    MySharedPrefs mySharedPrefs = new MySharedPrefs(Home.this);
    mySharedPrefs.setProfileDataN_E_P(name, email, phone);
}
//    saving data in shared pref



//    set background image
    private void setBackGroundImage() {
        Picasso.with(Home.this)
                .load(R.drawable.home_fade_background)
                .fit()
                .into(background_iv);
    }
//    set background image



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

//    onclick
    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id){
//            open search car activity
            case R.id.ch_car_ib:
               startActivity(new Intent(Home.this, SearchCar.class));
                //Log.w("r23", databaseReference.push().getPickUpLocationKey());
                break;
//            open search bike activity
            case R.id.ch_bike_ib:
                startActivity(new Intent(Home.this, SearchBike.class));
                break;
//                sign up or login
            case R.id.hh_login_sign:
               // Bundle animation = ActivityOptions.makeCustomAnimation(Home.this, R.anim.fade_in, R.anim.fade_out).toBundle();
               // startActivity(new Intent(Home.this, Login.class), animation);
                startActivity(new Intent(Home.this, Login.class));
                //  getSupportFragmentManager().beginTransaction().add(R.id.fragment_layout_home, new Login()).addToBackStack("login").commit();
                break;
//                edit profile
            case R.id.hh_signed_in_rl:
                startActivity(new Intent(Home.this, EditProfile.class));
                break;
        }

    }
//    onclick


    //end
}
