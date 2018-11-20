package com.carbyke.carbyke;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class SearchedCarList extends AppCompatActivity implements View.OnClickListener {

    private TextView place_name_tv, start_date_time_tv, end_date_time_tv;

    SharedPreferences sharedPreferencesLocation, sharedPreferencesDateTime;
    private static final String LOCATION = "location";
    private static final String STATION = "station";
    private static final String TYPE = "type";

    private static final String DATE_TIME = "date_time";
    private static final String START_DATE_TIME = "start_date_time";
    private static final String END_DATE_TIME = "end_date_time";

    //This is our tab layout
    private TabLayout tabLayout;

    //This is our viewPager
    private ViewPager viewPager;

    private ImageButton back_b;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searched_car_list);

        place_name_tv = findViewById(R.id.cc_place_name_tv);
        start_date_time_tv = findViewById(R.id.cc_start_date_time_tv);
        end_date_time_tv = findViewById(R.id.cc_end_date_time_tv);
        back_b = findViewById(R.id.cc_back_b);

//        shared prefs to get selection location data
        sharedPreferencesLocation = getSharedPreferences(LOCATION, Context.MODE_PRIVATE);
        sharedPreferencesDateTime = getSharedPreferences(DATE_TIME, Context.MODE_PRIVATE);

        //Initializing the tablayout
        tabLayout = findViewById(R.id.cc_tabLayout);

        //Adding the tabs using addTab() method
        tabLayout.addTab(tabLayout.newTab().setText("With Fuel"));
        tabLayout.addTab(tabLayout.newTab().setText("Without Fuel"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabTextColors(ColorStateList.valueOf(getResources().getColor(R.color.appColor)));

        //Initializing viewPager
        viewPager =  findViewById(R.id.cc_pager);

        //Creating our pager adapter
        SearchedCarListPager adapter = new SearchedCarListPager(getSupportFragmentManager(), tabLayout.getTabCount());

        //Adding adapter to pager
        viewPager.setAdapter(adapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));


        back_b.setOnClickListener(this);
        place_name_tv.setOnClickListener(this);

    }

    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
    }


    public void onTabUnselected(TabLayout.Tab tab) {

    }


    public void onTabReselected(TabLayout.Tab tab) {

    }

//    on start
    protected void onStart(){
        super.onStart();
        setLocationAndDateTime();


    }

    //    setting location from shared pref
    private void setLocationAndDateTime() {
//        String station = sharedPreferencesLocation.getString(STATION, "");
//        String start_date_time = sharedPreferencesDateTime.getString(START_DATE_TIME, "");
//        String end_date_time = sharedPreferencesDateTime.getString(END_DATE_TIME, "");

        MySharedPrefs mySharedPrefs = new MySharedPrefs(SearchedCarList.this);
        String station = mySharedPrefs.getSelectedLocationOrStation();
        String start_date_time = mySharedPrefs.getStartDateTime();
        String end_date_time = mySharedPrefs.getEndDateTime();

        if (!TextUtils.isEmpty(station)){
            place_name_tv.setText(station);
            start_date_time_tv.setText(start_date_time);
            end_date_time_tv.setText(end_date_time);
        }
    }
    // setting location from shared pref


//    on click
    @Override
    public void onClick(View view) {

        int id = view.getId();
//  back button
        if (id == R.id.cc_back_b){
            super.onBackPressed();
        }

        else if (id == R.id.cc_place_name_tv){
            MySharedPrefs mySharedPrefs = new MySharedPrefs(SearchedCarList.this);
            mySharedPrefs.setCameFromSOrSl("searched_list_car");
            SearchedCarList.this.finish();
            startActivity(new Intent(SearchedCarList.this, ChooseLocation.class));
        }

    }
//    on click


//    end
}
