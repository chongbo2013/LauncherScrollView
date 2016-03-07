package xu.ferris.launcherscrollview.radar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;



import xu.ferris.launcherscrollview.OverScrollView;
import xu.ferris.launcherscrollview.R;


/**
 * Created by 雷达扫描 on 2015/12/17.
 */
public class RadarLayout extends FrameLayout implements ILoadingLayout {
    private ImageView radar_lay01, radar_lay02, radar_lay03, radar_lay04;
    protected final OverScrollView.Mode mMode;

    private  FrameLayout  mInnerLayout;
    private TextView mHeaderText;
    private CharSequence mPullLabel;

    private CharSequence mReleaseLabel;
    public RadarLayout(Context context, final OverScrollView.Mode mode, TypedArray attrs) {
        super(context);
        mMode = mode;

        LayoutInflater.from(context).inflate(R.layout.radar_layout, this);

        mInnerLayout= (FrameLayout) findViewById(R.id.radar_innerLayout);

        radar_lay01 = (ImageView) findViewById(R.id.radar_lay01);
        radar_lay02 = (ImageView) findViewById(R.id.radar_lay02);
        radar_lay03 = (ImageView) findViewById(R.id.radar_lay03);
        radar_lay04 = (ImageView) findViewById(R.id.radar_lay04);
        mHeaderText= (TextView) findViewById(R.id.tv_msg);
       LayoutParams lp = (LayoutParams) mInnerLayout.getLayoutParams();

        switch (mode) {
            case PULL_FROM_END:
               lp.gravity = Gravity.CENTER_HORIZONTAL ;

                // Load in labels
                mPullLabel = context.getString(R.string.pull_to_refresh_from_bottom_pull_label);
//                mRefreshingLabel = context.getString(R.string.pull_to_refresh_from_bottom_refreshing_label);
                mReleaseLabel = context.getString(R.string.pull_to_refresh_from_bottom_release_label);
                break;

            case PULL_FROM_START:
            default:
                lp.gravity = Gravity.CENTER_HORIZONTAL ;

                // Load in labels
                mPullLabel = context.getString(R.string.pull_to_refresh_pull_label);
//                mRefreshingLabel = context.getString(R.string.pull_to_refresh_refreshing_label);
                mReleaseLabel = context.getString(R.string.pull_to_refresh_release_label);
                break;
        }

        reset();
    }


    public final void reset() {
        if (null != mHeaderText) {
            mHeaderText.setText(mPullLabel);
            mHeaderText.setVisibility(View.VISIBLE);
            radar_lay01.clearAnimation();
            radar_lay02.clearAnimation();
            radar_lay03.clearAnimation();
            radar_lay04.clearAnimation();
            radar_lay01.setVisibility(View.INVISIBLE);
            radar_lay02.setVisibility(View.INVISIBLE);
            radar_lay03.setVisibility(View.INVISIBLE);
            radar_lay04.setVisibility(View.INVISIBLE);
        }



        if(radarAnimationSet!=null){
            radarAnimationSet.cancel();
            radarAnimationSet=null;
        }

    }

    private  AnimationSet radarAnimationSet ;
    /**
     * 执行动画
     */
    public void runAnimation(){
        //执行闪烁
        if(radarAnimationSet==null) {
            radarAnimationSet= new AnimationSet(false);


            AlphaAnimation alphaAnimation_lay01 = new AlphaAnimation(0.2f, 1.0f);
            alphaAnimation_lay01.setDuration(500);
            alphaAnimation_lay01.setRepeatCount(Animation.INFINITE);
            alphaAnimation_lay01.setRepeatMode(Animation.REVERSE);
            radar_lay01.setAnimation(alphaAnimation_lay01);
            radarAnimationSet.addAnimation(alphaAnimation_lay01);

            //执行雷达扫描
            RotateAnimation rotateradar_lay04 = new RotateAnimation(0.0f, 2140f, Animation.RELATIVE_TO_PARENT, 0.5f, Animation.RELATIVE_TO_PARENT, 1.0f);
            rotateradar_lay04.setInterpolator(new LinearInterpolator());
            rotateradar_lay04.setDuration(5000);
            rotateradar_lay04.setRepeatCount(Animation.INFINITE);
            rotateradar_lay04.setRepeatMode(Animation.RESTART);
            radar_lay02.setAnimation(rotateradar_lay04);
            radarAnimationSet.addAnimation(rotateradar_lay04);


            ScaleAnimation mScaleAnimation_radar_lay03 = new ScaleAnimation(0.0f, 0.8f, 0.0f, 0.8f, Animation.RELATIVE_TO_PARENT, 0.5f, Animation.RELATIVE_TO_PARENT, 1.0f);
            mScaleAnimation_radar_lay03.setInterpolator(new LinearInterpolator());
            mScaleAnimation_radar_lay03.setDuration(2000);
            mScaleAnimation_radar_lay03.setRepeatCount(Animation.INFINITE);
            mScaleAnimation_radar_lay03.setRepeatMode(Animation.RESTART);
            radar_lay03.setAnimation(mScaleAnimation_radar_lay03);
            radarAnimationSet.addAnimation(mScaleAnimation_radar_lay03);

        }
        radarAnimationSet.start();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override
    public void setLastUpdatedLabel(CharSequence label) {

    }


    @Override
    public void setLoadingDrawable(Drawable drawable) {

    }


    @Override
    public void setPullLabel(CharSequence pullLabel) {

    }


    @Override
    public void setRefreshingLabel(CharSequence refreshingLabel) {

    }

    @Override
    public void setReleaseLabel(CharSequence releaseLabel) {

    }


    @Override
    public void setTextTypeface(Typeface tf) {

    }

    //上啦的时候传递偏移量
    public final void onPull(float scaleOfLayout) {

    }
    //上啦准备去刷新
    public final void pullToRefresh() {
        radar_lay01.setVisibility(View.INVISIBLE);
        radar_lay02.setVisibility(View.INVISIBLE);
        radar_lay03.setVisibility(View.INVISIBLE);
        radar_lay04.setVisibility(View.INVISIBLE);
        mHeaderText.setVisibility(View.VISIBLE);
        if (null != mHeaderText) {
            mHeaderText.setText(mPullLabel);
        }
    }

    //刷新中，显示雷达
    public final void refreshing() {
        if (null != mHeaderText) {
            if(mHeaderText.getVisibility()!=View.INVISIBLE){
                mHeaderText.setVisibility(View.INVISIBLE);
            }
        }
        if(radar_lay01.getVisibility()!=View.VISIBLE){
            radar_lay01.setVisibility(View.VISIBLE);
        }
        if(radar_lay02.getVisibility()!=View.VISIBLE){
            radar_lay02.setVisibility(View.VISIBLE);
        }
        if(radar_lay02.getVisibility()!=View.VISIBLE){
            radar_lay02.setVisibility(View.VISIBLE);
        }
        if(radar_lay04.getVisibility()!=View.VISIBLE){
            radar_lay04.setVisibility(View.VISIBLE);
        }


        runAnimation();
    }

    //松手刷新
    public final void releaseToRefresh() {
        if (null != mHeaderText) {
            mHeaderText.setText(mReleaseLabel);
        }
    }

    public final void setHeight(int height) {
        ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) getLayoutParams();
        lp.height = height;
        requestLayout();
    }

    public final void setWidth(int width) {
        ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) getLayoutParams();
        lp.width = width;
        requestLayout();
    }

    public final int getContentSize() {
         return mInnerLayout!=null?mInnerLayout.getHeight():0;

    }

}
