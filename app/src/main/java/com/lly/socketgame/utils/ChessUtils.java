package com.lly.socketgame.utils;

/**
 * ChessUtils[v 1.0.0]
 * classes:com.lly.socketgame.utils.ChessUtils
 *
 * @author lileiyi
 * @date 2018/11/21
 * @time 11:25
 * @description
 */
public class ChessUtils {

    // 15 * 15 格子
    private static final int MAX_GRID = 15;


    //连续数量
    private static final int LINK_COUNT = 5;

    /**
     * 判断是否五子连棋
     */
    public static boolean isCheckWin(int[][] allChess, int x, int y) {
        //拿到当前的颜色
        int color = allChess[x][y];

        int count = 1;
        //右
        while (x + 1 != MAX_GRID && allChess[x + count][y] == color) {
            count++;
            if (x + count - x == LINK_COUNT) {
                return true;
            }
            if (x + count == MAX_GRID) {
                break;
            }
        }
        count = 1;
        //左
        while (x != 0 && allChess[x - count][y] == color) {
            count++;
            if (x - (x - count) == LINK_COUNT) {
                return true;
            }
            if (x - count <= 0) {
                break;
            }
        }
        //上
        count = 1;
        while (y != 0 && allChess[x][y - count] == color) {
            count++;
            if (y - (y - count) == LINK_COUNT) {
                return true;
            }
            if (y - count <= 0) {
                break;
            }
        }
        //下
        count = 1;
        while (y + 1 != MAX_GRID && allChess[x][y + count] == color) {
            count++;
            if (y + count - y == LINK_COUNT) {
                return true;
            }
            if (y + count == MAX_GRID) {
                break;
            }

        }

        //右上
        count = 1;
        while ((x + 1 != MAX_GRID && y != 0) && allChess[x + count][y - count] == color) {
            count++;
//            if (y-(y-count)==LINK_COUNT) 这样应该也可以
            if (x + count - x == LINK_COUNT) {
                return true;
            }
            if (x + count == MAX_GRID || y - count <= 0) {
                break;
            }

        }


        //右下
        count = 1;
        while ((x + 1 != MAX_GRID && y + 1 != MAX_GRID) && allChess[x + count][y + count] == color) {
            count++;
            if (x + count - x == LINK_COUNT) {
                return true;
            }
            if (x + count == MAX_GRID || y + count == MAX_GRID) {
                break;
            }
        }

        //左上
        count = 1;
        while ((x != 0 && y != 0) && allChess[x - count][y - count] == color) {
            count++;
            if (x - (x - count) == LINK_COUNT) {
                return true;
            }
            if (x - count <= 0 || y - count <= 0) {
                break;
            }
        }

        //左下
        count = 1;
        while ((x != 0 && y + 1 != MAX_GRID) && allChess[x - count][y + count] == color) {
            count++;
            if (y + count - y == LINK_COUNT) {
                return true;
            }
            if (x - count <= 0 || y + count == MAX_GRID) {
                break;
            }
        }

        return false;
    }
}
