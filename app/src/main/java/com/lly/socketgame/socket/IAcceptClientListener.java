package com.lly.socketgame.socket;

/**
 * IAcceptClientListener[v 1.0.0]
 * classes:com.lly.socketgame.socket.IAcceptClientListener
 *
 * @author lileiyi
 * @date 2018/11/20
 * @time 10:47
 * @description
 */
public interface IAcceptClientListener {

    void onConnect(SocketDevice socket);


    void onDisableConnect();


}
