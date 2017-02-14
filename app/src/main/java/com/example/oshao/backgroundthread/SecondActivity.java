package com.example.oshao.backgroundthread;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by oshao on 1/5/2017.
 */

public class SecondActivity extends AppCompatActivity implements ReaderModifier {

    private Button button;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_activity);
        button = (Button) findViewById(R.id.button_back);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Intent intent = new Intent(this,ReaderConnectionService.class);
        startService(intent);

    }

    @Override
    public void readerCount(int count) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


}
