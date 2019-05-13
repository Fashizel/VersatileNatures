package com.example.myapplication.server;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

public class ServerHelper {

    public static final String TAG = ServerHelper.class.getSimpleName();
    private String mServerAddress;
    private final RequestQueue mQueue;

    public ServerHelper(Context context) {
        mServerAddress = null;
        mQueue = Volley.newRequestQueue(context);

    }

    public void setAddress(String address) {
        mServerAddress = address;
        Log.d(TAG, "Setting server address to " + address);
    }


    public void requestCycleData(int amount, long minimumTime, final CraneDataListener listener) {
        final Gson gson = new GsonBuilder().create();

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, mServerAddress + "/cycles/" + amount + "/" + minimumTime,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, response);


                        JsonArray array = (JsonArray) new JsonParser().parse(response);
                        List<CycleLoadData> newBatch = new ArrayList<>();
                        for (int i = 0; i < array.size(); i++) {
                            newBatch.add(gson.fromJson(array.get(i).toString(), CycleLoadData.class));
                        }
                        listener.onCycleLoadData(newBatch);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Cycle: " + error.toString());
            }
        });

        mQueue.add(stringRequest);
    }

    public void requestSensorData(int amount, long minimumTime, final CraneDataListener listener) {
        final Gson gson = new GsonBuilder().create();

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, mServerAddress + "/sensor/" + amount + "/" + minimumTime,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, response);


                        JsonArray array = (JsonArray) new JsonParser().parse(response);
                        List<SensorData> newBatch = new ArrayList<>();
                        for (int i = 0; i < array.size(); i++) {
                            newBatch.add(gson.fromJson(array.get(i).toString(), SensorData.class));
                        }
                        listener.onSensorData(newBatch);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Sensor: " + error.toString());
            }
        });

        mQueue.add(stringRequest);
    }

    public interface CraneDataListener {

        void onCycleLoadData(List<CycleLoadData> newBatch);

        void onSensorData(List<SensorData> newBatch);
    }

}