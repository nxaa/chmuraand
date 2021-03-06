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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.data.Geometry;
import com.google.maps.android.data.geojson.GeoJsonFeature;
import com.google.maps.android.data.geojson.GeoJsonLayer;
import com.google.maps.android.data.geojson.GeoJsonLineString;
import com.google.maps.android.data.geojson.GeoJsonMultiLineString;
import com.google.maps.android.data.geojson.GeoJsonMultiPoint;
import com.google.maps.android.data.geojson.GeoJsonMultiPolygon;
import com.google.maps.android.data.geojson.GeoJsonPoint;
import com.google.maps.android.data.geojson.GeoJsonPolygon;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import static com.example.domain.myapplication.Config.API_URL;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener {

    private String tripId = "1";
    private String tripName = "";
    private String tripDate = "";

    private TextView tripNameLabel;
    private TextView tripDateLabel;

    private GoogleMap mMap;
    private Polyline polyline;
    private ArrayList<Marker_MapElement> marker_mapelements;
    GeoJsonLayer layer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent.hasExtra("tripId")) {
            this.tripId = intent.getStringExtra("tripId");
        }
        if (intent.hasExtra("tripName")) {
            this.tripName = intent.getStringExtra("tripName");
        }
        if (intent.hasExtra("tripDate")) {
            this.tripDate = intent.getStringExtra("tripDate");
        }

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_maps);

        // tytul wycieczki
        tripNameLabel= (TextView) findViewById(R.id.tripName);
        tripNameLabel.setText(tripName);
        tripDateLabel= (TextView) findViewById(R.id.tripDateMap);
        tripDateLabel.setText(tripDate);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(this);

        marker_mapelements = new ArrayList<Marker_MapElement>();
        PolylineOptions rectOptions = new PolylineOptions();
        polyline = mMap.addPolyline(rectOptions);
        layer = new GeoJsonLayer(mMap, new JSONObject());

        this.refreshFromWeb();
    }

    @Override
    public void onResume(){  //After a pause OR at startup
        super.onResume();
        if(layer != null) {
            this.refreshFromWeb();
            Config.defaultLat="";
            Config.defaultLng="";
        }
    }


    public void refreshFromWeb() {
        for (Marker_MapElement m_el: marker_mapelements) {
            m_el.marker.remove();
        }
        marker_mapelements.clear();

        ArrayList<MapElement> mapElements =  this.getMapElements();

        for (MapElement el: mapElements) {
            MarkerOptions mOptions=this.mapElementToMarkerOptions(el);
            Marker mmm=mMap.addMarker(mOptions);

            marker_mapelements.add(new Marker_MapElement(mmm, el));
        }
        updatePolyline();


        JSONObject geoJSON = getGeoJSON();
        Log.w("Trip str", geoJSON.toString());
        layer.removeLayerFromMap();
        layer = new GeoJsonLayer(mMap, geoJSON);
        layer.addLayerToMap();

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(getBoundingFromLayer(), 200);
        mMap.moveCamera(cu);

    }

    public LatLngBounds getBoundingFromLayer() {
        List<LatLng> coordinates = new ArrayList<>();
        for (GeoJsonFeature feature : layer.getFeatures()) {
            if(feature.hasGeometry()) {
                Geometry gg = feature.getGeometry();
                coordinates.addAll(getCoordinatesFromGeometry(gg));
            }
        }

        for (Marker_MapElement el : marker_mapelements) {
            if (el.el.pos.latitude < 90.0){
                coordinates.add(el.el.pos);
            }else{
                Log.w("c ignored", el.el.pos.toString());
            }
        }

        LatLngBounds.Builder builder = LatLngBounds.builder();
        for (LatLng latLng : coordinates) {
            builder.include(latLng);
        }

        if(coordinates.isEmpty()){
            builder.include(new LatLng(47, 15));
            builder.include(new LatLng(55, 23));
        }

        LatLngBounds boundingBoxFromBuilder = builder.build();

        return boundingBoxFromBuilder;
    }

    private List<LatLng> getCoordinatesFromGeometry(Geometry geometry) {

        List<LatLng> coordinates = new ArrayList<>();

        // GeoJSON geometry types:
        // http://geojson.org/geojson-spec.html#geometry-objects

        switch (geometry.getGeometryType()) {
            case "Point":
                coordinates.add(((GeoJsonPoint) geometry).getCoordinates());
                break;
            case "MultiPoint":
                List<GeoJsonPoint> points = ((GeoJsonMultiPoint) geometry).getPoints();
                for (GeoJsonPoint point : points) {
                    coordinates.add(point.getCoordinates());
                }
                break;
            case "LineString":
                coordinates.addAll(((GeoJsonLineString) geometry).getCoordinates());
                break;
            case "MultiLineString":
                List<GeoJsonLineString> lines =
                        ((GeoJsonMultiLineString) geometry).getLineStrings();
                for (GeoJsonLineString line : lines) {
                    coordinates.addAll(line.getCoordinates());
                }
                break;
            case "Polygon":
                List<? extends List<LatLng>> lists =
                        ((GeoJsonPolygon) geometry).getCoordinates();
                for (List<LatLng> list : lists) {
                    coordinates.addAll(list);
                }
                break;
            case "MultiPolygon":
                List<GeoJsonPolygon> polygons =
                        ((GeoJsonMultiPolygon) geometry).getPolygons();
                for (GeoJsonPolygon polygon : polygons) {
                    for (List<LatLng> list : polygon.getCoordinates()) {
                        coordinates.addAll(list);
                    }
                }
                break;
        }


        return coordinates;
    }

    public void updatePolyline() {
        polyline.remove();
        PolylineOptions rectOptions = new PolylineOptions();
        for (Marker_MapElement m_el : marker_mapelements) {
            rectOptions.add(m_el.el.pos);
        }
        polyline = mMap.addPolyline(rectOptions);
    }

    public Date getLastDate() {
        Date date = new Date(1);
        if (marker_mapelements.size() > 0) {
            date = marker_mapelements.get(marker_mapelements.size() - 1).el.date;
        }
        return date;
    }

    public JSONObject getGeoJSON() {
        String result = Config.downloadDataFromURL(Config.API_URL + "trips/" + tripId);
        Log.w("Trip GeoJson url", Config.API_URL + "trips/" + tripId);
        Log.w("Trip GeoJson result", result);
        JSONObject obj = new JSONObject();
        try {
            JSONObject ret = new JSONObject(result);
            obj = new JSONObject(ret.getString("tripData"));
        } catch (Exception e) {
            // default file
            try {
                StringBuilder buf = new StringBuilder();
                InputStream json = getAssets().open("geojson.json");
                BufferedReader in = new BufferedReader(new InputStreamReader(json, "UTF-8"));
                String str;

                while ((str = in.readLine()) != null) {
                    buf.append(str);
                }

                in.close();
                obj = new JSONObject(buf.toString());
            } catch (Exception ee) {
                ee.printStackTrace();
            }
            Log.w("Trip GeoJson web err", "Bas gejson from web");
        }
        return obj;
    }

    public MarkerOptions mapElementToMarkerOptions(MapElement el) {
        MarkerOptions mOptions = new MarkerOptions().position(el.pos).title(el.title);
        try {
            URL url = new URL(el.iconURL);//+String.valueOf(el.iconID)
            Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            Bitmap bmpSized = Bitmap.createScaledBitmap(bmp, 120, 120, false);
            mOptions.icon(BitmapDescriptorFactory.fromBitmap(bmpSized));

        } catch (Exception e) {
            Log.w("File Error", "Cannot download: " + el.iconURL);
            //e.printStackTrace();
        }
        return mOptions;
    }

    public ArrayList<MapElement> getMapElements() {
        ArrayList<MapElement> arr = new ArrayList<MapElement>();

        String result = Config.downloadDataFromURL( Config.API_URL + "trips/"+tripId+"/media");
        Log.w("Trip data url", Config.API_URL + "trips/"+tripId+"/media");
        Log.w("Trip data result", result);
        try {
           // JSONArray medias= JSONArray(result);
            JSONArray jTripMedias = new JSONArray(result);
            for (int k = 0; k < jTripMedias.length(); k++) {
                JSONObject jMedia = jTripMedias.getJSONObject(k);

                if(jMedia.isNull("lat") || jMedia.isNull("lng") || jMedia.isNull("url")){
                    continue;
                }
                Log.w("h",jMedia.toString());
                String filename="no name";

                String mediaId = jMedia.getString("id");
                String mediaType = jMedia.getString("type");
                String mediaURL = jMedia.getString("url");
                String mediaLat = jMedia.getString("lat");
                String mediaLng = jMedia.getString("lng");

                if(!jMedia.isNull("fileName")){
                    filename = jMedia.getString("fileName");
                }
                if(!jMedia.isNull("minUrl")){
                    mediaURL = jMedia.getString("minUrl");
                }

                //if (mediaType.equals("Photo")) {
                arr.add(new MapElement(new LatLng(Float.valueOf(mediaLat), Float.valueOf(mediaLng)), filename , mediaURL, new Date(k), mediaId));
                //}

            }


        } catch (Exception e) {
            Log.e("Exception", "Error: " + e.toString());
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Błąd pobierania listy punktów", Toast.LENGTH_SHORT).show();
        }


        Collections.sort(arr);
        return arr;
    }

    /****************************************************
     ***************   Events ***************************
     *****************************************************/
    @Override
    public void onMapClick(LatLng point) {
        MapElement mapElement = new MapElement(point, this.getLastDate().toString(), "", this.getLastDate(), "");
        MarkerOptions mOptions = this.mapElementToMarkerOptions(mapElement);
        Marker mmm = mMap.addMarker(mOptions);
        marker_mapelements.add(new Marker_MapElement(mmm, mapElement));
        updatePolyline();
        Config.defaultLat=String.valueOf(point.latitude);
        Config.defaultLng=String.valueOf(point.longitude);
        MainActivity.startAddMediaActivity(this, tripId, "1");//"1" is a dummy value
        //onMarkerClick(mmm);
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        MapElement el = null;
        for (Marker_MapElement m_el : marker_mapelements) {
            if (m_el.marker.equals(marker)) {
                el = m_el.el;
                break;
            }
        }
        if (el == null) {
            return true;
        }
        try {


            URL url = new URL(el.iconURL);
            Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());

            Config.imageToDisplay = bmp;
            Intent goToNextActivity = new Intent(getApplicationContext(), DisplayImageActivity.class);
            startActivity(goToNextActivity);

        } catch (Exception e) {
            Log.w("File photo", "Cannot download: " + el.iconURL);
        }
        //MainActivity.startAddMediaActivity(this, tripId, el.pointId);


        return true;
    }


    /****************************************************
     ***************   Helper Classes *******************
     *****************************************************/
    private class MapElement implements Comparable<MapElement> {
        LatLng pos;
        String title;
        String iconURL;
        Date date;
        String pointId;

        public MapElement(LatLng ppos, String ptitle, String piconURL, Date pdate, String ppointId) {
            pos = ppos;
            title = ptitle;
            iconURL = piconURL;
            date = pdate;
            pointId = ppointId;
        }

        @Override
        public int compareTo(MapElement o) {
            return date.compareTo(o.date);
        }
    }

    public class Marker_MapElement implements Comparable<Marker_MapElement> {
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
