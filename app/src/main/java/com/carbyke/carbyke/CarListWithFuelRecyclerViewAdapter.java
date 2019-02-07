package com.carbyke.carbyke;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import mehdi.sakout.fancybuttons.FancyButton;

public class CarListWithFuelRecyclerViewAdapter extends RecyclerView.Adapter<CarListWithFuelRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private List<DataForRecyclerView> UploadInfoList;
    private static final String LOCATION = "location";
    private static final String STATION = "station";
    private static final String TYPE = "type";

    private ViewHolder h;
    private int last_selection = 0;
    private int count1 = 1;

    String payable_amount;


    CarListWithFuelRecyclerViewAdapter(Context context, List<DataForRecyclerView> TempList) {

        this.UploadInfoList = TempList;

        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_car_list_with_fuel, parent, false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final DataForRecyclerView info = UploadInfoList.get(position);

//        sharedPreferencesLocation = Objects.requireNonNull(context.getSharedPreferences(LOCATION, Context.MODE_PRIVATE));
//        String type = sharedPreferencesLocation.getString(TYPE, "");
        final MySharedPrefs mySharedPrefs = new MySharedPrefs(context);
        String type = mySharedPrefs.getDeliveryOrPickUpType();

        if (TextUtils.equals(type, "DELIVERY")){
           holder.distance_tv.setVisibility(View.GONE);
           holder.self_pick_up_place_tv.setVisibility(View.GONE);
        }
        else {
            calculateDistance(mySharedPrefs, holder, info);
            holder.self_pick_up_place_tv.setText(mySharedPrefs.getPickLocationMapLocation());
            holder.self_pick_up_place_tv.setCompoundDrawablesWithIntrinsicBounds(null, null,
                    context.getResources().getDrawable(R.drawable.ic_keyboard_arrow_right), null);
        }

        setData(holder, info);
        setCarImage(holder, info);

        setBackGround(holder, info);

        addSpaceAtLast(position, holder);

        holder.self_pick_up_place_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String station_lat, station_long;

                station_lat = mySharedPrefs.getPickLocationLat();
                station_long = mySharedPrefs.getPickLocationLong();
                if (TextUtils.isEmpty(station_lat) || TextUtils.isEmpty(station_long)){
                    Toast.makeText(context, "unable to fetch location", Toast.LENGTH_SHORT).show();
                    return;
                }
                context.startActivity(new Intent(context, StationMapLocation.class));
            }
        });

        holder.book_fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mySharedPrefs.getSelectedPosition() != holder.getAdapterPosition()){
                    Toast.makeText(context, "Select Price !", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(context, BookVehicle.class);
                intent.putExtra("car_image_url", info.getImage_url());
                intent.putExtra("general_vehicle_key", info.getGeneral_vehicle_key());
                intent.putExtra("number_plate_key", info.getNumber_plate_key());
                intent.putExtra("seater", info.getSeater());
                intent.putExtra("car_name", info.getCar_name());
                intent.putExtra("number_plate", info.getNumber_plate());
                intent.putExtra("latitude", mySharedPrefs.getPickLocationLat());
                intent.putExtra("longitude", mySharedPrefs.getPickLocationLong());
                intent.putExtra("pick_up_location_name", mySharedPrefs.getPickLocationMapLocation());

                context.startActivity(intent);
            }
        });

    }

    @SuppressLint("SetTextI18n")
    private void calculateDistance(MySharedPrefs mySharedPrefs, ViewHolder holder, DataForRecyclerView info){
        String my_lat, my_long, station_lat, station_long;
        my_lat = mySharedPrefs.getUserLatitude();
        my_long = mySharedPrefs.getUserLongitude();
        station_lat = mySharedPrefs.getPickLocationLat();
        station_long = mySharedPrefs.getPickLocationLong();

        try {
            Location my_location = new Location("");
            my_location.setLatitude(Double.parseDouble(my_lat));
            my_location.setLongitude(Double.parseDouble(my_long));

            Location station_location = new Location("");
            station_location.setLatitude(Double.parseDouble(station_lat));
            station_location.setLongitude(Double.parseDouble(station_long));

            float distance = my_location.distanceTo(station_location) / 1000;
            DecimalFormat df = new DecimalFormat("###.#");
            String l = df.format(distance);
            holder.distance_tv.setText(l+" km");

        }
        catch (Exception e){
            holder.distance_tv.setText("...");
            //
        }
    }

    private void addSpaceAtLast(int position, ViewHolder holder) {
        //adding padding to last card view

        if( position == getItemCount() -1){
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0
                    ,(int) context.getResources().getDimension(R.dimen.dp8)
                    , 0
                    , (int) context.getResources().getDimension(R.dimen.dp8));
            holder.cardView.setLayoutParams(params);
        }
    }

    private void setBackGround(final ViewHolder holder, final DataForRecyclerView info) {
        holder.price_1_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.price_1_rl.setBackground(context.getResources().getDrawable(R.drawable.simple_rectangle_green));
                float price = Float.valueOf(info.getWith_fuel_1()) * info.getTrip_cost_multiplier();
                MySharedPrefs mySharedPrefs = new MySharedPrefs(context);
                mySharedPrefs.setSelectedPrice(String.valueOf((int)price), holder.getAdapterPosition(), 1);
                if (h == holder && last_selection == 1){
                    return;
                }
                deSelectPreviousSelection(h);
                h = holder;
                last_selection = 1;
            }
        });

        holder.price_2_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.price_2_rl.setBackground(context.getResources().getDrawable(R.drawable.simple_rectangle_green));
                float price = Float.valueOf(info.getWith_fuel_2()) * info.getTrip_cost_multiplier();
                MySharedPrefs mySharedPrefs = new MySharedPrefs(context);
                mySharedPrefs.setSelectedPrice(String.valueOf((int)price), holder.getAdapterPosition(), 2);
                if (h == holder && last_selection == 2){
                    return;
                }
                deSelectPreviousSelection(h);
                h = holder;
                last_selection = 2;
            }
        });

        holder.price_3_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.price_3_rl.setBackground(context.getResources().getDrawable(R.drawable.simple_rectangle_green));
                float price = Float.valueOf(info.getWith_fuel_3()) * info.getTrip_cost_multiplier();
                MySharedPrefs mySharedPrefs = new MySharedPrefs(context);
                mySharedPrefs.setSelectedPrice(String.valueOf((int)price), holder.getAdapterPosition(), 3);
                if (h == holder && last_selection == 3){
                    return;
                }
                deSelectPreviousSelection(h);
                h = holder;
                last_selection = 3;
            }
        });


    }


    private void deSelectPreviousSelection(ViewHolder h) {
        try {
            if (last_selection == 1){
                h.price_1_rl.setBackground(null);
            }
            else if (last_selection == 2){
                h.price_2_rl.setBackground(null);
            }
            else if (last_selection == 3){
                h.price_3_rl.setBackground(null);
            }
        }
        catch (Exception e){
            //
        }

    }

    //    setting car image
    private void setCarImage(ViewHolder holder, DataForRecyclerView info) {
        Glide.with(context)
                .load(info.getImage_url())
                .into(holder.car_image_iv);
    }
    //    setting car image

    //    setting data
    private void setData(ViewHolder holder, DataForRecyclerView info) {

        holder.car_name_tv.setText(info.getCar_name()+" ("+info.getNumber_plate()+")");
        if (!TextUtils.isEmpty(info.getSeater())){
            holder.seat_tv.setText(String.format("%s Seater", String.valueOf(info.getSeater())));
        }
        holder.price_1_tv.setText(addComma(info.getWith_fuel_1(), info));
        holder.price_2_tv.setText(addComma(info.getWith_fuel_2(), info));
        holder.price_3_tv.setText(addComma(info.getWith_fuel_3(), info));
    }
