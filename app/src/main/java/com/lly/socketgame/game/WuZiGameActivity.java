package com.lly.socketgame.game;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.lly.socketgame.BaseActivity;
import com.lly.socketgame.R;
import com.lly.socketgame.bean.ChessInfo;
import com.lly.socketgame.messag.MessageObj;
import com.lly.socketgame.socket.ConnectManage;
import com.lly.socketgame.socket.IMessageCallBack;
import com.lly.socketgame.socket.IMessageSendListener;
import com.lly.socketgame.socket.SocketDevice;

/**
 * WuZiGameActivity[v 1.0.0]
 * classes:com.lly.socketgame.game.WuZiGameActivity
 *
 * @author lileiyi
 * @date 2018/11/19
 * @time 13:55
 * @description
 */
public class WuZiGameActivity extends BaseActivity implements View.OnClickListener {

    private GameSurfaceView mSurfaceView;


    private Button btn_exit, btn_go_back, btn_give_up;


    private int userType;

    public static void startGameActivity(Context context, int type) {
        Intent intent = new Intent(context, WuZiGameActivity.class);
        intent.putExtra("Type", type);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.wuzi_game_layout;
    }

    @Override
    protected void initView() {
        mSurfaceView = findViewById(R.id.sfv_view);
        btn_exit = findViewById(R.id.btn_exit);
        btn_go_back = findViewById(R.id.btn_go_back);
        btn_give_up = findViewById(R.id.btn_give_up);

        btn_exit.setOnClickListener(this);
        btn_go_back.setOnClickListener(this);
        btn_give_up.setOnClickListener(this);


        userType = getIntent().getIntExtra("Type", 0);
        mSurfaceView.setUserType(userType);
        mSurfaceView.setDisableChess(userType != 1);

        setListener();
    }

    @Override
    protected void initData() {
        ConnectManage.getInstance().registerMessageListener(new IMessageCallBack() {
            @Override
            public void acceptMessage(MessageObj messageObj) {
                switch (messageObj.getType()) {
                    case 3://落棋子信息
                        ChessInfo chessInfo = messageObj.getChessInfo();
                        mSurfaceView.addChess(chessInfo);
                        break;
                    case 4://失败提示信息
                        mSurfaceView.disableChess();
                        Toast.makeText(WuZiGameActivity.this, "你输了", Toast.LENGTH_SHORT).show();
                        break;
                    case 5://对方认输或者退出你胜利的提示
                        mSurfaceView.disableChess();
                        Toast.makeText(WuZiGameActivity.this, "对方认输,你获得了胜利!", Toast.LENGTH_SHORT).show();
                        break;
                    case 6://对方请求悔棋
                        goBackDialog();
                        break;
                    case 7://同意悔棋
                        mSurfaceView.onGoBack();
                        Toast.makeText(WuZiGameActivity.this, "对方同意悔棋", Toast.LENGTH_SHORT).show();
                        break;
                    case 8://拒绝悔棋
                        Toast.makeText(WuZiGameActivity.this, "对方拒绝悔棋", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    private void setListener() {
        mSurfaceView.setOnLocationlistener(new GameSurfaceView.onLocationListener() {
            @Override
            public void onLocation(int x, int y) {
                MessageObj messageObj = new MessageObj();
                messageObj.setType(3);
                ChessInfo chessInfo = new ChessInfo(x, y, userType);
                messageObj.setChessInfo(chessInfo);
                SocketDevice socketDevice = userType == 1 ? ConnectManage.getInstance().getClientDevice() : ConnectManage.getInstance().getServerDevice();
                socketDevice.sendMessage(messageObj, new IMessageSendListener() {
                    @Override
                    public void onSuccess() {
                        Log.v("test", "消息发送成功");
                    }

                    @Override
                    public void onFail() {

                    }
                });
            }

            @Override
            public void onWin() {
                sendMessage(4);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_exit://退出
                break;
            case R.id.btn_go_back:
                if (mSurfaceView.isGoBackChess()) {
                    sendMessage(6);
                } else {
                    showTaost("暂时不能悔棋哦");
                }
                break;
            case R.id.btn_give_up:
                break;
        }
    }

    /**
     * 是否同意对方悔棋
     */
    private void goBackDialog() {
        AlertDialog dialog = new AlertDialog.Builder(WuZiGameActivity.this).create();//创建对话框
        dialog.setCancelable(false);
        dialog.setTitle("对方请求悔棋,是否同意");//设置对话框标题
        //分别设置三个button
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "同意", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                sendMessage(7);
            }
        });

        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "拒绝", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                sendMessage(8);
            }
        });
        dialog.show();
    }


    /**
     * 发送消息
     */
    private void sendMessage(int type) {
        MessageObj messageObj = new MessageObj();
        messageObj.setType(type);
        SocketDevice socketDevice = userType == 1 ? ConnectManage.getInstance().getClientDevice() : ConnectManage.getInstance().getServerDevice();
        socketDevice.sendMessage(messageObj, new IMessageSendListener() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onFail() {

            }
        });
    }

}
