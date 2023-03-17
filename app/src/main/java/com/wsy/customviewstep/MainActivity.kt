package com.wsy.customviewstep

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var stepProgressView = findViewById<StepProgressView>(R.id.view)
        stepProgressView.apply {
            arcPaintWidth = 60F
            endPercent = 50
            arcColorArrayList = intArrayOf(
                Color.parseColor("#0063FF"),
                Color.parseColor("#30F9FE"),
                Color.parseColor("#F3F5F8")
            )
            arcColorArrayListShadow = intArrayOf(
                Color.parseColor("#ffffff"),
                Color.parseColor("#F3F5F8"),
                Color.parseColor("#ffffff")
            )
        }
    }
}