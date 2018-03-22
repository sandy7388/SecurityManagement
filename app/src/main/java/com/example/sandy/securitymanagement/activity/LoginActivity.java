package com.example.sandy.securitymanagement.activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.sandy.securitymanagement.R;
import com.example.sandy.securitymanagement.httpClient.HttpURL;
import com.example.sandy.securitymanagement.model.LoginUser;
import com.example.sandy.securitymanagement.sharedPrefManager.SessionManager;
import com.example.sandy.securitymanagement.volley.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText editText_user, editText_pass;
    private Button button_login;
    private TextView forgot_passwordTxt;
    private String username, password;
    private ProgressDialog progressDialog;
    private SessionManager session;
    private String user_id,loginStats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        editText_user = findViewById(R.id.editText_username);
        editText_pass = findViewById(R.id.editText_password);
        button_login = findViewById(R.id.login);
        forgot_passwordTxt = findViewById(R.id.forgot_passwordTxt);
        button_login.setOnClickListener(this);
        forgot_passwordTxt.setOnClickListener(this);
        checkNetworkConnection();
        session = new SessionManager(this);

        // Check either login or not
        checkLoginFromServerSide();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login:
                LoginCheck();
                break;
            case R.id.forgot_passwordTxt:
                startActivity(new Intent(LoginActivity.this, OTPGenerateActivity.class));
                break;
        }

    }

    private void LoginCheck()
    {

        username = editText_user.getText().toString();
        password = editText_pass.getText().toString();

        if (username.equals("")) {
            editText_user.setError("Please enter your username");
            editText_user.requestFocus();
            return;
        }
        if (password.equals("")) {
            editText_pass.setError("Please enter your password");
            editText_pass.requestFocus();
            return;
        }

        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        progressDialog.setCancelable(true);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, HttpURL.LOGIN_URL, new Response.Listener < String > () {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getString("success").equals("1"))
                    {

                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        LoginUser loginUser = new LoginUser(username, password,jsonObject.getString("user_id"));
                        session.userLogin(loginUser);
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();

                    }
                    if (jsonObject.getString("success").equals("0"))
                    {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        editText_user.setText("");
                        editText_pass.setText("");

                    }
                    if(jsonObject.getString("success").equals("2"))
                    {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        editText_user.setText("");
                        editText_pass.setText("");
                    }
                }

                catch (JSONException e)
                {
                    e.printStackTrace();
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();

                        error.printStackTrace();
                    }
                }) {
            @Override
            protected Map < String, String > getParams() throws AuthFailureError {
                Map < String, String > params = new HashMap < String, String > ();
                params.put("username", username);
                params.put("password", password);

                return params;
            }

        };
        VolleySingleton.getInstance(LoginActivity.this).addToRequestQueue(stringRequest);
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
    }

    private void checkNetworkConnection()
    {
        ConnectivityManager conMgr =  (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = null;
        if (conMgr != null) {
            netInfo = conMgr.getActiveNetworkInfo();
        }
        if (netInfo == null){
            new AlertDialog.Builder(LoginActivity.this)
                    .setTitle("Network Connection")
                    .setMessage("Please check your internet connection and then try to login")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .show();
        }
    }

    public void checkLoginFromServerSide()
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, HttpURL.LOGIN_CHECK_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response)
                    {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("success").equals("1"))
                            {
                                if (SessionManager.getInstance(LoginActivity.this).isLoggedIn()) {
                                    finish();
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    Toast.makeText(LoginActivity.this,jsonObject.getString("message"),Toast.LENGTH_SHORT).show();

                                }
                            }

                            else if (jsonObject.getString("success").equals("0"))
                                {
                                    SessionManager.getInstance(LoginActivity.this).logout();
                                    finish();
                                    //startActivity(new Intent(LoginActivity.this,LoginActivity.class));

                                    Toast.makeText(LoginActivity.this,jsonObject.getString("message"),Toast.LENGTH_SHORT).show();
                                }
                            if(jsonObject.getString("success").equals("2"))
                            {
                                progressDialog.dismiss();
                                Toast.makeText(LoginActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                editText_user.setText("");
                                editText_pass.setText("");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id",session.getUserID());
                return params;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

}