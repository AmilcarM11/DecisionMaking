package amilcarmenjivar.decisionmaking.views;

import java.util.List;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;

import static amilcarmenjivar.decisionmaking.views.ComboSeekBar.Dot;

/**
 * seekbar background with text on it.
 *
 * @author sazonov-adm
 *
 */
public class StepBarDrawable extends Drawable {
    private final ComboSeekBar mySlider;
    private final Drawable myBase;
    private final Paint textUnselected;
    private float mThumbRadius;
    /**
     * paints.
     */
    private final Paint unselectLinePaint;
    private List<Dot> mDots;
    private Paint selectLinePaint;
    private Paint circleLinePaint;
    private float mDotRadius;
    private Paint textSelected;
    private float mTextMargin;
    private int mTextHeight;
    private boolean mIsMultiline; // Si el texto es de varias lineas.

    public StepBarDrawable(Drawable base, ComboSeekBar slider, float thumbRadius, List<Dot> dots, int color, int textSize, boolean isMultiline) {
        mIsMultiline = isMultiline;
        mySlider = slider;
        myBase = base;
        mDots = dots;
        textUnselected = new Paint(Paint.ANTI_ALIAS_FLAG);
        textUnselected.setColor(color);
        textUnselected.setAlpha(255);

        textSelected = new Paint(Paint.ANTI_ALIAS_FLAG);
        textSelected.setTypeface(Typeface.DEFAULT_BOLD);
        textSelected.setColor(Color.BLACK);
        textSelected.setAlpha(255);

        mThumbRadius = thumbRadius;

        unselectLinePaint = new Paint();
        unselectLinePaint.setColor(color);

        unselectLinePaint.setStrokeWidth(toPix(1));

        selectLinePaint = new Paint();
        selectLinePaint.setColor(color);
        selectLinePaint.setStrokeWidth(toPix(3));

        circleLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circleLinePaint.setColor(color);

        Rect textBounds = new Rect();
        textSelected.setTextSize((int) (textSize * 2));
        textSelected.getTextBounds("M", 0, 1, textBounds);

        textUnselected.setTextSize(textSize);
        textSelected.setTextSize(textSize);

        mTextHeight = textBounds.height();
        mDotRadius = toPix(3); // was 5
        mTextMargin = toPix(3);
    }

    private float toPix(int size) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size, mySlider.getContext().getResources().getDisplayMetrics());
    }

    @Override
    protected final void onBoundsChange(Rect bounds) {
        myBase.setBounds(bounds);
    }

    @Override
    protected final boolean onStateChange(int[] state) {
        invalidateSelf();
        return false;
    }

    @Override
    public final boolean isStateful() {
        return true;
    }
    @Override
    public final void draw(Canvas canvas) {
        // Log.d("--- draw:" + (getBounds().right - getBounds().left));
        int height = this.getIntrinsicHeight() / 2;
        if (mDots.size() == 0) {
            canvas.drawLine(0, height, getBounds().right, height, unselectLinePaint);
            return;
        }
        // First paint the dots and the line
        float base = 1.0f / 7;
        for (Dot dot : mDots) {
            int value = Math.abs(dot.value);
            float radius = mDotRadius * base * (value < 3 ? 3 : value);
            canvas.drawCircle(dot.mX, height, radius, circleLinePaint);
        }
        canvas.drawLine(mDots.get(0).mX, height, mDots.get(mDots.size() - 1).mX, height, unselectLinePaint);

        // Now paint the text.
        for (Dot dot : mDots) {
            drawText(canvas, dot, dot.mX, height);
        }
    }

    /**
     * @param canvas
     *            canvas.
     * @param dot
     *            current dot.
     * @param x
     *            x cor.
     * @param y
     *            y cor.
     */
    private void drawText(Canvas canvas, Dot dot, float x, float y) {
        final Rect textBounds = new Rect();
        textSelected.getTextBounds(dot.text, 0, dot.text.length(), textBounds);
        float xres;
        if (dot.id == (mDots.size() - 1)) {
            //xres = getBounds().width() - textBounds.width();
            xres = x - (textBounds.width() / 2);
        } else if (dot.id == 0) {
            //xres = 0;
            xres = x - (textBounds.width() / 2);
        } else {
            xres = x - (textBounds.width() / 2);
        }

        float yres = y + mTextHeight/2 - mTextMargin*2;

        if (dot.isSelected) {
            canvas.drawText(dot.text, xres, yres, textSelected);
        }
    }

    @Override
    public final int getIntrinsicHeight() {
        if (mIsMultiline) {
            return (int) (selectLinePaint.getStrokeWidth() + mDotRadius + (mTextHeight) * 2  + mTextMargin);
        } else {
            return (int) (mThumbRadius + mTextMargin + mTextHeight + mDotRadius);
        }
    }

    @Override
    public final int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public void setAlpha(int alpha) {
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
    }
}