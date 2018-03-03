package kutuphane.com.kutuphane;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.LinearInterpolator;
import android.webkit.WebView;

public class ObservableWebView extends WebView {
    private OnScrollChangedCallback mOnScrollChangedCallback;
    private float x1 = 0;
    private int pageCount;
    private int current_x;

    public ObservableWebView(final Context context) {
        super(context);
    }

    public ObservableWebView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public ObservableWebView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onScrollChanged(final int l, final int t, final int oldl, final int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mOnScrollChangedCallback != null) mOnScrollChangedCallback.onScroll(l, t);
    }

    public OnScrollChangedCallback getOnScrollChangedCallback() {
        return mOnScrollChangedCallback;
    }

    public void setOnScrollChangedCallback(final OnScrollChangedCallback onScrollChangedCallback) {
        mOnScrollChangedCallback = onScrollChangedCallback;
    }

    @Override
    protected int computeVerticalScrollOffset() {
        return super.computeVerticalScrollOffset();
    }

    @Override
    protected int computeVerticalScrollRange() {
        return super.computeVerticalScrollRange();
    }

    @Override
    protected int computeHorizontalScrollRange() {
        return super.computeHorizontalScrollRange();
    }

    public static interface OnScrollChangedCallback {
        public void onScroll(int l, int t);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                float x2 = event.getX();
                float deltaX = x2 - x1;
                if (Math.abs(deltaX) > 100) {
                    if (x2 > x1) {
                        turnPageLeft();
                    } else {
                        turnPageRight();
                    }
                    return true;
                }
                break;
        }
        return true;
    }

    private void turnPageLeft() {
        if (getCurrentPage() > 0) {
            int scrollX = getPrevPagePosition();
            loadAnimation(scrollX);
            current_x = scrollX;
            scrollTo(scrollX, 0);
        }
    }

    private void turnPageRight() {
        if (getCurrentPage() < pageCount - 1) {
            int scrollX = getNextPagePosition();
            loadAnimation(scrollX);
            current_x = scrollX;
            scrollTo(scrollX, 0);
        }
    }

    private void loadAnimation(int scrollX) {
        ObjectAnimator anim = ObjectAnimator.ofInt(this, "scrollX", current_x, scrollX);
        anim.setDuration(500);
        anim.setInterpolator(new LinearInterpolator());
        anim.start();
    }

    private int getNextPagePosition() {
        int nextPage = getCurrentPage() + 1;
        return (int) Math.ceil(nextPage * this.getMeasuredWidth());
    }

    private int getPrevPagePosition() {
        int prevPage = getCurrentPage() - 1;
        return (int) Math.ceil(prevPage * this.getMeasuredWidth());
    }

    public int getCurrentPage() {
        return (int) (Math.ceil((double) this.getScrollX() / this.getMeasuredWidth()));
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public void setScrollPosition(int sayfa) {
        if(sayfa < pageCount) {
            int scrollX = (int) Math.ceil(sayfa * this.getMeasuredWidth());
            current_x = scrollX;
            scrollTo(scrollX, 0);
        }
    }
}