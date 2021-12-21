package com.example.hl.test01;


import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.util.Log;


class AccessibilityUtil{
    private static String TAG = "AccessibilityUtil";

    // To check if service is enabled

     static boolean isAccessibilitySettingsOn(Context context) {
        int accessibilityEnabled = 0;
        try {
            accessibilityEnabled = Settings.Secure.getInt(context.getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
            // 获取当前ACCESSIBILITY状态值
        } catch (Settings.SettingNotFoundException e) {
            Log.i(TAG, e.getMessage());
        }

        if (accessibilityEnabled == 1) {
            String services = Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            //Settings.Secure.getString获取设置里的系统属性值
            if (services != null) {
                if (services.toLowerCase().contains(context.getPackageName().toLowerCase())){
                    return true;
                }else{
                    DialogFragment newFragment = FirstDialog.newInstance(
                            R.string.dialog_title);
                    newFragment.show(newFragment.getFragmentManager(), "dialog");

                    return false; //todo:跳转设置页面
                }
                }
            }
        else{
            return false;

        }

        return false;
    }
}
