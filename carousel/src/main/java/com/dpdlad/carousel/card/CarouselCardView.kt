package com.dpdlad.carousel.card

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.dpdlad.carousel.R
import java.util.*

/**
 *  @author Praveen Kumar Sugumaran
 */
class CarouselCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var recyclerView: RecyclerView? = null
    private var onCarouselClickListener: CarouselClickListener? = null
    private var swipeTimer: Timer? = null
    private var swipeTask: SwiperTask? = null
    private val snapHelper = PagerSnapHelper()
    private val layoutManager: SnappingLinearLayoutManager
    private var totalBannerCount = 0
    private val urls = ArrayList<BannerUrlInfo>()
    private var slideInterval: Long = DEFAULT_SLIDE_INTERVAL
    private var isAutoScroll: Boolean = DEFAULT_AUTO_SCROLL

    init {
        orientation = HORIZONTAL
        inflate(context, R.layout.carousel_card_layout, this)
        layoutManager = SnappingLinearLayoutManager(context, LinearLayoutManager.HORIZONTAL)
        recyclerView = findViewById(R.id.recyclerViewResId)
        recyclerView?.apply {
            snapHelper.attachToRecyclerView(this)
            val layoutManager = SnappingLinearLayoutManager(context, LinearLayoutManager.HORIZONTAL)
            this.layoutManager = layoutManager
        }
    }

    fun setOnCarouselClickListener(onClickListener: CarouselClickListener?) {
        this.onCarouselClickListener = onClickListener
    }

    fun setSlideInterval(slideInterval: Long) {
        this.slideInterval = slideInterval
    }

    fun setAutoScroll(isAutoScroll: Boolean) {
        this.isAutoScroll = isAutoScroll
    }

    fun loadBanners(urls: ArrayList<BannerUrlInfo>) {
        this.urls.apply {
            clear()
            addAll(urls)
        }
        totalBannerCount = urls.size
        recyclerView?.adapter = CarouselViewAdapter().apply {
            setCarouselClickListener(onCarouselClickListener)
            update(urls)
            playCarousel()
        }
    }

    private fun resetScrollTimer() {
        stopScrollTimer()
        swipeTimer = Timer()
        swipeTask = SwiperTask(this, recyclerView, snapHelper)

    }

    private fun stopScrollTimer() {
        if (null != swipeTimer) {
            swipeTimer!!.cancel()
        }
        if (null != swipeTask) {
            swipeTask!!.cancel()
        }
    }

    /**
     * Starts auto scrolling
     */
    private fun playCarousel() {
        resetScrollTimer()
        swipeTask?.setTotalBannerCount(totalBannerCount)
        if (isAutoScroll) {
            swipeTimer?.schedule(
                swipeTask,
                slideInterval,
                slideInterval
            )
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        playCarousel()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        resetScrollTimer()
    }

    fun getBannerUrls(): ArrayList<BannerUrlInfo> {
        return this.urls
    }

    class SwiperTask(
        private val view: View,
        private val recyclerView: RecyclerView?,
        private val snapHelper: PagerSnapHelper
    ) :
        TimerTask() {
        private var totalBannerCount: Int = 0
        fun setTotalBannerCount(totalBannerCount: Int) {
            this.totalBannerCount = totalBannerCount
        }

        override fun run() {
            view.post {
                val snapView =
                    snapHelper.findSnapView((recyclerView?.layoutManager as LinearLayoutManager))
                var snapPosition =
                    (recyclerView.layoutManager as LinearLayoutManager).getPosition(
                        snapView!!
                    )
                if (snapPosition == totalBannerCount - 1) {
                    snapPosition = 0
                } else {
                    snapPosition++
                }
                recyclerView.smoothScrollToPosition(snapPosition)
            }
        }
    }

    companion object {
        private const val DEFAULT_SLIDE_INTERVAL = 10000L
        private const val DEFAULT_AUTO_SCROLL = true
    }
}