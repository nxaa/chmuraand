package com.example.domain.myapplication;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Intent goToNextActivity = new Intent(getApplicationContext(), LoginActivity.class);
        //Intent goToNextActivity = new Intent(getApplicationContext(), MapsActivity.class);
        //Intent goToNextActivity = new Intent(getApplicationContext(), DisplayImageActivity.class);
        //startActivity(goToNextActivity);
    }
    static  public void startAddMediaActivity(Activity activity, String tripId, String pointId) {
        Intent intent = new Intent(activity, AddMediaActivity.class);
        intent.putExtra("tripId", tripId);
        intent.putExtra("pointId", pointId);
        activity.startActivity(intent);
    }
    public void addMediaOnClick(View view) {
        startAddMediaActivity(this, null,null);
    }

    public void mapsOnClick(View view) {
        Intent intent = new Intent(getApplicationContext(), TripsListActivity.class);
        intent.putExtra("tripId", (String)null);
        startActivity(intent);
    }
    public void loginOnClick(View view) {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }
}
