package com.dpdlad.carousel;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RemoteViews.RemoteView;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.Timer;
import java.util.TimerTask;

@RemoteView
public class SimpleCarouselView extends FrameLayout {

    private final int DEFAULT_GRAVITY = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;

    private static final int DEFAULT_SLIDE_INTERVAL = 3500;
    private static final int DEFAULT_SLIDE_VELOCITY = 400;
    public static final int DEFAULT_INDICATOR_VISIBILITY = 0;


    private int mPageCount;
    private int slideInterval = DEFAULT_SLIDE_INTERVAL;
    private int mIndicatorGravity = DEFAULT_GRAVITY;
    private int indicatorMarginVertical;
    private int indicatorMarginHorizontal;
    private int pageTransformInterval = DEFAULT_SLIDE_VELOCITY;
    private int indicatorVisibility = DEFAULT_INDICATOR_VISIBILITY;

    private SimpleCarouselViewPager containerViewPager;
    private CircleCorePageIndicator mIndicator;
    private CarouselCardImageUpdater mCarouselCardImageUpdater = null;
    private CarouselImageClickListener carouselImageClickListener = null;

    private Timer swipeTimer;
    private SwipeTask swipeTask;

    private boolean autoPlay;
    private boolean disableAutoPlayOnUserInteraction;
    private boolean animateOnBoundary = true;

    private int previousState;

    private ViewPager.PageTransformer pageTransformer;

    public SimpleCarouselView(Context context) {
        super(context);
    }

