package com.example.crosswordkotlin

import android.content.Context
import android.content.res.Configuration
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

class BcgQuestionView : View {
    //region private properties
    //final
    private var path = Path()

    //colors
     var bcgColor = 0
     var borderColor = 0

    //dimensions
     var itemHeight = 0f
     var itemWidth = 0f
     var borderWidth = 0f
     var clearSideBorderX = 0f
     var lineHeight = 0f
     var lineStartX = 0f
     var lineStartY = 0f
     var radiusBorder = 0f
        set(value) {
            field =  value
                cornerDimensions = if (leftSide == true) {
                    floatArrayOf(
                            0f, 0f,  //top right
                        radiusBorder, radiusBorder,  //bottom right
                        radiusBorder, radiusBorder,
                        0f, 0f
                    )
                } else {
                    floatArrayOf( //top left
                        radiusBorder, radiusBorder,
                        0f, 0f,
                        0f, 0f,  //bottom left
                        radiusBorder, radiusBorder
                    )
                }
        }
     var endItem = 0f
     var startItem = 0f
     var marginTop = 0f
     var marginBottom = 0f

    //
    private var cornerDimensions : FloatArray = floatArrayOf()

    //
     var leftSide = false

    //paint
    private val itemPaint = Paint()
    private val borderPaint = Paint()

    //endregion
    //region constructors
    constructor(context: Context?) : super(context) {}
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }
    //endregion
    //region private methods
    private fun init(context: Context, typedArray: AttributeSet?) {
        val a = context.obtainStyledAttributes(typedArray, R.styleable.BcgQuestionView)
        bcgColor = a.getColor(R.styleable.BcgQuestionView_BcgColor, Color.BLACK)
        leftSide = a.getBoolean(R.styleable.BcgQuestionView_LeftSide, true)
        borderWidth = a.getDimension(R.styleable.BcgQuestionView_BorderWidth, 2f)
        borderColor = a.getColor(R.styleable.BcgQuestionView_BorderColor, Color.WHITE)
        radiusBorder = a.getDimension(R.styleable.BcgQuestionView_RadiusBorder, 0f)
        lineHeight = a.getDimension(R.styleable.BcgQuestionView_ClearLineWidth, 0f)
        marginTop = a.getDimension(R.styleable.BcgQuestionView_CustomMarginTop, 0f)
        marginBottom = a.getDimension(R.styleable.BcgQuestionView_CustomMarginBottom, 0f)
        itemPaint.color = bcgColor
        itemPaint.style = Paint.Style.FILL
        borderPaint.color = borderColor
        borderPaint.style = Paint.Style.STROKE
        borderPaint.strokeWidth = borderWidth
        cornerDimensions = if (leftSide == true) {
            floatArrayOf(
                0f, 0f,  //top right
                radiusBorder, radiusBorder,  //bottom right
                radiusBorder, radiusBorder,
                0f, 0f
            )
        } else {
            floatArrayOf( //top left
                radiusBorder, radiusBorder,
                0f, 0f,
                0f, 0f,  //bottom left
                radiusBorder, radiusBorder
            )
        }
        a.recycle()
    }

    private fun orientation() {
        if (leftSide == true) {
            clearSideBorderX = 0f
            endItem = itemWidth - borderWidth / 2
            startItem = 0f
            lineStartX = itemWidth - borderWidth
        } else {
            clearSideBorderX = itemWidth - borderWidth
            endItem = itemWidth
            startItem = borderWidth / 2
            lineStartX = 0f
        }
    }

    //endregion
    //region override methods
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val mode = MeasureSpec.getMode(heightMeasureSpec)
        val _containerSize = MeasureSpec.getSize(heightMeasureSpec)
        val modeWidth = MeasureSpec.getMode(widthMeasureSpec)
        itemWidth = MeasureSpec.getSize(widthMeasureSpec).toFloat()
        itemHeight = MeasureSpec.getSize(heightMeasureSpec).toFloat()
        orientation()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        path.addRoundRect(
            startItem,
            borderWidth / 2 + marginTop,
            endItem,
            itemHeight - borderWidth / 2 - marginBottom,
            cornerDimensions,
            Path.Direction.CW
        )
        canvas.drawPath(path, itemPaint)
        canvas.drawPath(path, borderPaint)
        canvas.drawRect(
            clearSideBorderX,
            borderWidth + marginTop,
            clearSideBorderX + borderWidth,
            itemHeight - borderWidth - marginBottom,
            itemPaint
        )

        canvas.drawRect(
            lineStartX,
            lineStartY,
            lineStartX + borderWidth,
            lineStartY + lineHeight,
            itemPaint
        )
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        path = Path()
        invalidate()
    }

    //endregion
    //region public methods


    fun setMargin(left: Float, top: Float, right: Float, bottom: Float) {
        marginTop = top
        marginBottom = bottom
    }

    //endregion
}