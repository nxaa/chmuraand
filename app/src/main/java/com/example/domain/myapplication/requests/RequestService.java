package com.example.domain.myapplication.requests;

import android.content.ComponentName;
import android.os.AsyncTask;

import com.example.domain.myapplication.Config;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by KOZAK on 24.05.2017.
 */

public class RequestService {
    private static String TRIPS_URL_BASE = "http://tripper-api.azurewebsites.net/trips/";
    private static String TRIPS_URL_BASE2 = "http://tripper-api.azurewebsites.net/trips";


    public String postTrip(String filepath, String name, String description) {
        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;
        InputStream inputStream = null;
        Map<String, String> params = new HashMap<>();
        String suffix = "";

        suffix = "?description=" + description + "&name=" + name;

        String twoHyphens = "--";
        String boundary = "*****" + Long.toString(System.currentTimeMillis()) + "*****";
        String lineEnd = "\r\n";

        String fileMimeType = "application/x-gpx+xml";

        String result = "";

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;

        String[] q = filepath.split("/");
        int idx = q.length - 1;

        try {
            File file = new File(filepath);
            FileInputStream fileInputStream = new FileInputStream(file);

            URL url = new URL(TRIPS_URL_BASE2 + suffix);
            connection = (HttpURLConnection) url.openConnection();

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("User-Agent", "Android Multipart HTTP Client 1.0");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"mediaFile\"; filename=\"" + q[idx] + "\"" + lineEnd);
            outputStream.writeBytes("Content-Type: " + fileMimeType + lineEnd);
            outputStream.writeBytes("Content-Transfer-Encoding: binary" + lineEnd);

            outputStream.writeBytes(lineEnd);

            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            while (bytesRead > 0) {
                outputStream.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            outputStream.writeBytes(lineEnd);

            // Upload POST Data
            Iterator<String> keys = params.keySet().iterator();
            while (keys.hasNext()) {
                String key = keys.next();
                String value = params.get(key);

                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"" + lineEnd);
                outputStream.writeBytes("Content-Type: text/plain" + lineEnd);
                outputStream.writeBytes(lineEnd);
                outputStream.writeBytes(value);
                outputStream.writeBytes(lineEnd);
            }

            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            if (200 != connection.getResponseCode()) {
                return "Error, response code = " + connection.getResponseCode();
            }

            inputStream = connection.getInputStream();

            result = this.convertStreamToString(inputStream);

            fileInputStream.close();
            inputStream.close();
            outputStream.flush();
            outputStream.close();

            return result;
        } catch (Exception e) {
            return "Error";
        }
    }

    public String postMedia(String tripId, String filepath) {
        return postMedia(tripId, filepath, null, null);
    }

    public String postMedia(String tripId, String filepath, String lat, String lng) {
        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;
        InputStream inputStream = null;
        Map<String, String> params = new HashMap<>();
        String suffix = "";

        if (lat != null && lng != null) {
            /*params.put("lat", lat);
            params.put("lng", lng);*/
            suffix = "?lat=" + lat + "&lng=" + lng;
        }

        String twoHyphens = "--";
        String boundary = "*****" + Long.toString(System.currentTimeMillis()) + "*****";
        String lineEnd = "\r\n";

        String fileMimeType = "image/png";

        String result = "";

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;

        String[] q = filepath.split("/");
        int idx = q.length - 1;

        try {
            File file = new File(filepath);
            FileInputStream fileInputStream = new FileInputStream(file);

            URL url = new URL(TRIPS_URL_BASE + tripId + "/media2" + suffix);
            connection = (HttpURLConnection) url.openConnection();

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("User-Agent", "Android Multipart HTTP Client 1.0");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"mediaFile\"; filename=\"" + q[idx] + "\"" + lineEnd);
            outputStream.writeBytes("Content-Type: " + fileMimeType + lineEnd);
            outputStream.writeBytes("Content-Transfer-Encoding: binary" + lineEnd);

            outputStream.writeBytes(lineEnd);

            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            while (bytesRead > 0) {
                outputStream.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            outputStream.writeBytes(lineEnd);

            // Upload POST Data
            Iterator<String> keys = params.keySet().iterator();
            while (keys.hasNext()) {
                String key = keys.next();
                String value = params.get(key);

                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"" + lineEnd);
                outputStream.writeBytes("Content-Type: text/plain" + lineEnd);
                outputStream.writeBytes(lineEnd);
                outputStream.writeBytes(value);
                outputStream.writeBytes(lineEnd);
            }

            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            if (200 != connection.getResponseCode()) {
                return "Error, response code = " + connection.getResponseCode()
                        + "tripId: " + tripId + " , filepath: " + filepath + " , lat: " + lat + " , lng: " + lng + "\n" + "URL: " + url.toString();
            }

            inputStream = connection.getInputStream();

            result = this.convertStreamToString(inputStream);

            fileInputStream.close();
            inputStream.close();
            outputStream.flush();
            outputStream.close();

            return result + "tripId: " + tripId + " , filepath: " + filepath + " , lat: " + lat + " , lng: " + lng + "\n" + "URL: " + url.toString();
        } catch (Exception e) {
            return "Error";
        }
    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public String generatePresentation(String tripId) {
        URL url;
        HttpURLConnection connection = null;
        String output = "";
        try {

            url = new URL(TRIPS_URL_BASE + tripId + "/run_job/presentation");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");

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
            return e.getMessage();
        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        } finally {
            connection.disconnect();
        }

        return output;
    }

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

    public String generatePoster(String tripId) {
        URL url;
        HttpURLConnection connection = null;
        String output = "";
        try {
            url = new URL(TRIPS_URL_BASE + tripId + "/run_job/poster");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");

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

    public String getTripMedia(String tripId, String mediaId) {
        URL url;
        HttpURLConnection connection = null;
        String output = "";
        try {
            url = new URL(TRIPS_URL_BASE + tripId + "/media/" + mediaId);
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

    public String getTripPhotos(String tripId, String photoId) {
        URL url;
        HttpURLConnection connection = null;
        String output = "";
        try {
            url = new URL(TRIPS_URL_BASE + tripId + "/photos/" + photoId);
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

    public String getTripMediaAll(String tripId) {
        URL url;
        HttpURLConnection connection = null;
        String output = "";
        try {
            url = new URL(TRIPS_URL_BASE + tripId + "/media");
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

    public HashMap<String, String> getTripsHashMap() {
        HashMap<String, String> map = new HashMap<String, String>();
        String result= Config.downloadDataFromURL(Config.API_URL+"trips");
        try {
            JSONObject jObject = new JSONObject(result);
            JSONArray jTrips = jObject.getJSONArray("trips");
            for(int i=0; i < jTrips.length(); i++) {
                JSONObject jTrip = jTrips.getJSONObject(i);
                String id=jTrip.getString("id");
                String name=jTrip.getString("name");
                map.put(id, name);
            }
        } catch (Exception e) {
            return null;
        }

        return map;
    }
}
