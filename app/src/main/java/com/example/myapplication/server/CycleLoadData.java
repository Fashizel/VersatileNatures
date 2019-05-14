package com.example.myapplication.server;

public class CycleLoadData {

    public int crane_id;
    public long step_end_time;
    public long start_time;
    public String load_type_category_name;
    public long step_start_time;
    public String load_type_name;
    public int step_num;
    public int id;
    public long end_time;


    @Override
    public String toString() {
        return "CycleLoadData{" +
                "crane_id=" + crane_id +
                ", step_end_time=" + step_end_time +
                ", start_time=" + start_time +
                ", load_type_category_name='" + load_type_category_name + '\'' +
                ", step_start_time=" + step_start_time +
                ", load_type_name='" + load_type_name + '\'' +
                ", step_num=" + step_num +
                ", id=" + id +
                ", end_time=" + end_time +
                '}';
    }
}
