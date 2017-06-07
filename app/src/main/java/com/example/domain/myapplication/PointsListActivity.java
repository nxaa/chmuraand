package com.example.domain.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

public class PointsListActivity extends AppCompatActivity {

    private ArrayList<ListElement> list;
    ArrayList<String> stringList;
    private String tripId="1";
    private String tripName="";
    private ArrayAdapter<String> adapter;

    ListView listView ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_points_list);
        Intent intent = getIntent();

        if(intent.hasExtra("tripId")) {
            this.tripId = intent.getStringExtra("tripId");
        }
        if(intent.hasExtra("tripName")) {
            this.tripName = intent.getStringExtra("tripName");
        }

        list= getListElements();

        // Get ListView object from xml
        listView = (ListView) findViewById(R.id.points_list);
        stringList=new ArrayList<String>();
         for(ListElement el:list){
            stringList.add(el.title);
         }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, stringList);


        // Assign adapter to ListView
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?>adapter,View v, int position,long id){
                ListElement el=list.get(position);
                Log.w("wwwww",el.title);
                Log.w("w",String.valueOf(el.pointId));
                MainActivity.startAddMediaActivity(PointsListActivity.this, tripId, String.valueOf(el.pointId));
            }
        });

    }


    public ArrayList<ListElement> getListElements() {
        ArrayList<ListElement> list= new ArrayList<ListElement>();
        String result=Config.downloadDataFromURL(Config.API_URL+"trips/"+tripId);
        Log.w("Trip data url",Config.API_URL+"trips/"+tripId);
        Log.w("Trip data result",result);
        try {
            JSONObject jTrip = new JSONObject(result);
            String id=jTrip.getString("id");
            String name=jTrip.getString("name");
            String created=jTrip.getString("created");
            String description=jTrip.getString("description");
            if(jTrip.has("medias")){
                Object jTripMediasCheck = jTrip.get("medias");
                if (jTripMediasCheck instanceof JSONArray) {
                    JSONArray jTripMedias=jTrip.getJSONArray("medias");
                    for(int k=0; k < jTripMedias.length();k++) {
                        JSONObject jMedia = jTripMedias.getJSONObject(k);
                        String mediaId=jMedia.getString("id");
                        String mediaType=jMedia.getString("type");
                        String mediaURL=jMedia.getString("url");
                        String mediaMinURL=jMedia.getString("minUrl");
                        list.add(new ListElement(mediaId, mediaType+" "+mediaURL));

                    }
                }

            }



        } catch (Exception e) {
            Log.e("Exception", "Error: " + e.toString());
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Błąd pobierania listy punktów", Toast.LENGTH_SHORT).show();
        }


        /*list.add(new ListElement("1","Punkt 1"));
        list.add(new ListElement("2","Punkt 2"));
        list.add(new ListElement("3","Punkt 3"));
        list.add(new ListElement("4","Punkt 4"));*/
        return list;
    }
    public void mapsOnClick(View view) {
        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        intent.putExtra("tripId", tripId);
        intent.putExtra("tripName",tripName);
        startActivity(intent);
    }

    public void addMediaOnClick(View view) {
        MainActivity.startAddMediaActivity(this, tripId, null);
    }

    public void plakatOnClick(View view) {
        Config.imageToDisplay = null;
        try {
            URL url = new URL(Config.API_URL + "trips/"+tripId+"/posters");
            Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            if(bmp == null ){
                throw new Exception("Error decoding");
            }
            Config.imageToDisplay = bmp;
        } catch (Exception e) {
            try {
                URL url = new URL("https://s-media-cache-ak0.pinimg.com/736x/74/6e/8a/746e8a9cbd686f8d5b1452982d0eb78b.jpg");
                Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                Config.imageToDisplay = bmp;
            } catch (Exception ee) {
                Log.w("File photo ERR", "ERR load photo from link");
            }
            Log.w("File photo", Config.API_URL + "trips/"+tripId+"/posters");

        }
        if(Config.imageToDisplay != null ){
            Intent goToNextActivity = new Intent(getApplicationContext(), DisplayImageActivity.class);
            startActivity(goToNextActivity);
        }
    }

    public void deleteTripOnClick(View view) {
        Config.sendDeleteUrl(getApplicationContext(),Config.API_URL+"trips/"+tripId);
        Intent goToNextActivity = new Intent(getApplicationContext(), TripsListActivity.class);
        goToNextActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(goToNextActivity);
    }

    /****************************************************
     ***************   Helper Classes *******************
     *****************************************************/
    private class ListElement  {
        String pointId;
        String title;

        public ListElement(String ptripId, String ptitle) {
            pointId = ptripId;
            title = ptitle;
        }
    }

}
