package com.example.andul.licenceplatecapture;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

/**
 * Created by andul on 11/4/2016.
 */

public class MarshMallowPermission {
    public static final int WRTIE_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 1;
    public static final int CAMERA_PERMISSION_REQUEST_CODE = 2;
    Activity activity;
    PermissionHandler handler;

    public MarshMallowPermission(Activity activity,PermissionHandler handler) {
        this.activity = activity;
        this.handler=handler;
    }

    public void checkPermissionForCamera(){//Checks if permission for camera granted, if not prompt user
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST_CODE);
        }
        else {
            handler.onPermissioCameraGranted();
        }

    }

    public void checkPermissionForExternalStorage(){//Checks if permission for write to external storage granted, if not prompt user
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    WRTIE_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE);

        }
        else {
            handler.onPermissionStorageGranted();
        }

    }



}
