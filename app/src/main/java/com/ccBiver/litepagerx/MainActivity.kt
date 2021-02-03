package com.ccBiver.litepagerx

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        for(i in 1..10){
            litePager.addViews(
                PageViewGroup(this).apply {
                    this.showFragment(supportFragmentManager,TestFragment())
                }
            )
        }
    }
}