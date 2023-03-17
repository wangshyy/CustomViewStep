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
            arcWidth = 60F
            arcColorArrayList = intArrayOf(
                Color.parseColor("#0063FF"),
                Color.parseColor("#30F9FE"),
                Color.parseColor("#F3F5F8")
            )
            endPercent = 50
        }
    }
}