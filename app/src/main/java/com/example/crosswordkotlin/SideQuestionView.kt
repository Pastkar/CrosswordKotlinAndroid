package com.example.crosswordkotlin

import android.content.Context
import android.content.res.Configuration
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

class SideQuestionView : View {
    //region private properties
    private var path = Path()

    //colors
    var bcgColor = 0
    var lineColor = 0

    //dimensions
    var itemHeight = 0f
    var itemWidth = 0f
    var lineWidth = 0f
    var paddingLine = 0f

    var borderWidth = 0f
    var lineStartX = 0f
    var clearSideBorderX = 0f
    var endItem = 0f
    var startItem = 0f

    //
    var cornerDimensions: FloatArray = floatArrayOf()

    //
    private var leftSide = false

    //paint
    private val itemPaint = Paint()
    private val linePaint = Paint()
    private val borderPaint = Paint()
    //endregion

    //endregion
    //region constructors
    constructor(context: Context?) : super(context) {}

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :  super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    //endregion

    //endregion
    //region override methods
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        path.addRoundRect(startItem, 0f, endItem, itemHeight, cornerDimensions, Path.Direction.CW)
        canvas.drawPath(path, itemPaint)
        canvas.drawPath(path, borderPaint)
        if(itemHeight > itemWidth * 2 + 10)
        canvas.drawRoundRect(
            lineStartX,
            itemWidth - lineWidth / 2,
            lineStartX + lineWidth,
            itemHeight - itemWidth + lineWidth / 2,
            lineWidth / 2,
            lineWidth / 2,linePaint
        )
        else{
            canvas.drawRoundRect(
                lineStartX,
                paddingLine,
                lineStartX + lineWidth,
                itemHeight - paddingLine ,
                lineWidth / 2,
                lineWidth / 2,linePaint
            )
        }
        canvas.drawRect(
            clearSideBorderX,
            borderWidth,
            clearSideBorderX + borderWidth,
            itemHeight - borderWidth,
            itemPaint
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val mode = MeasureSpec.getMode(heightMeasureSpec)
        val _containerSize = MeasureSpec.getSize(heightMeasureSpec)
        val modeWidth = MeasureSpec.getMode(widthMeasureSpec)
        itemWidth = MeasureSpec.getSize(widthMeasureSpec).toFloat()
        itemHeight = MeasureSpec.getSize(heightMeasureSpec).toFloat()
        orientation()
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        path = Path()
        invalidate()
    }
    //endregion

    //endregion
    //region private methods
    private fun orientation() {
        if (leftSide == true) {
            cornerDimensions = floatArrayOf(
                0f, 0f,  //top right
                itemWidth, itemWidth,  //bottom right
                itemWidth, itemWidth, 0f, 0f
            )
            lineStartX = itemWidth - paddingLine
            clearSideBorderX = 0f
            endItem = itemWidth - borderWidth / 2
            startItem = 0f
        } else {
            cornerDimensions = floatArrayOf( //top left
                itemWidth, itemWidth, 0f, 0f, 0f, 0f,  //bottom left
                itemWidth, itemWidth
            )
            lineStartX = paddingLine
            clearSideBorderX = itemWidth - borderWidth
            endItem = itemWidth
            startItem = borderWidth / 2
        }
    }

    private fun init(context: Context, typedArray: AttributeSet?) {
        val a = context.obtainStyledAttributes(typedArray, R.styleable.SideQuestionsView)
        bcgColor = a.getColor(R.styleable.SideQuestionsView_SQBcgColor, Color.BLACK)
        leftSide = a.getBoolean(R.styleable.SideQuestionsView_SQLeftSide, true)
        lineColor = a.getColor(R.styleable.SideQuestionsView_SQLineColor, Color.WHITE)
        lineWidth = a.getDimension(R.styleable.SideQuestionsView_SQLineWidth, 2f)
        paddingLine = a.getDimension(R.styleable.SideQuestionsView_SQPaddingLine, 5f)
        borderWidth = a.getDimension(R.styleable.SideQuestionsView_SQBorderWidth, 2f)
        //
        itemPaint.color = bcgColor
        itemPaint.style = Paint.Style.FILL
        linePaint.color = lineColor
        linePaint.style = Paint.Style.FILL
        borderPaint.color = lineColor
        borderPaint.style = Paint.Style.STROKE
        borderPaint.strokeWidth = borderWidth
        a.recycle()

    }
    //endregion

    //endregion
    //region public methods

    //endregion
}