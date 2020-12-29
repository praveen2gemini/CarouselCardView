package com.dpdlad.carouselcard

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dpdlad.carousel.card.BannerUrlInfo
import com.dpdlad.carousel.card.CarouselCardView
import com.dpdlad.carousel.card.CarouselClickListener

class MainActivity : AppCompatActivity(),
    CarouselClickListener {

    private var carouselCardRecycler: CarouselCardView? = null

    private var sampleNetworkImageURLs = arrayOf(
        "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQIVUf6Aj04OpTeyenjIl9bjZ0nRQSGLOJ9FA&usqp=CAU",
        "https://lh3.googleusercontent.com/proxy/K_xxZeOmWfnAs0B8ZHIROSn0OK94VfCgSMpH97_n4f2VBUWeLrn8FifuqUArDTVvVvu3MePVQUfghPgNoKbiU0uo4by3ZjRe1xwTlVlZc3875Vt2AXALPMvjYFO4fljiYQ",
        "https://d1.awsstatic.com/GameDev%20Site%20Redesign/Lumberyard/Downloads/lumberyard_download_dyn-veg_page-header.2b03b5c24c726a878a53ffd6d42eca0265099b4e.jpg"
    )

    private fun getBannerInfo(): ArrayList<BannerUrlInfo> {
        val banners = ArrayList<BannerUrlInfo>()
        sampleNetworkImageURLs.forEach {
            banners.add(BannerInfo(it))
        }
        return banners
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        carouselCardRecycler = findViewById(R.id.carouselRecyclerView)
        carouselCardRecycler?.apply {
            setOnCarouselClickListener(this@MainActivity)
            setAutoScroll(false)
            setSlideInterval(1000L)
            loadBanners(getBannerInfo())
        }
    }

    override fun onCarouselItemClicked(position: Int) {
        Toast.makeText(this, "onCarouselItemClicked $position", Toast.LENGTH_SHORT).show()
    }

}