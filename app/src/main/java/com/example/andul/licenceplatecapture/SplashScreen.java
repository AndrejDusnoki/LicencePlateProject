package com.example.andul.licenceplatecapture;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class SplashScreen extends Activity implements PermissionHandler {

    private MarshMallowPermission PermissionChecker;
    private boolean mCameraHasPermission;
    public static final int WRTIE_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 1;
    public static final int CAMERA_PERMISSION_REQUEST_CODE = 2;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        context = this;
        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.LOLLIPOP) {//if version marshamllow and above check permissions
            PermissionChecker = new MarshMallowPermission(this,this);
            PermissionChecker.checkPermissionForCamera();

        }
        else {
            Intent intent = new Intent(context, MainActivity.class);
            context.startActivity(intent);
        }


    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERMISSION_REQUEST_CODE: {//Callback for when permmision for camera checked
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mCameraHasPermission=true;
                    PermissionChecker.checkPermissionForExternalStorage();//checks permission for external storage
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    mCameraHasPermission=false;
                    Toast toast=Toast.makeText(this,"Permissions for camera and storage needed for app, closing app ",Toast.LENGTH_LONG);
                    toast.show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case WRTIE_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (mCameraHasPermission) {
                        Intent intent = new Intent(context, MainActivity.class);
                        context.startActivity(intent);//if permission granted start activity
                    }

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    Toast toast=Toast.makeText(this,"Permissions for camera and storage needed for app, closing app ",Toast.LENGTH_LONG);
                    toast.show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }


            // other 'case' lines to check for other
            // permissions this app might request
        }

    }


    @Override
    public void onPermissioCameraGranted() {
        mCameraHasPermission=true;
        PermissionChecker.checkPermissionForExternalStorage();
    }

    @Override
    public void onPermissionStorageGranted() {
        if(mCameraHasPermission){//if permissions previously added start activity
            Intent intent = new Intent(context, MainActivity.class);
            context.startActivity(intent);
        }
    }
}
