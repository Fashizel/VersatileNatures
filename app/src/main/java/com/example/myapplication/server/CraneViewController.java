package com.example.myapplication.server;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.example.myapplication.TopCraneInfoView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class CraneViewController implements ServerHelper.CraneDataListener {

    private static final String TAG = CraneViewController.class.getSimpleName();
    private static final int MIN_SIZE = 20;
    public static final float SECOND_WEIGHT = 1.0f;
    public static long NO_TIME = 0;
    private long mCurrentTime = NO_TIME;

    // Views
    private final TopCraneInfoView mTopCraneInfoView;
    // Data
    private final ServerHelper mServerHelper;
    private List<CycleLoadData> mCycleLoadDatas = new ArrayList<>();
    private List<SensorData> mSensorDatas = new ArrayList<>();
    // utils
    private Timer mTimer;
    private final Context mContext;
    private final Handler mUiHandler;
    private boolean mNeedsToStart;

    public CraneViewController(Context context, TopCraneInfoView topCraneInfoView, Handler uiHandler, ServerHelper serverHelper) {
        mUiHandler = uiHandler;
        mTopCraneInfoView = topCraneInfoView;
        mContext = context;
        mServerHelper = serverHelper;
    }

    private void tick() {
        if (!mCycleLoadDatas.isEmpty()) {
            Log.d(TAG, "Incrementing time " + mCurrentTime);

            if (mCurrentTime == NO_TIME) {
                Log.d(TAG, "First run...");
                moveToNextCycle();
            }

            if (shouldMoveFromLastCycle()) {
                mCycleLoadDatas.remove(0);
                moveToNextCycle();
            }

            // No Harm at refreshing this all the time
            refreshSensorData();
            mTopCraneInfoView.setEventTime(++mCurrentTime);
        } else {
            Toast.makeText(mContext, "End Of Data", Toast.LENGTH_SHORT).show();
            stop();
        }
    }

    private boolean shouldMoveFromLastCycle() {
        return mCurrentTime != NO_TIME && !mCycleLoadDatas.isEmpty() && mCurrentTime >= mCycleLoadDatas.get(0).step_end_time;
    }

    private void moveToNextCycle() {
        final int cyclesSize = mCycleLoadDatas.size();
        if (cyclesSize > 0) {

            tryAskForMore();

            CycleLoadData cycleRow = mCycleLoadDatas.get(0);
            Log.d(TAG, "Setting cycle (" + cycleRow + ")");
            mTopCraneInfoView.setCycleRow(cycleRow);

            if (mCurrentTime == NO_TIME) {
                mCurrentTime = cycleRow.step_start_time;
            }
        } else {
            Log.d(TAG, "Not data");
            stop();
            start();
        }
    }

    private void refreshSensorData() {
        if (!mSensorDatas.isEmpty()) {
            Iterator<SensorData> sensorIter = mSensorDatas.listIterator();

            int removed = 0;
            while (sensorIter.hasNext()) {
                SensorData sensorData = sensorIter.next();
                if(mCurrentTime != sensorData.event_timestamp ) {
                    sensorIter.remove();
                    removed++;
                } else{
                    break;
                }
            }

            Log.d(TAG, "removed " + removed + " sensor data items");

            if (!mSensorDatas.isEmpty()) {
                SensorData sensorData = mSensorDatas.get(0);
                Log.d(TAG, "Setting sensor (" + sensorData + ")");
                mTopCraneInfoView.setSensorData(sensorData);
            }
        }
    }


    private void tryAskForMore() {
        if (mCycleLoadDatas.size() < MIN_SIZE) {
            long timeToAsk = mCycleLoadDatas.isEmpty() ? mCurrentTime : mCycleLoadDatas.get(mCycleLoadDatas.size() - 1).step_end_time;
            Log.d(TAG, "Asking for more data" + MIN_SIZE + " - " + timeToAsk);
            mServerHelper.requestData(MIN_SIZE, timeToAsk, this);
        }
    }

    public void startStop() {
        if (mTimer != null) {
            stop();
        } else {
            start();
        }
    }

    private void start() {
        if (mCycleLoadDatas.isEmpty()) {
            mNeedsToStart = true;
            tryAskForMore();
        } else {
            startTimer();
        }
    }

    private void stop() {
        mTimer.cancel();
        mTimer = null;
        Log.d(TAG, "Stopping...");
    }

    private void startTimer() {
        Log.d(TAG, "Start Timer");
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                mUiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        tick();
                    }
                });

            }
        }, 0, (long) (1000 * SECOND_WEIGHT));
    }


    @Override
    public void onCycleLoadData(List<CycleLoadData> newBatch) {
        Log.d(TAG, "Got " + newBatch.size() + " Cycels (nts=" + mNeedsToStart + ")");
        mCycleLoadDatas.addAll(newBatch);
        if (mNeedsToStart) {
            mNeedsToStart = false;
            startTimer();
        }
    }

    @Override
    public void onSensorData(List<SensorData> newBatch) {
        Log.d(TAG, "Got " + newBatch.size() + " sensor (nts=" + mNeedsToStart + ")");
        mSensorDatas.addAll(newBatch);
    }

}
