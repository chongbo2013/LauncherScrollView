package xu.ferris.launcherscrollview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.Toast;

/**
 * 支持阻尼，上啦下啦刷新ScrollView
 * Created by ferris on 2016/3/4.
 */
public class LauncherScrollView extends ViewGroup {
    private Context mContext;


    private int desireWidth,desireHeight;

    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;

    protected final static int TOUCH_STATE_REST = 0;
    protected final static int TOUCH_STATE_SCROLLING = 1;

    protected int mTouchState;

    protected float mLastMotionX;
    protected float mLastMotionY;

    protected static final int INVALID_POINTER = -1;
    protected int mActivePointerId = INVALID_POINTER;

    private int mMaximumVelocity;

    ScrollView mScrollView;
    public static final int MIN_SNAP_VELOCITY = 300;

    public   int mTouchSlop ;




    public LauncherScrollView(Context context) {
        super(context);
        init(context);
    }

    public LauncherScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LauncherScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context mContext) {
        this.mContext=mContext;
        mScroller = new Scroller(getContext(), new ScrollInterpolator());

        mTouchState = TOUCH_STATE_REST;

        final ViewConfiguration configuration = ViewConfiguration.get(getContext());
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
        mTouchSlop = configuration.getScaledTouchSlop();
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

        final float y = ev.getY(pointerIndex);
        final int yDiff = (int) Math.abs(y - mLastMotionY);
        //如果大于最小移动阀值，就设置为滚动状态
        //并且更新上一次触摸位置
        if (yDiff > mTouchSlop) {
            mTouchState = TOUCH_STATE_SCROLLING;
            mLastMotionY = y;
        }
    }

    //重置触摸状态
    private void resetTouchState() {
        releaseVelocityTracker();
        mTouchState = TOUCH_STATE_REST;
        mActivePointerId = INVALID_POINTER;
    }


    @Override
    protected android.view.ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public android.view.ViewGroup.LayoutParams generateLayoutParams(
            AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected android.view.ViewGroup.LayoutParams generateLayoutParams(
            android.view.ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    public static class LayoutParams extends MarginLayoutParams {
        public int gravity = -1;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);

            TypedArray ta = c.obtainStyledAttributes(attrs,
                    R.styleable.SlideGroup);

            gravity = ta.getInt(R.styleable.SlideGroup_layout_gravity, -1);

            ta.recycle();
        }

        public LayoutParams(int width, int height) {
            this(width, height, -1);
        }

        public LayoutParams(int width, int height, int gravity) {
            super(width, height);
            this.gravity = gravity;
        }

        public LayoutParams(android.view.ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 计算所有child view 要占用的空间
        desireWidth = 0;
        desireHeight = 0;
        int count = getChildCount();
        for (int i = 0; i < count; ++i) {
            View v = getChildAt(i);
            if (v.getVisibility() != View.GONE) {
                LayoutParams lp = (LayoutParams) v.getLayoutParams();
                measureChildWithMargins(v, widthMeasureSpec, 0,
                        heightMeasureSpec, 0);

                //只是在这里增加了垂直或者水平方向的判断

                    desireWidth = Math.max(desireWidth, v.getMeasuredWidth()
                            + lp.leftMargin + lp.rightMargin);
                    desireHeight += v.getMeasuredHeight() + lp.topMargin
                            + lp.bottomMargin;

            }
        }
        // count with padding
        desireWidth += getPaddingLeft() + getPaddingRight();
        desireHeight += getPaddingTop() + getPaddingBottom();

        // see if the size is big enough
        desireWidth = Math.max(desireWidth, getSuggestedMinimumWidth());
        desireHeight = Math.max(desireHeight, getSuggestedMinimumHeight());

        setMeasuredDimension(resolveSize(desireWidth, widthMeasureSpec),
                resolveSize(desireHeight, heightMeasureSpec));
    }
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int parentLeft = getPaddingLeft();
        final int parentRight = r - l - getPaddingRight();
        final int parentTop = getPaddingTop();
        final int parentBottom = b - t - getPaddingBottom();
        int left = parentLeft;
        int top = parentTop;

        int count = getChildCount();
        for (int i = 0; i < count; ++i) {
            View v = getChildAt(i);
            if (v.getVisibility() != View.GONE) {
                LayoutParams lp = (LayoutParams) v.getLayoutParams();
                final int childWidth = v.getMeasuredWidth();
                final int childHeight = v.getMeasuredHeight();
                final int gravity = lp.gravity;
                final int horizontalGravity = gravity
                        & Gravity.HORIZONTAL_GRAVITY_MASK;
                final int verticalGravity = gravity
                        & Gravity.VERTICAL_GRAVITY_MASK;

                    // layout vertical, and only consider horizontal gravity
                    left = parentLeft;
                    top += lp.topMargin;
                    switch (horizontalGravity) {
                        case Gravity.LEFT:
                            break;
                        case Gravity.CENTER_HORIZONTAL:
                            left = parentLeft
                                    + (parentRight - parentLeft - childWidth) / 2
                                    + lp.leftMargin - lp.rightMargin;
                            break;
                        case Gravity.RIGHT:
                            left = parentRight - childWidth - lp.rightMargin;
                            break;
                    }
                    v.layout(left, top, left + childWidth, top + childHeight);
                    top += childHeight + lp.bottomMargin;
                }

        }
    }

    private static class ScrollInterpolator implements Interpolator {
        public ScrollInterpolator() {
        }

        public float getInterpolation(float t) {
            t -= 1.0f;
            return t * t * t * t * t + 1;
        }
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        }
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
    private final float DELTA_RATIO = 2; //数值越大, 头部空白下拉越慢. 值为1为完全跟手.
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

                    final float y = event.getY(pointerIndex);
                    final float deltaY = mLastMotionY - y;


                    int mScrollY = getScrollY();
                    int maxY = desireHeight - getHeight();
                    if (mScrollY > maxY||mScrollY < 0) {
                        // 超出了下边界，或者上边界，则增加滑动阻尼
                        scrollBy( 0,(int) deltaY/2);
                    }else{
                        scrollBy( 0,(int) deltaY);
                    }


                    mLastMotionY = y;

                } else {
                    determineScrollingStart(event);
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mTouchState == TOUCH_STATE_SCROLLING) {
                    mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                    int velocityY = (int) mVelocityTracker.getYVelocity(mActivePointerId);
                    completeMove(velocityY);
                }
                resetTouchState();
                break;
            case MotionEvent.ACTION_POINTER_UP:
                releaseVelocityTracker();
                break;
        }

        return true;
    }

    private void completeMove( float velocityY) {
        int mScrollY = getScrollY();
        int maxY = desireHeight - getHeight();

            if (mScrollY > maxY) {
                // 超出了下边界，弹回
                mScroller.startScroll(0, mScrollY, 0, maxY - mScrollY);
                invalidate();
            } else if (mScrollY < 0) {
                // 超出了上边界，弹回
                mScroller.startScroll(0, mScrollY, 0, -mScrollY);
                invalidate();
            } else if (Math.abs(velocityY) >= MIN_SNAP_VELOCITY && maxY > 0) {

                if (velocityY > 0 ) {
                //上滑，判断距离底部的距离
                    Toast.makeText(getContext(),"velocityY="+velocityY+"，上滑",Toast.LENGTH_SHORT).show();
                    int distance=desireHeight-mScrollY-getHeight();
                    mScroller.fling(0, mScrollY, 0, (int) -velocityY, 0, 0, 0, maxY);

                    invalidate();
                } else {
                    //下滑，判断距离顶部的距离
                    int distance=mScrollY;
                    mScroller.fling(0, mScrollY, 0, (int) -velocityY, 0, 0, 0, maxY);
                    Toast.makeText(getContext(),"velocityY="+velocityY+"，下滑",Toast.LENGTH_SHORT).show();
                }


            }

    }
}
