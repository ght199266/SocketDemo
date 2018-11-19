package com.lly.socketgame.game;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceView;

import com.lly.socketgame.R;

/**
 * WuZiGameActivity[v 1.0.0]
 * classes:com.lly.socketgame.game.WuZiGameActivity
 *
 * @author lileiyi
 * @date 2018/11/19
 * @time 13:55
 * @description
 */
public class WuZiGameActivity extends AppCompatActivity {

    private SurfaceView mSurfaceView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wuzi_game_layout);
        mSurfaceView = findViewById(R.id.sfv_view);
    }
}
