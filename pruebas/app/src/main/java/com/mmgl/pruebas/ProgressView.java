package com.mmgl.pruebas;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class ProgressView extends View {
    private int progress = 0;
    private boolean enabled = true;

    private Paint paint;

    public ProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setColor(0xFF3F51B5);
        paint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        float progressWidth = (progress / 100.0f) * width;

        canvas.drawRect(0, 0, progressWidth, height, paint);
    }

    public void setProgress(int progress) {
        this.progress = progress;
        invalidate();
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void incrementProgress(int value) {
        if (enabled) {
            this.progress += value;
            invalidate();
        }
    }
}
