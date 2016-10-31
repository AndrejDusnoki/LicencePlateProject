package com.example.andul.licenceplatecapture;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by andul on 10/30/2016.
 */

public class DialogBuilder {
    private TextView mTvChooseFolder;//Text View for choosing file manager
    private NumberPicker mPicker;//Pick value for ShotLimiter
    private Button mBtnApply;//Calls CompletionHandler from MainActivity

    public DialogBuilder(final Context context, final CompletionHandler handler) {
        //Create dialog
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final AlertDialog OptionDialog = builder.create();
        LayoutInflater inflater = (LayoutInflater)context.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
        View v=inflater.inflate(R.layout.dialog_view,null);
        mTvChooseFolder= (TextView) v.findViewById(R.id.tvChooseFolder);
        mPicker = (NumberPicker) v.findViewById(R.id.numPicker);
        mTvChooseFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //If click on ChooseFolder textview, start FileManagerActivity
               OptionDialog.dismiss();
               handler.onFileManagerStart();
            }
        });
        mBtnApply = (Button) v.findViewById(R.id.btnApply);
        //Set max and min and default values for number picker
        mPicker.setMaxValue(100);
        mPicker.setMinValue(1);
        mPicker.setValue(ShotValueSingleton.getInstance().getShotLimiter());//gets previously chosen ShotLimiter
        mBtnApply.setOnClickListener(new View.OnClickListener() {
            //On click save new value of ShotLimiter to SharedPrefs and singleton,
            // after witch dialog dismmises and onComplete gets called from main Activity
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = context.getSharedPreferences("MyPrefs", MODE_PRIVATE).edit();
                editor.putInt("ShotLimiter", mPicker.getValue());
                editor.commit();
                ShotValueSingleton.getInstance().setShotLimiter(mPicker.getValue());
                handler.onComplete();
                OptionDialog.dismiss();
            }
        });
        //Set view for dialog and display it to user
        OptionDialog.setView(v);
        OptionDialog.show();
    }


}
