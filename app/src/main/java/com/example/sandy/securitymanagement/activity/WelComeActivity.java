package com.example.sandy.securitymanagement.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;

import com.example.sandy.securitymanagement.R;

/**
 * Created by sandy on 21/11/17.
 */

public class WelComeActivity extends AppCompatActivity
{
    private static int SPLASH_TIME_OUT = 3000;

    ProgressBar progressDialog;

    //Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);
        progressDialog=findViewById(R.id.progressBar);
        SplashTask splashTask=new SplashTask();
        splashTask.execute();
    }

    private class SplashTask extends AsyncTask<Object,Object,Object>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=new ProgressBar(WelComeActivity.this);
            progressDialog.setVisibility(View.VISIBLE);
            progressDialog.getIndeterminateDrawable().setColorFilter(Color.rgb(240,136,120), PorterDuff.Mode.MULTIPLY);
        }

        @Override
        protected Object doInBackground(Object[] objects)
        {
            try
            {
                Thread.sleep(SPLASH_TIME_OUT);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            return null;
        }



        @Override
        protected void onPostExecute(Object object) {
            super.onPostExecute(object);

            Intent intent=new Intent(WelComeActivity.this, LoginActivity.class);
            startActivity(intent);
            progressDialog.setVisibility(View.GONE);
        }

        @Override
        protected void onProgressUpdate(Object[] values) {
            super.onProgressUpdate(values);
        }
    }

}
