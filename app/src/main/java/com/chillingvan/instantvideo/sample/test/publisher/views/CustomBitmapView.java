package com.chillingvan.instantvideo.sample.test.publisher.views;

import android.graphics.Canvas;

import com.chillingvan.canvasgl.ICanvasGL;


public abstract class CustomBitmapView {
    int loopTime;

    public abstract void draw(Canvas canvas);

    public abstract void draw(Canvas canvas, int frameTime);

    public abstract void draw(ICanvasGL canvasGL);

    public final int getLoopTime() {
        return loopTime;
    }
}