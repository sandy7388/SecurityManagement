package com.example.sandy.securitymanagement.sharedPrefManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.sandy.securitymanagement.activity.LoginActivity;
import com.example.sandy.securitymanagement.model.Location;
import com.example.sandy.securitymanagement.model.LoginUser;

/**
 * Created by sandy on 25/11/17.
 */

public class SessionManager
{
    private static SessionManager mInstance;
    private static Context mCtx;

    private static final String SHARED_PREF_NAME = "mySharedPref";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_USER_PASSWORD = "password";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_LOGIN_STATUS = "login_status";

    private static final String KEY_LOCATION_NAME = "location";
    private static final String KEY_WORKING_STATUS = "working_status";
    private static final String KEY_LOCATION_ID = "location_id";

    public SessionManager(Context context)
    {
        mCtx = context;
    }

    public static synchronized SessionManager getInstance(Context context)
    {
        if (mInstance == null)
        {
            mInstance = new SessionManager(context);
        }
        return mInstance;
    }

    // for user login to store  the user session
    public void userLogin(LoginUser loginUser)
    {

        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USERNAME, loginUser.getUsername());
        editor.putString(KEY_USER_PASSWORD,loginUser.getPassword());
        editor.putString(KEY_USER_ID,loginUser.getId());
        //editor.putString(KEY_LOGIN_STATUS,loginUser.getLoginStatus());

        editor.apply();
        editor.commit();

    }
    //check whether the user is logged in or not
    public boolean isLoggedIn()
    {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        if(sharedPreferences.getString(KEY_USERNAME, null) != null)
        {
            return true;
        }
        return false;
    }
    public String getUserID()
    {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USER_ID, null) ;
    }

    public String getLoginStatus()
    {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_LOGIN_STATUS, null) ;
    }

    public String getUsername()
    {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USERNAME, null) ;
    }
    // this method is used to logout the session
    public void logout()
    {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        editor.commit();
        mCtx.startActivity(new Intent(mCtx, LoginActivity.class));
    }

    public void locationSession(String location)
    {
        SharedPreferences sharedPreferences=mCtx.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(KEY_LOCATION_NAME,location);
        //editor.putString(KEY_LOCATION_ID,location.getLocation_id());
        editor.apply();
        editor.commit();

    }
    public String getLocation()
    {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_LOCATION_NAME, "") ;
    }

    public String getLocationId()
    {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME,  Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_LOCATION_ID, "");
    }

    public void workingStatusSession(String working_status)
    {
        SharedPreferences sharedPreferences=mCtx.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(KEY_WORKING_STATUS,working_status);
        editor.apply();
        editor.commit();
    }

    public String getWorkingStatus()
    {
        SharedPreferences sharedPreferences=mCtx.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_WORKING_STATUS,"");
    }

}
