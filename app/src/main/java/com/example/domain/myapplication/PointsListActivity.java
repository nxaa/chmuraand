package com.example.domain.myapplication;

import android.app.Activity;
import android.content.Intent;
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

import java.util.ArrayList;

public class PointsListActivity extends AppCompatActivity {
    private ArrayList<ListElement> list;
    ArrayList<String> stringList;
    private String tripId="1";
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
            //JSONObject jObject = new JSONObject(result);
            JSONArray jTrips = new JSONArray(result);
            for(int i=0; i < jTrips.length(); i++) {
                JSONObject jTrip = jTrips.getJSONObject(i);
                String id=jTrip.getString("tripId");
                if(!id.equals(tripId)){
                    continue;
                }
                if(jTrip.has("geometry")){
                    JSONObject jTripGeometry=jTrip.getJSONObject("geometry");
                    //JSONArray jTripCoordinates=jTripGeometry.getJSONArray("coordinates");
                    //for(int k=0; k < jTripCoordinates.length();k++) {
                        //Tutaj była jakaś magia, raz tablica raz tablica tablic
                    //}
                }
                if(jTrip.has("medias")){
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

            } // End Loop


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
        intent.putExtra("tripId", (String)null);
        startActivity(intent);
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
