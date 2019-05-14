package com.example.myapplication;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.myapplication.server.CycleLoadData;
import com.example.myapplication.server.SensorData;
import com.squareup.picasso.Picasso;

public class TopCraneInfoView extends RelativeLayout {

    private ImageView mCraneImage;
    private TextView mCraneNumber;
    private TextView mStepName;
    private TextView mCycleNumber;
    private TextView mEventTime;
    private TextView mLoadType;
    private TextView mCraneHeight;
    private TextView mCraneWeight;


    public static String[] STEP_NAME = new String[]{"NO_STEP", "movement toward the load",
            "load rigging",
            "movement with the load toward the destination",
            "unrigging of the load at the destination"};

    public TopCraneInfoView(Context context) {
        super(context);
        initView();
    }

    public TopCraneInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public TopCraneInfoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }


    private void initView() {
        LayoutInflater.from(this.getContext()).inflate(R.layout.top_crane_view, this, true);
        // init  views here
        mCraneImage = findViewById(R.id.crane_image);
        mCraneNumber = findViewById(R.id.crane_number);
        mStepName = findViewById(R.id.step_name);
        mCycleNumber = findViewById(R.id.cycle_number);
        mEventTime = findViewById(R.id.event_time);
        mLoadType = findViewById(R.id.crane_load_type);
        mCraneHeight = findViewById(R.id.crane_height);
        mCraneWeight = findViewById(R.id.crane_weight);
    }


    public void setSensorData(SensorData sensorData) {
        Picasso.get().load(sensorData.image_url).into(mCraneImage);
        mCraneHeight.setText(sensorData.acc_az + "");
        mCraneWeight.setText(sensorData.weight + "");
    }

    public void setCycleRow(CycleLoadData cycleLoadData) {
        mCraneNumber.setText(String.valueOf(cycleLoadData.crane_id));
        mStepName.setText(STEP_NAME[cycleLoadData.step_num]);
        mCycleNumber.setText(String.valueOf(cycleLoadData.step_num));
        mLoadType.setText(cycleLoadData.load_type_name);

    }


    public void setEventTime(long time) {
        mEventTime.setText(time + "");
    }
}