    public SimpleCarouselView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs, 0, 0);
    }

    public SimpleCarouselView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SimpleCarouselView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context, attrs, defStyleAttr, defStyleRes);
    }

    private void initView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        if (isInEditMode()) {
            return;
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.view_carousel, this, true);
            containerViewPager = view.findViewById(R.id.containerViewPager);
            mIndicator = view.findViewById(R.id.indicator);

            containerViewPager.addOnPageChangeListener(carouselOnPageChangeListener);


            //Retrieve styles attributes
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SimpleCarouselView, defStyleAttr, 0);
            try {
                indicatorMarginVertical = a.getDimensionPixelSize(R.styleable.SimpleCarouselView_indicatorMarginVertical, getResources().getDimensionPixelSize(R.dimen.default_indicator_margin_vertical));
                indicatorMarginHorizontal = a.getDimensionPixelSize(R.styleable.SimpleCarouselView_indicatorMarginHorizontal, getResources().getDimensionPixelSize(R.dimen.default_indicator_margin_horizontal));
                setPageTransformInterval(a.getInt(R.styleable.SimpleCarouselView_pageTransformInterval, DEFAULT_SLIDE_VELOCITY));
                setSlideInterval(a.getInt(R.styleable.SimpleCarouselView_slideInterval, DEFAULT_SLIDE_INTERVAL));
                setOrientation(a.getInt(R.styleable.SimpleCarouselView_indicatorOrientation, LinearLayout.HORIZONTAL));
                setIndicatorGravity(a.getInt(R.styleable.SimpleCarouselView_indicatorGravity, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL));
                setAutoPlay(a.getBoolean(R.styleable.SimpleCarouselView_autoPlay, true));
                setDisableAutoPlayOnUserInteraction(a.getBoolean(R.styleable.SimpleCarouselView_disableAutoPlayOnUserInteraction, false));
                setAnimateOnBoundary(a.getBoolean(R.styleable.SimpleCarouselView_animateOnBoundary, true));
                setPageTransformer(a.getInt(R.styleable.SimpleCarouselView_pageTransformer, SimpleCarouselViewPagerTransformer.DEFAULT));

                indicatorVisibility = a.getInt(R.styleable.SimpleCarouselView_indicatorVisibility, SimpleCarouselView.DEFAULT_INDICATOR_VISIBILITY);

                setIndicatorVisibility(indicatorVisibility);

                if (indicatorVisibility == View.VISIBLE) {
                    int fillColor = a.getColor(R.styleable.SimpleCarouselView_fillColor, 0);
                    if (fillColor != 0) {
                        setFillColor(fillColor);
                    }
                    int pageColor = a.getColor(R.styleable.SimpleCarouselView_pageColor, 0);
                    if (pageColor != 0) {
                        setPageColor(pageColor);
                    }
                    float radius = a.getDimensionPixelSize(R.styleable.SimpleCarouselView_radius, 0);
                    if (radius != 0) {
                        setRadius(radius);
                    }
                    setSnap(a.getBoolean(R.styleable.SimpleCarouselView_snap, getResources().getBoolean(R.bool.default_circle_indicator_snap)));
                    int strokeColor = a.getColor(R.styleable.SimpleCarouselView_strokeColor, 0);
                    if (strokeColor != 0) {
                        setStrokeColor(strokeColor);
                    }
                    float strokeWidth = a.getDimensionPixelSize(R.styleable.SimpleCarouselView_strokeWidth, 0);
                    if (strokeWidth != 0) {
                        setStrokeWidth(strokeWidth);
                    }
                }
            } finally {
                a.recycle();
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        resetScrollTimer();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        playCarousel();
    }

    public int getSlideInterval() {
        return slideInterval;
    }

    /**
     * Set interval for one slide in milliseconds.
     *
     * @param slideInterval integer
     */
    public void setSlideInterval(int slideInterval) {
        this.slideInterval = slideInterval;

        if (null != containerViewPager) {
            playCarousel();
        }
    }

    /**
     * Set interval for one slide in milliseconds.
     *
     * @param slideInterval integer
     */
    public void reSetSlideInterval(int slideInterval) {
        setSlideInterval(slideInterval);

        if (null != containerViewPager) {
            playCarousel();
        }
    }

    /**
     * Sets speed at which page will slide from one to another in milliseconds
     *
     * @param pageTransformInterval integer
     */
    public void setPageTransformInterval(int pageTransformInterval) {
        if (pageTransformInterval > 0) {
            this.pageTransformInterval = pageTransformInterval;
        } else {
            this.pageTransformInterval = DEFAULT_SLIDE_VELOCITY;
        }

        containerViewPager.setTransitionVelocity(pageTransformInterval);
    }

    public ViewPager.PageTransformer getPageTransformer() {
        return pageTransformer;
    }

    /**
     * Sets page transition animation.
     *
     * @param pageTransformer Choose from zoom, flow, depth, slide_over .
     */
    public void setPageTransformer(ViewPager.PageTransformer pageTransformer) {
        this.pageTransformer = pageTransformer;
        containerViewPager.setPageTransformer(true, pageTransformer);
    }

    /**
     * Sets page transition animation.
     *
     * @param transformer Pass {@link SimpleCarouselViewPagerTransformer#FLOW}, {@link SimpleCarouselViewPagerTransformer#ZOOM}, {@link SimpleCarouselViewPagerTransformer#DEPTH} or {@link SimpleCarouselViewPagerTransformer#SLIDE_OVER}
     * @attr
     */
    public void setPageTransformer(@SimpleCarouselViewPagerTransformer.Transformer int transformer) {
        setPageTransformer(new SimpleCarouselViewPagerTransformer(transformer));

    }

    /**
     * Sets whether to animate transition from last position to first or not.
     *
     * @param animateOnBoundary .
     */
    public void setAnimateOnBoundary(boolean animateOnBoundary) {
        this.animateOnBoundary = animateOnBoundary;
    }

    public boolean isAutoPlay() {
        return autoPlay;
    }

    private void setAutoPlay(boolean autoPlay) {
        this.autoPlay = autoPlay;
    }

    public boolean isDisableAutoPlayOnUserInteraction() {
        return disableAutoPlayOnUserInteraction;
    }

    private void setDisableAutoPlayOnUserInteraction(boolean disableAutoPlayOnUserInteraction) {
        this.disableAutoPlayOnUserInteraction = disableAutoPlayOnUserInteraction;
    }

    private void setData() {
        CarouselPagerAdapter carouselPagerAdapter = new CarouselPagerAdapter(getContext());
        containerViewPager.setAdapter(carouselPagerAdapter);
        if (getPageCount() > 1) {
            mIndicator.setViewPager(containerViewPager);
            mIndicator.requestLayout();
            mIndicator.invalidate();
            containerViewPager.setOffscreenPageLimit(getPageCount());
            playCarousel();
        }
    }

    private void stopScrollTimer() {
        if (null != swipeTimer) {
            swipeTimer.cancel();
        }

        if (null != swipeTask) {
            swipeTask.cancel();
        }
    }


    private void resetScrollTimer() {
        stopScrollTimer();

        swipeTask = new SwipeTask();
        swipeTimer = new Timer();
    }


    /**
     * Starts auto scrolling if
     */
    public void playCarousel() {

        resetScrollTimer();

        if (autoPlay && slideInterval > 0 && containerViewPager.getAdapter() != null && containerViewPager.getAdapter().getCount() > 1) {

            swipeTimer.schedule(swipeTask, slideInterval, slideInterval);
        }
    }

    /**
     * Pause auto scrolling unless user interacts provided autoPlay is enabled.
     */
    public void pauseCarousel() {

        resetScrollTimer();
    }

    /**
     * Stops auto scrolling.
     */
    public void stopCarousel() {

        this.autoPlay = false;
    }


    private class CarouselPagerAdapter extends PagerAdapter {
        private Context mContext;

        public CarouselPagerAdapter(Context context) {
            mContext = context;
        }

        @Override
        public Object instantiateItem(ViewGroup collection, int position) {

            Object objectToReturn;

            //Either let user set image to ImageView
            if (mCarouselCardImageUpdater != null) {

                ImageView imageView = new ImageView(mContext);
                imageView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));  //setting image position
//                imageView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 250));  //setting image position
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                objectToReturn = imageView;
                mCarouselCardImageUpdater.setImageForPosition(imageView, position);
                collection.addView(imageView);
                //Or let user add his own ViewGroup
            } else {
                throw new RuntimeException("View must set " + CarouselCardImageUpdater.class.getSimpleName() + ".");
            }

            return objectToReturn;
        }

        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((View) view);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public int getCount() {
            return getPageCount();
        }
    }

    ViewPager.OnPageChangeListener carouselOnPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            //Programmatic scroll

        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

            //User initiated scroll

            if (previousState == ViewPager.SCROLL_STATE_DRAGGING
                    && state == ViewPager.SCROLL_STATE_SETTLING) {

                if (disableAutoPlayOnUserInteraction) {
                    pauseCarousel();
                } else {
                    playCarousel();
                }

            } else if (previousState == ViewPager.SCROLL_STATE_SETTLING
                    && state == ViewPager.SCROLL_STATE_IDLE) {
            }

            previousState = state;

        }
    };

    private class SwipeTask extends TimerTask {
        public void run() {
            containerViewPager.post(new Runnable() {
                public void run() {

                    int nextPage = (containerViewPager.getCurrentItem() + 1) % getPageCount();

                    containerViewPager.setCurrentItem(nextPage, 0 != nextPage || animateOnBoundary);
                }
            });
        }
    }

    public void setImageListener(CarouselCardImageUpdater mCarouselCardImageUpdater) {
        this.mCarouselCardImageUpdater = mCarouselCardImageUpdater;
    }

    public void setCarouselImageClickListener(CarouselImageClickListener carouselImageClickListener) {
        this.carouselImageClickListener = carouselImageClickListener;
        containerViewPager.setCarouselImageClickListener(carouselImageClickListener);
    }

    public int getPageCount() {
        return mPageCount;
    }

    public void setPageCount(int mPageCount) {
        this.mPageCount = mPageCount;

        setData();
    }

    public void addOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        containerViewPager.addOnPageChangeListener(listener);
    }

    public void clearOnPageChangeListeners() {
        containerViewPager.clearOnPageChangeListeners();
    }

    public void setCurrentItem(int item) {
        containerViewPager.setCurrentItem(item);
    }

    public void setCurrentItem(int item, boolean smoothScroll) {
        containerViewPager.setCurrentItem(item, smoothScroll);
    }

    public int getCurrentItem() {
        return containerViewPager.getCurrentItem();
    }

    public int getIndicatorMarginVertical() {
        return indicatorMarginVertical;
    }

    public void setIndicatorMarginVertical(int _indicatorMarginVertical) {
        indicatorMarginVertical = _indicatorMarginVertical;
        FrameLayout.LayoutParams params = (LayoutParams) getLayoutParams();
        params.topMargin = indicatorMarginVertical;
        params.bottomMargin = indicatorMarginVertical;
    }

    public int getIndicatorMarginHorizontal() {
        return indicatorMarginHorizontal;
    }

    public SimpleCarouselViewPager getContainerViewPager() {
        return containerViewPager;
    }

    public void setIndicatorMarginHorizontal(int _indicatorMarginHorizontal) {
        indicatorMarginHorizontal = _indicatorMarginHorizontal;
        FrameLayout.LayoutParams params = (LayoutParams) getLayoutParams();
        params.leftMargin = indicatorMarginHorizontal;
        params.rightMargin = indicatorMarginHorizontal;
    }

    public int getIndicatorGravity() {
        return mIndicatorGravity;
    }

    public void setIndicatorGravity(int gravity) {
        mIndicatorGravity = gravity;
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = mIndicatorGravity;
        params.setMargins(indicatorMarginHorizontal, indicatorMarginVertical, indicatorMarginHorizontal, indicatorMarginVertical);
        mIndicator.setLayoutParams(params);
    }

    public void setIndicatorVisibility(int visibility) {
        mIndicator.setVisibility(visibility);
    }

    public int getOrientation() {
        return mIndicator.getOrientation();
    }

    public int getFillColor() {
        return mIndicator.getFillColor();
    }

    public int getStrokeColor() {
        return mIndicator.getStrokeColor();
    }

    public void setSnap(boolean snap) {
        mIndicator.setSnap(snap);
    }

    public void setRadius(float radius) {
        mIndicator.setRadius(radius);
    }

    public float getStrokeWidth() {
        return mIndicator.getStrokeWidth();
    }

    @Override
    public void setBackground(Drawable background) {
        super.setBackground(background);
    }

    public Drawable getIndicatorBackground() {
        return mIndicator.getBackground();
    }

    public void setFillColor(int fillColor) {
        mIndicator.setFillColor(fillColor);
    }

    public int getPageColor() {
        return mIndicator.getPageColor();
    }

    public void setOrientation(int orientation) {
        mIndicator.setOrientation(orientation);
    }

    public boolean isSnap() {
        return mIndicator.isSnap();
    }

    public void setStrokeColor(int strokeColor) {
        mIndicator.setStrokeColor(strokeColor);
    }

    public float getRadius() {
        return mIndicator.getRadius();
    }

    public void setPageColor(int pageColor) {
        mIndicator.setPageColor(pageColor);
    }

    public void setStrokeWidth(float strokeWidth) {
        mIndicator.setStrokeWidth(strokeWidth);
        int padding = (int) strokeWidth;
        mIndicator.setPadding(padding, padding, padding, padding);
    }
}
