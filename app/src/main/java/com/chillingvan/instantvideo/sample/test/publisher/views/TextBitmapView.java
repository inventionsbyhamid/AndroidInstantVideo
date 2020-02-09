package com.chillingvan.instantvideo.sample.test.publisher.views;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.chillingvan.canvasgl.ICanvasGL;
import com.chillingvan.instantvideo.sample.test.publisher.data.TextData;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;


public class TextBitmapView extends CustomBitmapView {
    private TextData data;
    private Paint paint;
    private Rect bounds;
    private Bitmap bitmap;

    public TextBitmapView(TextData data) {
        this.data = data;
        init();
    }

    private void init() {
        paint = new Paint(ANTI_ALIAS_FLAG);
        paint.setTextSize(data.getTextSize());
        paint.setColor(data.getTextColor());
        bounds = new Rect();
        paint.getTextBounds(data.getText(), 0, data.getText() == null ? 0 : data.getText().length(), bounds);
    }

    @Override
    public void draw(Canvas canvas) {
        draw(canvas, 0);
    }

    @Override
    public void draw(Canvas canvas, int timeFrame) {
        int height = bounds.height();
        int width = bounds.width();
        switch (data.getTextAlign().toLowerCase()) {
            case "center":
                canvas.drawText(data.getText(), data.getX() - width / 2, (data.getY() + height / 2), paint);
                break;
            case "right":
                canvas.drawText(data.getText(), data.getX() - width, (data.getY() + height / 2), paint);
                break;
            case "left":
                canvas.drawText(data.getText(), data.getX(), (data.getY() + height / 2), paint);
                break;
            default:
                canvas.drawText(data.getText(), data.getX(), (data.getY() + height / 2), paint);
        }
    }

    @Override
    public void draw(ICanvasGL canvasGL) {
        if (bitmap == null) {
            getBitmap();
        }
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        switch (data.getTextAlign().toLowerCase()) {
            case "center":
                canvasGL.drawBitmap(getBitmap(), data.getX() - width / 2, (data.getY() - height / 2));
                break;
            case "right":
                canvasGL.drawBitmap(getBitmap(), data.getX() - width, (data.getY() - height / 2));
                break;
            case "left":
                canvasGL.drawBitmap(getBitmap(), data.getX(), (data.getY()));
                break;
            default:
                canvasGL.drawBitmap(getBitmap(), data.getX(), (data.getY() - height / 2));
        }
    }

    public Bitmap getBitmap() {
        if (bitmap != null) {
            return bitmap;
        } else {
            float baseline = -paint.ascent(); // ascent() is negative
            int width = (int) (paint.measureText(data.getText()) + 0.5f); // round
            int height = (int) (baseline + paint.descent() + 0.5f);

            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//            bitmap = Glide.get(BaseApplication.getApplication()).getBitmapPool().get(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawText(data.getText(), 0, baseline, paint);
        }
        return bitmap;
    }

}
