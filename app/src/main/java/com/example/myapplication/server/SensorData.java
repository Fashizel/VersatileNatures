package com.example.myapplication.server;

public class SensorData {

    public int id;
    public int crane_id;
    public String image_url;
    public int weight;
    public long event_timestamp;
    public int acc_az;


    @Override
    public String toString() {
        return "SensorData{" +
                "id=" + id +
                ", crane_id=" + crane_id +
                ", image_url='" + image_url + '\'' +
                ", weight=" + weight +
                ", event_timestamp=" + event_timestamp +
                ", acc_az=" + acc_az +
                '}';
    }
}
