package com.wsy.customviewstep

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import java.lang.Math.*
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

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

    var arcPaintWidth: Float = 1F   //弧线画笔宽度

    var arcPaintColor = Color.BLACK //弧线画笔颜色
    var arcPaintStyle = Paint.Style.STROKE //弧线画笔样式，stroke:填充
    var isAntiAlias = true  //是否抗锯齿

    private
    val centerPoint: Pair<Float, Float>
        get() = Pair((width / 2).toFloat(), height.toFloat() / 2)   //中心点

    /**
     * 公式计算，有误差
     */
//    private val concentricCircleCenterP
//        get() = Pair(
//            (width - arcPaintWidth) * endPercent / 100 + arcPaintWidth / 2,
//            width/2-sqrt((width - arcPaintWidth) * (width - arcPaintWidth) / 4 - ((width - arcPaintWidth) / 2 - (width - arcPaintWidth) * endPercent / 100) * ((width - arcPaintWidth) / 2 - (width - arcPaintWidth) * endPercent / 100))
//        ) //跟随进度的同心圆的圆心

    private val concentricCircleCenterP: Pair<Double, Double>
        get() = Pair(
            (when (endPercent) {
                in 0..90 -> {
                    centerPoint.first - (width - arcPaintWidth) / 2 * cos(toRadians((180 * endPercent / 100).toDouble()))
                }
                else -> {
                    centerPoint.first + (width - arcPaintWidth) / 2 * cos(toRadians((180 * endPercent / 100).toDouble()))
                }
            }),
            centerPoint.second - (width - arcPaintWidth) / 2 * sin(toRadians((180 * endPercent / 100).toDouble()))
        )


    var arcColorArrayList = intArrayOf()    //弧形渐变颜色列表,需传参
    var arcColorArrayListShadow = intArrayOf()    //阴影弧形渐变颜色列表,需传参

    private val arcColorStartPlaceList  //每个颜色值的起始位置,需传endPercent
        get() = floatArrayOf(
            0F, (endPercent * 0.01 / 2).toFloat(), (endPercent * 0.01 / 2).toFloat()
        )
    private val startAndEndCircleR: Float
        get() = arcPaintWidth / 2  //首尾圆半径，为弧线宽度的一半，达到填充效果

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawStepProgress(canvas)
        invalidate()
    }

    private fun drawStepProgress(canvas: Canvas?) {

        canvas?.translate(0F, arcPaintWidth / 2)
        canvas?.save()//把当前的画布的状态进行保存，然后放入Canvas状态栈中
        //画弧线
        val arcPaint = Paint()
        arcPaint.color = arcPaintColor  //弧线画笔颜色
        arcPaint.style = arcPaintStyle  //弧线画笔样式
        arcPaint.strokeWidth = arcPaintWidth    //画笔宽度
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
            arcPaintWidth / 2,
            arcPaintWidth / 2,
            width.toFloat() - arcPaintWidth / 2,
            height.toFloat() - arcPaintWidth / 2
        )    //画布矩形框大小
        canvas?.drawArc(rectFArc, 180F, 180F, false, arcPaint)    //画弧线

        //画0点扇形
        val rectFFanStart = RectF(
            0F,
            (height / 2).toFloat() - 1F,
            arcPaintWidth,
            (height / 2).toFloat() + arcPaintWidth - 1F,
        )
        val fanPaintStart = Paint()
        fanPaintStart.color = arcColorArrayList[0]
        fanPaintStart.isAntiAlias = true
        resetCanvas(canvas)
        canvas?.translate(0F, -arcPaintWidth / 2 - 1F)
        canvas?.drawArc(
            rectFFanStart,
            0F,
            180F,
            false,
            fanPaintStart
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

        //画终点扇形
        val rectFFanEnd = RectF(
            width - arcPaintWidth,
            (height / 2).toFloat() - 1F,
            width.toFloat(),
            (height / 2).toFloat() + arcPaintWidth - 1F,
        )
        val fanPaintEnd = Paint()
        fanPaintEnd.color = arcColorArrayList.last()
        fanPaintEnd.isAntiAlias = true
        canvas?.drawArc(
            rectFFanEnd,
            0F,
            180F,
            false,
            fanPaintEnd
        )

        //画阴影弧线
        val arcPaintShadow = Paint()
        arcPaintShadow.style = arcPaintStyle  //弧线画笔样式
        arcPaintShadow.strokeWidth = arcPaintWidth    //画笔宽度
        arcPaintShadow.isAntiAlias = isAntiAlias    //抗锯齿
        //渐变色,扫描渲染（梯度渲染）
        val sweepGradientShadow = SweepGradient(
            centerPoint.first,
            centerPoint.second,   //（cx,cy）中心
            arcColorArrayListShadow,
            floatArrayOf(.2F, .5F, .8F)
        )
        //渐变矩阵旋转（渐变起点默认是0°，可以旋转至需要的起点角度）
        val matrixShadow = Matrix()
        matrixShadow.setRotate(
            90F,
            centerPoint.first,
            centerPoint.second,
        ) //degrees:角度，（px,py）圆心
        sweepGradientShadow.setLocalMatrix(matrixShadow)
        arcPaintShadow.shader = sweepGradientShadow
        resetCanvas(canvas)
        canvas?.translate(arcPaintWidth, arcPaintWidth)
        canvas?.scale(
            (centerPoint.first - arcPaintWidth) / centerPoint.first,
            (centerPoint.second - arcPaintWidth) / centerPoint.second
        )
        canvas?.drawArc(rectFArc, 180F, 180F, false, arcPaintShadow)

        //跟随进度的外圆
        val progressCirclePaintOut = Paint()
        progressCirclePaintOut.color = Color.WHITE
        progressCirclePaintOut.isAntiAlias = true
        resetCanvas(canvas)
        canvas?.drawCircle(
            concentricCircleCenterP.first.toFloat(),
            concentricCircleCenterP.second.toFloat(),
            arcPaintWidth,
            progressCirclePaintOut
        )
        //跟随进度的内圆
        val progressCirclePaintIn = Paint()
        progressCirclePaintIn.color = arcColorArrayList[0]
        progressCirclePaintIn.isAntiAlias = true
        canvas?.drawCircle(
            concentricCircleCenterP.first.toFloat(),
            concentricCircleCenterP.second.toFloat(),
            startAndEndCircleR,
            progressCirclePaintIn
        )
    }

    //重置画笔
    private fun resetCanvas(canvas: Canvas?) {
        canvas?.restore()//重置画笔，获取上一次保存的画笔状态
        canvas?.save()//把当前的画布的状态进行保存，然后放入Canvas状态栈中
    }

}