//    setting data


//    setting price
    private String addComma(String number, DataForRecyclerView info){
        try {
            float multiplier = info.getTrip_cost_multiplier();
            float new_cost = Float.valueOf(number) * multiplier;
            NumberFormat formatter = new DecimalFormat("#,###");
            return formatter.format((int)new_cost);
        }
        catch (Exception e){
            return "N.A";
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return UploadInfoList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView car_name_tv, seat_tv, distance_tv, self_pick_up_place_tv, price_1_tv, km100_dist_tv,
                price_2_tv, km200_dist_tv, price_3_tv, km350_dist_tv;
        private FancyButton book_fb;
        private ImageView car_image_iv;
        private RelativeLayout price_1_rl, price_2_rl, price_3_rl;
        private CardView cardView;

        ViewHolder(View itemView) {
            super(itemView);

            car_name_tv = itemView.findViewById(R.id.rc_car_name_tv);
            seat_tv = itemView.findViewById(R.id.rc_seat_tv);
            distance_tv = itemView.findViewById(R.id.rc_distance_tv);
            self_pick_up_place_tv = itemView.findViewById(R.id.rc_self_pick_up_place_tv);
            price_1_tv = itemView.findViewById(R.id.rc_fuel_1_price_tv);
            km100_dist_tv = itemView.findViewById(R.id.rc_fuel_1_dist_tv);
            price_2_tv = itemView.findViewById(R.id.rc_fuel_2_price_tv);
            km200_dist_tv = itemView.findViewById(R.id.rc_fuel_2_dist_tv);
            price_3_tv = itemView.findViewById(R.id.rc_fuel_3_price_tv);
            km350_dist_tv = itemView.findViewById(R.id.rc_fuel_3_dist_tv);
            book_fb = itemView.findViewById(R.id.rc_book_fb);
            car_image_iv = itemView.findViewById(R.id.rc_car_image_iv);
            price_1_rl = itemView.findViewById(R.id.rc_price_1_rl);
            price_2_rl = itemView.findViewById(R.id.rc_price_2_rl);
            price_3_rl = itemView.findViewById(R.id.rc_price_3_rl);
            cardView = itemView.findViewById(R.id.rc_cardView);
        }
    }
//end
}