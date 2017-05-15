package libraries.akshaypall.com.autoresizingtextview;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Akshay on 2017-05-14.
 */

public class AutoResizingTextView extends android.support.v7.widget.AppCompatTextView {
    private static final String TAG = "AutoResizingTextView";

    private boolean mIsAutoFitting;
    private boolean mListenerCalled ;
    private AutoResizingTextViewListener mListener;
    private float mMaxTextSize; // in PX
    private float mMinTextSize; // in SP
    private float mSizingIncrement;

    public AutoResizingTextView(Context context) {
        super(context);
        setup();
    }

    public AutoResizingTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    public AutoResizingTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup();
    }

    private void setup() {
        mMaxTextSize = getTextSize();
        mMinTextSize = 10.0f;
        mSizingIncrement = 2.0f;
        mIsAutoFitting = false;
        mListenerCalled = false;
    }

    public void setAutoFit (boolean toAutoFit){
        mIsAutoFitting = toAutoFit;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mIsAutoFitting && !mListenerCalled){
            reduceSizeToFit();
        }
    }

    /**
     * Update the max text size and reset view to size again
     * @param maxInPx
     */

    public void setMaxTextSize (float maxInPx){
        this.mMaxTextSize = maxInPx;
        resetViewForSizing();
    }

    /**
     * Update the min text size and reset view to size again
     * @param minTextSizeInSp
     */

    public void setMinTextSize(float minTextSizeInSp) {
        this.mMinTextSize = minTextSizeInSp;
        resetViewForSizing();
    }


    /**
     * Update increment by which resizing is done. Higher increments result in faster performance
     * but less precision. Note: this does NOT resize the view on call.
     * @param sizingIncrement
     */

    public void setSizingIncrement(float sizingIncrement) {
        this.mSizingIncrement = sizingIncrement;
    }

    /**
     * Required to attach a listener to allow for auto sizing to be tracked (it calls
     * onAutoSizeComplete()).
     * @param listener
     */

    public void setListener(AutoResizingTextViewListener listener) {
        this.mListener = listener;
    }


    /**
     * Resize the text size across a list of related views to the largest font that fits uniformly.
     * Call this method only after all the views in the list have correctly auto sized (i.e. after
     * onAutoSizeComplete() has been called for all the views) or if all the views have
     * mIsAutoFitting = false;
     * @param autoTextViews
     */

    public static void unifyTextSizeAcrossViews (List<AutoResizingTextView> autoTextViews,
                                                 float scaledDensityPxPerSp){
        List<Float> textSizes = new ArrayList<>();
        for (AutoResizingTextView view : autoTextViews){
            textSizes.add(view.getTextSize() / scaledDensityPxPerSp);
        }
        float min = Collections.min(textSizes);
        for (AutoResizingTextView view : autoTextViews){
            view.setTextSize(min);
        }
    }

    private void reduceSizeToFit() {
        Rect rect = new Rect();
        float textSize = this.getTextSize() / getResources().getDisplayMetrics().scaledDensity;
        int y1 = this.getLineBounds(0, rect);
        int y2 = this.getLineBounds(this.getLineCount() - 1, rect);
        if (y2 > this.getHeight() && textSize >= mMinTextSize){
            this.setTextSize(textSize - mSizingIncrement);
        } else if (y1 != 0 && y2 != 0 && mListener != null && !mListenerCalled) {
            mListenerCalled = true;
            mListener.onAutoSizeComplete();
        }
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        resetViewForSizing();
    }

    private void resetViewForSizing() {
        // Reset the listener called field to allow for onAutoSizeComplete to be called again as
        // changing values/orientation may allow for higher/lower text size, meaning auto sizing
        // should run again
        this.mListenerCalled = false;
        this.setTextSize(TypedValue.COMPLEX_UNIT_PX, mMaxTextSize);
    }

    public interface AutoResizingTextViewListener {
        void onAutoSizeComplete();
    }
}
