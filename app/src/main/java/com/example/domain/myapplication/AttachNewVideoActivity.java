package com.example.domain.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.VideoView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AttachNewVideoActivity extends AppCompatActivity {

    static final int REQUEST_VIDEO_CAPTURE = 1;
    private String tripId;
    private String pointId;
    private String mCurrentVideoString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        this.tripId = intent.getStringExtra("tripId");
        this.pointId = intent.getStringExtra("pointId");
        setContentView(R.layout.activity_attach_new_video);
        dispatchTakeVideoIntent();
        addItemsToSpinner();
    }

    public void retakeVideoOnClick(View view) {
        dispatchTakeVideoIntent();
    }

    private void dispatchTakeVideoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            Uri videoUri = intent.getData();
            VideoView mVideoView = (VideoView) findViewById(R.id.myVideoView);
            mVideoView.setVideoURI(videoUri);
        }
    }

    private void addItemsToSpinner() {
        Spinner spinner2 = (Spinner) findViewById(R.id.spinner);
        List<String> list = new ArrayList<String>();
        list.add("trip 1");
        list.add("trip 2");
        list.add("trip 3");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(dataAdapter);
    }
}
