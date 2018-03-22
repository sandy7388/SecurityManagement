package com.example.sandy.securitymanagement.httpClient;

/**
 * Created by sandy on 8/12/17.
 */

public class HttpURL
{

    public static final String BASE_URL = "http://wayzontech.co.in/guard/webservices/";

    // Save guard info
    public static final String UPDATE_URL= BASE_URL + "person";

    // Fetching Location from server
    public static final String LOCATION_URL = BASE_URL + "load_location";

    // Fetching Working status from server
    public static final String STATUS_URL = BASE_URL + "load_status";

    // User login url
    public static final String LOGIN_URL = BASE_URL + "login";

    // Mobile OTP
    public static final String GENERATE_OTP_URL = BASE_URL + "generate_otp";

    // Reset password
    public static final String FORGOT_PASSWORD_URL = BASE_URL +  "reset_password";

    // Broadcast receiver
    public static final String UI_BROADCAST_UPDATE = "com.example.securitymanagement.uiupdatebroadcast";

    // Logout
    public static final String LOGOUT_URL = BASE_URL + "android_logout";

    // Login check
    public static final String LOGIN_CHECK_URL = BASE_URL + "login_status";

}
