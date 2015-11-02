package com.example.androidchoi.jobdam;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.example.androidchoi.jobdam.Manager.NetworkManager;
import com.example.androidchoi.jobdam.Manager.PropertyManager;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        String id = PropertyManager.getInstance().getId();
        if (!TextUtils.isEmpty(id)) {
            String password = PropertyManager.getInstance().getPassword();
            NetworkManager.getInstance().login(id, password, new NetworkManager.OnResultListener<String>() {
                @Override
                public void onSuccess(String result) {
                    if (result.equals("ok")) {
                        goMain();
                    } else {
                        goLogin();
                    }
                }

                @Override
                public void onFail(int code) {
                    goLogin();
                }
            });
        } else {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    goLogin();
                }
            }, 1000);
        }
    }
    Handler mHandler = new Handler(Looper.getMainLooper());

    private void goMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
    private void goLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
