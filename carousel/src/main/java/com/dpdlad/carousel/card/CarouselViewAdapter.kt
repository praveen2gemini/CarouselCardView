package com.dpdlad.carousel.card

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.dpdlad.carousel.R
import com.squareup.picasso.Picasso


class CarouselViewAdapter : RecyclerView.Adapter<CarouselViewAdapter.ViewHolder>() {
    private val bannerUrls = ArrayList<String>()
    private var onClickListener: CarouselClickListener? = null

    class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        private var compatImageView: AppCompatImageView? = null
        private var cardParentView: CardView? = null
        private var onClickListener: CarouselClickListener? = null
        private var itemPosition: Int = 0

        init {
            cardParentView = view.findViewById(R.id.cardParentViewId)
            compatImageView = view.findViewById(R.id.imageViewResId)
            compatImageView?.setOnClickListener(this)
        }

        fun bind(url: String) {
            Picasso.with(view.context).load(url)
                .placeholder(R.drawable.image_1).error(R.drawable.image_2)
                .fit()
                .centerInside()
                .into(compatImageView)
        }

        fun foundLastItem(position: Int, itemCount: Int) {
            this.itemPosition = position
            cardParentView?.loadDefinedMargin(position, itemCount)
        }

        fun setOnCarouselClickListener(onClickListener: CarouselClickListener?) {
            this.onClickListener = onClickListener
        }

        override fun onClick(view: View?) {
            this.onClickListener?.onCarouselItemClicked(itemPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.single_view_carousel, parent, false)
        return ViewHolder(view).apply { setOnCarouselClickListener(onClickListener) }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(bannerUrls[position])
        holder.foundLastItem(position, itemCount)
    }

    override fun getItemCount(): Int {
        return bannerUrls.size
    }

    fun setCarouselClickListener(onClickListener: CarouselClickListener?) {
        this.onClickListener = onClickListener
    }

    fun update(urls: ArrayList<String>) {
        this.bannerUrls.apply {
            clear()
            addAll(urls)
        }
    }

}

private fun getScreenWidth(): Int {
    return Resources.getSystem().displayMetrics.widthPixels
}

private fun CardView.loadDefinedMargin(position: Int, itemCount: Int) {
    val deviceWidth = getScreenWidth()
    val defaultCardMargin: Int =
        context.resources.getDimensionPixelSize(R.dimen.default_card_margin)
    val pixels: Int = context.resources.getDimensionPixelSize(R.dimen.default_margin_vertical)
    val customLayoutParams =
        FrameLayout.LayoutParams((deviceWidth - pixels), (deviceWidth / 2))
    val lastItemDetected = (position == itemCount - 1)
    val firstItemDetected = (position == 0)
    customLayoutParams.marginEnd = defaultCardMargin
    if (firstItemDetected) {
        customLayoutParams.marginStart = 0
    } else if (lastItemDetected) {
        customLayoutParams.marginStart = 0
        customLayoutParams.marginEnd = 0
    }
    layoutParams = customLayoutParams
}
