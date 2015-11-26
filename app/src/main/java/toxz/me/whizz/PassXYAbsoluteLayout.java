package toxz.me.whizz;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsoluteLayout;

/**
 * Created by carlos on 5/24/14.
 */
public class PassXYAbsoluteLayout extends AbsoluteLayout {
    public PassXYAbsoluteLayout(Context context) {
        super(context);
    }

    public PassXYAbsoluteLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PassXYAbsoluteLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (getChildCount() == 1) {
            getChildAt(0).setX(ev.getX());
            getChildAt(0).setY(ev.getY());
        }
        return super.onInterceptTouchEvent(ev);
    }
}
