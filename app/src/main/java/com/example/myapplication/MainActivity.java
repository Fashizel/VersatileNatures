package com.example.myapplication;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.example.myapplication.server.CraneViewController;
import com.example.myapplication.server.ServerHelper;

public class MainActivity extends AppCompatActivity {

    private EditText mServerAddress;
    private ServerHelper mServerHelper;
    private CraneViewController mCraneViewController;
    private EditText mCraneId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mServerAddress = findViewById(R.id.server_id);
        mCraneId = findViewById(R.id.crane_id);
        mServerHelper = new ServerHelper(this);
        mCraneViewController = new CraneViewController(this, (TopCraneInfoView) findViewById(R.id.top_crane_view), new Handler(Looper.getMainLooper()), mServerHelper);
    }

    public void startStop(View view) {
        mCraneViewController.startStop();
    }

    public void setServerAddress(View view) {
        String address = mServerAddress.getText().toString();
        String craneId = mCraneId.getText().toString();
        mServerHelper.set(address, craneId);
    }
}
