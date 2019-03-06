package com.carbyke.carbyke;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MyCarTripsRecyclerViewAdapter extends RecyclerView.Adapter<MyCarTripsRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private List<DataForRecyclerView> UploadInfoList;
    private ProgressDialog progressDialog;

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("vehicle_details").child("cars");

    private DatabaseReference databaseReferenceBOOKINGRecord = FirebaseDatabase.getInstance().getReference()
            .child("record_of_car_bookings");

    MyCarTripsRecyclerViewAdapter(Context context, List<DataForRecyclerView> TempList) {

        this.UploadInfoList = TempList;

        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_my_car_trips, parent, false);

        return new ViewHolder(view);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final DataForRecyclerView info = UploadInfoList.get(position);

        holder.car_name_tv.setText(info.getCar_name());
        holder.number_plate_tv.setText(info.getNumber_plate());
        holder.price_tv.setText(""+addComma(info.getPayable_amount())+"/-");
        try{
            holder.start_date_tv.setText(info.getStart_date().substring(info.getStart_date().length() - 20));
        } catch (Exception e){
            holder.start_date_tv.setText(info.getStart_date());
        }
        try{
            holder.end_date_tv.setText(info.getEnd_date().substring(info.getEnd_date().length() - 20));
        } catch (Exception e){
            holder.end_date_tv.setText(info.getEnd_date());
        }

        if (TextUtils.equals(info.getTrip_status(), "BOOKED")){
            holder.status_tv.setText("STATUS: "+info.getTrip_status());
            holder.status_tv.setTextColor(context.getResources().getColor(R.color.googleYellow));
            holder.relativeLayout.setBackgroundColor(context.getResources().getColor(R.color.googleYellow));
            setCalculatedDaysOrHoursBooked(info.getStart_date(), holder);
        } else if (TextUtils.equals(info.getTrip_status(), "ACTIVE")){
            holder.status_tv.setText("STATUS: "+info.getTrip_status());
            holder.status_tv.setTextColor(context.getResources().getColor(R.color.lightGreen));
            holder.relativeLayout.setBackgroundColor(context.getResources().getColor(R.color.lightGreen));
            setCalculatedDaysOrHoursActive(info.getEnd_date(), holder);
        }  else if (TextUtils.equals(info.getTrip_status(), "COMPLETED")){
            holder.status_tv.setText("STATUS: "+info.getTrip_status());
            holder.status_tv.setTextColor(context.getResources().getColor(R.color.blue_btn_bg_pressed_color));
            holder.relativeLayout.setBackgroundColor(context.getResources().getColor(R.color.blue_btn_bg_pressed_color));
        } else if (TextUtils.equals(info.getTrip_status(), "CANCELLED")){
            holder.status_tv.setText("STATUS: "+info.getTrip_status());
            holder.status_tv.setTextColor(context.getResources().getColor(R.color.lightCoral));
            holder.relativeLayout.setBackgroundColor(context.getResources().getColor(R.color.lightCoral));
        }


        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("Please Wait...");

                progressDialog.show();
                databaseReferenceBOOKINGRecord.child(info.getBooking_key())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Intent intent = new Intent(context, BookingVehicleDetails.class);
                                intent.putExtra("basic_price", dataSnapshot.child("basic_price").getValue(String.class));
                                intent.putExtra("booking_date_time", dataSnapshot.child("booking_date_time").getValue(String.class));
                                intent.putExtra("booking_day_hour_minutes", dataSnapshot.child("booking_day_hour_minutes").getValue(String.class));
                                intent.putExtra("car_name", dataSnapshot.child("car_name").getValue(String.class));
                                intent.putExtra("end_date", dataSnapshot.child("end_date").getValue(String.class));
                                intent.putExtra("general_vehicle_key", info.getGeneral_vehicle_key());
                                intent.putExtra("method_of_car_picking", dataSnapshot.child("method_of_car_picking").getValue(String.class));
                                intent.putExtra("number_plate", dataSnapshot.child("number_plate").getValue(String.class));
                                intent.putExtra("number_plate_key", info.getNumber_plate_key());
                                intent.putExtra("price_package", dataSnapshot.child("price_package").getValue(Long.class));
                                intent.putExtra("total_payable_amount", dataSnapshot.child("total_payable_amount").getValue(Long.class));
                                intent.putExtra("start_date", dataSnapshot.child("start_date").getValue(String.class));
                                intent.putExtra("station_key", dataSnapshot.child("station_key").getValue(String.class));
                                intent.putExtra("user_UID", dataSnapshot.child("user_UID").getValue(String.class));
                                intent.putExtra("latitude", dataSnapshot.child("latitude").getValue(String.class));
                                intent.putExtra("longitude", dataSnapshot.child("longitude").getValue(String.class));
                                intent.putExtra("station_name", dataSnapshot.child("station_name").getValue(String.class));
                                intent.putExtra("starting_kilometers", dataSnapshot.child("starting_kilometers").getValue(String.class));
                                intent.putExtra("ending_kilometers", dataSnapshot.child("ending_kilometers").getValue(String.class));
                                intent.putExtra("insurance", dataSnapshot.child("insurance").getValue(Boolean.class));
                                intent.putExtra("pollution", dataSnapshot.child("pollution").getValue(Boolean.class));
                                intent.putExtra("rc", dataSnapshot.child("rc").getValue(Boolean.class));
                                intent.putExtra("knee", dataSnapshot.child("knee").getValue(Boolean.class));
                                intent.putExtra("booking_key", dataSnapshot.getKey());

                                if (TextUtils.equals(info.getTrip_status(), "BOOKED")){
                                    intent.putExtra("booking_status", "BOOKED");
                                } else if (TextUtils.equals(info.getTrip_status(), "ACTIVE")){
                                    intent.putExtra("booking_status", "ACTIVE");
                                } else if (TextUtils.equals(info.getTrip_status(), "COMPLETED")){
                                    intent.putExtra("booking_status", "COMPLETED");
                                }else if (TextUtils.equals(info.getTrip_status(), "CANCELLED")){
                                    intent.putExtra("booking_status", "CANCELLED");
                                }

                                progressDialog.dismiss();
                                context.startActivity(intent);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                progressDialog.dismiss();
                            }
                        });
            }
        });


        setCarImage(holder, info);
        addSpaceAtLast(position, holder);

    }


    private void setCalculatedDaysOrHoursBooked(String date, ViewHolder holder){

        try {

            Date d1 = Calendar.getInstance().getTime();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("EEEE MMM dd yyyy hh:mm aaa");
            Date d2 = sdf.parse(date);

            int time;

            time = (int) TimeUnit.MINUTES.convert(d2.getTime() - d1.getTime(), TimeUnit.MILLISECONDS);
            String days = String.valueOf(time/(24*60));
            String hours = String.valueOf((time%(24*60)) / 60);
            String minutes = String.valueOf((time%(24*60)) % 60);
            if (TextUtils.equals(days, "0")) days = null;
            if (TextUtils.equals(hours, "0")) hours = null;
            if (TextUtils.equals(minutes, "0")) minutes = null;

            holder.starts_in_tv.setVisibility(View.VISIBLE);
            setTimeLeftFrTripBooked(days, hours, minutes, holder, date);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    private void setTimeLeftFrTripBooked(String days, String hours, String minutes, ViewHolder holder, String date) {

        String d,h,m;
        if (TextUtils.equals(days, "1")) d = "DAY";
        else d = "DAYS";
        if (TextUtils.equals(hours, "1")) h = "HOUR";
        else h = "HOURS";
        if (TextUtils.equals(minutes, "1")) m = "MINUTE";
        else m = "MINUTES";

        String msg = "";
        if (!TextUtils.isEmpty(days) && !TextUtils.isEmpty(hours) && !TextUtils.isEmpty(minutes)){
            msg = "Trip Starts in "+days+" "+d+", "+hours+" "+h+" and "+minutes+" "+m;
            holder.starts_in_tv.setText(msg+"\n("+date+")");
            if (Integer.parseInt(days) <= 0 || Integer.parseInt(hours) <=0 || Integer.parseInt(minutes) <=0){
                msg = "You are already late for trip by "+Integer.parseInt(days)*-1+" days, "+Integer.parseInt(hours)*-1
                        +"hours and "+Integer.parseInt(minutes)*-1+" minutes.\nTrip should have started by ";
                holder.starts_in_tv.setText(msg+""+date+".");
                holder.starts_in_tv.setTextColor(context.getResources().getColor(R.color.red));            }
        }
        else if (!TextUtils.isEmpty(days) && TextUtils.isEmpty(hours) && TextUtils.isEmpty(minutes)){
            msg = "Trip Starts in "+days+" "+d;
            holder.starts_in_tv.setText(msg+"\n("+date+")");
            if (Integer.parseInt(days) <= 0){
                msg = "You are already late for trip by "+Integer.parseInt(days)*-1+" days "+".\nTrip should have started by ";
                holder.starts_in_tv.setText(msg+""+date+".");
                holder.starts_in_tv.setTextColor(context.getResources().getColor(R.color.red));            }
        }
        else if (TextUtils.isEmpty(days) && !TextUtils.isEmpty(hours) && TextUtils.isEmpty(minutes)){
            msg = "Trip Starts in "+hours+" "+h;
            holder.starts_in_tv.setText(msg+"\n("+date+")");
            if (Integer.parseInt(hours) <= 0){
                msg = "You are already late for trip by "+Integer.parseInt(hours)*-1+" hours "+".\nTrip should have started by ";
                holder.starts_in_tv.setText(msg+""+date+".");
                holder.starts_in_tv.setTextColor(context.getResources().getColor(R.color.red));            }
        }
        else if (TextUtils.isEmpty(days) && TextUtils.isEmpty(hours) && !TextUtils.isEmpty(minutes)){
            msg = "Trip Starts in "+minutes+" "+m;
            holder.starts_in_tv.setText(msg+"\n("+date+")");
            if (Integer.parseInt(minutes) <= 0){
                msg = "You are already late for trip by "+Integer.parseInt(minutes)*-1+" minutes "+".\nTrip should have started by ";
                holder.starts_in_tv.setText(msg+""+date+".");
                holder.starts_in_tv.setTextColor(context.getResources().getColor(R.color.red));            }
        }
        else if (!TextUtils.isEmpty(days) && !TextUtils.isEmpty(hours) && TextUtils.isEmpty(minutes)){
            msg = "Trip Starts in "+days+" "+d+" and "+hours+" "+h;
            holder.starts_in_tv.setText(msg+"\n("+date+")");
            if (Integer.parseInt(days) <=0 || Integer.parseInt(hours) <=0){
                msg = "You are already late for trip by "+Integer.parseInt(days)*-1+" days and "+Integer.parseInt(hours)*-1+" hours "+".\nTrip should have started by ";
                holder.starts_in_tv.setText(msg+""+date+".");
                holder.starts_in_tv.setTextColor(context.getResources().getColor(R.color.red));            }
        }
        else if (TextUtils.isEmpty(days) && !TextUtils.isEmpty(hours) && !TextUtils.isEmpty(minutes)){
            msg = "Trip Starts in "+hours+" "+h+" and "+minutes+" "+m;
            holder.starts_in_tv.setText(msg+"\n("+date+")");
            if (Integer.parseInt(hours) <=0 || Integer.parseInt(minutes) <=0){
                msg = "You are already late for trip by "+Integer.parseInt(hours)*-1+" hours and "+Integer.parseInt(minutes)*-1+" minutes "+".\nTrip should have started by ";
                holder.starts_in_tv.setText(msg+""+date+".");
                holder.starts_in_tv.setTextColor(context.getResources().getColor(R.color.red));
            }
        }
    }




    private void setCalculatedDaysOrHoursActive(String date, ViewHolder holder){

        try {

            Date d1 = Calendar.getInstance().getTime();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("EEEE MMM dd yyyy hh:mm aaa");
            Date d2 = sdf.parse(date);

            int time;

            time = (int) TimeUnit.MINUTES.convert(d2.getTime() - d1.getTime(), TimeUnit.MILLISECONDS);
            String days = String.valueOf(time/(24*60));
            String hours = String.valueOf((time%(24*60)) / 60);
            String minutes = String.valueOf((time%(24*60)) % 60);
            if (TextUtils.equals(days, "0")) days = null;
            if (TextUtils.equals(hours, "0")) hours = null;
            if (TextUtils.equals(minutes, "0")) minutes = null;

            holder.starts_in_tv.setVisibility(View.VISIBLE);
            setTimeLeftFrTripActive(days, hours, minutes, holder, date);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    private void setTimeLeftFrTripActive(String days, String hours, String minutes, ViewHolder holder, String date) {

        String d,h,m;
        if (TextUtils.equals(days, "1")) d = "DAY";
        else d = "DAYS";
        if (TextUtils.equals(hours, "1")) h = "HOUR";
        else h = "HOURS";
        if (TextUtils.equals(minutes, "1")) m = "MINUTE";
        else m = "MINUTES";

        String placeholder = "Trip ends in ";
        String msg = "";
        if (!TextUtils.isEmpty(days) && !TextUtils.isEmpty(hours) && !TextUtils.isEmpty(minutes)){
            msg = placeholder+days+" "+d+", "+hours+" "+h+" and "+minutes+" "+m;
            holder.starts_in_tv.setText(msg+"\n("+date+")");
            if (Integer.parseInt(days) <= 0 || Integer.parseInt(hours) <=0 || Integer.parseInt(minutes) <=0){
                msg = "Trip is ahead end trip time by "+Integer.parseInt(days)*-1+" days, "+Integer.parseInt(hours)*-1
                        +"hours and "+Integer.parseInt(minutes)*-1+" minutes.\nTrip should have ended by ";
                holder.starts_in_tv.setText(msg+""+date+".");
                holder.starts_in_tv.setTextColor(context.getResources().getColor(R.color.red));            }
        }
        else if (!TextUtils.isEmpty(days) && TextUtils.isEmpty(hours) && TextUtils.isEmpty(minutes)){
            msg = placeholder+days+" "+d;
            holder.starts_in_tv.setText(msg+"\n("+date+")");
            if (Integer.parseInt(days) <= 0){
                msg = "Trip is ahead end trip time by "+Integer.parseInt(days)*-1+" days "+".\nTrip should have ended by ";
                holder.starts_in_tv.setText(msg+""+date+".");
                holder.starts_in_tv.setTextColor(context.getResources().getColor(R.color.red));            }
        }
        else if (TextUtils.isEmpty(days) && !TextUtils.isEmpty(hours) && TextUtils.isEmpty(minutes)){
            msg = placeholder+hours+" "+h;
            holder.starts_in_tv.setText(msg+"\n("+date+")");
            if (Integer.parseInt(hours) <= 0){
                msg = "Trip is ahead end trip time by "+Integer.parseInt(hours)*-1+" hours "+".\nTrip should have ended by ";
                holder.starts_in_tv.setText(msg+""+date+".");
                holder.starts_in_tv.setTextColor(context.getResources().getColor(R.color.red));            }
        }
        else if (TextUtils.isEmpty(days) && TextUtils.isEmpty(hours) && !TextUtils.isEmpty(minutes)){
            msg = placeholder+minutes+" "+m;
            holder.starts_in_tv.setText(msg+"\n("+date+")");
            if (Integer.parseInt(minutes) <= 0){
                msg = "Trip is ahead end trip time by "+Integer.parseInt(minutes)*-1+" minutes "+".\nTrip should have ended by ";
                holder.starts_in_tv.setText(msg+""+date+".");
                holder.starts_in_tv.setTextColor(context.getResources().getColor(R.color.red));            }
        }
        else if (!TextUtils.isEmpty(days) && !TextUtils.isEmpty(hours) && TextUtils.isEmpty(minutes)){
            msg = placeholder+days+" "+d+" and "+hours+" "+h;
            holder.starts_in_tv.setText(msg+"\n("+date+")");
            if (Integer.parseInt(days) <=0 || Integer.parseInt(hours) <=0){
                msg = "Trip is ahead end trip time by "+Integer.parseInt(days)*-1+" days and "+Integer.parseInt(hours)*-1+" hours "+".\nTrip should have ended by ";
                holder.starts_in_tv.setText(msg+""+date+".");
                holder.starts_in_tv.setTextColor(context.getResources().getColor(R.color.red));            }
        }
        else if (TextUtils.isEmpty(days) && !TextUtils.isEmpty(hours) && !TextUtils.isEmpty(minutes)){
            msg = placeholder+hours+" "+h+" and "+minutes+" "+m;
            holder.starts_in_tv.setText(msg+"\n("+date+")");
            if (Integer.parseInt(hours) <=0 || Integer.parseInt(minutes) <=0){
                msg = "Trip is ahead end trip time by "+Integer.parseInt(hours)*-1+" hours and "+Integer.parseInt(minutes)*-1+" minutes "+".\nTrip should have ended by ";
                holder.starts_in_tv.setText(msg+""+date+".");
                holder.starts_in_tv.setTextColor(context.getResources().getColor(R.color.red));
            }
        }
    }


    //    setting price
    private String addComma(Long number){
        try {
            NumberFormat formatter = new DecimalFormat("#,###");
            return formatter.format(number);
        }
        catch (Exception e){
            return "N.A";
        }
    }

    //    setting car image
    private void setCarImage(final ViewHolder holder, final DataForRecyclerView info) {


        databaseReference.child(info.getGeneral_vehicle_key())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        try {
                            Glide.with(context)
                                    .load(dataSnapshot.child("image_url").getValue(String.class))
                                    .apply(new RequestOptions().placeholder(R.drawable.ic_car_placeholder).error(R.drawable.ic_car_placeholder))
                                    .into(holder.car_image_iv);
                        }
                        catch (Exception ignored){

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }
    //    setting car image

    private void addSpaceAtLast(int position, ViewHolder holder) {
        //adding padding to last card view

        if( position == 0){
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0
                    ,(int) context.getResources().getDimension(R.dimen.dp12)
                    , 0
                    , (int) context.getResources().getDimension(R.dimen.dp12));
            holder.cardView.setLayoutParams(params);
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

        private TextView status_tv, car_name_tv
                , number_plate_tv, price_tv, start_date_tv, end_date_tv, starts_in_tv;
        private ImageView car_image_iv;
        private CardView cardView;
        private RelativeLayout relativeLayout;

        ViewHolder(View itemView) {
            super(itemView);

            car_image_iv = itemView.findViewById(R.id.tx_car_image_iv);
            status_tv = itemView.findViewById(R.id.tx_status_tv);
            car_name_tv = itemView.findViewById(R.id.tx_car_name_tv);
            number_plate_tv = itemView.findViewById(R.id.tx_number_plate_tv);
            start_date_tv = itemView.findViewById(R.id.tx_start_date_tv);
            end_date_tv = itemView.findViewById(R.id.tx_end_date_tv);
            price_tv = itemView.findViewById(R.id.tx_price_tv);
            cardView = itemView.findViewById(R.id.tx_cardView);
            relativeLayout = itemView.findViewById(R.id.tx_trip_dates_rl);
            starts_in_tv = itemView.findViewById(R.id.tx_starts_in_tv);

        }
    }
//end
}