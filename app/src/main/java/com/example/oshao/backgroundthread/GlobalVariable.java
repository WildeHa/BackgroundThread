package com.example.oshao.backgroundthread;

import android.app.Application;
import android.content.Context;

/**
 * Created by oshao on 1/3/2017.
 */

public class GlobalVariable extends Application{

    private static Context context;
    private static int interval;

    @Override
    public void onCreate() {
        super.onCreate();
        context= this;
    }

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        GlobalVariable.context = context;
    }

    public static int getInterval() {
        return interval;
    }

    public static void setInterval(int interval) {
        GlobalVariable.interval = interval;
    }
}
