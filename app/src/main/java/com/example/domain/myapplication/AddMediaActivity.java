package com.example.domain.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class AddMediaActivity extends AppCompatActivity {

    private String tripId;
    private String pointId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        this.tripId = intent.getStringExtra("tripId");
        this.pointId = intent.getStringExtra("pointId");
        setContentView(R.layout.activity_add_media);
    }

    public void addNewPhotoOnClick(View view) {
        Intent intent = new Intent(this, AttachNewPictureActivity.class);
        intent.putExtra("tripId", tripId);
        intent.putExtra("pointId", pointId);
        startActivity(intent);
    }

    public void addNewVideoOnClick(View view) {
        Intent intent = new Intent(this, AttachNewVideoActivity.class);
        intent.putExtra("tripId", tripId);
        intent.putExtra("pointId", pointId);
        startActivity(intent);
    }

    public void addExistingPhotoOnClick(View view) {
        Intent intent = new Intent(this, AttachExistingMediaActivity.class);
        intent.putExtra("mediaExtension", ".png");
        intent.putExtra("tripId", tripId);
        intent.putExtra("pointId", pointId);
        startActivity(intent);
    }

    public void addExistingVideoOnClick(View view) {
        Intent intent = new Intent(this, AttachExistingMediaActivity.class);
        intent.putExtra("mediaExtension", ".avi");
        intent.putExtra("tripId", tripId);
        intent.putExtra("pointId", pointId);
        startActivity(intent);
    }
}
