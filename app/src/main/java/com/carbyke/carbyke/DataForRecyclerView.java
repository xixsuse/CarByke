package com.carbyke.carbyke;

public class DataForRecyclerView {

    private String station, car_name, image_url, with_fuel_1, with_fuel_2, with_fuel_3, without_fuel_1, without_fuel_2
            , without_fuel_3, seater, latitude
            , longitude, map_location, pickUpLocationKey
            , general_vehicle_key, number_plate_key
            , number_plate, trip_days, trip_hours, trip_minutes, start_date, end_date, trip_status, booking_key;

    float trip_cost_multiplier;
    Long payable_amount;

    DataForRecyclerView(){
        }

    public void setBooking_key(String booking_key) {
        this.booking_key = booking_key;
    }

    public String getBooking_key() {
        return booking_key;
    }

    public String getTrip_status() {
        return trip_status;
    }

    public void setTrip_status(String trip_status) {
        this.trip_status = trip_status;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public void setCar_name(String car_name) {
        this.car_name = car_name;
    }


    public void setPayable_amount(Long payable_amount) {
        this.payable_amount = payable_amount;
    }

    public Long getPayable_amount() {
        return payable_amount;
    }

    public void setWith_fuel_1(String with_fuel_1) {
        this.with_fuel_1 = with_fuel_1;
    }

    public void setWith_fuel_2(String with_fuel_2) {
        this.with_fuel_2 = with_fuel_2;
    }

    public void setWith_fuel_3(String with_fuel_3) {
        this.with_fuel_3 = with_fuel_3;
    }

    public float getTrip_cost_multiplier() {
        return trip_cost_multiplier;
    }

    public void setTrip_cost_multiplier(float trip_cost_multiplier) {
        this.trip_cost_multiplier = trip_cost_multiplier;
    }

    public String getTrip_days() {
        return trip_days;
    }

    public void setTrip_days(String trip_days) {
        this.trip_days = trip_days;
    }

    public String getTrip_hours() {
        return trip_hours;
    }

    public void setTrip_hours(String trip_hours) {
        this.trip_hours = trip_hours;
    }

    public String getTrip_minutes() {
        return trip_minutes;
    }

    public void setTrip_minutes(String trip_minutes) {
        this.trip_minutes = trip_minutes;
    }

    public String getNumber_plate() {
        return number_plate;
    }

    public void setNumber_plate(String number_plate) {
        this.number_plate = number_plate;
    }

    public String getNumber_plate_key() {
        return number_plate_key;
    }

    public String getGeneral_vehicle_key() {
        return general_vehicle_key;
    }

    public void setNumber_plate_key(String number_plate_key) {
        this.number_plate_key = number_plate_key;
    }

    public void setGeneral_vehicle_key(String general_vehicle_key) {
        this.general_vehicle_key = general_vehicle_key;
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

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getMap_location() {
        return map_location;
    }

    public void setMap_location(String map_location) {
        this.map_location = map_location;
    }

    public String getPickUpLocationKey() {
        return pickUpLocationKey;
    }

    public void setPickUpLocationKey(String pickUpLocationKey) {
        this.pickUpLocationKey = pickUpLocationKey;
    }
}
