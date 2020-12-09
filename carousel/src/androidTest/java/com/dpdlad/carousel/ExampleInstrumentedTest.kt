package com.dpdlad.carousel

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented carousel_card_layout, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under carousel_card_layout.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.dpdlad.carousel.carousel_card_layout", appContext.packageName)
    }
}