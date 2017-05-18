package com.example.domain.myapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;


public class RegisterActivity extends AppCompatActivity {

    Button b1, b2;
    EditText ed1, ed2, ed3, ed4;


    private int isAllValid() {
        if (!isLoginValid()) return 1;
        if (!isEmailValid()) return 2;
        if (!isPasswordValid()) return 3;
        return 0;
    }

    private String communicate(int val) {
        switch (val) {
            case 0:
                return "Poprawne dane rejestracji";
            case 1:
                return "Niepoprawny login";
            case 2:
                return "Niepoprawny email";
            case 3:
                return "Niepoprawne hasło";
            default:
                return "Nieznany błąd";
        }

    }

    private boolean isEmailValid() {
        String email = ed2.getText().toString();
        return email.length() > 0 && email.matches("\\w+@\\w+\\.\\w+");
    }

    private boolean isLoginValid() {
        String login = ed1.getText().toString();
        return login.length() > 0;
    }

    private boolean isPasswordValid() {
        String h1 = ed3.getText().toString();
        String h2 = ed4.getText().toString();
        return h1.equals(h2) && h1.length() >= 8;
    }


    private String sendRegisterRequest() {
        try {
            // Create URL
            URL RejestracjaAPI = null;
            try {
                RejestracjaAPI = new URL("/trip/register");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            // Create connection
            HttpsURLConnection RegisterConnection = null;

            RegisterConnection = (HttpsURLConnection) RejestracjaAPI.openConnection();

            RegisterConnection.setRequestMethod("POST");
            RegisterConnection.setRequestProperty("user_login", ed1.getText().toString());
            RegisterConnection.setRequestProperty("e-mail", ed2.getText().toString());
            RegisterConnection.setRequestProperty("password1", ed3.getText().toString());
            RegisterConnection.setRequestProperty("password2", ed4.getText().toString());

            RegisterConnection.setConnectTimeout(1000);

            if (RegisterConnection.getResponseCode() == 200) {
                InputStream responseBody = RegisterConnection.getInputStream();
                InputStreamReader responseBodyReader = new InputStreamReader(responseBody, "UTF-8");
                JsonReader jsonReader = new JsonReader(responseBodyReader);

                RegisterConnection.disconnect();

                jsonReader.beginObject(); // Start processing the JSON object
                while (jsonReader.hasNext()) { // Loop through all keys
                    String key = jsonReader.nextName(); // Fetch the next key
                    if (key.equals("error_message")) {
                        return jsonReader.nextString();
                    }

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
        setContentView(R.layout.activity_register);

        b1 = (Button) findViewById(R.id.button);
        b2 = (Button) findViewById(R.id.button2);
        ed1 = (EditText) findViewById(R.id.editText);
        ed2 = (EditText) findViewById(R.id.editText2);
        ed3 = (EditText) findViewById(R.id.editText3);
        ed4 = (EditText) findViewById(R.id.editText4);


        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Toast.makeText(getApplicationContext(), communicate(isAllValid()), Toast.LENGTH_SHORT).show();

                if (isAllValid() == 0) {

                    String result = sendRegisterRequest();
                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                    if(result.equals("success"))
                    {
                        Intent goToNextActivity = new Intent(getApplicationContext(), RegisterActivity.class);
                        startActivity(goToNextActivity);
                    }

                    // Stare dodawanie do plaintexta z hasłami poki nie ma laczenia z serwerem
                /*    String record = String.format("%s#%s\n", ed1.getText().toString(), ed3.getText().toString());
                    FileOutputStream outputStream;

                    try {
                        outputStream = openFileOutput("passes.txt", MODE_APPEND);
                        outputStream.write(record.getBytes());
                        outputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/
                }

            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });


    }
}
