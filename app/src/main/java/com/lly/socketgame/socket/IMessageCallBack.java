package com.lly.socketgame.socket;

import com.lly.socketgame.messag.MessageObj;

/**
 * IMessageCallBack[v 1.0.0]
 * classes:com.lly.socketgame.socket.IMessageCallBack
 *
 * @author lileiyi
 * @date 2018/11/20
 * @time 11:12
 * @description
 */
public interface IMessageCallBack {

    void acceptMessage(MessageObj messageObj);
}
