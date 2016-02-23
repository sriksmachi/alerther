package com.example.alerther.alerther;

import android.text.Html;

import com.google.android.gms.plus.internal.model.people.PersonEntity;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by chitti on 22-02-2016.
 */
public class AlertHerMobileServiceClient {

    final String mobileServiceURL = "https://sriksincidentservice.azure-mobile.net/tables/Incident";
    final String mobileServiceKey = "pvSHEiBircgslkHNAWyqpqeKVzALjI24";
    URL url = null;

    public AlertHerMobileServiceClient() {
        // Start building the request object to get the data
        try {
            url = new URL(mobileServiceURL);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<IncidentItem> GetAllIncidentItems() {
        ArrayList<IncidentItem> incidentItems = null;
        try {
            // Build a request object to connect to Azure Mobile Services
            HttpURLConnection urlRequest = (HttpURLConnection) url.openConnection();

            // Reading data so the http verb is "GET"
            urlRequest.setRequestMethod("GET");

            // Start building up the request header
            // (1) The data is JSON format
            // (2) We need to pass the service app id (we get this from the Azure Portal)
            urlRequest.addRequestProperty("Content-Type", "application/json");
            urlRequest.addRequestProperty("ACCEPT", "application/json");
            urlRequest.addRequestProperty("X-ZUMO-APPLICATION", mobileServiceKey);
            // We hold the json results
            // Prepare some objects to receive the bytes of data
            // from the Azure Mobile Service
            InputStream in = new BufferedInputStream(
                    urlRequest.getInputStream());
            BufferedReader bufferReader = new BufferedReader(
                    new InputStreamReader(in));

            // responseString will hold our JSON data
            StringBuilder responseString = new StringBuilder();
            String line;

            // Loop through the buffered input, reading JSON data
            while ((line = bufferReader.readLine()) != null) {
                responseString.append(line);
            }

            // Convert responseString into a JSONArray
            //JSONArray jsonArray = new JSONArray(responseString.toString());
            // Will hold an array of JSON objects
            //todos = new JSONObject[jsonArray.length()];
            Gson gson = new Gson();
            incidentItems = gson.fromJson(responseString.toString(), new TypeToken<List<IncidentItem>>() {
            }.getType());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return incidentItems;
    }

    public void PostIncident(IncidentItem item)
    {
        try {
            HttpURLConnection urlRequest = (HttpURLConnection) url.openConnection();
            urlRequest.setRequestMethod("POST");
            urlRequest.addRequestProperty("Content-Type", "application/json");
            urlRequest.addRequestProperty("ACCEPT", "application/json");
            urlRequest.addRequestProperty("X-ZUMO-APPLICATION", mobileServiceKey);
            urlRequest.setDoOutput(true);
            Gson gson = new Gson();
            String urlParameters = gson.toJson(item);
            DataOutputStream wr = new DataOutputStream(urlRequest.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();
            int responseCode = urlRequest.getResponseCode();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
