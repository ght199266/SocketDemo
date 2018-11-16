package com.lly.socketgame;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.lly.socketgame.client.ClientTask;
import com.lly.socketgame.utils.IpUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {


    private static final int SERVER_PORT = 8000;

    private boolean isAccept = true;

    private ServerSocket serverSocket;


    private TextView tv_address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        startServer();
        tv_address = findViewById(R.id.tv_address);

        tv_address.setText("IP地址：" + IpUtils.getIPAddress(this).getProperty("ip"));

    }

    public void startServer() {
        try {
            serverSocket = new ServerSocket(SERVER_PORT);
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    while (isAccept) {
                        try {

                            //监听一个客户端连接 分支
                            //asdsdasdasda
                            Socket socket = serverSocket.accept();
                            ClientTask clientTask = new ClientTask(socket);
                            new Thread(clientTask).start();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();
        } catch (IOException e) {
            e.printStackTrace();
            Log.v("test", "IOException22222222:=" + e.getMessage());
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        isAccept = false;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_startServer:
                startServer();
                break;
            case R.id.btn_connection:
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            Socket socket = new Socket("10.0.3.15", SERVER_PORT);
                            InputStream is = socket.getInputStream();
                            OutputStream os = socket.getOutputStream();

                            int temp;
                            byte[] lenght = new byte[1024];
                            while ((temp = is.read(lenght)) != -1) {
                                String msg = new String(lenght, 0, temp);
                                Log.v("test", "收到服务器的消息：=" + msg);
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }.start();
                break;
        }
    }
}
