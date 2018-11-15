package com.lly.socketgame.client;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * ClientTask[v 1.0.0]
 * classes:com.lly.socketgame.client.ClientTask
 *
 * @author lileiyi
 * @date 2018/11/15
 * @time 15:16
 * @description
 */
public class ClientTask implements Runnable {

    private Socket mSocket;

    private InputStream is;
    private OutputStream oS;


    public ClientTask(Socket mSocket) {
        this.mSocket = mSocket;
    }

    @Override
    public void run() {
        if (mSocket != null) {
            try {
                //获取服务器传输的消息
                is = mSocket.getInputStream();
                oS = mSocket.getOutputStream();
//                getMessage();
                senMessage("恭喜你连接成功了");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void getMessage() {
        try {
            int temp;
            byte[] lenght = new byte[1024];
            while ((temp = is.read(lenght)) != -1) {
                String msg = new String(lenght, 0, temp);
                Log.v("test", "收到的服务器消息：=" + msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void senMessage(String str) {
        if (oS != null) {
            try {
                oS.write(str.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.v("test", "消息发送失败");
        }
    }
}
