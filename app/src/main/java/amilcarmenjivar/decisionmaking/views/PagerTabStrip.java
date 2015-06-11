package amilcarmenjivar.decisionmaking.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import amilcarmenjivar.decisionmaking.R;

/**
 * Implementation created to set the tab indicator color from the layout file.
 * Created by Amilcar Menjivar on 24/05/2015.
 */
public class PagerTabStrip extends android.support.v4.view.PagerTabStrip {

    private int mColor;
    private int mColorAlt;

    public PagerTabStrip(Context context) {
        super(context);
    }

    public PagerTabStrip(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PagerTabStrip);

        mColor = a.getColor(R.styleable.PagerTabStrip_tabColor, R.color.accentColor);
        mColorAlt = a.getColor(R.styleable.PagerTabStrip_tabColorAlt, R.color.inconsistentColor);
        boolean mFullUnderline = a.getBoolean(R.styleable.PagerTabStrip_drawFullUnderline, true);
        a.recycle();

        this.setTabIndicatorColor(mColor);
        this.setDrawFullUnderline(mFullUnderline);
    }

    public void setTabColorMain() {
        setTabIndicatorColor(mColor);
    }

    public void setTabColorAlt() {
        setTabIndicatorColor(mColorAlt);
    }
}
