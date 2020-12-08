package com.dpdlad.carouselcard

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.dpdlad.carousel.CarouselView
import com.dpdlad.carousel.ImageListener
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity(), ImageListener {

    private var customCarouselView: CarouselView? = null
    var sampleImages = intArrayOf(
        R.drawable.image_1,
        R.drawable.image_2,
        R.drawable.image_3,
        R.drawable.image_4,
        R.drawable.image_5
    )
    private var sampleNetworkImageURLs = arrayOf(
        "https://lh3.googleusercontent.com/proxy/K_xxZeOmWfnAs0B8ZHIROSn0OK94VfCgSMpH97_n4f2VBUWeLrn8FifuqUArDTVvVvu3MePVQUfghPgNoKbiU0uo4by3ZjRe1xwTlVlZc3875Vt2AXALPMvjYFO4fljiYQ",
        "https://gateway-backbase.uxlztizplez.info:5444/api/contentservices/api/contentstream/contentRepository/mmp_release_banner.jpg",
        "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQIVUf6Aj04OpTeyenjIl9bjZ0nRQSGLOJ9FA&usqp=CAU",
        "https://d1.awsstatic.com/GameDev%20Site%20Redesign/Lumberyard/Downloads/lumberyard_download_dyn-veg_page-header.2b03b5c24c726a878a53ffd6d42eca0265099b4e.jpg"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        customCarouselView = findViewById(R.id.customCarouselView)
        customCarouselView?.apply {
            pageCount = sampleNetworkImageURLs.size
            slideInterval = 3000
            setImageListener(this@MainActivity)
        }
    }

    override fun setImageForPosition(position: Int, imageView: ImageView?) {
        Picasso.with(applicationContext).load(sampleNetworkImageURLs[position])
            .placeholder(sampleImages[0]).error(sampleImages[3]).fit().centerCrop()
            .into(imageView)
    }

}