package com.example.andul.licenceplatecapture;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.hardware.Camera.Parameters.FOCUS_MODE_AUTO;
import static android.hardware.Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE;

public class MainActivity extends Activity implements CompletionHandler,ImagesTakenHandler {
    private static final String TAG= MainActivity.class.getSimpleName();
    private ImagesTakenHandler mImgHandler;//Handler for limiting number of pictures taken
    private CompletionHandler mCompletionHandler;//Handler for listening when apply button in settings dialog is pressed
    private FrameLayout mFrameLeftShadow;//frame that shadows left side of screen
    private FrameLayout mFrameRightShadow;//frame that shadows right side of screen
    private String mTimeStarted;//holds time when image capture starts
    private ImageButton mBtnSnapshot;//button for starting image capture
    private ImageButton mBtnSettings;//button that calls settings dialog
    private FrameLayout mFrameTopShadow;//frame that shadows top side of screen
    private FrameLayout mFrameBottomShadow;//frame that shadows bottom side of screen
    private FrameLayout mFrameCameraView;//Frame that contains camera preview
    private CameraPreview mCameraPreview;//Previews camera input
    public Context context;
    private Camera mCamera;//holds instance of camera
    private static  final int FOCUS_AREA_SIZE= 300;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context=this;
        mCompletionHandler =this;
        mImgHandler =this;
        checkCameraHardware(this);//checks if device supports all parameters needed for application to work
        mCamera=getCameraInstance(); //gets instance of camera
        mFrameCameraView = (FrameLayout) findViewById(R.id.surfaceView);
        mCameraPreview = new  CameraPreview(this,mCamera, mFrameCameraView, mImgHandler);
        mFrameCameraView.addView(mCameraPreview);
        mFrameTopShadow = (FrameLayout) findViewById(R.id.TopShadow);
        mFrameBottomShadow = (FrameLayout) findViewById(R.id.BottomShadow);
        //Get display size and put width and height into variables
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int windth=displaymetrics.widthPixels;
        //Sets top and bottom shadow height to third of screen height
        mFrameTopShadow.getLayoutParams().height=height/3;
        mFrameBottomShadow.getLayoutParams().height=height/3;
        mFrameLeftShadow = (FrameLayout) findViewById(R.id.LeftShadow);
        mFrameRightShadow = (FrameLayout) findViewById(R.id.RightShadow);
        //Set lef and right shadow width to eight of screen size
        mFrameLeftShadow.getLayoutParams().width=windth/8;
        mFrameRightShadow.getLayoutParams().width=windth/8;
        //Set transparency of shadows to 50%
        mFrameLeftShadow.setAlpha(0.5f);
        mFrameRightShadow.setAlpha(0.5f);
        mFrameTopShadow.setAlpha(0.5f);
        mFrameBottomShadow.setAlpha(0.5f);
        mBtnSnapshot = (ImageButton) findViewById(R.id.btnTakeImage);
        mBtnSnapshot.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mCameraPreview.ButtonClicked){
                            //if button clicked true set icon of record button to blue icon
                            Drawable drawable=context.getResources().getDrawable(R.drawable.record_btn_1);
                                    mBtnSnapshot.setImageDrawable(drawable);
                                    mCameraPreview.ButtonClicked=false;
                                    mCameraPreview.ToastPhotosTaken();//Notify user of numer of shots taken
                            mCameraPreview.SavePhotos();//Convret taken photos to jpg
                            DateFormat df = new SimpleDateFormat("HH:mm:ss:SSS");
                            String date = df.format(Calendar.getInstance().getTime());
                            Log.d(TAG,"TIME FINISHED: "+date+" TIME STARTED :"+ mTimeStarted);//Log length of time that img capture session took
                        }
                        else {
                            //if button not clikced set icon of recor button to red icon
                            Drawable drawable=context.getResources().getDrawable(R.drawable.record_btn_2);
                            mBtnSnapshot.setImageDrawable(drawable);
                            DateFormat df = new SimpleDateFormat("HH:mm:ss:SSS");
                            String date = df.format(Calendar.getInstance().getTime());
                            mTimeStarted =date;
                            mCameraPreview.ButtonClicked=true;
                        }
                    }
                }
        );
        mBtnSettings = (ImageButton) findViewById(R.id.btnSettings);
        mBtnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //On click call settings dialog
                DialogBuilder builder=new DialogBuilder(context, mCompletionHandler);
            }
        });
