package com.carbyke.carbyke;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment;
import com.squareup.picasso.Picasso;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import mehdi.sakout.fancybuttons.FancyButton;

public class SearchCar extends AppCompatActivity implements View.OnClickListener {

    private ImageView background_iv;
    private ImageButton back_btn;
    FancyButton search_b;
    private TextView pick_up_time_tv, drop_off_time_tv, calculated_days_or_hours;
    Date date_to_be_set_for_drop_off, date_to_be_set_for_pick_up;
    private TextView set_location_tv, where_tv;

    SharedPreferences sharedPreferencesLocation, sharedPreferencesDateTime;
    private static final String LOCATION = "location";
    private static final String STATION = "station";
    private static final String TYPE = "type";

    private static final String DATE_TIME = "date_time";
    private static final String START_DATE_TIME = "start_date_time";
    private static final String END_DATE_TIME = "end_date_time";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_car);

        background_iv = findViewById(R.id.sc_background_iv);
        back_btn = findViewById(R.id.sc_back_b);
        pick_up_time_tv = findViewById(R.id.sc_pick_up_time_tv);
        drop_off_time_tv = findViewById(R.id.sc_drop_off_time_tv);
        calculated_days_or_hours = findViewById(R.id.sc_calculated_days_tv);
        search_b = findViewById(R.id.sc_search_b);
        set_location_tv = findViewById(R.id.sc_set_location_tv);
        where_tv = findViewById(R.id.sc_where_tv);

        date_to_be_set_for_pick_up = new Date();

        sharedPreferencesLocation = getSharedPreferences(LOCATION, Context.MODE_PRIVATE);

        //setImages();

        back_btn.setOnClickListener(this);
        pick_up_time_tv.setOnClickListener(this);
        drop_off_time_tv.setOnClickListener(this);
        search_b.setOnClickListener(this);
        set_location_tv.setOnClickListener(this);

    }

    public void onStart(){
        super.onStart();
        setLocation();
    }

//    setting location from shared pref
    private void setLocation() {
        String station = sharedPreferencesLocation.getString(STATION, "");
        if (!TextUtils.isEmpty(station)){
            String type = sharedPreferencesLocation.getString(TYPE, "");
            set_location_tv.setBackgroundResource(R.drawable.corner_rectangle_rent_home);
            set_location_tv.setText(station);
            set_location_tv.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
            where_tv.setTextColor(getResources().getColor(R.color.delPickIndicatorColor));
            where_tv.setText(type);
            where_tv.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.drawable.ic_drop_down_arrow),null);
        }
    }
//

    //    setting images
    private void setImages() {
        Picasso.with(SearchCar.this)
                .load(R.drawable.search_car_background_fade)
                .fit()
                .centerCrop()
                .into(background_iv);
    }
    //    setting images


