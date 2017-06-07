package com.example.domain.myapplication;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.domain.myapplication.requests.RequestService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class AttachExistingMediaActivity extends AppCompatActivity {

    private static final int READ_REQUEST_CODE = 42;
    private static final int MAP_REQUEST_CODE = 73;
    private String tripId;
    private String pointId;
    private String mediaExtension;
    private Uri uri;
    private static Context context;
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
        this.mediaExtension = intent.getStringExtra("mediaExtension");
        setContentView(R.layout.activity_attach_existing_media);
        addItemsToSpinner();
        attachEventListenerToSpinner();
        EditText xText = (EditText) findViewById(R.id.xText);
        EditText yText = (EditText) findViewById(R.id.yText);
        xText.setText(Config.defaultLat);
        yText.setText(Config.defaultLng);
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

    public void sendOnClick(View view) {
        EditText xText = (EditText) findViewById(R.id.xText);
        EditText yText = (EditText) findViewById(R.id.yText);
        EditText filePathText = (EditText) findViewById(R.id.filePathText);
        if(filePathText.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "Wybierz plik", Toast.LENGTH_SHORT).show();
            return;
        }
        if(xText.getText().toString().equals("") || yText.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "Uzupełnij lokalizację", Toast.LENGTH_SHORT).show();
            return;
        }
        String content = requestService.postMedia(tripId, xText.getText().toString(), xText.getText().toString(), yText.getText().toString());
        //TODO: TUTAJ COS TRZEBA ZROBIC Z TYM ZE SIE WYSYLA :P
        AlertDialog alertDialog = new AlertDialog.Builder(AttachExistingMediaActivity.this).create();
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

    public void findFileClick(View view) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
        intent.setType("image/" + mediaExtension.substring(1));

        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            if (resultData != null) {
                uri = resultData.getData();
                EditText mFilePathText = (EditText) findViewById(R.id.filePathText);
                mFilePathText.setText(getPath(context, uri));
            }
        }
        else if(requestCode == MAP_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            String lat = resultData.getStringExtra("lat");
            String lng = resultData.getStringExtra("lng");
            EditText xText = (EditText) findViewById(R.id.xText);
            EditText yText = (EditText) findViewById(R.id.yText);
            xText.setText(lat);
            yText.setText(lng);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public void mapOnClick(View view) {
        Intent i = new Intent(this, MapLocationActivity.class);
        startActivityForResult(i, MAP_REQUEST_CODE);
    }
}
