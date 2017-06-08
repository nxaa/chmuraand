package com.example.domain.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class TripsListActivity extends AppCompatActivity {
    private ArrayList<ListElement> list;
    ArrayList<String> stringList;
    //private String tripId="1";
    private ArrayAdapter<String> adapter;

    ListView listView ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_trips_list);
        list= getListElements();

        // Get ListView object from xml
        listView = (ListView) findViewById(R.id.list);
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
                Intent intent = new Intent(getApplicationContext(), PointsListActivity.class);
                intent.putExtra("tripId", String.valueOf(el.tripId));
                intent.putExtra("tripName", String.valueOf(el.title));
                startActivity(intent);
            }
        });

    }


    public ArrayList<ListElement> getListElements() {
        ArrayList<ListElement> list= new ArrayList<ListElement>();
        String result=Config.downloadDataFromURL(Config.API_URL+"trips");
        Log.w("Trips data url",Config.API_URL+"trips");
        Log.w("Trips data result",result);
        try {
            JSONObject jObject = new JSONObject(result);
                JSONArray jTrips = jObject.getJSONArray("trips");
            for(int i=0; i < jTrips.length(); i++) {
                JSONObject jTrip = jTrips.getJSONObject(i);
                String id=jTrip.getString("id");
                String name=jTrip.getString("name");
                String created=jTrip.getString("created");
                String description=jTrip.getString("description");
                if(jTrip.has("medias")) {
                    //nie wiem po co  ale moze komus przyda
                    JSONArray jTripMedias = jTrip.getJSONArray("medias");
                    for (int k = 0; k < jTripMedias.length(); k++) {
                        JSONObject jMedia = jTripMedias.getJSONObject(k);
                        String mediaId = jMedia.getString("id");
                        String mediaType = jMedia.getString("type");
                        String mediaURL = jMedia.getString("url");
                        String mediaMinURL = jMedia.getString("minUrl");

                    }
                }
                list.add(new ListElement(id, name));
            } // End Loop

        } catch (Exception e) {
            Log.e("Exception", "Error: " + e.toString());
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Błąd pobierania listy wycieczek", Toast.LENGTH_SHORT).show();
        }

        /*list.add(new ListElement(1,"Wycieczka Warszawa"));
        list.add(new ListElement(2,"Wycieczka Kraków"));
        list.add(new ListElement(3,"Poznan"));
        list.add(new ListElement(4,"Warszawa"));*/
        return list;
    }

    public void addNewTripButtonOnClick(View view) {
        Intent intent = new Intent(getApplicationContext(), AddTripActivity.class);
        startActivity(intent);
    }


    /****************************************************
     ***************   Helper Classes *******************
     *****************************************************/
    private class ListElement  {
        String tripId;
        String title;

        public ListElement(String ptripId, String ptitle) {
            tripId = ptripId;
            title = ptitle;
        }
    }

}