//    saving date and time in shared prefs
    private void saveDateTimeInSharedPrefs(String start_date_time, String end_date_time){
        sharedPreferencesDateTime = getSharedPreferences(DATE_TIME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferencesDateTime.edit();

        start_date_time = start_date_time.substring(start_date_time.length()-20, start_date_time.length()).replaceAll("\n"," ");
        end_date_time = end_date_time.substring(end_date_time.length()-20, end_date_time.length()).replaceAll("\n"," ");

        editor.putString(START_DATE_TIME, start_date_time);
        editor.putString(END_DATE_TIME, end_date_time);
        editor.apply();
    }
//    saving date and time in shared prefs


//    onclick
    @Override
    public void onClick(View view) {

        int id = view.getId();
        switch (id){
//            search button
            case R.id.sc_search_b:

//                check if location is selected
                if (TextUtils.isEmpty(set_location_tv.getText().toString().trim())){
                    Toast.makeText(this, "Select Location", Toast.LENGTH_SHORT).show();
                    return;
                }

                String start_date_time = pick_up_time_tv.getText().toString().trim();
                String end_date_time = drop_off_time_tv.getText().toString().trim();

                // check if both dates are selected
                if (!TextUtils.isEmpty(start_date_time) && !TextUtils.isEmpty(end_date_time)){
                    saveDateTimeInSharedPrefs(start_date_time, end_date_time);
                    startActivity(new Intent(SearchCar.this, SearchedCarList.class));
                }
                else {
                    Toast.makeText(this, "Select date time", Toast.LENGTH_SHORT).show();
                }
                break;

//            set location
            case R.id.sc_set_location_tv:
                startActivity(new Intent(SearchCar.this, ChooseLocation.class));
                break;

//            back button
            case R.id.sc_back_b:
                super.onBackPressed();
                break;

//                pick up date and time
            case R.id.sc_pick_up_time_tv:
//                if (Settings.Global.getInt(getContentResolver(), Settings.Global.AUTO_TIME, 0) != 1){
//                    Toast.makeText(this, "Automatic date time disabled", Toast.LENGTH_SHORT).show();
//                    return;
//                }
                final SwitchDateTimeDialogFragment dateTimeDialogFragment = new SwitchDateTimeDialogFragment();
                dateTimeDialogFragment.setHighlightAMPMSelection(true);
                dateTimeDialogFragment.startAtCalendarView();
                dateTimeDialogFragment.setCancelable(false);
                dateTimeDialogFragment.setMinimumDateTime(new Date());

                //adding one more day
                final Calendar calendar = Calendar.getInstance();
                if (TextUtils.isEmpty(pick_up_time_tv.getText().toString())){ // if date not set then pick next date by default otherwise choose date chosen previously
                    calendar.setTime(date_to_be_set_for_pick_up);
                    calendar.add(Calendar.DATE, 1);
                    date_to_be_set_for_pick_up = calendar.getTime();
                    dateTimeDialogFragment.setDefaultDateTime(date_to_be_set_for_pick_up);
                    dateTimeDialogFragment.setDefaultHourOfDay(12);
                    dateTimeDialogFragment.setDefaultMinute(0);
                }
                else {
                    dateTimeDialogFragment.setDefaultDateTime(date_to_be_set_for_pick_up);
                }
                handlePickUpTime(dateTimeDialogFragment);
                break;
                //pick up date time

            //                drop off date and time
            case R.id.sc_drop_off_time_tv:
                if (TextUtils.isEmpty(pick_up_time_tv.getText().toString())){
                    Toast.makeText(this, "Please Select Pick up time first", Toast.LENGTH_SHORT).show();
                }
                else {
                    handleDropOffTime();
                }
                break;
                //drop off date time
        }
        //switch
    }
    //   onclick


//    pick up time
    private void handlePickUpTime(final SwitchDateTimeDialogFragment dateTimeDialogFragment) {
        dateTimeDialogFragment.setOnButtonClickListener(new SwitchDateTimeDialogFragment.OnButtonClickListener() {
            @SuppressLint({"SetTextI18n", "SimpleDateFormat"})
            @Override
            public void onPositiveButtonClick(Date date) {

                if (checkIfPickTimeIsNotLessThanCurrentTime(date)){
                    pick_up_time_tv.callOnClick();
                    Toast.makeText(SearchCar.this, "Pick time can not be earlier than current time...", Toast.LENGTH_LONG).show();
                    //if (TextUtils.isEmpty(pick_up_time_tv.getText().toString())) date_to_be_set_for_pick_up = new Date();
                    return;
                }

                if (!TextUtils.isEmpty(drop_off_time_tv.getText().toString())){ // if pick time is greater than drop off time then change drop off time accordingly
                    if (checkIfPickTimeIsNotGreaterThanDropOffTime(date)){
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(date);
                        calendar.add(Calendar.DATE, 1);
                        date_to_be_set_for_drop_off = calendar.getTime();

                        SimpleDateFormat day, dateMonthYear, time;
                        day = new SimpleDateFormat("EEEE");
                        dateMonthYear = new SimpleDateFormat("MMM dd yyyy");
                        time = new SimpleDateFormat("hh:mm aaa");
                        drop_off_time_tv.setBackgroundResource(R.drawable.corner_rectangle_rent_home);
                        drop_off_time_tv.setText(day.format(date_to_be_set_for_drop_off)+"\n"+dateMonthYear.format(date_to_be_set_for_drop_off)+"\n"+time.format(date_to_be_set_for_drop_off));

                        Toast.makeText(SearchCar.this, "drop off time updated...", Toast.LENGTH_LONG).show();
                    }
                }


                SimpleDateFormat day, dateMonthYear, time;
                day = new SimpleDateFormat("EEEE");
                dateMonthYear = new SimpleDateFormat("MMM dd yyyy");
                time = new SimpleDateFormat("hh:mm aaa");
                pick_up_time_tv.setBackgroundResource(R.drawable.corner_rectangle_rent_home);
                pick_up_time_tv.setText(day.format(date)+"\n"+dateMonthYear.format(date)+"\n"+time.format(date));
                date_to_be_set_for_pick_up = date;


                //handling drop off time
                drop_off_time_tv.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_time_end),null,null,null);
                drop_off_time_tv.setHintTextColor(getResources().getColor(R.color.black90));
                drop_off_time_tv.setEnabled(true);
                if (TextUtils.isEmpty(drop_off_time_tv.getText().toString())) {
                    date_to_be_set_for_drop_off = date;
                }

                if (!TextUtils.isEmpty(pick_up_time_tv.getText().toString()) && !TextUtils.isEmpty(drop_off_time_tv.getText().toString())){
                    SetCalculatedDaysOrHours();
                }
            } // onPos ends

//            function
            private boolean checkIfPickTimeIsNotLessThanCurrentTime(Date date) {
                return date.compareTo(new Date()) < 0;
            }
// function
            private boolean checkIfPickTimeIsNotGreaterThanDropOffTime(Date date) {
                return date.compareTo(date_to_be_set_for_drop_off) > 0;
            }

            @Override
            public void onNegativeButtonClick(Date date) {
                //date_to_be_set_for_pick_up = new Date();
                // Date is get on negative button click
            }

        });
        dateTimeDialogFragment.show(getSupportFragmentManager(), "dialog_time");
    }
