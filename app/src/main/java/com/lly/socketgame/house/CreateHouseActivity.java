package com.lly.socketgame.house;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.lly.socketgame.R;
import com.lly.socketgame.adapter.MessageAdapter;
import com.lly.socketgame.messag.MessageObj;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

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


    RecyclerView recyclerView;

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

    private List<String> list = new ArrayList<>();
    MessageAdapter messageAdapter;

    private OutputStream outputStream;

    Socket socket;

    public static void startCreateHoseActivity(Context ctx, int type) {
        Intent intent = new Intent(ctx, CreateHouseActivity.class);
        intent.putExtra("Type", type);
        ctx.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_house_layout);
        initView();
        initData();
        setAdapter();
    }


    /**
     *
     */
    private void setAdapter() {
        messageAdapter = new MessageAdapter(list);
        recyclerView = findViewById(R.id.recycleview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(messageAdapter);
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
//                    Socket socket = new Socket("192.168.2.204", 8000);
                    Log.v("test", "正在加入房间...");
                    Socket socket = new Socket("192.168.31.108", 8000);
                    socket.setKeepAlive(true);
                    Log.v("test", "房間加入成功...");
                    outputStream = socket.getOutputStream();
                    getClientMessage(socket);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.v("test", "房间加入失败");
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
                socket = serverSocket.accept();
                addMessageAdapter("玩家：" + socket.getInetAddress() + "加入了游戏");
                Log.v("test", "收到一個新的客戶端...");
                //获取客户端消息的
                urgaentData();
                outputStream = socket.getOutputStream();
                getClientMessage(socket);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     *
     */
    private void urgaentData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        socket.sendUrgentData(0xff);
                        Log.v("test", "发送心跳包成功");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    try {
                        socket.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    Log.v("test", "发送心跳包失败");
                }
            }
        }).start();

    }


    /**
     * 添加到消息Adapter
     */
    private void addMessageAdapter(final String mesage) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                list.add(mesage);
                messageAdapter.notifyDataSetChanged();
            }
        });
    }

    private void sendMessage(final String str) {
        if (outputStream == null) {
            Toast.makeText(CreateHouseActivity.this, "消息发送失败", Toast.LENGTH_SHORT).show();
            return;
        }
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    MessageObj messageObj = new MessageObj();
                    messageObj.setType(1);
                    messageObj.setContet(str);
                    ObjectOutputStream objectOutput = new ObjectOutputStream(outputStream);
                    objectOutput.writeObject(messageObj);
                    objectOutput.flush();
                    addMessageAdapter(current == 1 ? "我:" + str : "我:" + str);
                    ed_input.setText("");
                } catch (IOException e) {
                    e.printStackTrace();
                    showMainThreadToast("消息发送失败");
                }
            }
        }.start();
    }

    private void showMainThreadToast(final String str) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(CreateHouseActivity.this, str, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 初始化View
     */
    private void initView() {
        current = getIntent().getIntExtra("Type", 0);

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
            while (true) {
                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                final MessageObj obj = (MessageObj) inputStream.readObject();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (current == create) {
                            addMessageAdapter("客户端：" + obj.getContet());
                        } else {
                            addMessageAdapter("服务器：" + obj.getContet());
                        }
                    }
                });
            }
        } catch (IOException | ClassNotFoundException e) {
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
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
