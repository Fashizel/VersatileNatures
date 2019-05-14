package com.example.myapplication.server;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

public class ServerHelper {

    public static final String TAG = ServerHelper.class.getSimpleName();
    private String mServerAddress;
    private final RequestQueue mQueue;

    private LoosRetryPolicy mRetryPoilicy = new LoosRetryPolicy();
    private String mCraneId;

    public ServerHelper(Context context) {
        mServerAddress = null;
        mQueue = Volley.newRequestQueue(context);

    }

    public void set(String address, String craneId) {
        mServerAddress = address;
        mCraneId = craneId;
        Log.d(TAG, "Setting server address to " + address + " and crane_id " + mCraneId);
    }


    public void requestData(int amount, long minimumTime, final CraneDataListener listener) {
        final Gson gson = new GsonBuilder().create();

        // Request a string response from the provided URL.
        final String url = mServerAddress + "/" +
                "data" + "/" +
                mCraneId + "/" +
                amount + "/" +
                minimumTime;
        Log.d(TAG, "Requesting: " + url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, response);

                        JsonObject tupleData = (JsonObject) new JsonParser().parse(response);
                        JsonArray cyclesJson = (JsonArray) tupleData.get("cycles");
                        JsonArray sensorjson = (JsonArray) tupleData.get("sensor");
                        parseCycleJson(cyclesJson, gson, listener);
                        parseSensorJson(sensorjson, gson, listener);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Cycle: " + error.toString());
            }
        });

        stringRequest.setRetryPolicy(mRetryPoilicy);
        mQueue.add(stringRequest);
    }

    private void parseCycleJson(JsonArray array, Gson gson, CraneDataListener listener) {
        List<CycleLoadData> newBatch = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            newBatch.add(gson.fromJson(array.get(i).toString(), CycleLoadData.class));
        }
        listener.onCycleLoadData(newBatch);
    }

    private void parseSensorJson(JsonArray array, Gson gson, CraneDataListener listener) {
        List<SensorData> newBatch = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            newBatch.add(gson.fromJson(array.get(i).toString(), SensorData.class));
        }
        listener.onSensorData(newBatch);
    }


    private class LoosRetryPolicy extends DefaultRetryPolicy {
        /**
         * The default socket timeout in milliseconds
         */
        public static final int DEFAULT_TIMEOUT_MS = 100000;

        /**
         * The default number of retries
         */
        public static final int DEFAULT_MAX_RETRIES = 1;

        /**
         * The default backoff multiplier
         */
        public static final float DEFAULT_BACKOFF_MULT = 1f;

        @Override
        public int getCurrentTimeout() {
            return DEFAULT_TIMEOUT_MS;
        }
    }

    public interface CraneDataListener {

        void onCycleLoadData(List<CycleLoadData> newBatch);

        void onSensorData(List<SensorData> newBatch);
    }

}