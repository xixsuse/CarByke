package com.carbyke.carbyke;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class BookVehicle extends AppCompatActivity {

    private String car_image_url, car_name, number_plate, general_key, number_plate_key, duration;
    private ImageView car_image_iv;
    private TextView car_name_tv, payable_amount_tv, duration_tv, start_date_tv, end_date_tv;
    private String days, hours, minutes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_vehicle);

        car_image_iv = findViewById(R.id.bv_car_image);
        car_name_tv = findViewById(R.id.bv_car_name);
        payable_amount_tv = findViewById(R.id.bv_price);
        duration_tv = findViewById(R.id.bv_duration);
        start_date_tv = findViewById(R.id.bv_start_date_tv);
        end_date_tv = findViewById(R.id.bv_end_date_tv);


        getSetData();

    }

    public void getSetData(){
        MySharedPrefs mySharedPrefs = new MySharedPrefs(BookVehicle.this);
        car_image_url = getIntent().getStringExtra("car_image_url");
        car_name = getIntent().getStringExtra("car_name");
        number_plate = getIntent().getStringExtra("number_plate");
        number_plate_key = getIntent().getStringExtra("number_plate_key");
        general_key = getIntent().getStringExtra("general_vehicle_key");
        duration = mySharedPrefs.getDuration();

        setCatImage();
        setCarNamePriceDuration();
        setStartEndDate();
    }


    private void setStartEndDate() {
        MySharedPrefs mySharedPrefs = new MySharedPrefs(BookVehicle.this);
        start_date_tv.setText(mySharedPrefs.getStartDateTime());
        end_date_tv.setText(mySharedPrefs.getEndDateTime());
    }

    private void setCarNamePriceDuration() {
        car_name_tv.setText(car_name+"\n"+number_plate);
        payable_amount_tv.setText("355");
        SetCalculatedDaysOrHours();

        String d,h,m;
        if (TextUtils.equals(days, "1")) d = "day";
        else d = "day";
        if (TextUtils.equals(hours, "1")) h = "hour";
        else h = "hour";
        if (TextUtils.equals(minutes, "1")) m = "minute";
        else m = "minutes";

        String msg = "";
        if (!TextUtils.isEmpty(days) && !TextUtils.isEmpty(hours) && !TextUtils.isEmpty(minutes)){
            msg = "For "+days+" "+d+" "+hours+" "+h+" "+minutes+" "+m;
        }
        else if (!TextUtils.isEmpty(days) && TextUtils.isEmpty(hours) && TextUtils.isEmpty(minutes)){
            msg = "For "+days+" "+d;
        }
        else if (TextUtils.isEmpty(days) && !TextUtils.isEmpty(hours) && TextUtils.isEmpty(minutes)){
            msg = "For "+hours+" "+h;
        }
        else if (TextUtils.isEmpty(days) && TextUtils.isEmpty(hours) && !TextUtils.isEmpty(minutes)){
            msg = "For "+minutes+" "+m;
        }
        else if (!TextUtils.isEmpty(days) && !TextUtils.isEmpty(hours) && TextUtils.isEmpty(minutes)){
            msg = "For "+days+" "+d+" "+hours+" "+h;
        }
        else if (TextUtils.isEmpty(days) && !TextUtils.isEmpty(hours) && !TextUtils.isEmpty(minutes)){
            msg = "For "+hours+" "+h+" "+minutes+" "+m;
        }
        duration_tv.setText(msg);
    }

    @SuppressLint("SetTextI18n")
    private void SetCalculatedDaysOrHours(){
        MySharedPrefs mySharedPrefs = new MySharedPrefs(BookVehicle.this);
        try {
            int time;
            Date d1 = new Date(mySharedPrefs.getStartDateTime());
            Date d2 = new Date(mySharedPrefs.getEndDateTime());

            time = (int) TimeUnit.MINUTES.convert(d2.getTime() - d1.getTime(), TimeUnit.MILLISECONDS);
            days = String.valueOf(time/(24*60));
            hours = String.valueOf((time%(24*60)) / 60);
            minutes = String.valueOf((time%(24*60)) % 60);
            if (TextUtils.equals(days, "0")) days = null;
            if (TextUtils.equals(hours, "0")) hours = null;
            if (TextUtils.equals(minutes, "0")) minutes = null;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //    set image
    private void setCatImage() {
        Glide.with(BookVehicle.this)
                .load(car_image_url)
                .into(car_image_iv);
    }
//    set image

}
