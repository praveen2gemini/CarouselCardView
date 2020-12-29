package com.dpdlad.carouselcard

import com.dpdlad.carousel.card.BannerUrlInfo

data class BannerInfo(private val url: String) : BannerUrlInfo {
    override fun getBannerUrl(): String {
        return url
    }

}
