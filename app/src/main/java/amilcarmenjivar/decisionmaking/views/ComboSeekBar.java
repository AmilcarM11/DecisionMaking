package amilcarmenjivar.decisionmaking.views;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SeekBar;

import amilcarmenjivar.decisionmaking.R;

public class ComboSeekBar extends SeekBar {
    private CustomThumbDrawable mThumb;
    private List<Dot> mDots = new ArrayList<Dot>();
    private OnItemClickListener mItemClickListener;
    private Dot prevSelected = null;
    private boolean isSelected = false;
    private int mColor;
    private int mTextSize;
    private boolean mIsMultiline;

    private int mIndex = 0;

    private int[] validNumbers = { 9, 8, 7, 6, 5, 4, 3, 2, 1, -2, -3, -4, -5, -6, -7, -8, -9 };

    /**
     * @param context
     *            context.
     */
    public ComboSeekBar(Context context) {
        super(context);
    }

    /**
     * @param context
     *            context.
     * @param attrs
     *            attrs.
     */
    public ComboSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ComboSeekBar);

        mColor = a.getColor(R.styleable.ComboSeekBar_myColor, R.color.accentColor);
        mTextSize = a.getDimensionPixelSize(R.styleable.ComboSeekBar_textSize, 18);
        mIsMultiline = a.getBoolean(R.styleable.ComboSeekBar_multiline, false);
        // do something with str

        a.recycle();
        mThumb = new CustomThumbDrawable(context, mColor);
        setThumb(mThumb);
        setProgressDrawable(new StepBarDrawable(this.getProgressDrawable(), this, mThumb.getRadius(), mDots, mColor, mTextSize, mIsMultiline));

        // default is not 0 and is a problem
        setPadding(0, 0, 0, 0);

        List<Integer> items = new ArrayList<Integer>();
        for(int v : validNumbers) {
            items.add(v);
        }
        setAdapter(items);
        setSelection(validNumbers.length / 2);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        isSelected = false;
        return super.onTouchEvent(event);
    }

    /**
     * @param color
     *            color.
     */
    public void setColor(int color) {
        mColor = color;
        mThumb.setColor(color);
        setProgressDrawable(new StepBarDrawable((StepBarDrawable) this.getProgressDrawable(), this, mThumb.getRadius(), mDots, color, mTextSize, mIsMultiline));
    }

    public boolean changeValue(int increment) {
        int position = getSelectedPosition();
        int newPosition = position + increment;
        if(newPosition >= 0 && newPosition < mDots.size()) {
            setSelection(newPosition);
            return true;
        }
        return false;
    }

    public void setInfo(int index, int value) {
        mIndex = index;
        for(int i=0; i<mDots.size(); i++) {
            if(mDots.get(i).value == value){
                setSelection(i);
            }
        }
    }

    public synchronized void setSelection(int position) {
        if ((position < 0) || (position >= mDots.size())) {
            throw new IllegalArgumentException("Position is out of bounds:" + position);
        }
        for (Dot dot : mDots) {
            if (dot.id == position) {
                dot.isSelected = true;
            } else {
                dot.isSelected = false;
            }
        }

        isSelected = true;
        invalidate();
    }

    public int getSelectedValue() {
        for(Dot d : mDots) {
            if(d.isSelected)
                return d.value;
        }
        return 0;
    }

    public int getSelectedPosition() {
        for(Dot d : mDots) {
            if(d.isSelected)
                return d.id;
        }
        return -1;
    }

    public void setAdapter(List<Integer> dots) {
        mDots.clear();
        int index = 0;
        for (int dotValue : dots) {
            Dot dot = new Dot();
            dot.value = dotValue;
            dot.text = Integer.toString(Math.abs(dotValue));
            dot.id = index++;
            mDots.add(dot);
        }
        initDotsCoordinates();
    }

    @Override
    public void setThumb(Drawable thumb) {
        if (thumb instanceof CustomThumbDrawable) {
            mThumb = (CustomThumbDrawable) thumb;
        }
        super.setThumb(thumb);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        if ((mThumb != null) && (mDots.size() > 1)) {
            if (isSelected) {
                for (Dot dot : mDots) {
                    if (dot.isSelected) {
                        Rect bounds = mThumb.copyBounds();
                        bounds.right = dot.mX;
                        bounds.left = dot.mX;
                        mThumb.setBounds(bounds);
                        break;
                    }
                }
            } else {
                int intervalWidth = mDots.get(1).mX - mDots.get(0).mX;
                Rect bounds = mThumb.copyBounds();
                // find nearest dot
                if ((mDots.get(mDots.size() - 1).mX - bounds.centerX()) < 0) {
                    bounds.right = mDots.get(mDots.size() - 1).mX;
                    bounds.left = mDots.get(mDots.size() - 1).mX;
                    mThumb.setBounds(bounds);

                    for (Dot dot : mDots) {
                        dot.isSelected = false;
                    }
                    mDots.get(mDots.size() - 1).isSelected = true;
                    handleClick(mDots.get(mDots.size() - 1));
                } else {
                    for (int i = 0; i < mDots.size(); i++) {
                        if (Math.abs(mDots.get(i).mX - bounds.centerX()) <= (intervalWidth / 2)) {
                            bounds.right = mDots.get(i).mX;
                            bounds.left = mDots.get(i).mX;
                            mThumb.setBounds(bounds);
                            mDots.get(i).isSelected = true;
                            handleClick(mDots.get(i));
                        } else {
                            mDots.get(i).isSelected = false;
                        }
                    }
                }
            }
        }
        super.onDraw(canvas);
    }

    private void handleClick(Dot selected) {
        if ((prevSelected == null) || (prevSelected.equals(selected) == false)) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(null, this, mIndex, selected.id);
            }
            prevSelected = selected;
        }
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        StepBarDrawable d = (StepBarDrawable) getProgressDrawable();

        int thumbHeight = mThumb == null ? 0 : mThumb.getIntrinsicHeight();
        int dw = 0;
        int dh = 0;
        if (d != null) {
            dw = d.getIntrinsicWidth();
            dh = Math.max(thumbHeight, d.getIntrinsicHeight());
        }

        dw += getPaddingLeft() + getPaddingRight();
        dh += getPaddingTop() + getPaddingBottom();

        setMeasuredDimension(resolveSize(dw, widthMeasureSpec), resolveSize(dh, heightMeasureSpec));
    }

    /**
     * dot coordinates.
     */
    private void initDotsCoordinates() {
        float intervalWidth = (getWidth() - (mThumb.getRadius() * 2)) / (mDots.size() - 1);
        for (Dot dot : mDots) {
            // TODO: I could fool around with the interval for a bigger impact.
            dot.mX = (int) (mThumb.getRadius() + intervalWidth * (dot.id));
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initDotsCoordinates();
    }

    /**
     * Sets a listener to receive events when a list item is clicked.
     *
     * @param clickListener
     *            Listener to register
     *
     * @see ListView#setOnItemClickListener(android.widget.AdapterView.OnItemClickListener)
     */
    public void setOnItemClickListener(AdapterView.OnItemClickListener clickListener) {
        mItemClickListener = clickListener;
    }

    public static class Dot {
        public int id;
        public int mX;
        public String text;
        public boolean isSelected = false;
        public int value;

        @Override
        public boolean equals(Object o) {
            return ((Dot) o).id == id;
        }
    }
}