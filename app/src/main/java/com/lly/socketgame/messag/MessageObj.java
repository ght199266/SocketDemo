package com.lly.socketgame.messag;

import com.lly.socketgame.bean.ChessInfo;

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


    /**
     * 区分消息类型
     * 1、游戏聊天消息
     * 2、开始游戏
     * 3、五子棋的位置--chessInfo
     * to do
     */
    private int type;
    //聊天消息内容
    private String contet;


    private ChessInfo chessInfo;

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

    public ChessInfo getChessInfo() {
        return chessInfo;
    }

    public void setChessInfo(ChessInfo chessInfo) {
        this.chessInfo = chessInfo;
    }

    @Override
    public String toString() {
        return "MessageObj{" +
                "type=" + type +
                ", contet='" + contet + '\'' +
                ", chessInfo=" + chessInfo +
                '}';
    }
}
