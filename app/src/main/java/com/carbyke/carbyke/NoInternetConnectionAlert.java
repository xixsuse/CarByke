package com.carbyke.carbyke;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

public class NoInternetConnectionAlert {

    private Context context;

    NoInternetConnectionAlert(Context context){
        this.context = context;
    }

    public void DisplayNoInternetConnection(){
        try {
            new MaterialDialog.Builder(context)
                    .title("No Internet Access!")
                    .titleColor(Color.BLACK)
                    .content("No internet connectivity detected. Please make sure you have working internet connection and try again.")
                    .icon(context.getResources().getDrawable(R.drawable.ic_no_internet_connection))
                    .contentColor(context.getResources().getColor(R.color.black))
                    .backgroundColor(context.getResources().getColor(R.color.white))
                    .positiveColor(context.getResources().getColor(R.color.red))
                    .positiveText("Turn on Mobile Data")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            Intent intent = new Intent();
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setAction(android.provider.Settings.ACTION_DATA_ROAMING_SETTINGS);
                            context.startActivity(intent);
                        }
                    })
                    .negativeColor(context.getResources().getColor(R.color.lightGreen))
                    .negativeText("Turn on Wifi")
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            context.startActivity(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK));
                        }
                    })
                    .neutralText("Cancel")
                    .neutralColor(context.getResources().getColor(R.color.black))
                    .show();
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }


}