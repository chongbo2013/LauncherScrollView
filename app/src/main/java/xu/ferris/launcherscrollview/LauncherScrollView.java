package xu.ferris.launcherscrollview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.Scroller;

/**
 * 支持阻尼，上啦下啦刷新ScrollView
 * Created by ferris on 2016/3/4.
 */
public class LauncherScrollView extends ViewGroup {
    private Context mContext;
    private int mWidth;
    private int mHeight;
    private int overScrollHight;
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

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        mWidth = widthSize;
        mHeight = heightSize;
        //overscroll的高度
        overScrollHight=mWidth/2;
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
                        startLeft + child.getMeasuredWidth(),
                        startTop + child.getMeasuredHeight());
               startTop += child.getMeasuredHeight();
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
}
