package com.example.domain.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.domain.myapplication.requests.RequestService;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class AttachNewPictureActivity extends AppCompatActivity {

    static final int REQUEST_TAKE_PHOTO = 1;
    private static final int MAP_REQUEST_CODE = 73;
    private String tripId;
    private String pointId;
    private static Context context;
    private String mCurrentPhotoPath;
    private RequestService requestService = new RequestService();
    HashMap<Integer,String> spinnerMap;
    List<String> spinnerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getApplicationContext();
        Intent intent = getIntent();
        this.tripId = intent.getStringExtra("tripId");
        this.pointId = intent.getStringExtra("pointId");
        setContentView(R.layout.activity_attach_new_picture);
        dispatchTakePictureIntent();
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

    public void retakePhotoOnClick(View view) {
        dispatchTakePictureIntent();
    }

    public void sendOnClick(View view) {
        EditText xText = (EditText) findViewById(R.id.xText);
        EditText yText = (EditText) findViewById(R.id.yText);
        if(xText.getText().toString().equals("") || yText.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "Uzupełnij lokalizację", Toast.LENGTH_SHORT).show();
            return;
        }
        String content = requestService.postMedia(tripId, mCurrentPhotoPath, xText.getText().toString(), yText.getText().toString());
        //TODO: TUTAJ COS TRZEBA ZROBIC Z TYM ZE SIE WYSYLA :P
        AlertDialog alertDialog = new AlertDialog.Builder(AttachNewPictureActivity.this).create();
        alertDialog.setTitle("Status");
        alertDialog.setMessage("OK");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();
    }

    public void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File outputDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        File outputDir = context.getFilesDir();
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                outputDir       /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case REQUEST_TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    ImageView mImageView = (ImageView) findViewById(R.id.myImageView);
                    mImageView.setImageBitmap(BitmapFactory.decodeFile(mCurrentPhotoPath));
                }
                break;
            case MAP_REQUEST_CODE:
                if(resultCode == Activity.RESULT_OK) {
                    String lat = data.getStringExtra("lat");
                    String lng = data.getStringExtra("lng");
                    EditText xText = (EditText) findViewById(R.id.xText);
                    EditText yText = (EditText) findViewById(R.id.yText);
                    xText.setText(lat);
                    yText.setText(lng);
                }
                break;
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
