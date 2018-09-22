package com.carbyke.carbyke;

import android.content.Context;
import android.support.v4.app.FragmentManager;

import com.roger.catloadinglibrary.CatLoadingView;

public class MyLoadingCat {
    private CatLoadingView catLoadingView;
    private FragmentManager fragmentManager;

    MyLoadingCat(FragmentManager fragmentManager){
        catLoadingView = new CatLoadingView();
        this.fragmentManager = fragmentManager;
    }

    public void displayCat(){
        catLoadingView.show(fragmentManager, "");
    }

    public void dismissCat(){
        catLoadingView.dismiss();
    }

}
