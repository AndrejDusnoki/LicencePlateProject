package com.example.andul.licenceplatecapture;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


import static android.content.ContentValues.TAG;

/**
 * Created by andul on 10/27/2016.
 */

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private  Camera.PreviewCallback mCallback;
    private Context mContext;
    private ArrayList<ImageObject> mImageArray = new ArrayList<>();//Array of image data and time when picture was takem
    private ArrayList<String> mImagePaths =new ArrayList<>();//Containes file paths of captured images that need to be converted
    private SurfaceHolder mHolder;//Holder for camera preview
    private Camera mCamera;//instance of camera
    private FrameLayout mCameraView;
    public boolean ButtonClicked;
    public boolean ShotLimiterChanged=false;
    public CameraPreview(final Context context, Camera camera, FrameLayout CameraView, final ImagesTakenHandler handler) {
        super(context);
        this.mContext = context;
        mCamera = camera;
        this.mCameraView =CameraView;
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        mCallback =new Camera.PreviewCallback() {
            public void onPreviewFrame(byte[] imageData, Camera arg1) {
                //method that gets called every time a frame is previewed
                final byte[] data = imageData;//contains image data from preview
                int shotLimiter=ShotValueSingleton.getInstance().getShotLimiter();//get value of ShotLimiter from singleton
                Log.d(TAG,"CALLED BACK"+ String.valueOf(mCamera.getParameters().getPreviewFormat()));
                if (ButtonClicked && mImagePaths.size()<shotLimiter) {
                    //Method called when record button is clicked, and runs till it reaches shotLimiter value
                    ImageObject image = new ImageObject();//Create new object that can hold image data and time of capture
                    try{
                        //Add image data and timestamp to image object, and write image data to new file on disk
                        image.setImage(data);
                        image.setTimeStamp(String.valueOf(new SimpleDateFormat("yyyyMMdd_HHmmssSS").format(new Date())));
                        NewFile ImageFile = new NewFile();//Creates new file on disk
                        FileOutputStream fos = new FileOutputStream(ImageFile.CreateFile(image.getTimeStamp(),context));
                        mImagePaths.add(ImageFile.GetFilePath());//Adds filepath to array
                        fos.write(image.getImage());//Writes data to created file
                        fos.close();
                    }catch (OutOfMemoryError e){
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.d("ImageArray", String.valueOf(mImageArray.size()));
                }
                if(mImagePaths.size()==shotLimiter){
                    //if number of written files equal to shotLimiter Convert files to jpg and call onMaxImagesTaken from MainActivity
                    ButtonClicked=false;
                    ToastPhotosTaken();
                    SavePhotos();
                    handler.onMaxImagesTaken();
                }
                CameraPreview.this.invalidate();
            }
        };
    }
    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            Log.d("CAMERA CHANGED", "CAMERA CHANGDE!!!");
            mCamera.setPreviewCallback(mCallback);
            mCamera.setPreviewDisplay(mHolder);
            holder.setFixedSize(1920, 1080);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d("TAG", "Error setting camera preview: " + e.getMessage());
        }
    }
    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.

        if(mCamera!=null){
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }

    }
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.
        if (mHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }
        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }
        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try {
            mCamera.setPreviewCallback
                    (mCallback);
            mCamera.setPreviewDisplay(mHolder);
            holder.setFixedSize(1920, 1080);
            mCamera.startPreview();
        } catch (Exception e) {
            Log.d("TAG", "Error starting camera preview: " + e.getMessage());
        }
    }
    class SavePhotoTask extends AsyncTask<byte[], String, String> {
        //Converts captured images to jpeg

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mCamera.setPreviewCallback(null);//Remove callback so images can't be taken till jpeg conversion finishes
            Toast toast = Toast.makeText(mContext, "Converting images to jpeg", Toast.LENGTH_SHORT);
            toast.show();
        }
        @Override
        protected void onPostExecute(String s) {
           super.onPostExecute(s);
            mCamera.setPreviewCallback(mCallback);//Upon completion of image conversion attach callback to camera
            Log.d("ADDED IMAGES", "ADDED IMAGES");
            Toast toast = Toast.makeText(mContext, "Images converted : " + String.valueOf(mImagePaths.size()), Toast.LENGTH_SHORT);
            toast.show();
            mImagePaths.clear();//Clear filepaths for new files to be added
        }
        @Override
        protected String doInBackground(byte[]... params) {
            //loops for captured images size
            for (int i = 0; i < mImagePaths.size(); i++) {
                Camera.Parameters parameters = mCamera.getParameters();
                Camera.Size size = parameters.getPreviewSize();//gets preview size needed for image conversion
                try {//Get captured image and convert it to bytes
                    File file=new File(mImagePaths.get(i));
                    FileInputStream inputStream=new FileInputStream(file);
                    byte [] imageBytes=IOUtils.toByteArray(inputStream);
                    //make new yuv image object witch will be converted to jpeg
                    YuvImage image = new YuvImage(imageBytes, parameters.getPreviewFormat(),
                            size.width, size.height,null);
                    NewFile ImageFile = new NewFile();
                    File pictureFile = ImageFile.ConvertedFile(mImagePaths.get(i));//Create a new file in witch will put converted jpeg
                    FileOutputStream filecon = new FileOutputStream(pictureFile);
                    image.compressToJpeg(
                            new Rect(0, 0, image.getWidth(), image.getHeight()), 90,//Writes jpeg to ImageFile
                            filecon);
                    file.delete();//Remove original non compressed image file
                } catch (FileNotFoundException e) {
                    Log.d(TAG, "File not found: " + e.getMessage());
                } catch (IOException e) {
                    Log.d(TAG, "Error accessing file: " + e.getMessage());
                } catch (IllegalArgumentException e) {
                    Log.d("YUV is null", "YUV IS NULL");
                }
            }
            return null;
        }
    }
    ;
    public void SavePhotos() {
        SavePhotoTask savePhotoTask = new SavePhotoTask();
        savePhotoTask.execute();
    }

    public void ToastPhotosTaken() {
        //Notifies user about number of images captured
        Toast toast = Toast.makeText(mContext, "Shots taken: " + String.valueOf(mImagePaths.size()), Toast.LENGTH_SHORT);
        toast.show();
    }
}
