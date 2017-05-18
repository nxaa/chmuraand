package com.example.domain.myapplication;


import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

public class LoginActivity extends AppCompatActivity {

    Button b1, b2;
    EditText ed1, ed2;

    TextView tx1;

    private boolean validate_login(String login, String pass) {

        // stara funkcja do walidacji
       /* String result = "";
        HashMap<String, String> credentials = new HashMap<String, String>();
        FileInputStream inputStream;

        try {
            inputStream = openFileInput("passes.txt");
            BufferedReader myReader = new BufferedReader(new InputStreamReader(inputStream));
            String aDataRow = "";
            while ((aDataRow = myReader.readLine()) != null) {
                result = aDataRow;

                Matcher m = Pattern.compile("(.+)#(.+)").matcher(result);
                if (m.find()) {
                    credentials.put(m.group(1), m.group(2));

                }

            }
            myReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (credentials.get(login) != null) {
            if (credentials.get(login).equals(pass)) {
                Toast.makeText(getApplicationContext(), "Access granted", Toast.LENGTH_SHORT).show();
                return true;
            }
        }*/



    return true;

}

    private String sendLoginRequest() {
        try {
            // Create URL
            URL LogowanieAPI = null;
            try {
                LogowanieAPI = new URL("/trip/loging");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            // Create connection
            HttpsURLConnection LoginConnection = null;

            LoginConnection = (HttpsURLConnection) LogowanieAPI.openConnection();

            LoginConnection.setRequestMethod("POST");
            LoginConnection.setRequestProperty("user_login", ed1.getText().toString());
            LoginConnection.setRequestProperty("password", ed2.getText().toString());

            LoginConnection.setConnectTimeout(1000);

            if (LoginConnection.getResponseCode() == 200) {
                InputStream responseBody = LoginConnection.getInputStream();
                InputStreamReader responseBodyReader = new InputStreamReader(responseBody, "UTF-8");
                JsonReader jsonReader = new JsonReader(responseBodyReader);
                LoginConnection.disconnect();
                jsonReader.beginObject(); // Start processing the JSON object
                while (jsonReader.hasNext()) { // Loop through all keys
                    String key = jsonReader.nextName(); // Fetch the next key

                    if (key.equals("result")) { // Check if desired key
                        // Fetch the value as a String
                        String value = jsonReader.nextString();
                        if (value.equals("success"))return "success";
                        break; // Break out of the loop
                    } else {
                        jsonReader.skipValue(); // Skip values of other keys
                    }
                }

            } else {
                return "fail";
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        return "fail";
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        b1 = (Button) findViewById(R.id.button);
        ed1 = (EditText) findViewById(R.id.editText);
        ed2 = (EditText) findViewById(R.id.editText2);

        b2 = (Button) findViewById(R.id.button2);
        tx1 = (TextView) findViewById(R.id.textView3);
        tx1.setVisibility(View.VISIBLE);


        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate_login(ed1.getText().toString(), ed2.getText().toString())) {
                    String result = sendLoginRequest();
                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                    if(result.equals("success"))
                    {
                        Intent goToNextActivity = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(goToNextActivity);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Niepoprawne hasło lub nazwa użytkownika", Toast.LENGTH_SHORT).show();

                }
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToNextActivity = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(goToNextActivity);


            }
        });


    }

}
