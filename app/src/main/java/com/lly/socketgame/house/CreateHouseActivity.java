package com.lly.socketgame.house;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.lly.socketgame.BaseActivity;
import com.lly.socketgame.R;
import com.lly.socketgame.adapter.MessageAdapter;
import com.lly.socketgame.game.WuZiGameActivity;
import com.lly.socketgame.messag.MessageObj;
import com.lly.socketgame.socket.ConnectManage;
import com.lly.socketgame.socket.IAcceptClientListener;
import com.lly.socketgame.socket.IMessageCallBack;
import com.lly.socketgame.socket.IMessageSendListener;
import com.lly.socketgame.socket.SocketDevice;

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
public class CreateHouseActivity extends BaseActivity implements IMessageCallBack, View.OnClickListener {


    RecyclerView recyclerView;

    EditText ed_input;
    Button btn_send, start_game;
    ImageView iv_other_head;


    private static final String houseName = "盲僧";

    private static final String otheName = "萌提莫";


    //房主
    private static final int create = 1;
    //其他玩家
    private static final int other = 2;

    //用户类型
    private int current;

    private List<String> list = new ArrayList<>();
    MessageAdapter messageAdapter;


    private SocketDevice mSocketDevice;


    public static void startCreateHoseActivity(Context ctx, int type) {
        Intent intent = new Intent(ctx, CreateHouseActivity.class);
        intent.putExtra("Type", type);
        ctx.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.create_house_layout;
    }

    /**
     * 填充数据
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
    protected void initData() {
        switch (current) {
            case create://房主
                //初始化服务器
                ConnectManage.getInstance().initServer(new IAcceptClientListener() {
                    @Override
                    public void onConnect(SocketDevice socket) {
                        CreateHouseActivity.this.mSocketDevice = socket;
                        addMessageAdapter("提莫队长 加入了游戏");
                        isShowHead(true);
                    }

                    @Override
                    public void onDisconnect(Socket socket) {
                        isShowHead(false);
                        Log.v("test", "设备连接断开：=");
                    }
                });
                ConnectManage.getInstance().registerMessageListener(this);
                break;
            case other://其他玩家
                ConnectManage.getInstance().connectServer(new IAcceptClientListener() {
                    @Override
                    public void onConnect(SocketDevice socket) {
                        isShowHead(true);
                        CreateHouseActivity.this.mSocketDevice = socket;
                        Log.v("test", "连接服务器成功：=");
                    }

                    @Override
                    public void onDisconnect(Socket socket) {
                        showDialog();
                    }
                });
                ConnectManage.getInstance().registerMessageListener(this);
                break;
            default:
        }

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


    private void isShowHead(final boolean isShow) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                iv_other_head.setVisibility(isShow ? View.VISIBLE : View.GONE);
            }
        });
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

    /**
     * 初始化View
     */
    protected void initView() {
        current = getIntent().getIntExtra("Type", 0);
        ed_input = findViewById(R.id.ed_input);
        btn_send = findViewById(R.id.btn_send);
        iv_other_head = findViewById(R.id.iv_other_head);
        start_game = findViewById(R.id.start_game);
        start_game.setVisibility(current == 1 ? View.VISIBLE : View.GONE);
        start_game.setOnClickListener(this);

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String content = ed_input.getText().toString();
                if (!TextUtils.isEmpty(content)) {
                    MessageObj messageObj = new MessageObj();
                    messageObj.setType(1);
                    messageObj.setContet(content);
                    mSocketDevice.sendMessage(messageObj, new IMessageSendListener() {
                        @Override
                        public void onSuccess() {
                            addMessageAdapter(current == 1 ? "我:" + content : "我:" + content);
                            ed_input.setText("");
                        }

                        @Override
                        public void onFail() {

                        }
                    });
                }
            }
        });
        setAdapter();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        ConnectManage.getInstance().onDestroy();
        ConnectManage.getInstance().registerMessageListener(null);
    }

    @Override
    public void acceptMessage(MessageObj messageObj) {
        if (messageObj.getType() == 1) {
            addMessageAdapter(current == 1 ? otheName + ":" + messageObj.getContet() : houseName + ":" + messageObj.getContet());
        } else if (messageObj.getType() == 2) {//开始游戏
            WuZiGameActivity.startGameActivity(this, current);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_game:
                MessageObj messageObj = new MessageObj();
                messageObj.setType(2);
                mSocketDevice.sendMessage(messageObj, new IMessageSendListener() {
                    @Override
                    public void onSuccess() {
                        WuZiGameActivity.startGameActivity(CreateHouseActivity.this, current);
                    }

                    @Override
                    public void onFail() {
                    }
                });
                break;
        }
    }
}
