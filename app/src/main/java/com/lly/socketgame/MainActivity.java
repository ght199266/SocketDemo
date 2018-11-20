package com.lly.socketgame;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.lly.socketgame.house.CreateHouseActivity;
import com.lly.socketgame.utils.IpUtils;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MainActivity extends BaseActivity {


    private boolean isAccept = true;

    private ServerSocket serverSocket;

    private TextView tv_address;

    Socket socket;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        Log.v("test", "Ip地址" + IpUtils.getIPAddress(this).getProperty("ip"));
    }

    @Override
    protected void initData() {

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        isAccept = false;
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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

