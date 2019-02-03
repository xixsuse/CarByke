package com.carbyke.carbyke;

import android.content.Context;
import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.Objects;

public class ShowMessage {

    private Context context;

    ShowMessage(Context context){
        this.context = context;
    }

    public void successMessage(String title, String message){
        try {
            new MaterialDialog.Builder(context)
                    .title(title)
                    .titleColor(context.getResources().getColor(R.color.black))
                    .content(message)
                    .contentColorRes(R.color.black)
                    .positiveText("Okay")
                    .positiveColorRes(R.color.black)
                    .backgroundColor(context.getResources().getColor(R.color.white))
                    .icon(context.getResources().getDrawable(R.drawable.ic_success))
                    .show();
        }
        catch (Exception e){
            //
        }
    }

    //    failed Message
    public void failMessageWithTitle(String title, String message){
        try {
            new MaterialDialog.Builder(context)
                    .title(title)
                    .titleColor(context.getResources().getColor(R.color.black))
                    .content(message)
                    .contentColorRes(R.color.black)
                    .positiveText("Okay")
                    .positiveColorRes(R.color.black)
                    .backgroundColor(context.getResources().getColor(R.color.white))
                    .icon(context.getResources().getDrawable(R.drawable.ic_error))
                    .show();
        }
        catch (Exception e){
            //
        }
    }


    //    failed Message without title
    public void failMessageWithoutTitle(String message){
        try {
            new MaterialDialog.Builder(context)
                    .content(message)
                    .contentColorRes(R.color.black)
                    .positiveText("Okay")
                    .positiveColorRes(R.color.black)
                    .backgroundColor(context.getResources().getColor(R.color.white))
                    .icon(context.getResources().getDrawable(R.drawable.ic_error))
                    .show();
        }
        catch (Exception e){
            //
        }
    }

}
