package com.lly.socketgame;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.lly.socketgame.house.CreateHouseActivity;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {


    private boolean isAccept = true;

    private ServerSocket serverSocket;


    private TextView tv_address;

    Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
                CreateHouseActivity.startCreateHoseActivity(MainActivity.this,1);
                break;
            case R.id.btn_connection:
                CreateHouseActivity.startCreateHoseActivity(MainActivity.this,2);
                break;
        }
    }


    private void sendUrgentData() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                while (true) {
                    Log.v("test", "发送心跳包：=");
                    try {
                        socket.sendUrgentData(0xff);
                        OutputStream os = socket.getOutputStream();
                        os.write(("好长好长的文字好长的文字的文字" +
                                "好长好长的文字好长的文字的文字" +
                                "好长好长的文字好长的文字的文字" +
                                "好长好长的文字好长的文字的文字" +
                                "好长好长的文字好长的文字的文字" +
                                "好长好长的文字好长的文字的文字" +
                                "好长好长的文字好长的文字的文字" +
                                "好长好长的文字好长的文字的文字" +
                                "好长好长的文字好长的文字的文字" +
                                "好长好长的文字好长的文字的文字" +
                                "好长好长的文字好长的文字的文字" +
                                "好长好长的文字好长的文字的文字" +
                                "好长好长的文字好长的文字的文字" +
                                "好长好长的文字好长的文字的文字" +
                                "好长好长的文字好长的文字的文字" +
                                "好长好长的文字好长的文字的文字" +
                                "好长好长的文字好长的文字的文字" +
                                "好长好长的文字好长的文字的文字" +
                                "好长好长的文字好长的文字的文字" +
                                "好长好长的文字好长的文字的文字吵吵的文字吵吵的文字吵吵的文" +
                                "好长好长的文字好长的文字的文字吵吵的文字吵吵的文字吵吵的文字" +
                                "好长好长的文字好长的文字的文字吵吵的文字吵吵的文字吵吵的文字" +
                                "好长好长的文字好长的文字的文字吵吵的文字吵吵的文字吵吵的文字" +
                                "好长好长的文字好长的文字的文字吵吵的文字吵吵的文字吵吵的文字" +
                                "好长好长的文字好长的文字的文字吵吵的文字吵吵的文字吵吵的文字" +
                                "好长好长的文字好长的文字的文字吵吵的文字吵吵的文字吵吵的文字" +
                                "字").getBytes());
                    } catch (IOException e) {
                        Log.v("test", "发送心跳包出错了");
                        e.printStackTrace();
                    }
                    try {
                        Thread.sleep(4000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

    }
}

