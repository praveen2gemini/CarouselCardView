# CarouselCardView
A complete custom carousel view.(100% user customizable)

Initialize the CarouselCardView in your Activity or Fragment

```
private var carouselCardView: CarouselCardView? = null
```

```
carouselCardView = findViewById(R.id.carouselCard)
        carouselCardRecycler?.apply {
            setOnCarouselClickListener(this@MainActivity) // Detecting Carousel card click detection
            setAutoScroll(true) // Auto scroll enabled
            setSlideInterval(1000L)// Every 1 second
            loadBanners(getBannerInfo()) // Passing list of banner urls as input to CarouselCardView
        }
```

Add the following line on your app build.gradle

```
implementation 'com.github.praveen2gemini:CarouselCardView:0.0.3-SNAPSHOT@aar'
```
