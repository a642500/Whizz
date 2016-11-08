package android.widget;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import toxz.me.whizz.R;

/**
 * Created by Carlos on 11/7/16.
 */

public class ButtonSpinner extends Spinner {
    private static final int MODE_DROPDOWN = 1;

    public ButtonSpinner(Context context) {
        super(context);
        init(context);
    }

    public ButtonSpinner(Context context, int mode) {
        super(context, mode);
        init(context);
    }

    public ButtonSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ButtonSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public ButtonSpinner(Context context, AttributeSet attrs, int defStyleAttr, int mode) {
        super(context, attrs, defStyleAttr, mode);
        init(context);
    }

    public ButtonSpinner(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes, int mode) {
        super(context, attrs, defStyleAttr, defStyleRes, mode);
        init(context);
    }

    public ButtonSpinner(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes, int mode, Resources.Theme popupTheme) {
        super(context, attrs, defStyleAttr, defStyleRes, mode, popupTheme);
        init(context);
    }

    private void init(Context context) {
        mButtonView = (ImageButton) inflate(context, R.layout.layout_alarm_button, null);
        this.setBackground(null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);


        // super add the selected item view to layout, but we don't need it.
        removeAllViewsInLayout();

        int position = getSelectedItemPosition();
        //TODO change view color
        makeView(position);

        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.AT_MOST) {
            final int measuredWidth = getMeasuredWidth();
            setMeasuredDimension(
                    Math.min(Math.max(measuredWidth, measureButtonWidth()),
                            MeasureSpec.getSize(widthMeasureSpec)),
                    getMeasuredHeight());
        }

        mHeightMeasureSpec = heightMeasureSpec;
        mWidthMeasureSpec = widthMeasureSpec;

    }

    private int measureButtonWidth() {
        final int widthMeasureSpec =
                MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MeasureSpec.UNSPECIFIED);
        final int heightMeasureSpec =
                MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MeasureSpec.UNSPECIFIED);

        if (mButtonView.getLayoutParams() == null) {
            mButtonView.setLayoutParams(new LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT));
        }

        mButtonView.setClickable(false);

        mButtonView.measure(widthMeasureSpec, heightMeasureSpec);
        return mButtonView.getMeasuredWidth();
    }

    int mHeightMeasureSpec;
    int mWidthMeasureSpec;


    private ImageButton mButtonView;

    private void makeView(int position) {
        View child = mButtonView;

        switch (position) {
            case 0:
                mButtonView.setImageResource(R.drawable.bottom_bar_notice_red);
                break;
            case 1:
                mButtonView.setImageResource(R.drawable.bottom_bar_notice_blue);
                break;
            case 2:
                mButtonView.setImageResource(R.drawable.bottom_bar_notice_grey);
                break;
            default:
                mButtonView.setImageResource(R.drawable.bottom_bar_notice_grey);
                break;
        }

        ViewGroup.LayoutParams lp = child.getLayoutParams();
        if (lp == null) {
            lp = generateDefaultLayoutParams();
        }
        addViewInLayout(child, 0, lp);

        child.setSelected(hasFocus());
        child.setEnabled(isEnabled());

        // Get measure specs
        int childHeightSpec = ViewGroup.getChildMeasureSpec(mHeightMeasureSpec,
                getPaddingTop() + getPaddingBottom(), lp.height);
        int childWidthSpec = ViewGroup.getChildMeasureSpec(mWidthMeasureSpec,
                getPaddingLeft() + getPaddingRight(), lp.width);

        // Measure child
        child.measure(childWidthSpec, childHeightSpec);

        int childLeft;
        int childRight;

        // Position vertically based on gravity setting
        int childTop = getPaddingTop()
                + ((getMeasuredHeight() - getPaddingBottom() -
                getPaddingTop() - child.getMeasuredHeight()) / 2);
        int childBottom = childTop + child.getMeasuredHeight();

        int width = child.getMeasuredWidth();
        childLeft = 0;
        childRight = childLeft + width;

        child.layout(childLeft, childTop, childRight, childBottom);
    }
}
