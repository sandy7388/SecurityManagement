package com.example.sandy.securitymanagement.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.EventLogTags;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import com.example.sandy.securitymanagement.database_helper.DBContract;
import com.example.sandy.securitymanagement.httpClient.HttpURL;
import com.example.sandy.securitymanagement.locationTracker.LocationService;
import com.example.sandy.securitymanagement.R;
import com.example.sandy.securitymanagement.database_helper.DBHelper;
import com.example.sandy.securitymanagement.model.DataPart;
import com.example.sandy.securitymanagement.model.GuardInfo;
import com.example.sandy.securitymanagement.receiver.NetworkCheck;
import com.example.sandy.securitymanagement.sharedPrefManager.SessionManager;
import com.example.sandy.securitymanagement.volley.VolleySingleton;


public class MainActivity extends RuntimePermissionActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener, View.OnFocusChangeListener {

    private static final int REQUEST_PERMISSIONS = 20;

    String mCurrentPhotoPath;
    // Activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    // directory name to store captured images and videos
    private static final String IMAGE_DIRECTORY_NAME = "Hello Camera";
    public String locationStrItem;
    private Uri fileUri; // file url to store image/video
    private String idStr = "";
    private String imgPath = "";
    private final String LATSTR = "lats";
    private final String LONGSTR = "longs";
    private static final int CAMERA_REQUEST = 1888;
    private DBHelper database;
    public String locationStr, nameStr;
    public String working_statusStr;
    public String dateTime;
    public ProgressDialog progressDialog;
    public LocationService locationService;
    public Bitmap bitmap;
    private EditText editText_guard;
    private Spinner spinner_status;
    private Button button_update;
    //private ImageButton image_button_camera;
    private String lantStr, longStr;
    private ImageView imageView;
    BroadcastReceiver broadcastReceiver;
    ArrayList<String> working_status;
    ArrayList<String> location;
    ArrayList<String> autocomplete_location;
    GuardInfo guardInfo;
    private static File imagePath;
    private Uri currentImageUri;
    private AutoCompleteTextView autoCompleteTextView;
    private Map<String, String> location_idMap;
    SessionManager session;
    JSONArray jsonArray;
    String locationIdItem = "";
    IntentFilter filter1;
    private NetworkCheck networkCheck = new NetworkCheck();
    private TextView usernametxt;
    private static final String TAG = "MainActivity";

    private String loginStatus = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main_screen);
            brodcastReceiverTest();
            initialisation();
            hideKeyboard();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void brodcastReceiverTest() {
        if (android.os.Build.VERSION.SDK_INT >= 7) {
            filter1 = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
            registerReceiver(networkCheck, filter1);
        }
    }

    //    private final NetworkCheck networkCheck = new NetworkCheck() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if(intent.getAction().equalsIgnoreCase("android.net.conn.CONNECTIVITY_CHANGE")) {
