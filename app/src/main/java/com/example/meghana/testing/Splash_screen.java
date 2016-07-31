package com.example.meghana.testing;

import android.content.Context;
import android.content.Intent;
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
                    if (!getSharedPreferences("login", Context.MODE_PRIVATE).getString("login", "false").equals("false")) {
                        intent = new Intent(Splash_screen.this, NavDrawerActivity.class);
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
