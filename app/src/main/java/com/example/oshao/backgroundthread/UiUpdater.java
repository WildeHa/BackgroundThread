package com.example.oshao.backgroundthread;

/**
 * Created by oshao on 1/3/2017.
 */

public interface UiUpdater  {

    void onIntervalChanged(long interval);
    void onCountChanged(int count);

}
