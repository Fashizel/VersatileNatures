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

    public void start() {
        requestCraneLoadData(0);
    }

    public void setAddress(String address) {
        mServerAddress = address;
        Log.d(TAG, "Setting server address to " + address);
    }

    private void requestCraneLoadData(long minimumTime) {
        Log.d(TAG, "requestCraneLoadData");
        final Gson gson = new GsonBuilder().create();

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, mServerAddress + "/steps/" + minimumTime,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, response);

                        JsonArray array = (JsonArray) new JsonParser().parse(response);
                        List<CycleLoadData> newBatch = new ArrayList<>();
                        for (int i = 0; i < array.size(); i++) {
                            newBatch.add(gson.fromJson(array.get(i).toString(), CycleLoadData.class));
                        }
                        //mCraneDataListener.onBatch(newBatch);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, error.toString());
            }
        });

        mQueue.add(stringRequest);
    }

    public void requestCycleRows(int amount, long minimumTime, final CraneDataListener listener) {
        final Gson gson = new GsonBuilder().create();

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://192.168.1.27:5000/steps/" + amount + "/" + minimumTime,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, response);


                        JsonArray array = (JsonArray) new JsonParser().parse(response);
                        List<CycleLoadData> newBatch = new ArrayList<>();
                        for (int i = 0; i < array.size(); i++) {
                            newBatch.add(gson.fromJson(array.get(i).toString(), CycleLoadData.class));
                        }
                        listener.onBatch(newBatch);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, error.toString());
            }
        });

        mQueue.add(stringRequest);
    }

    public interface CraneDataListener {
        void onBatch(List<CycleLoadData> newBatch);

        void showing();

    }


}