package xu.ferris.launcherscrollview.radar;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import xu.ferris.launcherscrollview.R;
/**
 *
 * Created by ferris on 2016/3/8.
 */
public class BlackLayout extends FrameLayout implements ILoadingLayout {

    public BlackLayout(Context context) {
        super(context);
    }

    public BlackLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BlackLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }





    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }


    /**
     * Set the Last Updated Text. This displayed under the main label when
     * Pulling
     *
     * @param label - Label to set
     */
    @Override
    public void setLastUpdatedLabel(CharSequence label) {

    }

    /**
     * Set the drawable used in the loading layout. This is the same as calling
     * <code>setLoadingDrawable(drawable, Mode.BOTH)</code>
     *
     * @param drawable - Drawable to display
     */
    @Override
    public void setLoadingDrawable(Drawable drawable) {

    }

    /**
     * Set Text to show when the Widget is being Pulled
     * <code>setPullLabel(releaseLabel, Mode.BOTH)</code>
     *
     * @param pullLabel - CharSequence to display
     */
    @Override
    public void setPullLabel(CharSequence pullLabel) {

    }

    /**
     * Set Text to show when the Widget is refreshing
     * <code>setRefreshingLabel(releaseLabel, Mode.BOTH)</code>
     *
     * @param refreshingLabel - CharSequence to display
     */
    @Override
    public void setRefreshingLabel(CharSequence refreshingLabel) {

    }

    /**
     * Set Text to show when the Widget is being pulled, and will refresh when
     * released. This is the same as calling
     * <code>setReleaseLabel(releaseLabel, Mode.BOTH)</code>
     *
     * @param releaseLabel - CharSequence to display
     */
    @Override
    public void setReleaseLabel(CharSequence releaseLabel) {

    }

    /**
     * Set's the Sets the typeface and style in which the text should be
     * displayed. Please see
     * {@link TextView#setTypeface(Typeface)
     * TextView#setTypeface(Typeface)}.
     *
     * @param tf
     */
    @Override
    public void setTextTypeface(Typeface tf) {

    }
}
