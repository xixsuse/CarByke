package com.carbyke.carbyke;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
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

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.squareup.picasso.Picasso;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {


    SharedPreferences sharedPreferencesAnonymousAuth;
    private static final String PREF_AU = "pref_au";
    private static final String STATUS = "status";

    private ImageView background_iv;
    private ImageButton car_ib, bike_ib;

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

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        car_ib.setOnClickListener(this);
        bike_ib.setOnClickListener(this);
    }

//    logging in
    private void AnonymousAuth() {
        //        anonymously logging in for security and better user experience
        FirebaseAnonymousAuth firebaseAnonymousAuth = new FirebaseAnonymousAuth(Home.this);
        firebaseAnonymousAuth.Auth();
    }
    //    logging in


    //    on start
    public void onStart(){
        super.onStart();
        sharedPreferencesAnonymousAuth = getSharedPreferences(PREF_AU, MODE_PRIVATE);
        if (!TextUtils.equals(sharedPreferencesAnonymousAuth.getString(STATUS, ""), "success")){
            AnonymousAuth();
        }

    }
//    on Start




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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
            case R.id.ch_car_ib:
                startActivity(new Intent(Home.this, SearchCar.class));
                break;
            case R.id.ch_bike_ib:
                startActivity(new Intent(Home.this, SearchBike.class));
                break;
        }

    }
//    onclick


    //end
}
