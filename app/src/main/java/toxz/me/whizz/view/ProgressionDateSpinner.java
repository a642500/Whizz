package toxz.me.whizz.view;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v4.view.ViewCompat;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.view.menu.ShowableListMenu;
import android.support.v7.widget.ForwardingListener;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.TintTypedArray;
import android.support.v7.widget.ViewUtils;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Locale;

import toxz.me.whizz.R;

/**
 * Created by Carlos on 11/8/16.
 */

@SuppressWarnings("RestrictedApi")
public class ProgressionDateSpinner extends ImageButton {
    static final boolean IS_AT_LEAST_M = Build.VERSION.SDK_INT >= 23;
    private static final int MAX_ITEMS_MEASURED = 15;
    final Rect mTempRect = new Rect();
    private Calendar mCalendar = null;
    private ProgressionAdapter mProgressionAdapter = new DefaultProgressionAdapter();
    private DropDownPopup mPopup;
    private ForwardingListener mForwardingListener;
    private Context mPopupContext;
    private int mDropDownWidth;

    public ProgressionDateSpinner(Context context) {
        super(context);
        init(context, null, 0, 0);
    }

    public ProgressionDateSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public ProgressionDateSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ProgressionDateSpinner(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    public ProgressionAdapter getProgressionAdapter() {
        return mProgressionAdapter;
    }

    public void setProgressionAdapter(@NonNull ProgressionAdapter progressionAdapter) {
        this.mProgressionAdapter = progressionAdapter;
    }

    @SuppressLint("PrivateResource")
    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TintTypedArray a = TintTypedArray.obtainStyledAttributes(context, attrs,
                android.support.v7.appcompat.R.styleable.Spinner, defStyleAttr, 0);


        @SuppressLint("PrivateResource")
        final int popupThemeResId = a.getResourceId(android.support.v7.appcompat.R.styleable.Spinner_popupTheme, 0);
        if (popupThemeResId != 0) {
            mPopupContext = new ContextThemeWrapper(context, popupThemeResId);
        } else {
            // If we're running on a < M device, we'll use the current context and still handle
            // any dropdown popup
            mPopupContext = context;
        }


        mPopup = new DropDownPopup(mPopupContext, attrs, defStyleAttr, defStyleRes);

        final TintTypedArray pa = TintTypedArray.obtainStyledAttributes(
                mPopupContext, attrs, R.styleable.Spinner, defStyleAttr, 0);


        mDropDownWidth = ViewGroup.LayoutParams.WRAP_CONTENT;// from AOSP
        mPopup.setBackgroundDrawable(
                getResources().getDrawable(R.drawable.abc_popup_background));
        pa.recycle();


        setImageResource(mProgressionAdapter.getDefaultRes());
        mForwardingListener = new ForwardingListener(this) {
            @Override
            public ShowableListMenu getPopup() {
                return mPopup;
            }

            @Override
            protected boolean onForwardingStarted() {
                if (!mPopup.isShowing()) {
                    mPopup.show();
                }
                return true;
            }

            @Override
            protected boolean onForwardingStopped() {
                return super.onForwardingStopped();
            }
        };
    }

    @Override
    public boolean performClick() {
        boolean handled = super.performClick();

        if (!handled) {
            handled = true;

            if (!mPopup.isShowing()) {
                mPopup.show();
            }
        }

        return handled;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mForwardingListener != null
                && mForwardingListener.onTouch(this, event) || super.onTouchEvent(event);
    }

    public ProgressionAdapter.Level getLevel() {
        return mProgressionAdapter.getLevel(getCalendar());
    }

    @Nullable
    public Calendar getCalendar() {
        return mCalendar;
    }

    public void setCalendar(Calendar calendar) {
        mCalendar = calendar;
        this.setImageResource(mProgressionAdapter.getDrawableResByLevel(getLevel()));
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        throw new UnsupportedOperationException("This View unable support setOnClickListener");
    }