//        setFocus();
    }
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            //device doesn't have camera, warn user
            Toast toast=Toast.makeText(this,"Camera not found on this device", Toast.LENGTH_LONG);
            toast.show();
            return false;
        }
    }
    public  Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
            List<Camera.Size>sizes=c.getParameters().getSupportedPreviewSizes();
            if(sizes.contains(1920)){//Check if device supports 1080p preview, if yes set it
                c.getParameters().setPreviewSize(1920,1080);
                c.getParameters().setPictureSize(1920,1080);//set picture size
            }
            else {
                //if device doesnt support 1080p, set max preview size
                c.getParameters().setPreviewSize(sizes.get(sizes.size()).width,sizes.get(sizes.size()).height);
                c.getParameters().setPictureSize(sizes.get(sizes.size()).width,sizes.get(sizes.size()).height);
            }
            Log.d(TAG,"CAMERA OPENED");
        }
        catch (Exception e){
            // Camera is not available
        }
        return c; // returns null if camera is unavailable
    }
    private void setFocus(){
        Camera.Parameters params = mCamera.getParameters();
        if (params.getSupportedFlashModes().contains(FOCUS_MODE_CONTINUOUS_PICTURE)){ // check that continuous focus mode is supported
            params.setFocusMode(FOCUS_MODE_AUTO);
            params.setFocusMode(FOCUS_MODE_CONTINUOUS_PICTURE);
        }
        else {
            params.setFocusMode(FOCUS_MODE_AUTO);
        }
        mCamera.setParameters(params);
    }
    @Override
    protected void onResume() {
        super.onResume();
        ShotValueSingleton.getInstance().setShotLimiter(getShotLimiter());//Add shared preference value for ShotLimiter to singleton
        //empty camera view, release camera , get new camera instance and reconnect it with preview
        mFrameCameraView.removeAllViews();
        mCamera.release();
        mCamera=getCameraInstance();
        if (mCamera!=null){
            mCameraPreview = new  CameraPreview(this,mCamera, mFrameCameraView, mImgHandler);
            mFrameCameraView.addView(mCameraPreview);
        }
        mFrameCameraView.setOnTouchListener(new FrameLayout.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mCamera != null) {
                    //Set focus on touch
                    mCamera.cancelAutoFocus();
                    //Get user touch paramteres and convert them to rect for focus
                    Rect focusRect = calculateTapArea(event.getX(), event.getY());

                    Camera.Parameters parameters = mCamera.getParameters();
                    //If camera not set to focus mode auto, set it
                    if (parameters.getFocusMode() != Camera.Parameters.FOCUS_MODE_AUTO) {
                        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                    }
                    //If camera supports focusAreas, set focus area to calculated focus area from focusRect
                    if (parameters.getMaxNumFocusAreas() > 0) {
                        List<Camera.Area> mylist = new ArrayList<Camera.Area>();
                        mylist.add(new Camera.Area(focusRect, 1000));
                        parameters.setFocusAreas(mylist);
                    }
                    try {
                        mCamera.cancelAutoFocus();
                        mCamera.setParameters(parameters);//add paramaters to camera
                        mCamera.startPreview();
                        mCamera.autoFocus(new Camera.AutoFocusCallback() {
                            @Override
                            public void onAutoFocus(boolean success, Camera camera) {
                                //if camera not set to continuos focus mode, set it
                                if (camera.getParameters().getSupportedFocusModes().contains(FOCUS_MODE_CONTINUOUS_PICTURE
                                    )&&camera.getParameters().getFocusMode() != Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE){
                                    Camera.Parameters parameters = camera.getParameters();
                                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                                    //remove focus areas
                                    if (parameters.getMaxNumFocusAreas() > 0) {
                                        parameters.setFocusAreas(null);
                                    }
                                    camera.setParameters(parameters);
                                    camera.startPreview();
                                }
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return false;
            }
        });
    }
    private Rect calculateTapArea(float x, float y) {
        //Convert user touch params to rect for focus
        int left = (Float.valueOf((x / mCameraPreview.getWidth()) * 2000 - 1000).intValue());
        int top = (Float.valueOf((y / mCameraPreview.getHeight()) * 2000 - 1000).intValue());
        return new Rect(left, top, left + FOCUS_AREA_SIZE, top + FOCUS_AREA_SIZE);
    }
    private int getShotLimiter(){
        //return value ShotLimiter value from sharedPrefs
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        return prefs.getInt("ShotLimiter",20);
    }
    @Override
    public void onComplete() {
        //Notify cameraPreview that ShotLimiter was changed
       mCameraPreview.ShotLimiterChanged=true;
    }

    @Override
    public void onFileManagerStart() {
        Intent intent=new Intent(context,FileManagerActivity.class);
        mCameraPreview.surfaceDestroyed(mCameraPreview.getHolder());
        context.startActivity(intent);
        this.finish();
    }

    @Override
    public void onMaxImagesTaken() {
        ///gets called when number of images taken reaches ShotLimiter value
        Drawable drawable=context.getResources().getDrawable(R.drawable.record_btn_1);//Set record button to blue icon
        mBtnSnapshot.setImageDrawable(drawable);
        DateFormat df = new SimpleDateFormat("HH:mm:ss:SSS");
        String date = df.format(Calendar.getInstance().getTime());
        Log.d(TAG,"TIME FINISHED: "+date+" TIME STARTED :"+ mTimeStarted);//Show duration of image capture
    }

    @Override
    public void onBackPressed() {
        //If this wasn't here, app would sometimes go into Splashscreen
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory( Intent.CATEGORY_HOME );
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(homeIntent);
    }
}
