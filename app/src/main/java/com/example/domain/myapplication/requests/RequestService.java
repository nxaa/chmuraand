package com.example.domain.myapplication.requests;

import android.content.ComponentName;
import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by KOZAK on 24.05.2017.
 */

public class RequestService {
    private static String TRIPS_URL_BASE = "http://tripper-api.azurewebsites.net/trips/";
/*    public String postMedia(String tripId, String m_id) {
        URL url;
        HttpURLConnection connection = null;
        String output = "";
        try {
            url = new URL(TRIPS_URL_BASE + tripId + "/media");
            connection = (HttpURLConnection) url.openConnection();
            connection.setUseCaches(false);
            connection.setDoOutput(true);

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "");

            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                return "Error bang, response code = " + responseCode;
            }

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String a;
            while ((a = bufferedReader.readLine()) != null) {
                output += a;
            }


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }

        return output;
    }*/

    public String getPresentations(String tripId) {
        URL url;
        HttpURLConnection connection = null;
        String output = "";
        try {
            url = new URL(TRIPS_URL_BASE + tripId + "/presentations");
            connection = (HttpURLConnection) url.openConnection();

            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                return "Error, response code = " + responseCode;
            }

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String a;
            while ((a = bufferedReader.readLine()) != null) {
                output += a;
            }


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }

        return output;
    }

    public String getPosters(String tripId) {
        URL url;
        HttpURLConnection connection = null;
        String output = "";
        try {
            url = new URL(TRIPS_URL_BASE + tripId + "/posters");
            connection = (HttpURLConnection) url.openConnection();

            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                return "Error, response code = " + responseCode;
            }

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String a;
            while ((a = bufferedReader.readLine()) != null) {
                output += a;
            }


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }

        return output;
    }

    public String getTrip(String tripId) {
        URL url;
        HttpURLConnection connection = null;
        String output = "";
        try {
            url = new URL(TRIPS_URL_BASE + tripId);
            connection = (HttpURLConnection) url.openConnection();

            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                return "Error, response code = " + responseCode;
            }

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String a;
            while ((a = bufferedReader.readLine()) != null) {
                output += a;
            }


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }

        return output;
    }

    public String getTrips() {
        URL url;
        HttpURLConnection connection = null;
        String output = "";
        try {
            url = new URL(TRIPS_URL_BASE);
            connection = (HttpURLConnection) url.openConnection();

            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                return "Error, response code = " + responseCode;
            }

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String a;
            while ((a = bufferedReader.readLine()) != null) {
                output += a;
            }


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }

        return output;
    }


    public String sendDummyRequest(String uri) {
        URL url;
        HttpURLConnection connection = null;
        String output = "";
        try {
            url = new URL("http://tripper-api.azurewebsites.net/trips/4bd46eab-4b36-4f02-becc-5a2b5d4b8914");
            connection = (HttpURLConnection) url.openConnection();

            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                return "Error bang, response code = " + responseCode;
            }

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String a;
            while ((a = bufferedReader.readLine()) != null) {
                output += a;
            }


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }

        return output;
    }
}
