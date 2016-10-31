package com.example.andul.licenceplatecapture;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by andul on 10/30/2016.
 */

public class RecylerViewAdapter extends RecyclerView.Adapter<RecylerViewAdapter.CustomViewHolder> implements CompletionHandler{
    public CompletionHandler handler;
    private static final String TAG= FileManagerActivity.class.getSimpleName();
    private FileDirectory mFileDirectory =new FileDirectory(); //Returns file names and paths from added path
    //Put file paths and file names from external storage root into mFeedItemList
    private ArrayList<FilePath> mFeedItemList = mFileDirectory.getFiles(Environment.getExternalStorageDirectory().getAbsolutePath());
    private Context mContext;
    private ArrayList<String> mNavigatedPaths =new ArrayList<String>();//Contains paths from navigated folders
    private ArrayList<String> mSelectedFolder =new ArrayList<>();//Contains filepath of selected folder, and all previous folders

    public RecylerViewAdapter(Context context) {
        this.mContext = context;
    }
    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        handler=this;
        //Get view for item nad pass to customViewHolder
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.rec_view_item, null);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(final CustomViewHolder customViewHolder, int i) {
        //Gets called for each item in mFeedItemList
            //same as above, except postiton of items is one less because currently in root folder, no need for back navigation
            customViewHolder.textView.setText(mFeedItemList.get(customViewHolder.getAdapterPosition()).getFileName());
            customViewHolder.textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mNavigatedPaths.size()==0){
                        //if navigated paths=null, that means its root directory
                        mNavigatedPaths.add((Environment.getExternalStorageDirectory().getAbsolutePath()));
                    }
                    else{
                        //if not in root, add to navigated paths mSelectedFolder wich contains all previously navigated directories
                        mNavigatedPaths.clear();
                        mNavigatedPaths.addAll(mSelectedFolder);
                    }
                    //add to mSelected folder current directory
                    mSelectedFolder.add(mFeedItemList.get(customViewHolder.getAdapterPosition()).getDirectory());
                    mFeedItemList = mFileDirectory.getFiles(mFeedItemList.get(customViewHolder.getAdapterPosition()).getDirectory());
                    notifyDataSetChanged();
                }
            });
    }
    @Override
    public int getItemCount() {
        //set number of items
        return (mFeedItemList.size());
    }

    @Override
    public void onComplete() {
        NavigateBack();
    }

    @Override
    public void onFileManagerStart() {

    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        //View holder for items
        protected ImageView imageView;
        protected TextView textView;

        public CustomViewHolder(View view) {
            super(view);
            this.imageView= (ImageView) view.findViewById(R.id.img);
            this.textView= (TextView) view.findViewById(R.id.itemName);
        }
    }
    public void NavigateBack(){
        if(mNavigatedPaths.size()==0){
            //if mNavigatedPaths.size is 0, navigate to root
            mFeedItemList=mFileDirectory.getFiles(Environment.getExternalStorageDirectory().getAbsolutePath());
            try{
                //if exception caught, that means it is in root directory, then start SplashScreen
                mSelectedFolder.remove(mSelectedFolder.size()-1);
            }catch (ArrayIndexOutOfBoundsException e){
                Intent intent=new Intent(mContext,SplashScreen.class);
                mContext.startActivity(intent);
            }

            notifyDataSetChanged();
        }else{
            //Navigate to most recent directory im mNavigatedPaths
            mFeedItemList=mFileDirectory.getFiles(mNavigatedPaths.get(mNavigatedPaths.size()-1));
            mNavigatedPaths.remove(mNavigatedPaths.size()-1);
            mSelectedFolder.remove(mSelectedFolder.size()-1);
            notifyDataSetChanged();
        }

    }
    public View.OnClickListener getFolderSelectListener(){
        View.OnClickListener listener=new View.OnClickListener() {
            //ClickListener from choose Folder buttom
            @Override
            public void onClick(View v) {
                //Add seleted folder filepath to sharedPrefs and navigate back to splashScreen
                SharedPreferences.Editor editor = mContext.getSharedPreferences("MyPrefs", MODE_PRIVATE).edit();
                editor.putString("FolderDirectory", mSelectedFolder.get(mSelectedFolder.size()-1));
                editor.commit();
                Log.d(TAG," Added to shared"+ mSelectedFolder.get(mSelectedFolder.size()-1));
                Intent intent=new Intent(mContext,SplashScreen.class);
                mContext.startActivity(intent);
            }
        };
                return listener;
    }
}
