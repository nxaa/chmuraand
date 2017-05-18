package com.example.domain.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
        setContentView(R.layout.activity_trips_list);
        Intent intent = getIntent();
       /* if(intent.hasExtra("tripId")) {
            this.tripId = intent.getStringExtra("tripId");
        }*/

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
                startActivity(intent);
            }
        });

    }


    public ArrayList<ListElement> getListElements() {
        ArrayList<ListElement> list= new ArrayList<ListElement>();
        list.add(new ListElement(1,"Wycieczka Warszawa"));
        list.add(new ListElement(2,"Wycieczka Kraków"));
        list.add(new ListElement(3,"Poznan"));
        list.add(new ListElement(4,"Warszawa"));
        return list;
    }


    /****************************************************
     ***************   Helper Classes *******************
     *****************************************************/
    private class ListElement  {
        int tripId;
        String title;

        public ListElement(int ptripId, String ptitle) {
            tripId = ptripId;
            title = ptitle;
        }
    }

}
