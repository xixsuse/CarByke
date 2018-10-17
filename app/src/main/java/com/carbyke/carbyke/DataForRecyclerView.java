package com.carbyke.carbyke;

public class DataForRecyclerView {

    String station, car_name, image_url;
    int w_100kms, w_200kms, w_350kms, f_100kms, f_200kms, f_350kms, seater;

    DataForRecyclerView(){
        }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public String getCar_name() {
        return car_name;
    }

    public int getW_100kms() {
        return w_100kms;
    }

    public int getW_200kms() {
        return w_200kms;
    }

    public int getW_350kms() {
        return w_350kms;
    }

    public int getF_100kms() {
        return f_100kms;
    }

    public int getF_200kms() {
        return f_200kms;
    }

    public int getF_350kms() {
        return f_350kms;
    }

    public int getSeater() {
        return seater;
    }

    public String getImage_url() {
        return image_url;
    }
}
