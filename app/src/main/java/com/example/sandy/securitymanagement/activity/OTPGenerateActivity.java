package com.example.sandy.securitymanagement.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.sandy.securitymanagement.R;
import com.example.sandy.securitymanagement.httpClient.HttpURL;
import com.example.sandy.securitymanagement.volley.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class OTPGenerateActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText usernameEdt, otpEdt;
    private Button btn_mobile_username, btn_otp_submit;
    private String usernameStr;
    JSONObject jsonObject;
    ProgressDialog progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpgenerate);
        Toolbar toolbar = findViewById(R.id.toolbar_generate_otp);
        setSupportActionBar(toolbar);
        usernameEdt = findViewById(R.id.editText_Mobile_username);
        otpEdt = findViewById(R.id.editText_otp);
        btn_mobile_username = findViewById(R.id.button_generate_otp);
        btn_otp_submit = findViewById(R.id.button_submit_otp);
        progressBar = new ProgressDialog(this);
        btn_otp_submit.setOnClickListener(this);
        btn_mobile_username.setOnClickListener(this);
        otpEdt.setEnabled(false);
        setTitle("Generate OTP");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_generate_otp:
                GenerateOTP();
                break;
            case R.id.button_submit_otp:
                SubmitOTP();
                break;

        }
    }
    private void SubmitOTP() {
        try {
            if (jsonObject.getString("message").equals(otpEdt.getText().toString())) {
                progressBar.show();
                Toast.makeText(OTPGenerateActivity.this, "OTP Matched", Toast.LENGTH_SHORT).show();
                progressBar.dismiss();
                Intent intent = new Intent(OTPGenerateActivity.this, ForgotPasswordActivity.class);

                // Send username to next ativity
                intent.putExtra("username", usernameEdt.getText().toString());
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(OTPGenerateActivity.this, "OTP does not matched, Please enter valid OTP and try again !", Toast.LENGTH_SHORT).show();
                //Log.i("Message","Please enter valid OTP to forgot your password");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void GenerateOTP() {
        usernameStr = usernameEdt.getText().toString();

        if (usernameStr.equals("")) {
            usernameEdt.setError("Please enter your valid username");
            usernameEdt.requestFocus();
        }
        progressBar.setMessage("Please wait...");
        progressBar.setCancelable(true);
        progressBar.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, HttpURL.GENERATE_OTP_URL, new Response.Listener < String > () {
            @Override
            public void onResponse(String response) {
                try {
                    jsonObject = new JSONObject(response);

                    if (jsonObject.getString("success").equals("1")) {
                        Toast.makeText(OTPGenerateActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        otpEdt.setEnabled(true);
                        progressBar.dismiss();
                        //otpEdt.setText(jsonObject.getString(response));
                    } else if (jsonObject.getString("success").equals("2")) {
                        Toast.makeText(OTPGenerateActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        progressBar.dismiss();

                    } else {
                        Toast.makeText(OTPGenerateActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        progressBar.dismiss();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

            }
        }) {
            @Override
            protected Map < String, String > getParams() throws AuthFailureError {
                Map < String, String > params = new HashMap < > ();
                params.put("username", usernameStr);
                return params;
            }
        };

        VolleySingleton.getInstance(OTPGenerateActivity.this).addToRequestQueue(stringRequest);
    }
}