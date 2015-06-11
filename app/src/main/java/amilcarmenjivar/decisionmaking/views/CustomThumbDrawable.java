package amilcarmenjivar.decisionmaking.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;

/**
 * seekbar background with text on it.
 *
 * @author sazonov-adm
 */
public class CustomThumbDrawable extends Drawable {
    /**
     * paints.
     */
    private Paint circlePaint;
    private Paint circlePaintRing;
    private Context mContext;
    private float mRadius;
    private float mSmallRadius;

    private Rect suggestionBounds = null;

    public CustomThumbDrawable(Context context, int color) {
        mContext = context;
        mRadius = toPix(12);
        mSmallRadius = toPix(9);
        setColor(color);
    }

    public void setSuggestionBounds(Rect bounds) {
        if (bounds == null)  {
            suggestionBounds = null;
        } else {
            if(suggestionBounds == null)
                suggestionBounds = new Rect();
            suggestionBounds.left = bounds.left;
            suggestionBounds.top = bounds.top;
            suggestionBounds.right = bounds.right;
            suggestionBounds.bottom = bounds.bottom;
        }
    }

    public void setColor(int color) {
        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor((0xA0 << 24) + (color & 0x00FFFFFF));
        circlePaintRing = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaintRing.setStyle(Paint.Style.STROKE);
        circlePaintRing.setColor((0x80 << 24) + (color & 0x00FFFFFF));
        circlePaintRing.setStrokeWidth(toPix(3));
        invalidateSelf();
    }

    public float getRadius() {
        return mRadius;
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
        int height = this.getBounds().centerY();
        int width = this.getBounds().centerX();


        if (suggestionBounds == null) {
            canvas.drawCircle(width + mRadius, height, mRadius, circlePaint);
        } else {
            int width2 = suggestionBounds.centerX();

            if(width == width2) {
                // Actual mark
                canvas.drawCircle(width + mRadius, height, mRadius, circlePaint);
            } else {
                // Suggestion mark
                canvas.drawCircle(width2 + mRadius, height, mRadius, circlePaintRing);
                // Actual mark
                canvas.drawCircle(width + mRadius, height, mSmallRadius, circlePaint);
            }

        }
    }

    @Override
    public int getIntrinsicHeight() {
        return (int) (mRadius * 2);
    }

    @Override
    public int getIntrinsicWidth() {
        return (int) (mRadius * 2);
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

    private float toPix(int size) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size,
                mContext.getResources().getDisplayMetrics());
    }
}