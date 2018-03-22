package com.example.sandy.securitymanagement.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.example.sandy.securitymanagement.activity.MainActivity;
import com.example.sandy.securitymanagement.activity.VolleyMultipart;
import com.example.sandy.securitymanagement.database_helper.DBContract;
import com.example.sandy.securitymanagement.database_helper.DBHelper;
import com.example.sandy.securitymanagement.httpClient.HttpURL;
import com.example.sandy.securitymanagement.model.DataPart;
import com.example.sandy.securitymanagement.sharedPrefManager.SessionManager;
import com.example.sandy.securitymanagement.volley.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sandy on 21/11/17.
 */

public class NetworkCheck extends BroadcastReceiver
{

    SessionManager session;
    DBHelper databaseHelper;
    //   GuardInfo guardInfo;
    @Override
    public void onReceive(final Context context, Intent intent)
    {
        session=new SessionManager(context);
        if (checkNetworkConnection(context))
        {
            databaseHelper=new DBHelper(context);

            final SQLiteDatabase database = databaseHelper.getWritableDatabase();

            //Cursor cursor=databaseHelper.readFromLocalDatabase(database);
            Cursor cursor = databaseHelper.getUnsyncedNames();
            while (cursor.moveToNext())
            {


                final int sync_status=cursor.getInt(cursor.getColumnIndex(DBContract.COLUMN_SYNC_STATUS));

                if (sync_status==DBContract.SYNC_STATUS_FAILED)
                {
                    final String name=cursor.getString(cursor.getColumnIndex(DBContract.COLUMN_NAME));
                    final String lantStr=cursor.getString(cursor.getColumnIndex(DBContract.COLUMN_LATITUDE));
                    final String longStr=cursor.getString(cursor.getColumnIndex(DBContract.COLUMN_LONGITUDE));
                    final String dateTime=cursor.getString(cursor.getColumnIndex(DBContract.COLUMN_DATETIME));
                    final String guard_locationStr= cursor.getString(cursor.getColumnIndex(DBContract.COLUMN_LOCATION));
                    final String working_statusStr=cursor.getString(cursor.getColumnIndex(DBContract.COLUMN_WORKING_STATUS));
                    final String file_image=cursor.getString(cursor.getColumnIndex(DBContract.COLUMN_IMAGE));
                    final String id=cursor.getString(cursor.getColumnIndex(DBContract.COLUMN_ID));

                    VolleyMultipart volleyMultipart = new VolleyMultipart(Request.Method.POST, HttpURL.UPDATE_URL, new Response.Listener<NetworkResponse>()
                    {
                        @Override
                        public void onResponse(NetworkResponse response)
                        {
                            try
                            {

                                String resultResponse = new String(response.data);
                                System.out.println(resultResponse);
                                JSONObject jsonObject = new JSONObject(resultResponse);
                                if (jsonObject.getString("success").equals("1"))
                                {
                                    //guardInfo.setStatus(DBContract.SYNC_STATUS_OK);

                                    String guard_id=jsonObject.getString("guard_id");

                                    if(databaseHelper.deleteTitle(guard_id,database))
                                    {
                                        Toast.makeText(context,"Data is synced "+guard_id,Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        Toast.makeText(context,"Data is not synced "+guard_id,Toast.LENGTH_SHORT).show();

                                    }

                                    System.out.println(guard_id);
                                    context.sendBroadcast(new Intent(HttpURL.UI_BROADCAST_UPDATE));


//                                    databaseHelper.updateLocalDatabase(guard_id,DBContract.SYNC_STATUS_OK);
//                                    context.sendBroadcast(new Intent(HttpURL.UI_BROADCAST_UPDATE));


                                }

                            }
                            catch (JSONException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error)
                        {
                            NetworkResponse networkResponse = error.networkResponse;
                            String errorMessage = "Unknown error";
                            if (networkResponse == null)
                            {
                                if (error.getClass().equals(TimeoutError.class))
                                {
                                    errorMessage = "Request timeout";
                                }
                                else if (error.getClass().equals(NoConnectionError.class))
                                {
                                    errorMessage = "Failed to connect server";
                                }
                            }
                            else
                            {
                                String result = new String(networkResponse.data);
                                try {

                                    JSONObject response = new JSONObject(result);

                                    Log.e("Error Status", result);

                                    String status = response.getString("status");
                                    String message = response.getString("message");

                                    Log.e("Error Status", status);
                                    Log.e("Error Message", message);

                                    if (networkResponse.statusCode == 404)
                                    {
                                        errorMessage = "Resource not found";
                                    }
                                    else if (networkResponse.statusCode == 401)
                                    {
                                        errorMessage = message+" Please login again";
                                    }
                                    else if (networkResponse.statusCode == 400)
                                    {
                                        errorMessage = message+ " Check your inputs";
                                    }
                                    else if (networkResponse.statusCode == 500)
                                    {
                                        errorMessage = message+" Something is getting wrong";
                                    }
                                }
                                catch (JSONException e)
                                {
                                    e.printStackTrace();
                                }
                            }
                            Log.i("Error", errorMessage);
                            error.printStackTrace();


                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams()
                        {
                            Map<String, String> params = new HashMap<String, String>();

                            params.put("name", name);
                            params.put("location", guard_locationStr);
                            params.put("working_status", working_statusStr);
                            params.put("latitude", String.valueOf(lantStr));
                            params.put("langitute", String.valueOf(longStr));
                            params.put("dateTime", dateTime);
                            params.put("user_id", session.getUserID());
                            params.put("guard_id",id);

                            return params;

                        }

                        @Override
                        protected Map<String, DataPart> getByteData()
                        {
                            Map<String, DataPart> params = new HashMap<>();
                            params.put("file_name", new DataPart("file_image.jpg",
                                    MainActivity.convertFileToByteArray(new File(file_image)), "image/jpeg"));
                            return params;

                        }
                    };

                    int socketTimeout = 60000; // 60 seconds. You can change it
                    RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

                    volleyMultipart.setRetryPolicy(policy);

                    VolleySingleton.getInstance(context).addToRequestQueue(volleyMultipart);

                }
            }

        }

    }

    public boolean checkNetworkConnection(Context context)
    {
        ConnectivityManager connectivityManager= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo= null;
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return (networkInfo !=null && networkInfo.isConnected());
    }
}