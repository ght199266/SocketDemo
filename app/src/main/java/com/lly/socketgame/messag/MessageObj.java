package com.lly.socketgame.messag;

import java.io.Serializable;

/**
 * MessageObj[v 1.0.0]
 * classes:com.lly.socketgame.messag.MessageObj
 *
 * @author lileiyi
 * @date 2018/11/18
 * @time 14:32
 * @description 傳輸的數據
 */

public class MessageObj implements Serializable {

    //消息类型 1、聊天消息 2、游戏消息
    private int type;
    //聊天消息内容
    private String contet;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContet() {
        return contet;
    }

    public void setContet(String contet) {
        this.contet = contet;
    }
}
