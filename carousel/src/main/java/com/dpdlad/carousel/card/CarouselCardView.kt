package com.dpdlad.carousel.card

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.dpdlad.carousel.R

class CarouselCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    private var recyclerView: RecyclerView? = null
    private var onCarouselClickListener: CarouselClickListener? = null

    init {
        orientation = HORIZONTAL
        inflate(context, R.layout.carousel_card_layout, this)
        recyclerView = findViewById(R.id.recyclerViewResId)
        recyclerView?.apply {
            val snapHelper = PagerSnapHelper()
            snapHelper.attachToRecyclerView(this)
            val layoutManager = LinearLayoutManager(context)
            layoutManager.orientation = LinearLayoutManager.HORIZONTAL
            this.layoutManager = layoutManager
        }
    }

    fun setOnCarouselClickListener(onClickListener: CarouselClickListener?) {
        this.onCarouselClickListener = onClickListener
    }


    fun loadBanners(urls: ArrayList<String>) {
        recyclerView?.adapter = CarouselViewAdapter().apply {
            setCarouselClickListener(onCarouselClickListener)
            update(urls)
        }
    }
}