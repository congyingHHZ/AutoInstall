package com.example.hl.test01;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Context mContext = this;
        boolean serverState = AccessibilityUtil.isAccessibilitySettingsOn(mContext);

        if (!serverState){
            DialogFragment newFragment = FirstDialog.newInstance(
                    R.string.dialog_title);
            newFragment.show(getSupportFragmentManager(), "dialog");
        }


    }

}

