package com.gms_worldwide.androidboxappsdk.init;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Andrew Kochura.
 * <a href="https://www.linkedin.com/in/andrewkochura">LinkedIn</a>
 * <a href="https://github.com/andrewkochura">GitHub</a>
 */
public class SplashActivity extends AppCompatActivity {

    String TAG = SplashActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.gms_worldwide.hybersdk.R.layout.activity_splash);
    }

}