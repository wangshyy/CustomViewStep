package com.wsy.customviewstep

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

/**
 *  author : wsy
 *  date   : 2023/3/13
 *  desc   : 自定义步数进度条
 */
class StepProgressView(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) :
    View(context, attrs, defStyleAttr) {
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?) : this(context, null, 0)

    var startPercent: Int = 0 //开始百分比
    var endPercent: Int = 0 //结束百分比

    var arcWidth: Float = 1F   //弧线画笔宽度

    var arcPaintColor = Color.BLACK //弧线画笔颜色
    var arcPaintStyle = Paint.Style.STROKE //弧线画笔样式，stroke:填充
    var isAntiAlias = true  //是否抗锯齿

    private val centerPoint: Pair<Float, Float>
        get() = Pair((width / 2).toFloat(), height.toFloat() / 2)   //中心点

    var arcColorArrayList = intArrayOf()    //弧形渐变颜色列表,需传参
    private val arcColorStartPlaceList  //每个颜色值的起始位置,需传参
        get() = floatArrayOf(
            0F,
            ((endPercent * 0.01) / 2 / 2).toFloat(),
            (endPercent * 0.01 / 2).toFloat()
        )
    private val startAndEndCircleR: Float
        get() = arcWidth / 2  //首尾圆半径，为弧线宽度的一半，达到填充效果

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawStepProgress(canvas)
        invalidate()
    }

    private fun drawStepProgress(canvas: Canvas?) {

        //画弧线
        val arcPaint = Paint()
        arcPaint.color = arcPaintColor  //弧线画笔颜色
        arcPaint.style = arcPaintStyle  //弧线画笔样式
        arcPaint.strokeWidth = arcWidth    //画笔宽度
        arcPaint.isAntiAlias = isAntiAlias    //抗锯齿


        //渐变色,扫描渲染（梯度渲染）
        val sweepGradient = SweepGradient(
            centerPoint.first,
            centerPoint.second,   //（cx,cy）中心
            arcColorArrayList,
//            intArrayOf(Color.parseColor("#13CF8D"), Color.parseColor("#D6EE02")),//渐变颜色列表
            arcColorStartPlaceList
//            floatArrayOf(0F, .2F)   //每个颜色值的起始位置
        )
        //渐变矩阵旋转（渐变起点默认是0°，可以旋转至需要的起点角度）
        val matrix = Matrix()
        matrix.setRotate(
            -180F,
            centerPoint.first,
            centerPoint.second,
        ) //degrees:角度，（px,py）圆心
        sweepGradient.setLocalMatrix(matrix)
        arcPaint.shader = sweepGradient

        val rectFArc = RectF(
            arcWidth / 2,
            arcWidth / 2,
            width.toFloat() - arcWidth / 2,
            height.toFloat() - arcWidth / 2
        )    //画布矩形框大小
        canvas?.drawArc(rectFArc, 180F, 180F, false, arcPaint)    //画弧线

        //画0点圆
        val circlePaintStart = Paint()
        circlePaintStart.color = arcColorArrayList[0]
        circlePaintStart.isAntiAlias = true
        canvas?.drawCircle(
            arcWidth / 2,
            (height / 2).toFloat(),
            startAndEndCircleR,
            circlePaintStart
        )

//        //中心点
//        val centerP = Paint()
//        centerP.color = arcColorArrayList[0]
//        centerP.isAntiAlias = true
//        canvas?.drawCircle(
//            centerPoint.first,
//            centerPoint.second,
//            startAndEndCircleR,
//            circlePaintStart
//        )

        //画终点圆
        val circlePaintEnd = Paint()
        circlePaintEnd.color = arcColorArrayList.last()
        circlePaintEnd.isAntiAlias = true
        canvas?.drawCircle(
            width - arcWidth / 2,
            (height / 2).toFloat(),
            startAndEndCircleR,
            circlePaintEnd
        )
    }

}