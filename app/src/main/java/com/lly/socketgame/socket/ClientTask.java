package com.lly.socketgame.socket;

import com.lly.socketgame.messag.MessageObj;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * ClientTask[v 1.0.0]
 * classes:com.lly.socketgame.socket.ClientTask
 *
 * @author lileiyi
 * @date 2018/11/20
 * @time 10:30
 * @description 监听客户端连接的任务
 */
public class ClientTask implements Runnable {


    private Socket mSocket;
    private OutputStream outputStream;


    private IMessageCallBack IMessageCallBack;

    private boolean isRuning = true;


    public ClientTask(Socket mSocket, IMessageCallBack back) {
        this.mSocket = mSocket;
        this.IMessageCallBack = back;
    }

    @Override
    public void run() {
        try {
            outputStream = mSocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (isRuning) {
            try {
                ObjectInputStream inputStream = new ObjectInputStream(mSocket.getInputStream());
                final MessageObj obj = (MessageObj) inputStream.readObject();
                if (IMessageCallBack != null) {
                    IMessageCallBack.acceptMessage(obj);
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void isStop() {
        isRuning = false;
    }
}
