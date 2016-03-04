
package xu.ferris.launcherscrollview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.Scroller;

public class SwitchPagedView extends ViewGroup {

    private Context mContext;
    private int pageCount = 3;
    private int mWidth;
    private int mHeight;

    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;

    protected final static int TOUCH_STATE_REST = 0;
    protected final static int TOUCH_STATE_SCROLLING = 1;
    protected final static int TOUCH_STATE_PREV_PAGE = 2;
    protected final static int TOUCH_STATE_NEXT_PAGE = 3;
    protected int mTouchState;

    protected float mLastMotionX;
    protected float mLastMotionY;

    protected static final int INVALID_POINTER = -1;
    protected int mActivePointerId = INVALID_POINTER;

    private int mMaximumVelocity;

    protected static final int PAGE_SNAP_ANIMATION_DURATION = 750;

    public static final int MIN_SNAP_VELOCITY = 600;

    public static final int MIN_MOVE_SPACE = 12;

    protected int mCurrentPage;

    public SwitchPagedView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = context;
        init();
    }

    private static class ScrollInterpolator implements Interpolator {
        public ScrollInterpolator() {
        }

        public float getInterpolation(float t) {
            t -= 1.0f;
            return t * t * t * t * t + 1;
        }
    }

    private void init() {
        mScroller = new Scroller(getContext(), new ScrollInterpolator());

        mTouchState = TOUCH_STATE_REST;

        mCurrentPage = 0;

        final ViewConfiguration configuration = ViewConfiguration.get(getContext());
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();

        for (int i = 0; i < pageCount; i++) {
            PagedViewGridLayout layout = new PagedViewGridLayout(mContext);
            addView(layout, new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT));
        }
    }

    //添加按下速度检测
    private void acquireVelocityTrackerAndAddMovement(MotionEvent ev) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);
    }

    //释放速度检测
    private void releaseVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.clear();
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    //确定是否要滚动
    protected void determineScrollingStart(MotionEvent ev) {
        final int pointerIndex = ev.findPointerIndex(mActivePointerId);
        if (pointerIndex == -1)
            return;

        final float x = ev.getX(pointerIndex);
        final int xDiff = (int) Math.abs(x - mLastMotionX);
        //如果大于最小移动阀值，就设置为滚动状态
        //并且更新上一次触摸位置
        if (xDiff > MIN_MOVE_SPACE) {
            mTouchState = TOUCH_STATE_SCROLLING;
            mLastMotionX = x;
        }
    }

    //重置触摸状态
    private void resetTouchState() {
        releaseVelocityTracker();
        mTouchState = TOUCH_STATE_REST;
        mActivePointerId = INVALID_POINTER;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //添加按下速度检测
        acquireVelocityTrackerAndAddMovement(ev);

        //判断下，如果没有子类，不拦截事件
        if (getChildCount() <= 0)
            return super.onInterceptTouchEvent(ev);

        final int action = ev.getAction();
        //如果处于移动状态，并且是正在滚动，则直接拦截事件
        if ((action == MotionEvent.ACTION_MOVE) &&
                (mTouchState == TOUCH_STATE_SCROLLING)) {
            return true;
        }


        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_MOVE:
                //判断活动的手指是有效的，则进行判断其是否符合最小移动距离，才进行拦截
                if (mActivePointerId != INVALID_POINTER) {
                    determineScrollingStart(ev);
                }
                break;

            case MotionEvent.ACTION_DOWN: {

                mLastMotionX = ev.getX();
                mLastMotionY = ev.getY();
                //按下的时候，始终保持，只有一个手指按下
                mActivePointerId = ev.getPointerId(0);

                //如果滚动已经结束，就重置下触摸状态，停止动画
                if (mScroller.isFinished()) {
                    mTouchState = TOUCH_STATE_REST;
                    mScroller.abortAnimation();
                }

                break;
            }

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                //当手指抬起的时候，进行各种状态重置
                resetTouchState();
                break;

            case MotionEvent.ACTION_POINTER_UP:
                //其他手指如果抬起来，是是否速度检测器
                releaseVelocityTracker();
                break;
        }

        return mTouchState != TOUCH_STATE_REST;
    }

    protected void snapToPage(int whichPage, int duration) {

        mCurrentPage = whichPage;

        // awakenScrollBars(duration);

        int dx = mCurrentPage * mWidth - getScrollX();

        if (!mScroller.isFinished()) {
            mScroller.abortAnimation();
        }
        mScroller.startScroll(getScrollX(), 0, dx, 0, duration);

        invalidate();
    }

    protected void snapToPage(int whichPage) {
        snapToPage(whichPage, PAGE_SNAP_ANIMATION_DURATION);
    }

    protected void snapToDestination() {
        int nextPage = (getScrollX() + mWidth / 2) / mWidth;
        nextPage = Math.max(0, nextPage);
        nextPage = Math.min(getChildCount() - 1, nextPage);

        snapToPage(nextPage, PAGE_SNAP_ANIMATION_DURATION);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        if (getChildCount() <= 0)
            return super.onTouchEvent(event);

        acquireVelocityTrackerAndAddMovement(event);

        final int action = event.getAction();

        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:

                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }

                mLastMotionX = event.getX();
                mLastMotionY = event.getY();

                mActivePointerId = event.getPointerId(0);

                break;

            case MotionEvent.ACTION_MOVE:
                if (mTouchState == TOUCH_STATE_SCROLLING) {

                    final int pointerIndex = event.findPointerIndex(mActivePointerId);

                    if (pointerIndex == -1)
                        return true;

                    final float x = event.getX(pointerIndex);
                    final float deltaX = mLastMotionX - x;

                    scrollBy((int) deltaX, 0);

                    mLastMotionX = x;

                } else {
                    determineScrollingStart(event);
                }
                break;

            case MotionEvent.ACTION_UP:
                if (mTouchState == TOUCH_STATE_SCROLLING) {

                    mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                    int velocityX = (int) mVelocityTracker.getXVelocity(mActivePointerId);

                    if (velocityX > MIN_SNAP_VELOCITY && mCurrentPage > 0) {
                        snapToPage(mCurrentPage - 1);
                    }
                    else if (velocityX < -MIN_SNAP_VELOCITY && mCurrentPage < (getChildCount() - 1)) {
                        snapToPage(mCurrentPage + 1);
                    } else {
                        snapToDestination();
                    }

                }

                resetTouchState();
                break;

            case MotionEvent.ACTION_CANCEL:
                if (mTouchState == TOUCH_STATE_SCROLLING) {
                    snapToDestination();
                }
                resetTouchState();
                break;

            case MotionEvent.ACTION_POINTER_UP:
                releaseVelocityTracker();
                break;
        }

        return true;
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        mWidth = widthSize;
        mHeight = heightSize;

        setMeasuredDimension(widthSize, heightSize);

        Log.e("launcher123", "onMeasure widthSize = " + widthSize);
        Log.e("launcher123", "onMeasure heightSize = " + heightSize);

        int childCount = getChildCount();

        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);

            child.measure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int startLeft = 0;
        int startTop = 0;
        int childCount = getChildCount();

        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);

            if (child.getVisibility() != View.GONE)
                child.layout(startLeft, startTop,
                        startLeft + mWidth,
                        startTop + mHeight);

            startLeft += mWidth;
        }
    }
}
