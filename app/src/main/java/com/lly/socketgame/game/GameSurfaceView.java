package com.lly.socketgame.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.lly.socketgame.R;
import com.lly.socketgame.bean.ChessInfo;
import com.lly.socketgame.utils.ChessUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * WuZiGanmeSurfaceView[v 1.0.0]
 * classes:com.lly.socketgame.game.WuZiGanmeSurfaceView
 *
 * @author lileiyi
 * @date 2018/11/19
 * @time 14:00
 * @description
 */
public class GameSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable, IGameOperation {


    private SurfaceHolder mSurfaceHolder;

    private Canvas mCanvas;

    private boolean isDrawing;


    private int mWidth;

    private int mHeight;

    //是否允许下棋
    private boolean isDisableChess = true;


    /**
     * 棋盘 15 x 15;
     */
    private static final int LINE_MAX = 15;

    /**
     * 棋盘四周Padding
     */
    private static final float CHESS_PADDING = 30;

    /**
     * 棋盘背景图片
     */
    private Bitmap mChessBackground;


    /**
     * 棋盘高度
     */
    private int mChessHeight;

    /**
     * 二维数组保存棋子落下的X、Y坐标
     */
    private int[][] AllChess = new int[LINE_MAX][LINE_MAX];

    /**
     * 每个宫格的大小
     */
    private int mChess_gird_size;

    /**
     * 绘制棋盘宫格的画笔
     */
    private Paint mChessLinePaint;

    /**
     * 宫格画笔的颜色
     */
    private String mChessLineColor = "#5E5E5E";

    /**
     * 宫格画笔的宽度
     */
    private int mChessLineWidth = 4;


    /**
     * 每30帧刷新一次屏幕
     **/
    public static final int TIME_IN_FRAME = 100;

    private List<ChessInfo> chessInfos = new ArrayList<>();


    private onLocationListener onLocationlistener;


    private int userType;

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public GameSurfaceView(Context context) {
        this(context, null);
    }

    public GameSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GameSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        setFocusable(true);
        setFocusableInTouchMode(true);
        this.setKeepScreenOn(true);

