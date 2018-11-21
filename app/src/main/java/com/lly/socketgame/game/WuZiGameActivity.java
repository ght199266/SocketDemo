package com.lly.socketgame.game;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
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
public class WuZiGameActivity extends BaseActivity {

    private GameSurfaceView mSurfaceView;


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
        userType = getIntent().getIntExtra("Type", 0);
        mSurfaceView.setUserType(userType);
        setListener();
    }

    @Override
    protected void initData() {
        ConnectManage.getInstance().registerMessageListener(new IMessageCallBack() {
            @Override
            public void acceptMessage(MessageObj messageObj) {
                if (messageObj.getType() == 3) {
                    ChessInfo chessInfo = messageObj.getChessInfo();
                    mSurfaceView.addChess(chessInfo);
                    mSurfaceView.setDisableChess(true);
                } else if (messageObj.getType() == 4) {//你输了
                    mSurfaceView.setDisableChess(false);
                    Toast.makeText(WuZiGameActivity.this, "你输了", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setListener() {
        mSurfaceView.setOnLocationlistener(new GameSurfaceView.onLocationListener() {
            @Override
            public void onLocation(int x, int y) {
                Log.v("test", "X:" + x);
                Log.v("test", "y:" + y);
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

                Toast.makeText(WuZiGameActivity.this, "恭喜你,你胜利了！！", Toast.LENGTH_SHORT).show();

                MessageObj messageObj = new MessageObj();
                messageObj.setType(4);
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
        });
    }
}
