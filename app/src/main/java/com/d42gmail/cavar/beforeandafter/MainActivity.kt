package com.d42gmail.cavar.beforeandafter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val imageLeftUrl =
            "https://www.publicdomainpictures.net/pictures/10000/velka/947-1262213425CFHP.jpg"
        val imageRightUrl =
            "https://www.publicdomainpictures.net/pictures/80000/velka/sunset-from-sandpatch.jpg"
        beforeAndAfterView.loadImagesByUrl(imageLeftUrl, imageRightUrl)
        beforeAndAfterView.setRoundCorners(true)
    }
}
