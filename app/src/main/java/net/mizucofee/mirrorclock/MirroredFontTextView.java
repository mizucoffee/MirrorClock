package net.mizucofee.mirrorclock;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;

public class MirroredFontTextView extends android.support.v7.widget.AppCompatTextView {

    public MirroredFontTextView(Context context) {
        super(context);
        init(context, null, 0);
    }
    public MirroredFontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }
    public MirroredFontTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }
    private void init(Context context, AttributeSet attrs, int defStyle) {
        // Load attributes
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TextViewPlusFont, 0, 0);
        try {
            String fontInAssets = ta.getString(R.styleable.TextViewPlusFont_customFont);
            setTypeface(Typefaces.get(context, fontInAssets));
        } finally {
            ta.recycle();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.translate(getWidth(), 0);
        canvas.scale(-1, 1);
        super.onDraw(canvas);
    }
}