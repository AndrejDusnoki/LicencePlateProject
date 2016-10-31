package com.example.andul.licenceplatecapture;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by andul on 10/30/2016.
 */

public class FileDirectory {
    public ArrayList<FilePath> getFiles(String filepath) {//Get Filepaths and filenames from input
        File rootFile=new File(filepath);//Creates new file from input
        ArrayList<FilePath> inFiles = new ArrayList<FilePath>();//List for holding Filepaths and filenames
        File[] files = rootFile.listFiles();
        for (File file : files) {
            //Loop for all files in directory
            if (file.isDirectory()) {
                //If filepath is directory add to array
                FilePath filePath=new FilePath();
                filePath.setDirectory(file.getAbsolutePath());
                filePath.setFileName(file.getName());
                inFiles.add(filePath);
            }
        }
        return inFiles;//return array of file paths and file names
    }
}
