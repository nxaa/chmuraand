package com.example.domain.myapplication;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.domain.myapplication.requests.RequestService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class PointsListActivity extends AppCompatActivity {

    private ArrayList<ListElement> list;
    private TripDetails details;
    ArrayList<String> stringList;
    private String tripId="1";
    private String tripName="";

    private TextView tripNameLabel;
    private TextView tripDate;
    private TextView tripDescription;
    private ArrayAdapter<String> adapter;
    private RequestService requestService = new RequestService();


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
        details = getDetails();

        // ustawianie nazwy wycieczki
        tripNameLabel = (TextView) findViewById(R.id.tripNameMenu);
        tripNameLabel.setText(details.title);

        tripDate = (TextView) findViewById(R.id.tripDate);
        tripDate.setText("z dnia : " +details.created.substring(0,10));

        tripDescription = (TextView) findViewById(R.id.tripDesc);
        tripDescription.setText(details.description);

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

    public TripDetails getDetails() {
        TripDetails details = null;
        String result=Config.downloadDataFromURL(Config.API_URL+"trips/"+tripId);
        Log.w("Trip data url",Config.API_URL+"trips/"+tripId);
        Log.w("Trip data result",result);
        try {
            JSONObject jTrip = new JSONObject(result);
            String id=jTrip.getString("id");
            String name=jTrip.getString("name");
            String created=jTrip.getString("created");
            String description=jTrip.getString("description");
            details = new TripDetails(id,name,created,description);

        } catch (Exception e) {
            Log.e("Exception", "Error: " + e.toString());
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Błąd pobierania listy punktów", Toast.LENGTH_SHORT).show();
        }


        return details;
    }




    public void mapsOnClick(View view) {
        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        intent.putExtra("tripId", tripId);
        intent.putExtra("tripName",details.title);
        intent.putExtra("tripDate",tripDate.getText());
        startActivity(intent);
    }

    public void addMediaOnClick(View view) {
        MainActivity.startAddMediaActivity(this, tripId, null);
    }

    public void generujPlakatOnClick(View view) {
        String output = requestService.generatePoster(tripId);

        AlertDialog alertDialog = new AlertDialog.Builder(PointsListActivity.this).create();
        alertDialog.setTitle("Info");
        alertDialog.setMessage("Rozpoczęto generowanie plakatu, " + output);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();
    }

    public void generujPrezentacjeOnClick(View view) {
        String output = requestService.generatePresentation(tripId);

        AlertDialog alertDialog = new AlertDialog.Builder(PointsListActivity.this).create();
        alertDialog.setTitle("Info");
        alertDialog.setMessage("Rozpoczęto generowanie prezentacji, " + output);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();
    }

    public void plakatOnClick(View view) {
        Config.imageToDisplay = null;

        HttpURLConnection connection = null;
        String output = "";
        try {
            URL url = new URL(Config.API_URL + "trips/"+tripId+"/posters");
            connection = (HttpURLConnection) url.openConnection();

            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                AlertDialog alertDialog = new AlertDialog.Builder(PointsListActivity.this).create();
                alertDialog.setTitle("Status");
                alertDialog.setMessage("cos sie popsulo");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                alertDialog.show();
            }

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String a;
            while ((a = bufferedReader.readLine()) != null) {
                output += a;
            }

            JSONObject myObject = new JSONObject(output);
            Iterator<String> iterator = myObject.keys();
            String currentLink = "";
            long currentTime = 0l;


            while (iterator.hasNext()) {
                String key = iterator.next();
                String objStr = myObject.get(key).toString();
                JSONObject obj = new JSONObject(objStr);
                String link = obj.get("link").toString();

                int stIndex = link.indexOf("?st=");
                int seIndex = link.indexOf("&se=");

                if (stIndex < 0 || seIndex < 0) {
                    break;
                }

                String data = link.substring(stIndex + 4, seIndex);
                data = data.replaceAll("%3A", ":").replaceAll("T", " ").replaceAll("Z", "");

                SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                Date date = dt.parse(data);
                long time = date.getTime();

                if (time > currentTime) {
                    currentTime = time;
                    currentLink = link;
                }
            }

            URL url2 = new URL(currentLink);

            Bitmap bmp = BitmapFactory.decodeStream(url2.openConnection().getInputStream());
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

        } finally {
            connection.disconnect();
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


    private class TripDetails  {
        String pointId;
        String title;
        String created;
        String description;

        public TripDetails(String ptripId, String ptitle,String pcreated,String pdescription) {
            pointId = ptripId;
            title = ptitle;
            created  = pcreated;
            description = pdescription;
        }
    }

}
