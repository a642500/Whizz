package toxz.me.whizz.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.CallSuper;
import android.support.annotation.ColorInt;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by Carlos on 11/25/16.
 */

public class RectAnimationLinearLayout extends LinearLayout {

    public RectAnimationLinearLayout(Context context) {
        super(context);
        init();
    }

    public RectAnimationLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RectAnimationLinearLayout(Context context, @Nullable AttributeSet attrs, int
            defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RectAnimationLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int
            defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private RectDrawable mDrawable;

    private void init() {
        mDrawable = new RectDrawable(1000, 500, getResources().getColor(android.R.color
                .darker_gray));
        mDrawable.setCallback(new Drawable.Callback() {
            @Override
            public void invalidateDrawable(@NonNull Drawable who) {
                invalidate(who.getBounds());
            }

            @Override
            public void scheduleDrawable(@NonNull Drawable who, @NonNull Runnable what, long when) {

            }

            @Override
            public void unscheduleDrawable(@NonNull Drawable who, @NonNull Runnable what) {

            }
        });
        setWillNotDraw(false);

        //        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        //            setForeground(mDrawable);
        //        } else {
        //            //TODO see low api source to find how to implement
        //        }
    }


    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        /*
         *
         * View draw in this order:
         *      1. Draw the background
         *      2. If necessary, save the canvas' layers to prepare for fading
         *      3. Draw view's content
         *      4. Draw children, Overlay is part of the content and draws beneath Foreground
         *      5. If necessary, draw the fading edges and restore layers
         *      6. Draw decorations (scrollbars for instance)
         *
         * Just call mDrawable.draw(canvas) here, the drawable will be covered by children.
         *
         * Set ViewOverlay( > android 4.3), the ProgressionDateSpinner still covers the drawable.
         *
         * Use setForeground(mDrawable) call solve this but it requires api 23.
         *
         * Override public void draw(Canvas canvas) is look like ViewOverlay ?!!!
         */
    }

    @Override @CallSuper public void draw(Canvas canvas) {
        super.draw(canvas);
        mDrawable.setBounds(getLeft(), getTop(), getRight(), getBottom());
        mDrawable.draw(canvas);
    }

    private static class RectDrawable extends Drawable {

        private long drawTime = 0L;
        private long mDuration = 1000L;
        private long mDisappearDelay = 500L;
        private Paint mPaint;
        private boolean start = false;

        public RectDrawable(long mDuration, long disappearDelay, @ColorInt int color) {
            this.mDuration = mDuration;
            this.mDisappearDelay = disappearDelay;
            this.mPaint = new Paint();
            mPaint.setStrokeWidth(20);
            mPaint.setColor(color);
        }


        public void play() {
            start = true;
            invalidateSelf();
        }

        @Override
        public void draw(@NonNull Canvas canvas) {
            if (!start) {
                return;
            }

            long now = System.currentTimeMillis();
            if (drawTime == 0) {
                drawTime = now;
            }
            long delta = now - drawTime;
            float percentage = ((float) delta) / mDuration;
            float over = ((float) mDisappearDelay) / mDuration + 1;


            int width = getBounds().width();
            int height = getBounds().height();

            canvas.save();
            canvas.clipRect(getBounds());

            canvas.drawLine(0, 4, 0, height - 4, mPaint);

            int drawWidth = (int) (width * Math.min(1, percentage));
            canvas.drawLine(0, 4, drawWidth, 4, mPaint);
            canvas.drawLine(0, height - 4, drawWidth, height - 4, mPaint);

            if (percentage > 1) {
                canvas.drawLine(width, 4, width, height - 4, mPaint);
            }

            canvas.restore();

            if (percentage >= over) {
                start = false;
                drawTime = 0;
            }
            invalidateSelf();
        }

        @Override
        public void setAlpha(@IntRange(from = 0, to = 255) int alpha) {
            mPaint.setAlpha(alpha);
        }

        @Override
        public void setColorFilter(@Nullable ColorFilter colorFilter) {
            mPaint.setColorFilter(colorFilter);
        }

        @Override
        public int getOpacity() {
            return PixelFormat.UNKNOWN;
        }
    }

    public void play() {
        mDrawable.play();
    }
}
