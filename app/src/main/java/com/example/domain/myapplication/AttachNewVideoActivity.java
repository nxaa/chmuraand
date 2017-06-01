package com.example.domain.myapplication;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.domain.myapplication.requests.RequestService;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AttachNewVideoActivity extends AppCompatActivity {

    static final int REQUEST_VIDEO_CAPTURE = 1;
    private static final int MAP_REQUEST_CODE = 73;
    private String tripId;
    private String pointId;
    private String mCurrentVideoString;
    private RequestService requestService = new RequestService();
    HashMap<Integer,String> spinnerMap;
    List<String> spinnerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        this.tripId = intent.getStringExtra("tripId");
        this.pointId = intent.getStringExtra("pointId");
        setContentView(R.layout.activity_attach_new_video);
        dispatchTakeVideoIntent();
        addItemsToSpinner();
        attachEventListenerToSpinner();
    }

    private void attachEventListenerToSpinner() {
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tripId = spinnerMap.get(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                // sometimes you need nothing here
            }
        });
    }

    public void sendOnClick(View view) {
        EditText xText = (EditText) findViewById(R.id.xText);
        EditText yText = (EditText) findViewById(R.id.yText);
        if(xText.getText().toString().equals("") || yText.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "Uzupełnij lokalizację", Toast.LENGTH_SHORT).show();
            return;
        }
        String content = requestService.postMedia(tripId, mCurrentVideoString, xText.getText().toString(), yText.getText().toString());
        //TODO: TUTAJ COS TRZEBA ZROBIC Z TYM ZE SIE WYSYLA :P
        AlertDialog alertDialog = new AlertDialog.Builder(AttachNewVideoActivity.this).create();
        alertDialog.setTitle("Dziala");
        alertDialog.setMessage(content);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();
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
        else if(requestCode == MAP_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            String lat = intent.getStringExtra("lat");
            String lng = intent.getStringExtra("lng");
            EditText xText = (EditText) findViewById(R.id.xText);
            EditText yText = (EditText) findViewById(R.id.yText);
            xText.setText(lat);
            yText.setText(lng);
        }
    }

    private void addItemsToSpinner() {
        Spinner spinner2 = (Spinner) findViewById(R.id.spinner);

        HashMap<String, String> map = requestService.getTripsHashMap();
        if(map == null) {
            Toast.makeText(getApplicationContext(), "Błąd pobierania listy wycieczek", Toast.LENGTH_SHORT).show();
        }
        else {
            int position = 0;
            spinnerMap = new HashMap<>();
            spinnerList = new ArrayList<String>();
            int i = 0;
            for (String key : map.keySet())
            {
                if(key.equals(tripId)) position = i;
                spinnerMap.put(i,key);
                spinnerList.add(i,map.get(key));
                i++;
            }
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, spinnerList);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner2.setAdapter(dataAdapter);
            spinner2.setSelection(position);
        }
    }

    public void mapOnClick(View view) {
        Intent i = new Intent(this, MapLocationActivity.class);
        startActivityForResult(i, MAP_REQUEST_CODE);
    }
}
