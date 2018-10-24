package com.carbyke.carbyke;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import mehdi.sakout.fancybuttons.FancyButton;

public class CarListWithFuelRecyclerViewAdapter extends RecyclerView.Adapter<CarListWithFuelRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private List<DataForRecyclerView> UploadInfoList;
    SharedPreferences sharedPreferencesLocation;
    private static final String LOCATION = "location";
    private static final String STATION = "station";
    private static final String TYPE = "type";
    private int selectedPos = RecyclerView.NO_POSITION;
    private int selected_pos_for_background;

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

        setData(holder, info);
        setCarImage(holder, info);

        setBackGround(holder, position);

    }

    private void setBackGround(final ViewHolder holder, final int position) {
        holder.price_1_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.price_1_rl.setBackground(context.getResources().getDrawable(R.drawable.simple_rectangle));
                selected_pos_for_background = position;
            }
        });
    }

    //    setting car image
    private void setCarImage(ViewHolder holder, DataForRecyclerView info) {
        Picasso.with(context)
                .load(info.getImage_url())
                .into(holder.car_image_iv);
    }
    //    setting car image

    //    setting data
    private void setData(ViewHolder holder, DataForRecyclerView info) {
        holder.car_name_tv.setText(info.getCar_name());
        if (!TextUtils.isEmpty(info.getSeater())){
            holder.seat_tv.setText(String.format("%s Seater", String.valueOf(info.getSeater())));
        }
        holder.price_1_tv.setText(addComma(info.getWith_fuel_1()));
        holder.price_2_tv.setText(addComma(info.getWith_fuel_2()));
        holder.price_3_tv.setText(addComma(info.getWith_fuel_3()));
    }
//    setting data


    private String addComma(String number){
        try {
            NumberFormat formatter = new DecimalFormat("#,###");
            return formatter.format(Integer.parseInt(number));
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
        private RelativeLayout price_1_rl;

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
        }
    }
//end
}