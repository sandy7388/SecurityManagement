package com.example.sandy.securitymanagement.database_helper;

/**
 * Created by sandy on 21/12/17.
 */

public class DBContract
{
    // Sync status, Where
    // o means data is synced with server and
    // 1 means data is not synced with server

    public static final int SYNC_STATUS_OK = 0;
    public static final int SYNC_STATUS_FAILED = 1;


    public static final String DB_NAME = "Security_Management";
    public static final String TABLE_NAME = "GuardInfo";
    public static final String COLUMN_ID = "guard_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_USER_ID = "user_id";

    public static final String COLUMN_LOCATION = "location";
    public static final String COLUMN_WORKING_STATUS = "working_status";
    public static final String COLUMN_DATETIME = "datetime";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "langtitute";
    public static final String COLUMN_IMAGE = "file_name";
    public static final String COLUMN_SYNC_STATUS = "sync_status";


}
