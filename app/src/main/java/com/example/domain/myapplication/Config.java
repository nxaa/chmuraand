package com.example.domain.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by pacman on 5/10/2017.
 */

public class Config {
    public static final String API_URL="http://tripper-api.azurewebsites.net/";


    //0 means no object
    public static  int tripID=0;
    public static  int tripElementID=0;
    public static Bitmap imageToDisplay;

    public static boolean sendDeleteUrl(Context context, String urlStr) {
        try {
            Log.w("DELETE url",urlStr);
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded" );
            connection.setRequestMethod("DELETE");
            connection.connect();
            int code = connection.getResponseCode();
            Log.w("DELETE response code",String.valueOf(code));
            Toast.makeText(context, "Usunięto", Toast.LENGTH_SHORT).show();
            return true;
        }catch(Exception e){
            Toast.makeText(context, "Błąd zaptyania DELETE", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public static String downloadDataFromURL(String url){
        String out="";
        try {
            out = new Scanner(new URL(url).openStream(), "UTF-8").useDelimiter("\\A").next();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return out;
    }
}
