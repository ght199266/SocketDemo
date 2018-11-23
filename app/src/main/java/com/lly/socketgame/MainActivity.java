package com.lly.socketgame;

import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.lly.socketgame.house.CreateHouseActivity;
import com.lly.socketgame.service.AudioServer;
import com.lly.socketgame.utils.IpUtils;

public class MainActivity extends BaseActivity {


    private ToggleButton toggleButton;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        Log.v("test", "Ip地址" + IpUtils.getIPAddress(this).getProperty("ip"));
        toggleButton = findViewById(R.id.toggleButton);
    }

    @Override
    protected void initData() {
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    AudioServer.startAudioServer(MainActivity.this);
                } else {
                    AudioServer.stopAudioServer(MainActivity.this);
                }
            }
        });
    }


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_startServer:
                CreateHouseActivity.startCreateHoseActivity(MainActivity.this, 1);
                break;
            case R.id.btn_connection:
                CreateHouseActivity.startCreateHoseActivity(MainActivity.this, 2);
                break;
        }
    }

}

