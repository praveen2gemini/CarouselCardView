package com.dpdlad.carousel;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.Interpolator;

import androidx.viewpager.widget.ViewPager;

import java.lang.reflect.Field;

public class CarouselCardViewPager extends ViewPager {

    private CarouselImageClickListener carouselImageClickListener;
    private float oldX = 0, newX = 0, sens = 5;

    public void setCarouselImageClickListener(CarouselImageClickListener carouselImageClickListener) {
        this.carouselImageClickListener = carouselImageClickListener;
    }

    public CarouselCardViewPager(Context context) {
        super(context);
        postInitViewPager();
    }

    public CarouselCardViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        postInitViewPager();
    }

    private CarouselCardViewPagerScroller mScroller = null;

    /**
     * Override the Scroller instance with our own class so we can change the
     * duration
     */
    private void postInitViewPager() {
        try {
            Class<?> viewpager = ViewPager.class;
            Field scroller = viewpager.getDeclaredField("mScroller");
            scroller.setAccessible(true);
            Field interpolator = viewpager.getDeclaredField("sInterpolator");
            interpolator.setAccessible(true);

            mScroller = new CarouselCardViewPagerScroller(getContext(),
                    (Interpolator) interpolator.get(null));
            scroller.set(this, mScroller);
        } catch (Exception e) {
        }
    }

    /**
     * Set the factor by which the duration will change
     */
    public void setTransitionVelocity(int scrollFactor) {
        mScroller.setmScrollDuration(scrollFactor);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                oldX = ev.getX();
                break;

            case MotionEvent.ACTION_UP:
                newX = ev.getX();
                if (Math.abs(oldX - newX) < sens) {
                    if(carouselImageClickListener != null)
                        carouselImageClickListener.onCarouselImageSelected(getCurrentItem());
                    return true;
                }
                oldX = 0;
                newX = 0;
                break;
        }

        return super.onTouchEvent(ev);
    }

}