        init();
    }

    /**
     * 初始化
     */
    private void init() {
        mChessLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mChessLinePaint.setColor(Color.parseColor(mChessLineColor));
        mChessLinePaint.setStrokeWidth(mChessLineWidth);
        mChessBackground = BitmapFactory.decodeResource(getResources(), R.mipmap.bg_wuzi);
    }

    /**
     * 是否禁止下棋
     */
    public boolean isDisableChess() {
        return isDisableChess;
    }

    /**
     * 是否能悔棋
     */
    public boolean isGoBackChess() {
        if (isDisableChess && chessInfos.size() > 0) {
            for (ChessInfo info : chessInfos) {
                if (info.type == userType) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 禁止下棋
     */
    public void disableChess() {
        isDisableChess = true;
    }

    /**
     * 启用下棋
     */
    public void enableChess() {
        isDisableChess = false;
    }

    public void setDisableChess(boolean disableChess) {
        isDisableChess = disableChess;
    }

    public void setOnLocationlistener(GameSurfaceView.onLocationListener onLocationlistener) {
        this.onLocationlistener = onLocationlistener;

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        isDrawing = true;
        new Thread(this).start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isDrawing = false;
    }


    @Override
    public void run() {
        while (isDrawing) {
            long startTime = System.currentTimeMillis();
            synchronized (mSurfaceHolder) {
                /*拿到当前画布 然后锁定**/
                draw();
                /*绘制结束后解锁显示在屏幕上**/
//                mSurfaceHolder.unlockCanvasAndPost(mCanvas);
            }
            /*取得更新结束的时间**/
            long endTime = System.currentTimeMillis();
            /*计算出一次更新的毫秒数**/
            int diffTime = (int) (endTime - startTime);
            /*确保每次更新时间为30帧**/
            while (diffTime <= TIME_IN_FRAME) {
                diffTime = (int) (System.currentTimeMillis() - startTime);
                /*线程等待**/
                Thread.yield();
            }
        }
    }

    private void draw() {
        try {
            mCanvas = mSurfaceHolder.lockCanvas();
            onDrawChess();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mCanvas != null)
                mSurfaceHolder.unlockCanvasAndPost(mCanvas);//保证每次都将绘图的内容提交
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int specMode = MeasureSpec.getMode(heightMeasureSpec);
        int specSize = MeasureSpec.getSize(widthMeasureSpec);
        mChess_gird_size = (int) ((specSize - (CHESS_PADDING * 2)) / (LINE_MAX - 1));
        setMeasuredDimension(specSize, (int) ((mChess_gird_size * (LINE_MAX - 1)) + CHESS_PADDING * 2));

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }


    /**
     * 绘制棋盘背景
     */
    private void drawChessBackground() {
        //背景图片缩放
        Matrix matrix = new Matrix();
        float scaleX = mWidth * 1.0f / mChessBackground.getWidth();
        float scaleY = (mChessHeight + CHESS_PADDING * 2) * 1.0f / mChessBackground.getHeight();
        matrix.setScale(scaleX, scaleY);
        //绘制背景
        mCanvas.drawBitmap(mChessBackground, matrix, mChessLinePaint);
    }


    /**
     * 绘制宫格线
     */
    private void drawChessGrid() {
        mChessLinePaint.setColor(Color.parseColor(mChessLineColor));
        mChessLinePaint.setStrokeWidth(mChessLineWidth);
        for (int i = 0; i < LINE_MAX; i++) {
            mCanvas.drawLine(CHESS_PADDING, i * mChess_gird_size + CHESS_PADDING, (LINE_MAX - 1) * mChess_gird_size + CHESS_PADDING, i * mChess_gird_size + CHESS_PADDING, mChessLinePaint);
            mCanvas.drawLine(i * mChess_gird_size + CHESS_PADDING, CHESS_PADDING, i * mChess_gird_size + CHESS_PADDING, mChessHeight + CHESS_PADDING, mChessLinePaint);
        }
    }


    /**
     * 绘制五子棋中的中间和四周的五个点
     */
    private void drawChessPoint() {
        mChessLinePaint.setColor(Color.BLACK);
        mChessLinePaint.setStrokeWidth(15);
//        mChessLinePaint.setStrokeCap(Paint.Cap.ROUND);

        float centerX = mChess_gird_size * 1.0f * (LINE_MAX / 2) + CHESS_PADDING;
        float centerY = mChess_gird_size * 1.0f * (LINE_MAX / 2) + CHESS_PADDING;

        //最中心的圆点
        mCanvas.drawPoint(centerX, centerY, mChessLinePaint);

        //四周的点
        int space = mChess_gird_size * 4;
        mCanvas.drawPoint(centerX - space, centerY - space, mChessLinePaint);//左上
        mCanvas.drawPoint(centerX - space, centerY + space, mChessLinePaint);//左下

        mCanvas.drawPoint(centerX + space, centerY - space, mChessLinePaint);//右上
        mCanvas.drawPoint(centerX + space, centerY + space, mChessLinePaint);//右下
    }


    /**
     * 绘制棋子
     */
    private void drawChess() {
        if (chessInfos.size() > 0) {
            for (ChessInfo info : chessInfos) {
                mChessLinePaint.setColor(info.type == 1 ? Color.BLACK : Color.WHITE);
                mCanvas.drawCircle(info.x * mChess_gird_size + CHESS_PADDING, info.y * mChess_gird_size + CHESS_PADDING, mChess_gird_size / 3, mChessLinePaint);
            }
        }
    }

    /**
     * 绘制棋盘
     */
    private void onDrawChess() {
        mChessHeight = mChess_gird_size * LINE_MAX - mChess_gird_size;
        //绘制背景
        drawChessBackground();
        //绘制宫格线
        drawChessGrid();
        //绘制四周的点
        drawChessPoint();
        //绘制棋子
        drawChess();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isDisableChess)
            return true;
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                float eventX = event.getX() - CHESS_PADDING;
                float eventY = event.getY() - CHESS_PADDING;
                if (eventX > 0 && eventY > 0) {
                    int locationX = Math.round(eventX / mChess_gird_size);
                    int locationY = Math.round(eventY / mChess_gird_size);
                    if (isChess(locationX, locationY)) {
                        AllChess[locationX][locationY] = userType;
                        drawChess(locationX, locationY);
                        if (ChessUtils.isCheckWin(AllChess, locationX, locationY)) {//胜利
                            onLocationlistener.onWin();
                        } else {
                            if (onLocationlistener != null) {
                                onLocationlistener.onLocation(locationX, locationY);
                            }
                        }
                        isDisableChess = true;
                    } else {
                        Log.v("test", "已经有棋子了");
                    }
                }
                break;
        }
        return true;
    }


    /**
     * 是否有棋子
     */
    private boolean isChess(int x, int y) {
        return AllChess[x][y] == 0;
    }

    /**
     * 绘制棋子
     */
    private void drawChess(int x, int y) {
        ChessInfo chessInfo = new ChessInfo(x, y, userType);
        chessInfos.add(chessInfo);
    }

    @Override
    public boolean addChess(ChessInfo chessInfo) {
        if (chessInfos.size() < (LINE_MAX * LINE_MAX)) {
            chessInfos.add(chessInfo);
            AllChess[chessInfo.x][chessInfo.y] = chessInfo.type;
            enableChess();
            return true;
        }
        return false;
    }

    @Override
    public void onGoBack() {
        ChessInfo chessInfo = chessInfos.get(chessInfos.size() - 1);
        AllChess[chessInfo.x][chessInfo.y] = 0;
        chessInfos.remove(chessInfos.size() - 1);
    }

    public interface onLocationListener {
        void onLocation(int x, int y);

        void onWin();
    }

}