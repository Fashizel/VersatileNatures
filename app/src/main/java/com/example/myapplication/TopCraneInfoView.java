package com.example.myapplication;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.myapplication.server.CycleLoadData;

public class TopCraneInfoView extends RelativeLayout {

    private ImageView mCraneImage;
    private TextView mCraneNumber;
    private TextView mStepName;
    private TextView mCycleNumber;
    private TextView mEventTime;
    private TextView mLoadType;
    private View mCraneHeight;
    private View mCraneWeight;

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


    public void setCycleRow(CycleLoadData craneRow){
        //Picasso.get().load('').into(mCraneImage);
        mCraneNumber.setText(String.valueOf(craneRow.crane_id));
        //mStepName.setText(craneRow.load_type_name);
        mCycleNumber.setText(String.valueOf(craneRow.step_num));
        mLoadType.setText(craneRow.load_type_name);
        //mCraneHeight
        //mCraneWeight
    }


    public void setEventTime(long time) {
        mEventTime.setText(time+"");
    }
}
