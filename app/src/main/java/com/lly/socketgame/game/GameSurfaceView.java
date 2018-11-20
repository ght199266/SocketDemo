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
public class GameSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable {


    private SurfaceHolder mSurfaceHolder;

    private Canvas mCanvas;

    private boolean isDrawing;


    private int mWidth;

    private int mHeight;


    private int mChessSpace;


    private static final int LINE_MAX = 15;


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

    /**
     * 每30帧刷新一次屏幕
     **/
    public static final int TIME_IN_FRAME = 200;

    @Override
    public void run() {
//        while (isDrawing) {
//            draw();
//        }

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
            drawGrid();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mCanvas != null)
                mSurfaceHolder.unlockCanvasAndPost(mCanvas);//保证每次都将绘图的内容提交
        }
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    /**
     * 绘制棋盘
     */
    private void drawGrid() {
        //使用画笔
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.parseColor("#5E5E5E"));
        paint.setStrokeWidth(5);

//        Log.v("test", "mWidth:=" + mWidth);

        mChessSpace = mWidth / 15;

//        Log.v("test", "mChessSpace:=" + mChessSpace);

        int girdHeight = mChessSpace * LINE_MAX - mChessSpace;

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.bg_wuzi);

        Matrix matrix = new Matrix();

        float scalex = mWidth * 1.0f / bitmap.getWidth();
        float scaley = (girdHeight + 60) * 1.0f / bitmap.getHeight();


        matrix.setScale(scalex, scaley);

        mCanvas.drawBitmap(bitmap, matrix, paint);

        int padding = 30;

        for (int i = 0; i < LINE_MAX; i++) {
            mCanvas.drawLine(padding, i * mChessSpace + padding, mWidth - padding, i * mChessSpace + padding, paint);

            mCanvas.drawLine(i * mChessSpace + padding, padding, i * mChessSpace + padding, girdHeight + padding, paint);
        }


        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(15);
        paint.setStrokeCap(Paint.Cap.ROUND);
        float centerX = mChessSpace * 1.0f * (LINE_MAX / 2) + padding;
        float centerY = mChessSpace * 1.0f * (LINE_MAX / 2) + padding;
        //最中心的圆点
        mCanvas.drawPoint(centerX, centerY, paint);
        //四周
        int sizhou = mChessSpace * 4;
        mCanvas.drawPoint(centerX - sizhou, centerY - sizhou, paint);//左上
        mCanvas.drawPoint(centerX - sizhou, centerY + sizhou, paint);//左下

        mCanvas.drawPoint(centerX + sizhou, centerY - sizhou, paint);//右上
        mCanvas.drawPoint(centerX + sizhou, centerY + sizhou, paint);//右下


        if (chessInfos.size() > 0) {
            for (ChessInfo info : chessInfos) {

                paint.setColor(info.type == 1 ? Color.BLACK : Color.WHITE);

                mCanvas.drawCircle(info.x * mChessSpace + padding, info.y * mChessSpace + padding, mChessSpace / 3, paint);
            }
        }


    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
//                Log.v("test", "mChessSpace：" + mChessSpace);
                Log.v("test", "坐标：" + event.getX());
                Log.v("test", "坐标：" + event.getY());
                Log.v("test", "坐标：" + event.getY() / mChessSpace);

                float eventX = event.getX() - 30.0f;
                float eventy = event.getY() - 30.0f;

                int logcationX = Math.round(eventX / mChessSpace);
                int logcationY = Math.round(eventy / mChessSpace);

                drawChess(logcationX, logcationY);

                if (onLocationlistener != null) {
                    onLocationlistener.onLocation(logcationX, logcationY);
                }

                break;

        }
        return true;
    }

    /**
     * 绘制棋子
     */
    private void drawChess(int x, int y) {
        ChessInfo chessInfo = new ChessInfo(x, y, userType);
        chessInfos.add(chessInfo);
    }

    public interface onLocationListener {
        void onLocation(int x, int y);
    }


    /**
     * 添加一个棋子
     *
     * @param chessInfo
     */
    public void addChess(ChessInfo chessInfo) {
        chessInfos.add(chessInfo);
    }
}