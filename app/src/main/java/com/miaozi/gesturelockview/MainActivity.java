package com.miaozi.gesturelockview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GestureLockView gestureLockView = findViewById(R.id.lock);
        gestureLockView.setCorrectValue("0124678");
        gestureLockView.setLockValueCallBack(new GestureLockView.LockValueCallBack() {
            @Override
            public void valueCallBack(String value) {
                Log.d("TAG","value="+value);
            }
        });
    }
}