    private int compatMeasureContentWidth(SpinnerAdapter adapter, Drawable background) {
        if (adapter == null) {
            return 0;
        }

        int width = 0;
        View itemView = null;
        int itemType = 0;
        final int widthMeasureSpec =
                MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MeasureSpec.UNSPECIFIED);
        final int heightMeasureSpec =
                MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MeasureSpec.UNSPECIFIED);

        // Make sure the number of items we'll measure is capped. If it's a huge data set
        // with wildly varying sizes, oh well.
        int position = getLevel().ordinal();

        int start = Math.max(0, position);
        final int end = Math.min(adapter.getCount(), start + MAX_ITEMS_MEASURED);
        final int count = end - start;
        start = Math.max(0, start - (MAX_ITEMS_MEASURED - count));
        for (int i = start; i < end; i++) {
            final int positionType = adapter.getItemViewType(i);
            if (positionType != itemType) {
                itemType = positionType;
                itemView = null;
            }
            itemView = adapter.getView(i, itemView, null);
            if (itemView.getLayoutParams() == null) {
                itemView.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
            }
            itemView.measure(widthMeasureSpec, heightMeasureSpec);
            width = Math.max(width, itemView.getMeasuredWidth());
        }

        // Add background padding to measured width
        if (background != null) {
            background.getPadding(mTempRect);
            width += mTempRect.left + mTempRect.right;
        }

        return width;
    }

    public interface ProgressionAdapter {
        @NonNull
        Level getLevel(@Nullable Calendar calendar);

        @DrawableRes
        int getDrawableResByLevel(Level level);

        int getDefaultRes();

        enum Level {
            HIGH, MEDIUM, LOW
        }
    }

    public interface OnSelectedListener {
        void onSelected(ProgressionAdapter.Level level, Calendar cl);

        void onCancel();
    }

    public void setOnSelectedListener(OnSelectedListener listener) {
        mSelectedListener = listener;
    }

    private OnSelectedListener mSelectedListener;

    private static class DefaultProgressionAdapter implements ProgressionAdapter {
        @NonNull
        @Override
        public Level getLevel(final Calendar calendar) {
            if (calendar == null)
                return Level.LOW;

            final Calendar today = Calendar.getInstance(Locale.getDefault());
            if (today.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)
                    && today.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)
                    && today.get(Calendar.DAY_OF_YEAR) == calendar.get(Calendar.DAY_OF_YEAR)) {
                return Level.HIGH;

            }

            if (DateUtils.isToday(calendar.getTimeInMillis())) {
                return Level.HIGH;
            }

            today.add(Calendar.DAY_OF_YEAR, 1);

            if (today.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)
                    && today.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)
                    && today.get(Calendar.DAY_OF_YEAR) == calendar.get(Calendar.DAY_OF_YEAR)) {
                return Level.MEDIUM;
            }

            return Level.LOW;
        }

        @Override
        public int getDrawableResByLevel(Level level) {
            switch (level) {
                case HIGH:
                    return R.drawable.bottom_bar_notice_red;
                case MEDIUM:
                    return R.drawable.bottom_bar_notice_blue;
                default:
                    return R.drawable.bottom_bar_notice_grey;
            }
        }

        @Override
        public int getDefaultRes() {
            return getDrawableResByLevel(Level.LOW);
        }
    }

    private class DropDownPopup extends ListPopupWindow {

        private Rect mVisibleRect = new Rect();
        private ListAdapter mAdapter;

        private DropDownPopup(@NonNull Context context,
                              @Nullable AttributeSet attrs,
                              @AttrRes int defStyleAttr,
                              @StyleRes int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);

            setAnchorView(ProgressionDateSpinner.this);
            setModal(true);
            setPromptPosition(POSITION_PROMPT_ABOVE);
            setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item,
                    new String[]{"今天", "明天", "选择日期..."}));
            setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (position == 0 || position == 1) {
                        final Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.DAY_OF_YEAR,
                                calendar.get(Calendar.DAY_OF_YEAR) + position);
                        if (!calendar.equals(getCalendar())) {
                            if (mSelectedListener != null) {
                                mSelectedListener.onSelected(getProgressionAdapter().getLevel(calendar), calendar);
                            }
                        }
                        setCalendar(calendar);
                    } else {
                        // TODO show dialog pick date

                        Toast.makeText(getContext(), "Pick date", Toast.LENGTH_SHORT).show();
                    }
                    dismiss();
                }
            });
        }

        @Override
        public void setAdapter(ListAdapter adapter) {
            super.setAdapter(adapter);
            mAdapter = adapter;
        }

        void computeContentWidth() {
            final Drawable background = getBackground();
            int hOffset = 0;
            if (background != null) {
                background.getPadding(mTempRect);
                hOffset = ViewUtils.isLayoutRtl(ProgressionDateSpinner.this) ? mTempRect.right
                        : -mTempRect.left;
            } else {
                mTempRect.left = mTempRect.right = 0;
            }

            final int spinnerPaddingLeft = ProgressionDateSpinner.this.getPaddingLeft();
            final int spinnerPaddingRight = ProgressionDateSpinner.this.getPaddingRight();
            final int spinnerWidth = ProgressionDateSpinner.this.getWidth();
            if (mDropDownWidth == WRAP_CONTENT) {
                int contentWidth = compatMeasureContentWidth(
                        (SpinnerAdapter) mAdapter, getBackground());
                final int contentWidthLimit = getContext().getResources()
                        .getDisplayMetrics().widthPixels - mTempRect.left - mTempRect.right;
                if (contentWidth > contentWidthLimit) {
                    contentWidth = contentWidthLimit;
                }
                setContentWidth(Math.max(
                        contentWidth, spinnerWidth - spinnerPaddingLeft - spinnerPaddingRight));
            } else if (mDropDownWidth == MATCH_PARENT) {
                setContentWidth(spinnerWidth - spinnerPaddingLeft - spinnerPaddingRight);
            } else {
                setContentWidth(mDropDownWidth);
            }
            if (ViewUtils.isLayoutRtl(ProgressionDateSpinner.this)) {
                hOffset += spinnerWidth - spinnerPaddingRight - getWidth();
            } else {
                hOffset += spinnerPaddingLeft;
            }
            setHorizontalOffset(hOffset);
        }

        public void show() {
            final boolean wasShowing = isShowing();

            computeContentWidth();

            setInputMethodMode(ListPopupWindow.INPUT_METHOD_NOT_NEEDED);
            super.show();
            final ListView listView = getListView();
            listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

            setSelection(getLevel().ordinal());

            if (wasShowing) {
                // Skip setting up the layout/dismiss listener below. If we were previously
                // showing it will still stick around.
                return;
            }

            // Make sure we hide if our anchor goes away.
            // TODO: This might be appropriate to push all the way down to PopupWindow,
            // but it may have other side effects to investigate first. (Text editing handles, etc.)
            final ViewTreeObserver vto = getViewTreeObserver();
            if (vto != null) {
                final ViewTreeObserver.OnGlobalLayoutListener layoutListener
                        = new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (!isVisibleToUser(ProgressionDateSpinner.this)) {
                            dismiss();
                        } else {
//                            computeContentWidth();

                            // Use super.show here to update; we don't want to move the selected
                            // position or adjust other things that would be reset otherwise.
                            ProgressionDateSpinner.DropDownPopup.super.show();
                        }
                    }
                };
                vto.addOnGlobalLayoutListener(layoutListener);
                setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        final ViewTreeObserver vto = getViewTreeObserver();
                        if (vto != null) {
                            vto.removeGlobalOnLayoutListener(layoutListener);
                        }
                    }
                });
            }
        }

        /**
         * Simplified version of the the hidden View.isVisibleToUser()
         */
        boolean isVisibleToUser(View view) {
            return ViewCompat.isAttachedToWindow(view) && view.getGlobalVisibleRect(mVisibleRect);
        }
    }
}
