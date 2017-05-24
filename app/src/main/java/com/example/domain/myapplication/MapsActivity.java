package com.example.domain.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Scanner;

import static com.example.domain.myapplication.Config.API_URL;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener {

    private String tripId="1";
    private GoogleMap mMap;
    private Polyline polyline;
    private ArrayList<Marker_MapElement> marker_mapelements;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if(intent.hasExtra("tripId")) {
            this.tripId = intent.getStringExtra("tripId");
        }

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(this);

        marker_mapelements=new ArrayList<Marker_MapElement>();
        PolylineOptions rectOptions = new PolylineOptions();
        polyline = mMap.addPolyline(rectOptions);

        this.refreshFromWeb();
    }

    public void refreshFromWeb(){
        for (Marker_MapElement m_el: marker_mapelements) {
            m_el.marker.remove();
        }
        marker_mapelements.clear();


        ArrayList<MapElement> mapElements =  this.getMapElements();

        double latitude=0;
        double longitude=0;
        for (MapElement el: mapElements) {
            latitude+=el.pos.latitude;
            longitude+=el.pos.longitude;

            MarkerOptions mOptions=this.mapElementToMarkerOptions(el);
            Marker mmm=mMap.addMarker(mOptions);

            marker_mapelements.add(new Marker_MapElement(mmm, el));
        }

        if(mapElements.size()!=0){
            latitude/=mapElements.size();
            longitude/=mapElements.size();
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude)));
        }
       ;
        updatePolyline();
    }
    public void updatePolyline(){
        polyline.remove();
        PolylineOptions rectOptions = new PolylineOptions();
        for (Marker_MapElement m_el: marker_mapelements) {
            rectOptions.add(m_el.el.pos);
        }
        polyline = mMap.addPolyline(rectOptions);
    }

    public Date getLastDate(){
        Date date=new Date(1);
        if(marker_mapelements.size()>0){
            date=marker_mapelements.get(marker_mapelements.size() - 1).el.date;
        }
        return date;
    }


    public MarkerOptions mapElementToMarkerOptions(MapElement el){
        MarkerOptions mOptions=new MarkerOptions().position(el.pos).title(el.title);
        try {
            URL url = new URL(API_URL+"trip/photos_miniature/"+tripId+"/"+String.valueOf(el.iconID));//+String.valueOf(el.iconID)
            Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            Bitmap bmpSized=Bitmap.createScaledBitmap(bmp, 60, 60, false);
            mOptions.icon(BitmapDescriptorFactory.fromBitmap(bmpSized));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return mOptions;
    }


    public ArrayList<MapElement> getMapElements() {
        ArrayList<MapElement> arr=new  ArrayList<MapElement>();

        arr.add(new MapElement(new LatLng(51,20),new String("Polska1"),10,new Date(1)));
        arr.add(new MapElement(new LatLng(53,20),new String("Polska3"),11,new Date(3)));
        arr.add(new MapElement(new LatLng(52,21),new String("Polska4"),12,new Date(4)));
        arr.add(new MapElement(new LatLng(52,19),new String("Polska2"),13,new Date(2)));

        Collections.sort(arr);
        return arr;
    }
/****************************************************
 ***************   Events ***************************
*****************************************************/
    @Override
    public void onMapClick(LatLng point) {
        MapElement mapElement=new MapElement(point,this.getLastDate().toString(),0,this.getLastDate());
        MarkerOptions mOptions=this.mapElementToMarkerOptions(mapElement);
        Marker mmm=mMap.addMarker(mOptions);
        marker_mapelements.add(new Marker_MapElement(mmm, mapElement));
        updatePolyline();
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        MapElement el=null;
        for (Marker_MapElement m_el: marker_mapelements) {
            if(m_el.marker.equals(marker)){
                el=m_el.el;
                break;
            }
        }
        if(el==null){
            return true;
        }
        try {


            URL url = new URL(Config.API_URL+"photos/"+String.valueOf(el.iconID));
            Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());

            Config.imageToDisplay=bmp;
            //Intent goToNextActivity = new Intent(getApplicationContext(), DisplayImageActivity.class);
            //startActivity(goToNextActivity);
            MainActivity.startAddMediaActivity(this, tripId, String.valueOf(el.pointId));

        } catch (Exception e) {
            e.printStackTrace();
        }



        return true;
    }



















    /****************************************************
     ***************   Helper Classes *******************
     *****************************************************/
    private class MapElement implements Comparable<MapElement> {
        LatLng pos;
        String title;
        int iconID;
        Date date;
        int pointId;

        public MapElement(LatLng ppos, String ptitle, int piconID, Date pdate) {
            pos = ppos;
            title = ptitle;
            iconID = piconID;
            date = pdate;
        }
        @Override
        public int compareTo(MapElement o) {
            return date.compareTo(o.date);
        }
    }
    private class Marker_MapElement  implements Comparable<Marker_MapElement> {
        MapElement el;
        Marker marker;

        public Marker_MapElement(Marker pmarker, MapElement pel) {
            el = pel;
            marker = pmarker;
        }

        @Override
        public int compareTo(Marker_MapElement o) {
            return el.compareTo(o.el);
        }
    }
}
