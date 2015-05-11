package amilcarmenjivar.decisionmaking.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Used in ComparisonFragment for changing the value on the seek bar
 * Created by Amilcar Menjivar on 08/05/2015.
 */
public class MyTextView extends TextView {

    private int mIndex = -1;

    private int mMovement = 0;

    public MyTextView(Context context) {
        super(context);
    }

    public MyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void configure(int index, int increment) {
        this.mIndex = index;
        this.mMovement = increment;
    }

    public int getIndex() {
        return mIndex;
    }

    public int getMovement() {
        return mMovement;
    }
}
