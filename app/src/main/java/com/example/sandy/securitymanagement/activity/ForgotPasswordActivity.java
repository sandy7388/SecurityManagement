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

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener
{
    private EditText usernameEdt_forgot,passwordEdt_forgot,confirm_passwordEdt_forgot;

    private Button btn_reset_password;

    ProgressDialog progressBar;

    private String usernameStr_forgot,passwordStr_forgot,confirm_passwordStr_forgot;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        Toolbar toolbar = findViewById(R.id.toolbar_forgot);
        setSupportActionBar(toolbar);
        usernameEdt_forgot=findViewById(R.id.username_forgot);
        passwordEdt_forgot=findViewById(R.id.enter_forgot_password);
        confirm_passwordEdt_forgot=findViewById(R.id.confirm_forgot_password);
        btn_reset_password=findViewById(R.id.button_reset_password);
        btn_reset_password.setOnClickListener(this);
        progressBar=new ProgressDialog(this);
        setTitle("Forgot Password");

        if(getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        // String parameters
        usernameStr_forgot=usernameEdt_forgot.getText().toString();
        passwordStr_forgot=passwordEdt_forgot.getText().toString();
        confirm_passwordStr_forgot=confirm_passwordEdt_forgot.getText().toString();

        // Take username from previous activity
        usernameEdt_forgot.setText(getIntent().getExtras().getString("username"));

    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.button_reset_password:
                ResetPassword();
                break;
        }
    }

    private void ResetPassword()
    {
        usernameStr_forgot=usernameEdt_forgot.getText().toString();
        passwordStr_forgot=passwordEdt_forgot.getText().toString();
        confirm_passwordStr_forgot=confirm_passwordEdt_forgot.getText().toString();

        if (passwordStr_forgot.equals(""))
        {
            passwordEdt_forgot.setError("Please enter your new password");
            passwordEdt_forgot.requestFocus();
            return;

        }
        if (!passwordStr_forgot.equals(confirm_passwordStr_forgot))
        {
            confirm_passwordEdt_forgot.setError("Password does not matching");
            confirm_passwordEdt_forgot.requestFocus();
            confirm_passwordEdt_forgot.setText("");
            passwordEdt_forgot.setText("");
            return;
        }

        progressBar.setMessage("Loading");
        progressBar.show();
        progressBar.setCancelable(true);
        StringRequest stringRequest=new StringRequest(Request.Method.POST, HttpURL.FORGOT_PASSWORD_URL,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        try
                        {
                            JSONObject jsonObject=new JSONObject(response);

                            if (jsonObject.getString("success").equals("0"))
                            {
                                progressBar.dismiss();
                                Toast.makeText(getApplicationContext(),jsonObject.getString("message"),Toast.LENGTH_SHORT).show();

                            }

                            if (jsonObject.getString("success").equals("1"))
                            {
                                progressBar.dismiss();
                                Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                                startActivity(new Intent(ForgotPasswordActivity.this,LoginActivity.class));
                                finish();
                            }
                            else if (jsonObject.getString("success").equals("0"))
                            {
                                progressBar.dismiss();
                                Toast.makeText(getApplicationContext(),jsonObject.getString("message"),Toast.LENGTH_SHORT).show();

                            }
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener()
                {

                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        error.printStackTrace();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> params=new HashMap<String, String>();
                params.put("username_forgot",usernameStr_forgot);
                params.put("password_forgot",passwordStr_forgot);
                params.put("confirm_password",confirm_passwordStr_forgot);

                return params;
            }
        };

        VolleySingleton.getInstance(ForgotPasswordActivity.this).addToRequestQueue(stringRequest);

    }
}
