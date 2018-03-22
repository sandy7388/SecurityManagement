package com.example.sandy.securitymanagement.database_helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.sandy.securitymanagement.model.GuardInfo;

import static java.lang.System.out;

/**
 * Created by sandy on 21/11/17.
 */

public class DBHelper extends SQLiteOpenHelper
{

    // Database version
    private static final int DB_VERSION = 1;

    // Constructor
    public DBHelper(Context context) {
        super(context, DBContract.DB_NAME, null, DB_VERSION);
    }

    // Create table
    public static final String CREATE_TABLE ="create table " +
            DBContract.TABLE_NAME+ "(" +
            DBContract.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,  "+
            DBContract.COLUMN_NAME+ " text, "+
            DBContract.COLUMN_USER_ID+ " text, "+
            DBContract.COLUMN_SYNC_STATUS+ " integer, "+
            DBContract.COLUMN_IMAGE+ " blob, "+
            DBContract.COLUMN_LATITUDE+ " text, "+
            DBContract.COLUMN_LONGITUDE+ " text, "+
            DBContract.COLUMN_LOCATION+ " text, "+
            DBContract.COLUMN_WORKING_STATUS+ " text, "+
            DBContract.COLUMN_DATETIME+ " text);";

    // Drop table if exits
    private static final String DROP_TABLE= "drop table if exits "+DBContract.TABLE_NAME;

    @Override
    public void onCreate(SQLiteDatabase db)
    {

        db.execSQL(CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL(DROP_TABLE);
        onCreate(db);

    }

    // Method to save sqlite database
    public String saveToLocalDatabase(GuardInfo guardInfo,SQLiteDatabase database)
    {

        ContentValues contentValues = new ContentValues();
        //contentValues.put(DBContract.COLUMN_ID,guardInfo.getId());
        contentValues.put(DBContract.COLUMN_NAME, guardInfo.getName());
        contentValues.put(DBContract.COLUMN_SYNC_STATUS, guardInfo.getStatus());
        contentValues.put(DBContract.COLUMN_LOCATION, guardInfo.getLocation());
        contentValues.put(DBContract.COLUMN_WORKING_STATUS,guardInfo.getWorking_status());
        contentValues.put(DBContract.COLUMN_IMAGE,guardInfo.getBlob());
        contentValues.put(DBContract.COLUMN_LONGITUDE,guardInfo.getLongitude());
        contentValues.put(DBContract.COLUMN_LATITUDE,guardInfo.getLatitude());
        contentValues.put(DBContract.COLUMN_DATETIME,guardInfo.getDatetime());

        database.insert(DBContract.TABLE_NAME, null, contentValues);
        Cursor cursor=getCount();
        String id="";
        while (cursor.moveToNext())
        {
            id=cursor.getString(cursor.getColumnIndex(DBContract.COLUMN_ID));
        }
        return id;

    }
    // Method to read from sqlite database
    public Cursor readFromLocalDatabase(SQLiteDatabase database)
    {

        String[] projection=
                {
                        DBContract.COLUMN_NAME,DBContract.COLUMN_SYNC_STATUS,
                        DBContract.COLUMN_LOCATION,DBContract.COLUMN_WORKING_STATUS,
                        DBContract.COLUMN_IMAGE,DBContract.COLUMN_LONGITUDE,
                        DBContract.COLUMN_LATITUDE,DBContract.COLUMN_DATETIME
                };
        return database.query(DBContract.TABLE_NAME,projection,null,null,null,null,null);
    }

    // Update the sqlite database that data is synced with the server or not
    public void updateLocalDatabase(String id, int status)
    {
        //int id= Integer.parseInt(guardInfo.getId());
        SQLiteDatabase database = this.getReadableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBContract.COLUMN_SYNC_STATUS,status);

       database.update(DBContract.TABLE_NAME, contentValues, DBContract.COLUMN_ID + "=" + id, null);
       database.close();
    }


    public Cursor getUnsyncedNames() {
        SQLiteDatabase database = this.getReadableDatabase();
        String sql = "SELECT * FROM " + DBContract.TABLE_NAME + " WHERE " + DBContract.COLUMN_SYNC_STATUS + " = 1;";
        Cursor cursor = database.rawQuery(sql, null);
        return cursor;
    }

    public Cursor getCount() {
        SQLiteDatabase database = this.getReadableDatabase();
        String sql = "SELECT * FROM " + DBContract.TABLE_NAME +";";
        Cursor cursor = database.rawQuery(sql, null);
        return cursor;
    }

    public boolean deleteTitle(String id,SQLiteDatabase database)
    {
        database = this.getReadableDatabase();
        return database.delete(DBContract.TABLE_NAME, DBContract.COLUMN_ID + "=" + id, null) > 0;
    }




}