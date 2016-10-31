package com.example.andul.licenceplatecapture;

/**
 * Created by andul on 10/28/2016.
 */

public class ImageObject {
    //Contains image data and timestam form capture
    private byte[] Image;
    private String TimeStamp;

    public byte[] getImage() {
        return Image;
    }

    public void setImage(byte[] image) {
        Image = image;
    }

    public String getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        TimeStamp = timeStamp;
    }
}
