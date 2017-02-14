package com.example.oshao.backgroundthread;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements UiUpdater {

    public final static String ACTION_TYPE_SERVICE = "action.type.service";
    public final static String ACTION_READER_COUNT = "action.type.thread";

    private IntentFilter intentFilter;

    private LocalBroadcastManager localBroadcastManager;
    private MyBroadCastReceiver broadCastReceiver;

    private static final String TAG = "MainActivity";
    public static boolean isMainActivity;

    private SeekBar seekBar;
    private TextView textViewInternal;
    private TextView textViewCount;
    private TextView textViewServiceStatus;
    private TextView textViewReaderCount;
    private Button buttonGoConnection;
    private Button buttonClear;

    private TrueLoop trueLoop;

    int progress = 10;

    private int savedCount = 0;
    private long savedInterval = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isMainActivity = true;

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        textViewInternal = (TextView) findViewById(R.id.textView_internal);
        textViewCount = (TextView) findViewById(R.id.textView_count);
        textViewServiceStatus = (TextView) findViewById(R.id.textView_service_status);
        textViewReaderCount = (TextView) findViewById(R.id.textView_reader_count);
        buttonGoConnection = (Button) findViewById(R.id.button_go_connection);
        buttonClear = (Button) findViewById(R.id.button_clear);

        textViewInternal.setText("Interval Modified : 0");
        textViewCount.setText("Counting : 0");

        if (savedInstanceState != null) {

            savedCount = savedInstanceState.getInt("savedCount");
            savedInterval = savedInstanceState.getLong("savedInterval");

        }

        trueLoop = new TrueLoop(this);

        // pass trueLoop.epcHandler to Raymond API

        GlobalVariable.setInterval(progress);
//        isMainActivity = true;
        trueLoop.BackgroundLoop();

        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        broadCastReceiver = new MyBroadCastReceiver();

        intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_TYPE_SERVICE);
        intentFilter.addAction(ACTION_READER_COUNT);

        localBroadcastManager.registerReceiver(broadCastReceiver, intentFilter);

    }

    @Override
    protected void onStart() {
        super.onStart();

        isMainActivity = true;
        Log.i(TAG, "savedCount ; " + savedCount);

        buttonGoConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                startActivity(intent);

            }
        });
        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                trueLoop.reset();
                seekBar.setProgress(0);

                stopService(new Intent(MainActivity.this, ReaderConnectionService.class));

//                finish();
//                startActivity(getIntent());

            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {


            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

//                progress = progress+ i;

                GlobalVariable.setInterval(progress + i);
//                Log.v(TAG, "onStopTrackingTouch === " + i);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {


            }
        });

        seekBar.setProgress((int) savedInterval);
        trueLoop.BackgroundLoop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("savedCount", savedCount);
        outState.putLong("savedInterval", savedInterval);

    }

    @Override
    protected void onResume() {
        super.onResume();
        isMainActivity = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isMainActivity = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isMainActivity = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isMainActivity = false;
        localBroadcastManager.unregisterReceiver(broadCastReceiver);
    }

    @Override
    public void onCountChanged(final int count) {

        savedCount = count;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textViewCount.setText("Counting : " + Integer.toString(count));
            }
        });
    }

    @Override
    public void onIntervalChanged(final long interval) {

        savedInterval = interval;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textViewInternal.setText("Interval modified : " + Integer.toString((int) interval / 100));
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public class MyBroadCastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case ACTION_TYPE_SERVICE:
                    Log.v(TAG, "Service Status :" + intent.getStringExtra("status"));
                    textViewServiceStatus.setText("Starting");
                    break;
                case ACTION_READER_COUNT:
                    Log.v(TAG, "Thread Status: " + intent.getStringExtra("count"));
                    textViewReaderCount.setText("Send :" + intent.getStringExtra("count") + " times ");
                    break;

            }
        }
    }
}
