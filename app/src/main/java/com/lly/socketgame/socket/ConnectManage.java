package com.lly.socketgame.socket;

import android.os.Looper;

import com.lly.socketgame.messag.MessageObj;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * ConnectManage[v 1.0.0]
 * classes:com.lly.socketgame.socket.ConnectManage
 *
 * @author lileiyi
 * @date 2018/11/20
 * @time 10:14
 * @description 客户端服务器连接管理--目前仅支持1v1的连接
 */
public class ConnectManage {

    private android.os.Handler handler = new android.os.Handler(Looper.myLooper());

    private static final String IP_ADDRESS = "192.168.2.204";

    private static final int SERVER_PORT = 8000;

    private static ConnectManage connectManage;

    private IAcceptClientListener mIAcceptClientListener;
    private IMessageCallBack mImessageCallBack;

//    private Socket mClientSocke;


    private SocketDevice mServerSocket;

    private SocketDevice mClientsocketDevice;

    //是否销魂
    private boolean isDestory = false;

    /**
     * 心跳轮询时间
     */
    private static final int POLL_TIME = 1000;

    public static ConnectManage getInstance() {
        if (connectManage == null) {
            synchronized (ConnectManage.class) {
                if (connectManage == null) {
                    connectManage = new ConnectManage();
                }
            }
        }
        return connectManage;
    }

    /**
     * 初始化Socket服务器
     */
    public void initServer(IAcceptClientListener listener) {
        try {
            ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
            acceptTask(serverSocket, listener);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 开启线程监听客户端的连接
     */
    private void acceptTask(final ServerSocket serverSocket, final IAcceptClientListener listener) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                while (!isDestory) {
                    try {
                        Socket socket = serverSocket.accept();
                        mServerSocket = new SocketDevice(socket, handler);
                        if (listener != null) {
                            listener.onConnect(mServerSocket);
                        }
                        ClientTask clientTask = new ClientTask(socket, new IMessageCallBack() {
                            @Override
                            public void acceptMessage(final MessageObj messageObj) {
                                if (mImessageCallBack != null) {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            mImessageCallBack.acceptMessage(messageObj);
                                        }
                                    });
                                }
                            }
                        });
                        Thread thread = new Thread(clientTask);
                        thread.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }


    /**
     * 结束设备连接监听
     *
     * @param listener
     */
    public void registerAcceptListener(IAcceptClientListener listener) {
        this.mIAcceptClientListener = listener;
    }


    /**
     * 注册消息收到监听
     *
     * @param messageCallBack
     */
    public void registerMessageListener(IMessageCallBack messageCallBack) {
        this.mImessageCallBack = messageCallBack;
    }

    /**
     * 连接服务器
     */
    public void connectServer(final IAcceptClientListener listener) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    Socket mClientSocke = new Socket(IP_ADDRESS, SERVER_PORT);
                    mClientsocketDevice = new SocketDevice(mClientSocke, handler);
//                    pollCheckConnect(mClientSocke, new IConnectListener() {
//                        @Override
//                        public void disConnect() {
//                            //连接断开
//                            listener.onDisconnect(mClientSocke);
//                        }
//                    });
                    listener.onConnect(mClientsocketDevice);
//                    socket.getOutputStream();
                    while (!isDestory) {
                        try {
                            ObjectInputStream inputStream = new ObjectInputStream(mClientSocke.getInputStream());
                            final MessageObj obj = (MessageObj) inputStream.readObject();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (mImessageCallBack != null) {
                                        mImessageCallBack.acceptMessage(obj);
                                    }
                                }
                            });
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }


    /**
     * 轮询检查是否连接
     */
    private void pollCheckConnect(final Socket socket, final IConnectListener listener) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                while (!isDestory)
                    try {
                        if (socket != null) {
                            socket.sendUrgentData(0xff);
                        }
                        try {
                            Thread.sleep(POLL_TIME);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
//                        if (mIAcceptClientListener != null) {
//                            mIAcceptClientListener.onDisconnect(mClientSocke);
//                        }
                        listener.disConnect();
                        break;
                    }
            }
        }.start();
    }


    /**
     * 销毁页面
     */
    public void onDestroy() {
        isDestory = true;
    }


    /**
     *
     */
    private interface IConnectListener {

        void disConnect();
    }

    /**
     * 获取客户端的Socket，给客户端发送消息
     *
     * @return
     */
    public SocketDevice getClientDevice() {
        return mServerSocket;
    }


    /**
     * 获取
     *
     * @return
     */
    public SocketDevice getServerDevice() {

        return mClientsocketDevice;
    }

}
