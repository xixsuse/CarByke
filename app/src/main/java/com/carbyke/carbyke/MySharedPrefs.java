package com.carbyke.carbyke;

import android.content.Context;
import android.content.SharedPreferences;

public class MySharedPrefs {

    private Context context;
    private SharedPreferences sharedPreferences;

//    login
    private final static String LOGIN = "login";
    private final static String LOGGED_IN_OR_NOT = "logged_in";

//    shared pref login mode
    private SharedPreferences sharedPreferencesLoginMode;
    private final static String MODE = "mode";
    private final static String LOGIN_MODE = "login_mode";

//    delivery location
    private static final String LOCATION = "location";
    private static final String STATION = "station";
    private static final String TYPE = "type";

//    edit profile data
    private final static String PROFILE_DATA = "profile_data";
    private final static String PROFILE = "profile";
    private final static String EMAIL = "email";
    private final static String NAME = "name";
    private final static String PHONE_NUMBER = "phone_number";
    private final static String GENDER = "gender";

//    date time booking
    private static final String DATE_TIME = "date_time";
    private static final String START_DATE_TIME = "start_date_time";
    private static final String END_DATE_TIME = "end_date_time";

//    user location lat and long
    private static final String USER_LAT_LOG = "user_lat_long";
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";

//    selected pick up station data
    private static final String SELECTED_PICK_UP_LOCATION = "selected_pick_up_location";
    private static final String MAP_LOCATION = "map_location";



    MySharedPrefs(Context context){
        this.context = context;
    }

//    login, setter login page
    public void setLoginPref(String value){
        sharedPreferences = context.getSharedPreferences(LOGIN, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LOGGED_IN_OR_NOT, value);
        editor.apply();
    }
    public String getLoggedInOrNot() {
        sharedPreferences = context.getSharedPreferences(LOGIN, Context.MODE_PRIVATE);
        return sharedPreferences.getString(LOGGED_IN_OR_NOT, "");
    }

    //    shared pref login mode
    public void setLoginMode(String login_mode){
        sharedPreferences = context.getSharedPreferences(MODE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LOGIN_MODE, login_mode);
        editor.apply();
    }
    public String getLoginMode() {
        sharedPreferences = context.getSharedPreferences(MODE, Context.MODE_PRIVATE);
        return sharedPreferences.getString(LOGIN_MODE, "");
    }

//    delivery location
    public void setSelectedLocationOrStation(String station, String type){
        sharedPreferences = context.getSharedPreferences(LOCATION, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(STATION, station);
        editor.putString(TYPE, type);
        editor.apply();
    }
    public String getSelectedLocationOrStation() {
        sharedPreferences = context.getSharedPreferences(LOCATION, Context.MODE_PRIVATE);
        return sharedPreferences.getString(STATION, "");
    }
    public String getDeliveryOrPickUpType() {
        sharedPreferences = context.getSharedPreferences(LOCATION, Context.MODE_PRIVATE);
        return sharedPreferences.getString(TYPE, "");
    }

//    edit profile data
    public void initiateProfileData(){
        sharedPreferences = context.getSharedPreferences(PROFILE_DATA, Context.MODE_PRIVATE);
    }

    public void setProfileData(String name, String email, String phone, String gender){
        sharedPreferences = context.getSharedPreferences(PROFILE_DATA, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(NAME, name);
        editor.putString(EMAIL, email);
        editor.putString(PHONE_NUMBER, phone);
        editor.putString(GENDER, gender);
        editor.apply();
    }
    public void setProfileDataN_E_P(String name, String email, String phone){
        sharedPreferences = context.getSharedPreferences(PROFILE_DATA, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(NAME, name);
        editor.putString(EMAIL, email);
        editor.putString(PHONE_NUMBER, phone);
        editor.apply();
    }
    public void setProfileDataNAME_GENDER(String name, String gender){
        sharedPreferences = context.getSharedPreferences(PROFILE_DATA, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(NAME, name);
        editor.putString(GENDER, gender);
        editor.apply();
    }
    public String getProfileName() {
        sharedPreferences = context.getSharedPreferences(PROFILE_DATA, Context.MODE_PRIVATE);
        return sharedPreferences.getString(NAME, "");
    }
    public String getProfileEmail()
    {   sharedPreferences = context.getSharedPreferences(PROFILE_DATA, Context.MODE_PRIVATE);
        return sharedPreferences.getString(EMAIL, "");
    }
    public String getProfilePhoneNumber() {
        sharedPreferences = context.getSharedPreferences(PROFILE_DATA, Context.MODE_PRIVATE);
        return sharedPreferences.getString(PHONE_NUMBER, "");
    }
    public String getProfileGender() {
        sharedPreferences = context.getSharedPreferences(PROFILE_DATA, Context.MODE_PRIVATE);
        return sharedPreferences.getString(GENDER, "");
    }

//    date time booking
    public void setDatetimeBooking(String start_date_time, String end_date_time){
        sharedPreferences = context.getSharedPreferences(START_DATE_TIME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(START_DATE_TIME, start_date_time);
        editor.putString(END_DATE_TIME, end_date_time);
        editor.apply();
    }
    public String getStartDateTime() {
        sharedPreferences = context.getSharedPreferences(START_DATE_TIME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(START_DATE_TIME, "");
    }
    public String getEndDateTime() {
        sharedPreferences = context.getSharedPreferences(START_DATE_TIME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(END_DATE_TIME, "");
    }


//    user location lat and long
    public void setUserLatLog(String latitude, String longitude){
        sharedPreferences = context.getSharedPreferences(USER_LAT_LOG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LATITUDE, latitude);
        editor.putString(LONGITUDE, longitude);
        editor.apply();
    }
    public String getUserLatitude() {
        sharedPreferences = context.getSharedPreferences(USER_LAT_LOG, Context.MODE_PRIVATE);
        return sharedPreferences.getString(LATITUDE, "");
    }
    public String getUserLongitude() {
        sharedPreferences = context.getSharedPreferences(USER_LAT_LOG, Context.MODE_PRIVATE);
        return sharedPreferences.getString(LONGITUDE, "");
    }


//    selected pick up station data
    public void setPickLocationData(String latitude, String longitude, String map_location){
        sharedPreferences = context.getSharedPreferences(SELECTED_PICK_UP_LOCATION, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LATITUDE, latitude);
        editor.putString(LONGITUDE, longitude);
        editor.putString(MAP_LOCATION, map_location);
        editor.apply();
    }
    public String getPickLocationLat() {
        sharedPreferences = context.getSharedPreferences(SELECTED_PICK_UP_LOCATION, Context.MODE_PRIVATE);
        return sharedPreferences.getString(LATITUDE, "");
    }
    public String getPickLocationLong() {
        sharedPreferences = context.getSharedPreferences(SELECTED_PICK_UP_LOCATION, Context.MODE_PRIVATE);
        return sharedPreferences.getString(LONGITUDE, "");
    }
    public String getPickLocationMapLocation() {
        sharedPreferences = context.getSharedPreferences(SELECTED_PICK_UP_LOCATION, Context.MODE_PRIVATE);
        return sharedPreferences.getString(MAP_LOCATION, "");
    }


    //end
}
