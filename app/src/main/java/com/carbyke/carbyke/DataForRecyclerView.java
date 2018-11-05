package com.carbyke.carbyke;

public class DataForRecyclerView {

    private String station, car_name, image_url, with_fuel_1, with_fuel_2, with_fuel_3, without_fuel_1, without_fuel_2
            , without_fuel_3, seater, latitude, longitude, map_location;

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

    public String getWith_fuel_1() {
        return with_fuel_1;
    }

    public String getWith_fuel_2() {
        return with_fuel_2;
    }

    public String getWith_fuel_3() {
        return with_fuel_3;
    }

    public String getWithout_fuel_1() {
        return without_fuel_1;
    }

    public String getWithout_fuel_2() {
        return without_fuel_2;
    }

    public String getWithout_fuel_3() {
        return without_fuel_3;
    }

    public String getSeater() {
        return seater;
    }

    public String getImage_url() {
        return image_url;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getMap_location() {
        return map_location;
    }
}