//                Log.d(TAG,"Network connect");
//            }
//        }
//    };
    void initialisation() {
        session = new SessionManager(this);
        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        database = new DBHelper(this);
        editText_guard = findViewById(R.id.editText_guard_name);
        autoCompleteTextView = findViewById(R.id.autoText);
        spinner_status = findViewById(R.id.spinner_status);
        imageView = findViewById(R.id.imageView);
        usernametxt = findViewById(R.id.textview_username);
        button_update = findViewById(R.id.button_upload);
        location = new ArrayList<String>();

        location_idMap = new HashMap<String, String>();
        working_status = new ArrayList<String>();
       /* if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }*/

        setTitle("Security Guard");
        autocomplete_location = new ArrayList<>();

        // Date Time
        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        dateTime = simpleDateFormat.format(calendar.getTime());

        // TO open camera

        button_update.setOnClickListener(this);
        imageView.setOnClickListener(this);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                readFromLocalStorage();
            }
        };

        // Read from local storage
        readFromLocalStorage();

        // Save the spinner data offline
        saveSpinnerOffline();

        locationService = new LocationService(this);
        lantStr = getlocation().get(LATSTR);
        longStr = getlocation().get(LONGSTR);

        // For getting session
        if (!SessionManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        // Spinner to load location from server
        spinner_status.setOnItemSelectedListener(this);
        //spinner_location.setOnItemSelectedListener(this);
        if (!isDeviceSupportCamera()) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! Your device doesn't support camera",
                    Toast.LENGTH_LONG).show();
            // will close the app if the device does't have camera
            finish();
        }

        usernametxt.setText(session.getUsername());
        permission();

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
    }

    @Override
    public void onPermissionsGranted(final int requestCode) {
        Toast.makeText(this, "Permissions Received.", Toast.LENGTH_LONG).show();
    }

    void permission() {
        MainActivity.super.requestAppPermissions(new String[]{
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION
        }, R.string
                .runtime_permissions_txt, REQUEST_PERMISSIONS);
    }

    private boolean isDeviceSupportCamera() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageView:
                captureImage();
                //
                //                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                //                startActivityForResult(cameraIntent, CAMERA_REQUEST);
                //                currentImageUri = getImageFileUri();
                ////                Intent intentPicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, currentImageUri); // set the image file name
                //                startActivityForResult(cameraIntent, CAMERA_REQUEST);

                //                // start the image capture Intent
                //                startActivityForResult(intentPicture, CAMERA_REQUEST);
                break;
            case R.id.button_upload:

                nameStr = editText_guard.getText().toString();
                locationStr = autoCompleteTextView.getText().toString();
                guardInfo = new GuardInfo(DBContract.SYNC_STATUS_OK,
                        session.getUserID(),
                        dateTime, nameStr,
                        working_statusStr,
                        locationIdItem, lantStr,
                        longStr, imgPath);
                uploadImageAndDataToServer();
                break;
        }
    }

    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

            System.out.println(this.getApplicationContext().getPackageName() + ".activity");
            //             fileUri = FileProvider.getUriForFile(this,
            //                     this.getApplicationContext().getPackageName()+".activity" , createImageFile());

        } catch (Exception e) {
            e.printStackTrace();
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");
        File image = File.createTempFile(
                imageFileName, /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    private static Uri getImageFileUri() {
        // Create a storage directory for the images
        // To be safe(er), you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this

        imagePath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyProject");
        if (!imagePath.exists()) {
            if (!imagePath.mkdirs()) {
                return null;
            } else {
                //create new folder
            }
        }

        // Create an image file name
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File image = new File(imagePath, "MyProject_" + timeStamp + ".jpg");

        if (!image.exists()) {
            try {
                image.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Create an File Uri

        return Uri.fromFile(image);
    }

    // Camera request
    //    @Override
    //    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    //    {
    //        if (requestCode == CAMERA_REQUEST || resultCode == RESULT_OK || data != null)
    //        {
    //            Uri imageUri = data.getData();
    //
    //            if(imageUri!=null) {
    //                try {
    //                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
    //                    //System.out.println(imgPath);
    //                    imageView.setImageBitmap(bitmap);
    //
    //                } catch (FileNotFoundException e) {
    //                    e.printStackTrace();
    //                } catch (IOException e) {
    //                    e.printStackTrace();
    //                }
    //            }
    //
    //        }
    //    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // successfully captured the image
                // display it in image view
                try {
                    previewCapturedImage();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }
        } else if (requestCode == CAMERA_CAPTURE_VIDEO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // video successfully recorded
                // preview the recorded video
                //previewVideo();
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled recording
                Toast.makeText(getApplicationContext(),
                        "User cancelled video recording", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to record video
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to record video", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    private void previewCapturedImage() throws FileNotFoundException {
        try {
            // hide video preview
            // videoPreview.setVisibility(View.GONE);
            int compressionRatio = 2;
            imageView.setVisibility(View.VISIBLE);

            // bimatp factory
            BitmapFactory.Options options = new BitmapFactory.Options();

            // downsizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 8;
            imgPath = fileUri.getPath();
            final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(),
                    options);
            //bitmap.compress(Bitmap.CompressFormat.JPEG,compressionRatio,new FileOutputStream(String.valueOf(fileUri)));
            imageView.setImageBitmap(bitmap);

            //imgPath = getRealPathFromURI(this, fileUri);

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {
                    MediaStore.Images.Media.DATA
            };
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    // Load spinner url for working status from server
    private void loadSpinnerWorkingStatus(String working_status_url) {

        StringRequest stringRequest = new StringRequest(working_status_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    session.workingStatusSession(response);
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        String status_name = jsonObject.getString("working_status");

                        working_status.add(status_name);


                    }
                    spinner_status.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, working_status));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

            }
        });
        VolleySingleton.getInstance(MainActivity.this).addToRequestQueue(stringRequest);

    }

    // Load spinner url for location from server
    /*private void loadSpinnerLocation(final String location_url)
    {
        StringRequest stringRequest=new StringRequest(location_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                try
                {
                    session.locationSession(response);
                    JSONArray jsonArray=new JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); i++)
                        {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String location_name = jsonObject.getString("location");

                            location.add(location_name);

                        }

                    spinner_location.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item,location));

                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
            },new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                error.printStackTrace();

            }
        });
        VolleySingleton.getInstance(MainActivity.this).addToRequestQueue(stringRequest);
    }
*/
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Spinner spinner = (Spinner) parent;

        switch (spinner.getId()) {
            case R.id.spinner_location:

                //guard_locationStr=spinner_location.getSelectedItem().toString();

                break;
            case R.id.spinner_status:

                working_statusStr = spinner_status.getSelectedItem().toString();

                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Do code here for onNothing selected
    }

    // For Logout
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.item_logout:
                /*SessionManager.getInstance(this).logout();
                finish();*/

                androidLogout();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    // For getting Location
    public HashMap<String, String> getlocation() {
        HashMap<String, String> map = new HashMap<String, String>();

        // Create class object
        locationService = new LocationService(MainActivity.this);

        // Check if GPS enabled
        if (locationService.canGetLocation()) {
            double latitude = locationService.getLatitude();
            double longitude = locationService.getLongitude();
            map.put(LATSTR, String.valueOf(latitude));
            map.put(LONGSTR, String.valueOf(longitude));
        } else {

            locationService.showSettingsAlert();
        }
        return map;
    }

    private void uploadImageAndDataToServer() {
        //final String name = editText_guard.getText().toString();
        //String locationString = autoCompleteTextView.getText().toString();

        if (imgPath.equals("")) {
            Toast.makeText(this, "Image not selected", Toast.LENGTH_LONG).show();
            return;
        }
        if (nameStr.equals("") || nameStr.length() < 3 || nameStr.length() > 20) {
            editText_guard.setError("Please enter guard name");
            editText_guard.requestFocus();
            return;
        }
        if (locationStr.equals("")) {
            Toast.makeText(this, "Location is not selected", Toast.LENGTH_LONG).show();
            return;
        }

        if (locationStrItem == null) {
            Toast.makeText(this, "Please select valid location from suggestions", Toast.LENGTH_LONG).show();
            return;
        }
        if (checkNetworkConnection()) {
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Loading.....");
            progressDialog.setCancelable(false);
            progressDialog.show();
            guardInfo.setStatus(DBContract.SYNC_STATUS_OK);
            idStr = saveToLocalStorage(guardInfo);

            VolleyMultipart volleyMultipart = new VolleyMultipart(Request.Method.POST, HttpURL.UPDATE_URL, new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    try {

                        String resultResponse = new String(response.data);
                        JSONObject jsonObject = new JSONObject(resultResponse);
                        if (jsonObject.getString("success").equals("1")) {
                            guardInfo.setStatus(DBContract.SYNC_STATUS_OK);
                            Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            startActivity(new Intent(MainActivity.this, MainActivity.class));
                            finish();
                        } else if (jsonObject.getString("Failed").equals("0")) {
                            guardInfo.setStatus(DBContract.SYNC_STATUS_FAILED);
                            saveToLocalStorage(guardInfo);
                            Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            startActivity(new Intent(MainActivity.this, MainActivity.class));
                            finish();

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    NetworkResponse networkResponse = error.networkResponse;
                    String errorMessage = "Unknown error";
                    if (networkResponse == null) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            errorMessage = "Request timeout";
                        } else if (error.getClass().equals(NoConnectionError.class)) {
                            errorMessage = "Failed to connect server";
                        }
                    } else {
                        String result = new String(networkResponse.data);
                        try {

                            JSONObject response = new JSONObject(result);

                            Log.e("Error Status", result);

                            String status = response.getString("status");
                            String message = response.getString("message");

                            Log.e("Error Status", status);
                            Log.e("Error Message", message);

                            if (networkResponse.statusCode == 404) {
                                errorMessage = "Resource not found";
                            } else if (networkResponse.statusCode == 401) {
                                errorMessage = message + " Please login again";
                            } else if (networkResponse.statusCode == 400) {
                                errorMessage = message + " Check your inputs";
                            } else if (networkResponse.statusCode == 500) {
                                errorMessage = message + " Something is getting wrong";
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    Log.i("Error", errorMessage);
                    error.printStackTrace();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();

                    params.put("name", nameStr);
                    params.put("location", locationIdItem);
                    params.put("working_status", working_statusStr);
                    params.put("latitude", String.valueOf(lantStr));
                    params.put("langitute", String.valueOf(longStr));
                    params.put("dateTime", dateTime);
                    params.put("user_id", session.getUserID());
                    params.put("guard_id", idStr);
                    return params;
                }

                @Override
                protected Map<String, DataPart> getByteData() {
                    Map<String, DataPart> params = new HashMap<>();
                    params.put("file_name", new DataPart("file_image.jpg", convertFileToByteArray(new File(imgPath)), "image/jpeg"));
                    return params;

                }

                @Override
                protected Response<NetworkResponse> parseNetworkResponse(NetworkResponse response) {
                    if (response.headers == null) {
                        // cant just set a new empty map because the member is final.
                        response = new NetworkResponse(
                                response.statusCode,
                                response.data,
                                Collections.<String, String>emptyMap(), // this is the important line, set an empty but non-null map.
                                response.notModified,
                                response.networkTimeMs);
                    }
                    return super.parseNetworkResponse(response);
                }
            };
            int socketTimeout = 60000; // 60 seconds. You can change it
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

            volleyMultipart.setRetryPolicy(policy);
            //AppController.getInstance().addToRequestQueue(postRequest);

            VolleySingleton.getInstance(this).addToRequestQueue(volleyMultipart);

        } else {

            guardInfo.setStatus(DBContract.SYNC_STATUS_FAILED);
            saveToLocalStorage(guardInfo);
            startActivity(new Intent(MainActivity.this, MainActivity.class));
            finish();

        }
    }

    public static byte[] convertFileToByteArray(File file) {
        byte[] byteArray = null;
        int compressionRatio = 2;
        try {
            InputStream inputStream = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024 * 8];
            int bytesRead = 0;

            Bitmap bitmap = BitmapFactory.decodeFile (file.getPath ());
            bitmap.compress (Bitmap.CompressFormat.JPEG, compressionRatio, new FileOutputStream (file));


            while ((bytesRead = inputStream.read(b)) != -1) {
                bos.write(b, 0, bytesRead);
            }

            byteArray = bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArray;
    }

    // Method to read name from sqlite database
    private void readFromLocalStorage() {

        try {
            //arrayList.clear();
            DBHelper databaseHelper = new DBHelper(this);
            SQLiteDatabase database = databaseHelper.getReadableDatabase();
            Cursor cursor = databaseHelper.readFromLocalDatabase(database);
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex(DBContract.COLUMN_NAME));
                String working_status = cursor.getString(cursor.getColumnIndex(DBContract.COLUMN_WORKING_STATUS));
                String location = cursor.getString(cursor.getColumnIndex(DBContract.COLUMN_LOCATION));
                String latitude = cursor.getString(cursor.getColumnIndex(DBContract.COLUMN_LATITUDE));
                String longitude = cursor.getString(cursor.getColumnIndex(DBContract.COLUMN_LONGITUDE));
                String dateTime = cursor.getString(cursor.getColumnIndex(DBContract.COLUMN_DATETIME));
                byte[] image = cursor.getBlob(cursor.getColumnIndex(DBContract.COLUMN_IMAGE));
                int sync_status = cursor.getInt(cursor.getColumnIndex(DBContract.COLUMN_SYNC_STATUS));
            }
            //adapter.notifyDataSetChanged();
            cursor.close();
            databaseHelper.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // Method to check the network connection
    public boolean checkNetworkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return (networkInfo != null && networkInfo.isConnected());
    }

    // Method to save name to sqlite database
    public String saveToLocalStorage(GuardInfo guardInfo) {
        try {
            DBHelper databaseHelper = new DBHelper(this);
            SQLiteDatabase database = databaseHelper.getWritableDatabase();

            String id = databaseHelper.saveToLocalDatabase(guardInfo, database);
            readFromLocalStorage();
            databaseHelper.close();


            return id;

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }


    public void saveSpinnerOffline() {

        try {
            if (checkNetworkConnection()) {
                autoCompleteLocation(HttpURL.LOCATION_URL);
            } else {
                jsonArray = new JSONArray(session.getLocation());
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String location = jsonObject.getString("location");
                    String location_id = jsonObject.getString("l_id");

                    autocomplete_location.add(location);

                }

                autoCompleteTextView.setThreshold(1);
                autoCompleteTextView.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_dropdown_item_1line, autocomplete_location));
                autoCompleteTextView.setOnItemClickListener(this);
                autoCompleteTextView.setOnFocusChangeListener(this);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Save offline spinner data for working status
        try {
            // Spinner url from server
            if (checkNetworkConnection()) {
                loadSpinnerWorkingStatus(HttpURL.STATUS_URL);
            } else {
                JSONArray jsonArray = new JSONArray(session.getWorkingStatus());
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    String status_name = jsonObject.getString("working_status");

                    working_status.add(status_name);

                }
                spinner_status.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, working_status));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click back again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);

        //finish();
    }

    /*
     * Creating file uri to store image/video
     */
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /*
     * returning image / video
     */
    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create " +
                        IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    private void autoCompleteLocation(final String location) {

        StringRequest stringRequest = new StringRequest(location, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    session.locationSession(response);
                    jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        String location = jsonObject.getString("location");
                        String location_id = jsonObject.getString("l_id");

                        autocomplete_location.add(location);


                    }
                    autoCompleteTextView.setThreshold(1);
                    autoCompleteTextView.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_dropdown_item_1line, autocomplete_location));
                    autoCompleteTextView.setOnItemClickListener(MainActivity.this);
                    autoCompleteTextView.setOnFocusChangeListener(MainActivity.this);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

            }
        });
        VolleySingleton.getInstance(MainActivity.this).addToRequestQueue(stringRequest);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        locationStrItem = parent.getAdapter().getItem(position).toString();

        System.out.println("locationStrItem " + locationStrItem);
        for (int i = 0; i < jsonArray.length(); i++) {

            try {
                if (jsonArray.getJSONObject(i).getString("location").equals(locationStrItem)) {
                    locationIdItem = jsonArray.getJSONObject(i).getString("l_id");
                    System.out.println("locationIdItem " + locationIdItem);

                    autoCompleteTextView.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            locationStrItem = null;


                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    protected void onDestroy() {
        unregisterReceiver(networkCheck);

        super.onDestroy();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {

            if (locationStrItem == null) {
                autoCompleteTextView.setError("Select location from list");
            }

        }
    }

    public void hideKeyboard()
    {
        autoCompleteTextView.setInputType( InputType.TYPE_TEXT_VARIATION_URI );
        autoCompleteTextView.setOnKeyListener(new View.OnKeyListener() {
            /**
             * This listens for the user to press the enter button on
             * the keyboard and then hides the virtual keyboard
             */
            public boolean onKey(View arg0, int arg1, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (arg1 == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(autoCompleteTextView.getWindowToken(), 0);
                    }
                    return true;
                }
                return false;
            }

        });
    }


    private void androidLogout()
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, HttpURL.LOGOUT_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try
                {
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getString("success").equals("1"))
                    {
                        if (checkNetworkConnection())
                        {
                            SessionManager.getInstance(MainActivity.this).logout();
                            finish();
                        }
                        else
                            Toast.makeText(MainActivity.this,"Please check your network connection",Toast.LENGTH_SHORT).show();
                    }

                }
                catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id",session.getUserID());
                params.put("login_status",loginStatus);
                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

}
