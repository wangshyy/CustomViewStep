package com.wsy.customviewstep

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.View

/**
 *  author : wsy
 *  date   : 2023/3/13
 *  desc   : 自定义步数进度条
 */
class StepProgressView(context: Context?,val endPercent: Int?) : View(context) {
    private var percent: Int = 0
        set(value) {
            field = value
            invalidate()
        }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawStepProgress(canvas)
        invalidate()
    }

    private fun drawStepProgress(canvas: Canvas?){


        //画弧线
        val arcPaint = Paint()
        arcPaint.color = Color.BLACK   //画笔颜色
        arcPaint.style = Paint.Style.STROKE //填充
        arcPaint.strokeWidth = 40F //画笔宽度
        arcPaint.isAntiAlias = true    //抗锯齿
        val rectFArc = RectF(60F,60F,400F,400F)    //画布矩形框大小
        canvas?.drawArc(rectFArc,180F,180F,false,arcPaint)    //画弧线

        //画0点圆
        val circlePaint = Paint()
        circlePaint.color = Color.BLACK
        circlePaint.isAntiAlias = true
        canvas?.drawCircle(60F,230F,20F,circlePaint)
    }
}