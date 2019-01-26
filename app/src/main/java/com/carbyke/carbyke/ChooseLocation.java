package com.carbyke.carbyke;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;

import java.util.Objects;

public class ChooseLocation extends AppCompatActivity implements View.OnClickListener {

    //This is our tab layout
    private TabLayout tabLayout;

    //This is our viewPager
    private ViewPager viewPager;

    private ImageButton back_b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_location);

        //Initializing the tab layout
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        //Adding the tabs using addTab() method
        tabLayout.addTab(tabLayout.newTab().setText("Self Pick-up"));
        tabLayout.addTab(tabLayout.newTab().setText("Delivery"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        //Initializing viewPager
        viewPager = (ViewPager) findViewById(R.id.pager);

        //Creating our pager adapter
        LocationPager adapter = new LocationPager(getSupportFragmentManager(), tabLayout.getTabCount());

        //Adding adapter to pager
        viewPager.setAdapter(adapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

       // setupTabIcons();

        back_b = findViewById(R.id.cl_back_b);

        back_b.setOnClickListener(this);

    }

//    private void setupTabIcons() {
//        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
//        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
//    }


    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
    }


    public void onTabUnselected(TabLayout.Tab tab) {

    }


    public void onTabReselected(TabLayout.Tab tab) {

    }

//    on click
    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.cl_back_b){
            onBackPressed();
        }
    }
//    on click

    public void onBackPressed(){
        MySharedPrefs mySharedPrefs = new MySharedPrefs(ChooseLocation.this);
        String came_from = mySharedPrefs.getCameFromSOrSl();
        if (TextUtils.equals(came_from,"searched_list_car")){
         ChooseLocation.this.finish();
            Intent intent = new Intent(ChooseLocation.this, SearchedCarList.class);
            intent.putExtra("start_date",getIntent().getLongExtra("start_date",0));
            intent.putExtra("duration", getIntent().getStringExtra("duration"));
            startActivity(intent);
        }
        else {
            ChooseLocation.this.finish();
        }
    }

    //ends
}
