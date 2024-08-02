package com.liaowei.music.common.utils;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionUtil {
    public static void checkPermission(Activity activity, String[] permissions, int code) {
        for (String permission : permissions) {
            int check = ContextCompat.checkSelfPermission(activity, permission);
            if (check != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, permissions, code);
            }
        }
    }
}
