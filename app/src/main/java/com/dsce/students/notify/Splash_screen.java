package com.dsce.students.notify;

import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Splash_screen extends AppCompatActivity {

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1500);
                    if (PreferenceManager.getDefaultSharedPreferences(Splash_screen.this).getBoolean(Constants.LOGIN_PREF,false)) {
                        intent = new Intent(Splash_screen.this, NewsFeedActivity.class);
                    }
                    else {
                        intent = new Intent(Splash_screen.this, Login_students.class);
                    }
                    startActivity(intent);
                    finish();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

    }
}
