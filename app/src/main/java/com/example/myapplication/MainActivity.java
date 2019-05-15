package com.example.myapplication;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.server.ServerHelper;
import com.example.myapplication.view.SensorGraphViewsHelper;
import com.example.myapplication.view.TopCraneInfoView;

import lecho.lib.hellocharts.view.LineChartView;

public class MainActivity extends AppCompatActivity {

    private EditText mServerAddress;
    private ServerHelper mServerHelper;
    private CraneViewController mCraneViewController;
    private EditText mCraneId;
    private Button mStartStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mServerAddress = findViewById(R.id.server_id);
        mCraneId = findViewById(R.id.crane_id);
        mServerHelper = new ServerHelper(this);
        SensorGraphViewsHelper sensorGraphViewsHelper = new SensorGraphViewsHelper((LineChartView) findViewById(R.id.height_graph),
                (LineChartView) findViewById(R.id.weight_graph));
        mCraneViewController = new CraneViewController(this, (TopCraneInfoView) findViewById(R.id.top_crane_view), sensorGraphViewsHelper, new Handler(Looper.getMainLooper()), mServerHelper);
        mStartStop = findViewById(R.id.start_stop);
    }

    public void startStop(View view) {
        if(!checkValidity()){
            return;
        }

        String buttonText = mStartStop.getText().toString();
        if (buttonText.equalsIgnoreCase("START")) {
            mStartStop.setText("STOP");
        } else {
            mStartStop.setText("START");
        }
        mCraneViewController.startStop();
    }

    private boolean checkValidity() {
        if (TextUtils.isEmpty(mServerHelper.getServerAddress())) {
            Toast.makeText(this, "Server address is not set", Toast.LENGTH_SHORT).show();
            return false;
        } else
            return true;
    }

    public void setServerAddress(View view) {
        String address = mServerAddress.getText().toString();
        String craneId = mCraneId.getText().toString();
        mServerHelper.set(address, craneId);
    }
}
