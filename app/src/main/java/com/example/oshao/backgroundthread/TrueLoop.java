package com.example.oshao.backgroundthread;

import android.os.Handler;
import android.os.Message;
import android.util.Log;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


/**
 * Created by oshao on 1/3/2017.
 */

public class TrueLoop {

    private static final String TAG = "TrueLoop";
    private UiUpdater uiUpdater;


    public TrueLoop(UiUpdater uiUpdater) {

        this.uiUpdater = uiUpdater;

    }

    ExecutorService executor = Executors.newSingleThreadExecutor();

    Runnable longRunningTask = new Runnable() {
        @Override
        public void run() {
            Log.v(TAG, "Get interval from MainActivity  " + GlobalVariable.getInterval());

            while (MainActivity.isMainActivity) {
                iterate();
            }

        }
    };

    public Handler epcHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            //uiUpdater.appendEpc(msg.what);
        }
    };

    Future longRunnigTaskFuture = executor.submit(longRunningTask);

    int count = 0;


    public void BackgroundLoop() {

        executor.execute(longRunningTask);

    }

    public void iterate()
    {
        try {

            Thread.sleep(GlobalVariable.getInterval());

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        count++;

        uiUpdater.onCountChanged(count);
        uiUpdater.onIntervalChanged(GlobalVariable.getInterval());

        Log.v("TrueLoop", " Count on UI " + count);
    }


    public void reset() {

        count = 0;
        GlobalVariable.setInterval(0);

        longRunnigTaskFuture.cancel(true);

        uiUpdater.onCountChanged(0);
        uiUpdater.onIntervalChanged(0);

    }


}
