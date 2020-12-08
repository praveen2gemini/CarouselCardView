package com.dpdlad.carousel;

import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;

public class CarouselCardViewPagerScroller extends Scroller {


    private int mScrollDuration = 600;

    public CarouselCardViewPagerScroller(Context context) {
        super(context);
    }

    public CarouselCardViewPagerScroller(Context context, Interpolator interpolator) {
        super(context, interpolator);
    }

    public int getmScrollDuration() {
        return mScrollDuration;
    }

    public void setmScrollDuration(int mScrollDuration) {
        this.mScrollDuration = mScrollDuration;
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        super.startScroll(startX, startY, dx, dy, mScrollDuration);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy) {
        super.startScroll(startX, startY, dx, dy, mScrollDuration);
    }


}