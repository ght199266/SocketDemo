package com.lly.socketgame.house;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lly.socketgame.R;
import com.lly.socketgame.utils.IpUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * CreateHouseActivity[v 1.0.0]
 * classes:com.lly.socketgame.house.CreateHouseActivity
 *
 * @author lileiyi
 * @date 2018/11/16
 * @time 10:16
 * @description
 */
public class CreateHouseActivity extends AppCompatActivity {


    TextView tv_house;
    TextView tv_name;

    EditText ed_input;
    Button btn_send;

    private static final int SERVER_PORT = 8000;

    private ServerSocket serverSocket;

    private boolean isAccept = true;


    //房主
    private static final int create = 1;
    //其他玩家
    private static final int other = 2;

    //用户类型
    private int current;


    private OutputStream outputStream;


    public static void startCreateHoseActivity(Context ctx, int type) {
        Intent intent = new Intent(ctx, CreateHouseActivity.class);
        intent.putExtra("Type", type);
        ctx.startActivity(intent);
    }


    /**
     * 创建数据
     */
    private void initData() {
        switch (current) {
            case create://房主
                createServer();
                break;
            case other://其他玩家
                connectServer();
                break;
            default:
        }
    }

    /**
     * 连接服务器
     */
    private void connectServer() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    Socket socket = new Socket("192.168.2.204", 8000);
                    socket.setKeepAlive(true);
                    outputStream = socket.getOutputStream();
                    getClientMessage(socket);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }

    /**
     * 创建服务器等待其他玩家加入
     */
    private void createServer() {
        try {
            serverSocket = new ServerSocket(SERVER_PORT);
            //开启一个线程监听客户端连接
            new Thread(new AcceptClientTask()).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 监听客户端连接的Task
     */
    private class AcceptClientTask implements Runnable {
        @Override
        public void run() {
            if (serverSocket == null) {
                return;
            }
            try {
                final Socket socket = serverSocket.accept();
                //获取客户端消息的
                getClientMessage(socket);

                outputStream = socket.getOutputStream();

                //更新Ui
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CreateHouseActivity.this, "玩家：" + socket.getInetAddress() + "加入了游戏", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_house_layout);
        initView();
        initData();
    }


    private void sendMessage(String str) {
        if (outputStream == null) {
            Toast.makeText(CreateHouseActivity.this, "消息发送失败", Toast.LENGTH_SHORT).show();
            return;
        }
        try {

            outputStream.write(str.getBytes());
            Toast.makeText(CreateHouseActivity.this, "消息发送成功", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(CreateHouseActivity.this, "消息发送失败", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 初始化View
     */
    private void initView() {
        current = getIntent().getIntExtra("Type", 0);
        tv_house = findViewById(R.id.tv_house);
        tv_house.setText("房主:" + IpUtils.getIPAddress(this).getProperty("ip"));
        tv_name = findViewById(R.id.tv_name);

        ed_input = findViewById(R.id.ed_input);
        btn_send = findViewById(R.id.btn_send);


        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(ed_input.getText().toString())) {
                    sendMessage(ed_input.getText().toString());
                }
            }
        });
    }

    /**
     * 获取客户端的消息
     *
     * @param socket
     */
    private void getClientMessage(Socket socket) {
        try {
            InputStream is = socket.getInputStream();
            int temp;
            byte[] bytes = new byte[1024];
            while ((temp = is.read(bytes)) != -1) {
                final String msg = new String(bytes, 0, temp);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (current == create) {
                            Toast.makeText(CreateHouseActivity.this, "收到客户端消息：" + msg, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(CreateHouseActivity.this, "收到服务端消息：" + msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isAccept = false;
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
