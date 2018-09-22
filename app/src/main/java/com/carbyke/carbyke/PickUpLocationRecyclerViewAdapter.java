package com.carbyke.carbyke;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.List;

public class PickUpLocationRecyclerViewAdapter extends RecyclerView.Adapter<PickUpLocationRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private List<DataForRecyclerView> UploadInfoList;
    SharedPreferences sharedPreferencesLocation;
    private static final String LOCATION = "location";
    private static final String STATION = "station";
    private int selectedPos = RecyclerView.NO_POSITION;

    PickUpLocationRecyclerViewAdapter(Context context, List<DataForRecyclerView> TempList) {

        this.UploadInfoList = TempList;

        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_list_pick_up_location, parent, false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final DataForRecyclerView info = UploadInfoList.get(position);


        holder.base_name_tv.setText(info.getStation());
       // holder.base_name_tv.setTextColor(selectedPos == position ? context.getResources().getColor(R.color.black) : Color.TRANSPARENT);

        if (selectedPos == position){
            holder.base_name_tv.setTextColor(context.getResources().getColor(R.color.black));
            holder.tick_ib.setVisibility(View.VISIBLE);
            PickUpLocation.continue_b.setEnabled(true);
            PickUpLocation.continue_b.setBackgroundResource(R.color.googleGreen);
            sharedPreferencesLocation = context.getSharedPreferences(LOCATION, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferencesLocation.edit();
            editor.putString(STATION, info.getStation());
            editor.apply();
        }

        holder.base_name_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (position == RecyclerView.NO_POSITION) return;   // just a precaution in case if previous value is a null
                // Updating old as well as new positions
                notifyItemChanged(selectedPos);
                selectedPos = position;
                notifyItemChanged(selectedPos);
            }
        });

    }


    private void selection(String station){
        new MaterialDialog.Builder(context)
                //.title("")
                //.titleColor(context.getResources().getColor(R.color.black))
                .backgroundColor(Color.WHITE)
                .content(station)
                .contentColor(context.getResources().getColor(R.color.black))
                .positiveText("Continue")
                .positiveColor(context.getResources().getColor(R.color.black))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                    }
                })
                .negativeText("Cancel")
                .negativeColor(context.getResources().getColor(R.color.black))
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    }
                })
                .show();
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

        TextView base_name_tv;
        ImageButton tick_ib;

        ViewHolder(View itemView) {
            super(itemView);

            base_name_tv = itemView.findViewById(R.id.rul_base_name_tv);
            tick_ib = itemView.findViewById(R.id.rul_tick_ib);

        }
    }
//end
}