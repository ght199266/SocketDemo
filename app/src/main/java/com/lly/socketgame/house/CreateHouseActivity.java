package com.lly.socketgame.house;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
    ImageView iv_other_head;

    private static final int SERVER_PORT = 8000;

    private ServerSocket serverSocket;


    private static final String houseName = "盲僧";

    private static final String otheName = "萌提莫";


    private boolean isDestory = false;


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
        Window window = getWindow();
//5.0版本及以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }

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
                    socket = new Socket("192.168.2.204", 8000);
                    clientSend();
                    isShowHead(true);
                    outputStream = socket.getOutputStream();
                    getClientMessage(socket);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.v("test", "房间加入失败");
                }
            }
        }.start();
    }


    private void clientSend() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (!isDestory) {
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (socket != null) {
                            socket.sendUrgentData(0xff);
                        }
                        Log.v("test", "客户端开送心跳包");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.v("test", "客户端开送心跳包");
                    showDialog();
                }
            }
        }).start();
    }

    private void showDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog dialog = new AlertDialog.Builder(CreateHouseActivity.this).create();//创建对话框
                dialog.setCancelable(false);
                dialog.setTitle("房主已退出房间游戏解散!");//设置对话框标题
                //分别设置三个button
                dialog.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();//关闭对话框
                        CreateHouseActivity.this.finish();
                    }
                });
                dialog.show();
            }
        });
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
                while (!isDestory) {
                    socket = serverSocket.accept();
                    addMessageAdapter(otheName + " 加入了游戏");
                    isShowHead(true);
                    //获取客户端消息的
                    sendUrgentData();
                    outputStream = socket.getOutputStream();
                    getClientMessage(socket);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void isShowHead(final boolean isShow) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                iv_other_head.setVisibility(isShow ? View.VISIBLE : View.GONE);
            }
        });
    }

    /**
     * 每隔一段时间发送心跳包
     */
    private void sendUrgentData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (!isDestory) {
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (socket != null) {
                            socket.sendUrgentData(0xff);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    isShowHead(false);
                    addMessageAdapter(otheName + " 离开了游戏");
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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ed_input.setText("");
                        }
                    });
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
        iv_other_head = findViewById(R.id.iv_other_head);

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
            while (!isDestory) {
                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                final MessageObj obj = (MessageObj) inputStream.readObject();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (current == create) {
                            addMessageAdapter(otheName + ":" + obj.getContet());
                        } else {
                            addMessageAdapter(houseName + ":" + obj.getContet());
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
        isDestory = true;
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            if (socket != null) {
                socket.close();
                socket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
