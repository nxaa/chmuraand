package com.example.domain.myapplication;

import android.graphics.Bitmap;

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
