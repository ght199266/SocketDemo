package com.lly.socketgame.bean;

import java.io.Serializable;

/**
 * ChessInfo[v 1.0.0]
 * classes:com.lly.socketgame.bean.ChessInfo
 *
 * @author lileiyi
 * @date 2018/11/19
 * @time 15:57
 * @description
 */
public class ChessInfo implements Serializable {

    public int x;
    public int y;

    public int type;

    public ChessInfo(int x, int y, int type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }
}
