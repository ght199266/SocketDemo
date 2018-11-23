package com.lly.socketgame.socket;

import android.os.Handler;

import com.lly.socketgame.messag.MessageObj;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * SocketDevice[v 1.0.0]
 * classes:com.lly.socketgame.socket.SocketDevice
 *
 * @author lileiyi
 * @date 2018/11/20
 * @time 14:57
 * @description
 */
public class SocketDevice {


    private android.os.Handler mHandler;

    private Socket mSocket;

    public SocketDevice(Socket mSocket, Handler handler) {
        this.mSocket = mSocket;
        this.mHandler = handler;
    }

    public Socket getSocket() {
        return mSocket;
    }

    /**
     * 发送消息
     */
    public void sendMessage(final MessageObj messageObj, final IMessageSendListener listener) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    ObjectOutputStream objectOutput = new ObjectOutputStream(mSocket.getOutputStream());
                    objectOutput.writeObject(messageObj);
                    objectOutput.flush();
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onSuccess();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onFail();
                        }
                    });
                }
            }
        }.start();
    }
}
