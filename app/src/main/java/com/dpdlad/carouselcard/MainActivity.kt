package com.dpdlad.carouselcard

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dpdlad.carousel.CarouselCardImageUpdater
import com.dpdlad.carousel.SimpleCarouselView
import com.dpdlad.carousel.card.CarouselCardView
import com.dpdlad.carousel.card.CarouselClickListener
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity(),
    CarouselCardImageUpdater, CarouselClickListener {

    private var customSimpleCarouselView: SimpleCarouselView? = null
    private var carouselCardRecycler: CarouselCardView? = null
    var sampleImages = intArrayOf(
        R.drawable.image_1,
        R.drawable.image_2,
        R.drawable.image_3,
        R.drawable.image_4,
        R.drawable.image_5
    )
    private var sampleNetworkImageURLs = arrayOf(
        "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQIVUf6Aj04OpTeyenjIl9bjZ0nRQSGLOJ9FA&usqp=CAU",
        "https://lh3.googleusercontent.com/proxy/K_xxZeOmWfnAs0B8ZHIROSn0OK94VfCgSMpH97_n4f2VBUWeLrn8FifuqUArDTVvVvu3MePVQUfghPgNoKbiU0uo4by3ZjRe1xwTlVlZc3875Vt2AXALPMvjYFO4fljiYQ",
        "https://d1.awsstatic.com/GameDev%20Site%20Redesign/Lumberyard/Downloads/lumberyard_download_dyn-veg_page-header.2b03b5c24c726a878a53ffd6d42eca0265099b4e.jpg"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        customSimpleCarouselView = findViewById(R.id.customCarouselView)
        carouselCardRecycler = findViewById(R.id.carouselRecyclerView)
        customSimpleCarouselView?.apply {
            visibility = View.GONE
            pageCount = sampleNetworkImageURLs.size
            slideInterval = 3000
            setImageListener(this@MainActivity)
        }
        carouselCardRecycler?.setOnCarouselClickListener(this)
        carouselCardRecycler?.loadBanners(sampleNetworkImageURLs.toCollection(ArrayList()))
    }

    override fun setImageForPosition(imageView: ImageView?, position: Int) {
        Picasso.with(applicationContext).load(sampleNetworkImageURLs[position])
            .placeholder(sampleImages[0]).error(sampleImages[3]).fit().centerCrop()
            .into(imageView)
    }

    override fun onCarouselItemClicked(position: Int) {
        Toast.makeText(this, "onCarouselItemClicked $position", Toast.LENGTH_SHORT).show()
    }

}