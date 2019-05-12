package com.example.myapplication.server;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.example.myapplication.TopCraneInfoView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class CraneViewController implements ServerHelper.CraneDataListener {

    private static final String TAG = CraneViewController.class.getSimpleName();
    private static final int MIN_SIZE = 1;
    public static long NO_TIME = 0;

    // Views
    private final TopCraneInfoView mTopCraneInfoView;
    // Data
    private final ServerHelper mServerHelper;
    private long mCurrentTime = NO_TIME;
    private List<CycleLoadData> mCycleLoadDatas = new ArrayList<>();

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

            if(mCurrentTime == NO_TIME){
                Log.d(TAG, "First run...");
                moveToNextCycle();
            }

            if (shouldMoveFromLastCycle()) {
                mCycleLoadDatas.remove(0);
                moveToNextCycle();
            }


            mTopCraneInfoView.setEventTime(++mCurrentTime);
        } else {
            Toast.makeText(mContext, "End Of Data", Toast.LENGTH_SHORT).show();
            stop();
        }
    }

    private boolean shouldMoveFromLastCycle() {
        return mCurrentTime != NO_TIME && !mCycleLoadDatas.isEmpty() && mCurrentTime > mCycleLoadDatas.get(0).step_end_time;
    }

    private void moveToNextCycle() {
        final int cyclesSize = mCycleLoadDatas.size();
        if (cyclesSize > 0) {
            if (cyclesSize < MIN_SIZE) {
                askForMore(mCycleLoadDatas.get(cyclesSize - 1).step_end_time);
            }

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


    private void askForMore(long time) {
        Log.d(TAG, "Asking for more " + MIN_SIZE + " - " + time);
        mServerHelper.requestCycleRows(MIN_SIZE, time, this);
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
            askForMore(mCurrentTime);
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
        }, 0, 1000);
    }


    @Override
    public void onBatch(List<CycleLoadData> newBatch) {
        Log.d(TAG, "Got " + newBatch.size() + " Cycels (nts=" + mNeedsToStart + ")");
        mCycleLoadDatas.addAll(newBatch);
        if (mNeedsToStart) {
            mNeedsToStart = false;
            startTimer();
        }
    }

    @Override
    public void showing() {

    }

}
