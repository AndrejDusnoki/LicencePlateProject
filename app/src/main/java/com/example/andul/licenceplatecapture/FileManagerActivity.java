package com.example.andul.licenceplatecapture;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;

public class FileManagerActivity extends Activity {
   private RecyclerView mRecyclerView;
   private RecylerViewAdapter mAdapter;
   private RecyclerView.LayoutManager mManager;
   private Button btnFolderSelect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_manager);
        mRecyclerView = (RecyclerView) findViewById(R.id.RecyclerView);
        btnFolderSelect= (Button) findViewById(R.id.btnFolderSelect);
        mAdapter = new RecylerViewAdapter(this);
        mManager =new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mManager);
        mRecyclerView.setAdapter(mAdapter);
        btnFolderSelect.setOnClickListener(mAdapter.getFolderSelectListener());
    }

    @Override
    public void onBackPressed() {
        CompletionHandler handler=mAdapter.handler;
        handler.onComplete();
    }
}