//    pick up time


//    drop off time
    private void handleDropOffTime() {
        final SwitchDateTimeDialogFragment dateTimeDialogFragment = new SwitchDateTimeDialogFragment();
        dateTimeDialogFragment.setHighlightAMPMSelection(true);
        dateTimeDialogFragment.startAtCalendarView();
        dateTimeDialogFragment.setMinimumDateTime(date_to_be_set_for_pick_up);

        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date_to_be_set_for_drop_off);
        if (TextUtils.isEmpty(drop_off_time_tv.getText().toString())) calendar.add(Calendar.DATE, 1);

        Date d = calendar.getTime();

        dateTimeDialogFragment.setDefaultDateTime(d);
        if (TextUtils.isEmpty(drop_off_time_tv.getText().toString())){
            dateTimeDialogFragment.setDefaultHourOfDay(12);
            dateTimeDialogFragment.setDefaultMinute(0);
        }


        dateTimeDialogFragment.setOnButtonClickListener(new SwitchDateTimeDialogFragment.OnButtonClickListener() {
            @SuppressLint({"SetTextI18n", "SimpleDateFormat"})
            @Override
            public void onPositiveButtonClick(Date date) {

                if (checkIfDropOffTimeIsNotLessThanPickUpTime(date)){
                    drop_off_time_tv.callOnClick();
                    Toast.makeText(SearchCar.this, "Drop off time can not be earlier/equal to pick-up time...", Toast.LENGTH_LONG).show();
                    return;
                }

                SimpleDateFormat day, dateMonthYear, time;
                day = new SimpleDateFormat("EEEE");
                dateMonthYear = new SimpleDateFormat("MMM dd yyyy");
                time = new SimpleDateFormat("hh:mm aaa");
                drop_off_time_tv.setBackgroundResource(R.drawable.corner_rectangle_rent_home);
                drop_off_time_tv.setText(day.format(date)+"\n"+dateMonthYear.format(date)+"\n"+time.format(date));

                date_to_be_set_for_drop_off = date; // saving previous selected date
                if (!TextUtils.isEmpty(pick_up_time_tv.getText().toString()) && !TextUtils.isEmpty(drop_off_time_tv.getText().toString())){
                    SetCalculatedDaysOrHours();
                }

            }

            private boolean checkIfDropOffTimeIsNotLessThanPickUpTime(Date date) {
                return date.compareTo(date_to_be_set_for_pick_up) < 0 || date.compareTo(date_to_be_set_for_pick_up) == 0;
            }


            @Override
            public void onNegativeButtonClick(Date date) {
                // Date is get on negative button click
            }
        });
        dateTimeDialogFragment.show(getSupportFragmentManager(), "dialog_time");
    }
//    drop off time

    @SuppressLint("SetTextI18n")
    private void SetCalculatedDaysOrHours(){
        try {
             int time;
             String days, hours, minutes;
             time = (int) TimeUnit.MINUTES.convert(date_to_be_set_for_drop_off.getTime() - date_to_be_set_for_pick_up.getTime(), TimeUnit.MILLISECONDS);
             days = String.valueOf(time/(24*60));
             hours = String.valueOf((time%(24*60)) / 60);
             minutes = String.valueOf((time%(24*60)) % 60);

             calculated_days_or_hours.setVisibility(View.VISIBLE);
             calculated_days_or_hours.setText("Note: Car will be rented for "+days+" days, "+ hours+" hours and "+minutes+" minutes.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    end
}
