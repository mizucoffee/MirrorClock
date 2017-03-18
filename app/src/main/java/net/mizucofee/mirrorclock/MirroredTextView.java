package net.mizucofee.mirrorclock;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

public class MirroredTextView extends android.support.v7.widget.AppCompatTextView {

    public MirroredTextView(Context context) {
        super(context);
    }
    public MirroredTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public MirroredTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.translate(getWidth(), 0);
        canvas.scale(-1, 1);
        super.onDraw(canvas);
    }
}