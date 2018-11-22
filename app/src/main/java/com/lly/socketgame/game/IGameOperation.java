package com.lly.socketgame.game;

import com.lly.socketgame.bean.ChessInfo;

/**
 * IGameOperation[v 1.0.0]
 * classes:com.lly.socketgame.game.IGameOperation
 *
 * @author lileiyi
 * @date 2018/11/22
 * @time 16:30
 * @description 五子棋操作
 */
public interface IGameOperation {

    /**
     * 添加棋子
     */
    boolean addChess(ChessInfo chessInfo);

    /**
     * 悔棋
     */
    void onGoBack();

}
