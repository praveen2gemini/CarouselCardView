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

class CarouselCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val DEFAULT_SLIDE_INTERVAL = 3500
    private var recyclerView: RecyclerView? = null
    private var onCarouselClickListener: CarouselClickListener? = null
    private var swipeTimer: Timer? = null
    private var swipeTask: SwiperTask? = null
    private val snapHelper = PagerSnapHelper()
    private val layoutManager: SnappingLinearLayoutManager
    private var totalBannerCount = 0

    init {
        orientation = HORIZONTAL
        inflate(context, R.layout.carousel_card_layout, this)
        layoutManager = SnappingLinearLayoutManager(context, LinearLayoutManager.HORIZONTAL)
        recyclerView = findViewById(R.id.recyclerViewResId)
        recyclerView?.apply {
            snapHelper.attachToRecyclerView(this)
            val layoutManager = SnappingLinearLayoutManager(context, LinearLayoutManager.HORIZONTAL)
//            val layoutManager = LinearLayoutManager(context)
//            layoutManager.orientation = LinearLayoutManager.HORIZONTAL
            this.layoutManager = layoutManager
        }
//        recyclerView?.postDelayed({ recyclerView?.smoothScrollToPosition(3) }, 2000L)
    }

    fun setOnCarouselClickListener(onClickListener: CarouselClickListener?) {
        this.onCarouselClickListener = onClickListener
    }


    fun loadBanners(urls: ArrayList<String>) {
        totalBannerCount = urls.size
        recyclerView?.adapter = CarouselViewAdapter().apply {
            setCarouselClickListener(onCarouselClickListener)
            update(urls)
            playCarousel()
        }
    }

    fun resetScrollTimer() {
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
     * Starts auto scrolling if
     */
    fun playCarousel() {
        resetScrollTimer()
        swipeTask?.setTotalBannerCount(totalBannerCount)
        swipeTimer?.schedule(
            swipeTask,
            DEFAULT_SLIDE_INTERVAL.toLong(),
            DEFAULT_SLIDE_INTERVAL.toLong()
        )
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        playCarousel()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        resetScrollTimer()
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
}