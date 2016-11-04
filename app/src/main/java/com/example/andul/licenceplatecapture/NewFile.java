package com.example.andul.licenceplatecapture;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by andul on 10/27/2016.
 */

public  class NewFile {
    private String Filepath;

    public File CreateFile(String TimeStamp,Context context) {


        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(getImageDirectory(context));
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){

                Log.d("MyCameraApp", "failed to create directory");
            }
        }

        // Create a media file name

        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "IMG_"+ TimeStamp );
        Filepath=mediaStorageDir.getPath() + File.separator +
                "IMG_"+ TimeStamp ;

        return mediaFile;
    }
    public File ConvertedFile(String path){
        //Create file for converted image
        File convretedImageFile = new File(path+".jpg");
        return convretedImageFile;
    }
    public String GetFilePath(){
        return Filepath;
    }
    private String getImageDirectory(Context context){
        //Return chosen file direcotry
        SharedPreferences prefs = context.getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String imageDirectory = prefs.getString("FolderDirectory",Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES)+"/"+ "MyCameraApp");
        return imageDirectory;
    }
}
