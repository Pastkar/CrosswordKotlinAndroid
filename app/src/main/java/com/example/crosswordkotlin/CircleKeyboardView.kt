package com.example.crosswordkotlin

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.res.ResourcesCompat

class CircleKeyboardView : View, AnimatorUpdateListener {
    //line colors
    private val LINE_COLOR = Color.parseColor("#e6e8d4")
    private val TEXT_COLOR = Color.BLACK
    private val minAnimateValue = 0
    private var maxAnimateValue = 1

    //circle param
    private var centerCircleX = 0
    private var centerCircleY = 0
    private var radiusCircle = 0f

    //start first cell animate at 0 sec
    //next at secondDelay
    //next at thirdDelay
    private var secondDelay = 0
    private var thirdDelay = 0

    //time animation all items in both bay
    private var animateEndTimeInBothWay = 0

    //additional components
    private var animateDiff = 1
    private var fontDiff = 1f
    private var minTextSize = 0
    private var animationInProgress = false

    //region constructors
    constructor(context: Context?) : super(context) {}
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs)
    }

    //endregion
    //region public methods
    fun setWord(value: List<KeyboardInputModel>) {
        if (mAnimator != null) {
            mAnimator!!.cancel()
        }
        //
        if (_itemsList == null) _itemsList = ArrayList() else _itemsList!!.clear()
        //
        if (_lines == null) _lines = ArrayList() else _lines!!.clear()
        //
        _tempOpenedString = ""
        for (i in value.indices) {
            _itemsList!!.add(Cell(value[i].value, value[i].isSolved, TextPaint(titlePaint)))
        }
        _itemCount = _itemsList!!.size
        _angle = 360 / _itemCount
        if (_centerCoordinate != 0) setParamForButtons()
        animateEndTimeInBothWay = animateTime + secondDelay * ((_itemCount shl 1) - 1)
        invalidate()
    }

    fun setOnResultListener(listener: IKeybordCircleResult?) {
        _keyboardResult = listener
    }

    fun startAnimate() {
        if (mAnimator == null) {
            // sets the range of our value
            mAnimator = ValueAnimator()
            // registers our AnimatorUpdateListener
            mAnimator!!.addUpdateListener(this)
            mAnimator!!.interpolator = LinearInterpolator()
            mAnimator!!.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    animationInProgress = false
                    _keyboardResult?.AnimateEnded()
                }
            })
        }
        mAnimator!!.duration = animateEndTimeInBothWay.toLong()
        mAnimator!!.setIntValues(0, animateEndTimeInBothWay)
        mAnimator!!.start()
        animationInProgress = true
    }

    fun openLetter(letter: Char): Boolean {
        if (_itemsList == null || _itemsList!!.size == 0) return false
        var wordSolved = true
        var invalidate = false
        var prevOpened: Cell? = null
        var newOpened: Cell? = null
        for (rec in _itemsList!!) {
            if (letter == rec.title && !rec.openedByHint && newOpened == null) newOpened =
                rec else if (!rec.openedByHint) wordSolved = false
            if (rec.openedByHint && !rec.selected && prevOpened == null) prevOpened = rec
        }
        if (newOpened != null) {
            newOpened.openedByHint = true
            invalidate = true
        }
        if (prevOpened != null) {
            _tempOpenedString += prevOpened.title
            prevOpened.selected = true
            prevOpened.locked = true
        }
        if (newOpened != null && prevOpened != null) {
            val halfItemSize = newOpened.width shr 1
            val prevX = prevOpened.x + halfItemSize
            val prevY = prevOpened.y + halfItemSize
            val newX = newOpened.x + halfItemSize
            val newY = newOpened.y + halfItemSize
            _lines!!.add(Line(prevX, prevY, newX, newY, true))
        }
        if (invalidate) invalidate()
        return wordSolved
    }

    fun cleanLine() {
        if (_lines != null) _lines!!.clear()
    }

    //endregion
    //region overrides
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (_itemsList == null || _itemsList!!.size == 0 || _itemCount == 0 || _angle == 0 || _itemSize == 0) return
        //add circle
        canvas.drawCircle(
            centerCircleX.toFloat(),
            centerCircleY.toFloat(),
            radiusCircle,
            circlePaint
        )
        //add left padding for centering content
        canvas.translate(_contentPaddding.toFloat(), 0f)
        val currentAngel = 0
        _texts!!.clear()
        for (i in 0 until _itemCount) {
            //rotate for drawing next item
            val currentCell = _itemsList!![i]
            //turn relatively center
            //extraPadding padding using for animating view
            currentCell.width -= currentCell.extraPadding
            currentCell.height -= currentCell.extraPadding
            var offset: Float
            //animating text - makes it bigger or smaller depends value
            if (currentCell.extraPadding != 0) {
                var extraPadding = currentCell.extraPadding
                if (currentCell.extraPadding > minTextSize) extraPadding = minTextSize
                val fontSize = _textSize - extraPadding * fontDiff
                currentCell.paint.textSize = fontSize
                offset = initOffsets(currentCell.paint)
            } else {
                offset = _textOffset
            }

            //animate item
            if (currentCell.width < currentCell.minSize) currentCell.minSize = currentCell.width
            val r = currentCell.width + currentCell.x
            val b = currentCell.height + currentCell.y
            currentCell.x += currentCell.extraPadding
            currentCell.y += currentCell.extraPadding
            rect.left = (currentCell.x + cornerWidth / 2).toInt()
            rect.top = (currentCell.y + cornerWidth / 2).toInt()
            rect.right = (r - cornerWidth / 2).toInt()
            rect.bottom = (b - cornerWidth / 2).toInt()
            buttonBcgPaint.shader = currentCell.linearGradient
            canvas.drawRoundRect(
                rect.left.toFloat(),
                rect.top.toFloat(),
                rect.right.toFloat(),
                rect.bottom.toFloat(),
                cornerRadius,
                cornerRadius,
                buttonBcgPaint
            )
            if (currentCell.isSelected() || currentCell.openedByHint) {
                canvas.drawRoundRect(
                    rect.left.toFloat(),
                    rect.top.toFloat(),
                    rect.right.toFloat(),
                    rect.bottom.toFloat(),
                    cornerRadius,
                    cornerRadius,
                    buttonPressedBorderPaint
                )
            } else if (currentCell.isSolved) {
                canvas.drawRoundRect(
                    rect.left.toFloat(),
                    rect.top.toFloat(),
                    rect.right.toFloat(),
                    rect.bottom.toFloat(),
                    cornerRadius,
                    cornerRadius,
                    buttonNotPressedBorderPaint
                )
            } else {
                canvas.drawRoundRect(
                    rect.left.toFloat(),
                    rect.top.toFloat(),
                    rect.right.toFloat(),
                    rect.bottom.toFloat(),
                    cornerRadius,
                    cornerRadius,
                    buttonNotPressedBorderPaint
                )
            }
            //text should be over line
            _texts.add(
                TempText(
                    currentCell.title.toString(), rect.centerX(),
                    (rect.centerY() + offset).toInt(), currentCell.paint
                )
            )
        }
        if (_lines != null) {
            for (line in _lines!!) canvas.drawLine(
                line.startX.toFloat(),
                line.startY.toFloat(),
                line.endX.toFloat(),
                line.endY.toFloat(),
                linePaint
            )
        }
        canvas.drawLine(
            _tempLineStartX.toFloat(),
            _tempLineStartY.toFloat(),
            _tempLineEndX.toFloat(),
            _tempLineEndY.toFloat(),
            linePaint
        )
        if (_texts != null && _texts.size > 0) {
            for (text in _texts) canvas.drawText(
                text.title,
                text.x.toFloat(),
                text.y.toFloat(),
                text.paint
            )
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (animationInProgress || _itemSize == 0) return true
        var x = event.x.toInt()
        val y = event.y.toInt()

        //sub extra space from left
        x -= _contentPaddding
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val item = containsCurrentCoordinate(x, y)
                if (item != null) {
                    _tempLineStartX = item.x + (item.width shr 1)
                    _tempLineStartY = item.y + (item.height shr 1)
                    _tempLineEndX = x
                    _tempLineEndY = y
                    invalidate()
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (startLinePointExist()) {
                    val item = containsCurrentCoordinate(x, y)
                    if (item != null) {
                        val halfItemSize = item.width shr 1
                        val newX = item.x + halfItemSize
                        val newY = item.y + halfItemSize
                        _lines!!.add(Line(_tempLineStartX, _tempLineStartY, newX, newY, false))
                        _tempLineStartX = newX
                        _tempLineStartY = newY
                    }
                    _tempLineEndX = x
                    _tempLineEndY = y
                    invalidate()
                }
            }
            MotionEvent.ACTION_UP -> {
                if (startLinePointExist()) {
                    _resultString = _tempOpenedString + _resultString
                    if (_keyboardResult != null && _resultString != null && _resultString!!.length == _itemsList!!.size) _keyboardResult?.OnWordCompleted(
                        _resultString
                    )
                    cleanTempLineCoordinates()
                    invalidate()
                }
            }
        }
        return true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val mode = MeasureSpec.getMode(heightMeasureSpec)
        _containerSize = MeasureSpec.getSize(heightMeasureSpec)
        val modeWidth = MeasureSpec.getMode(widthMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        _contentPaddding = width - _containerSize shr 1
        _itemSize = _containerSize / 5
        if (_itemSize == 0) return
        _xCoordinate = _containerSize - _itemSize shr 1
        _centerCoordinate = (_containerSize shr 1) - (_itemSize shr 1)
        _textSize = setTextSizeForWidth(titlePaint, (_itemSize / 3).toFloat(), "Д")
        _textOffset = initOffsets(titlePaint)
        fontDiff = _textSize / maxAnimateValue
        minTextSize = maxAnimateValue * 2 / 3
        maxAnimateValue = (_itemSize * 0.35f).toInt()
        animateDiff = animateTime / maxAnimateValue
        if (_itemsList != null && _itemsList!!.size > 0) for (rec in _itemsList!!) rec.paint.textSize =
            _textSize
        //circle param
        centerCircleX = width / 2
        centerCircleY = height / 2
        radiusCircle = (Math.min(width - _itemSize, height - _itemSize) / 2).toFloat()
        setParamForButtons()
    }

    //endregion
    //region private methods
    private fun initOffsets(paint: Paint): Float {
        val textHeight = (paint.descent() - paint.ascent()).toInt()
        return (textHeight shr 1) - paint.descent()
    }

    private fun init(context: Context, typedArray: AttributeSet?) {
        val a = context.obtainStyledAttributes(typedArray, R.styleable.CircleKeyboardView)
        var font: Typeface? = null
        try {
            val fontName = a.getResourceId(R.styleable.CircleKeyboardView_CKTextFont, 0)
            if (fontName > 0) {
                font = ResourcesCompat.getFont(context, fontName)
            }
        } catch (ex: Exception) {
        }
        circleColor = a.getColor(R.styleable.CircleKeyboardView_CKBcgCircleColor, LINE_COLOR)
        circleWidth = a.getDimension(R.styleable.CircleKeyboardView_CKBcgCircleWidth, 2f)
        lineColor = a.getColor(R.styleable.CircleKeyboardView_CKLineColor, LINE_COLOR)
        textColor = a.getColor(R.styleable.CircleKeyboardView_CKTextColor, TEXT_COLOR)
        lineWidth = a.getDimension(R.styleable.CircleKeyboardView_CKLineWidth, 10f)
        borderButtonColor =
            a.getColor(R.styleable.CircleKeyboardView_CKButtonBorderColor, TEXT_COLOR)
        cornerRadius =
            a.getDimension(R.styleable.CircleKeyboardView_CKRadiusCircleKeyboardButton, 0f)
        cornerWidth = a.getDimension(R.styleable.CircleKeyboardView_CKButtonBorderWidth, 2f)
        a.recycle()
        buttonNotPressedBorderPaint.color = borderButtonColor
        buttonNotPressedBorderPaint.style = Paint.Style.STROKE
        buttonNotPressedBorderPaint.strokeWidth = cornerWidth
        buttonPressedBorderPaint.color = borderButtonColor
        buttonPressedBorderPaint.style = Paint.Style.STROKE
        buttonPressedBorderPaint.strokeWidth = cornerWidth
        buttonPressedBorderPaint.setShadowLayer(15f, 0f, 0f, borderButtonColor)
        circlePaint.style = Paint.Style.STROKE
        circlePaint.strokeWidth = circleWidth
        circlePaint.color = circleColor
        linePaint.style = Paint.Style.STROKE
        linePaint.color = borderButtonColor
        linePaint.strokeWidth = lineWidth
        linePaint.setShadowLayer(15f, 0f, 0f, borderButtonColor)
        if (font != null) titlePaint.typeface = font
        titlePaint.color = textColor
        titlePaint.style = Paint.Style.FILL
        titlePaint.textAlign = Paint.Align.CENTER
        secondDelay = animateTime / 3
        thirdDelay = animateTime * 2 / 3
    }

    private fun setParamForButtons() {
        var currentAngel = 0
        for (i in _itemsList!!.indices) {
            currentAngel += _angle
            val currentCell = _itemsList!![i]
            rotatePoint(
                _centerCoordinate,
                _centerCoordinate,
                currentAngel,
                _xCoordinate,
                0,
                _itemSize,
                currentCell
            )
        }
    }

    private fun rotatePoint(cx: Int, cy: Int, angle: Int, x: Int, y: Int, size: Int, result: Cell) {
        var x = x
        var y = y
        val resultX: Int
        val resultY: Int
        val angleRad = angle * (Math.PI / 180)
        val s = Math.sin(angleRad)
        val c = Math.cos(angleRad)

        // translate point back to origin:
        x -= cx
        y -= cy

        // rotate point
        val xnew = x * c - y * s
        val ynew = x * s + y * c

        // translate point back:
        resultX = (xnew + cx).toInt()
        resultY = (ynew + cy).toInt()
        result.x = resultX
        result.y = resultY
        result.height = size
        result.width = result.height
        result.setGradient()
    }

    private fun containsCurrentCoordinate(x: Int, y: Int): Cell? {
        if (_itemsList == null || _itemsList!!.size == 0) return null
        for (item in _itemsList!!) {
            if (contains(item, x, y) && !item.selected) {
                item.selected = true
                _resultString += item.title
                return item
            }
        }
        return null
    }

    private fun contains(r: Cell, x: Int, y: Int): Boolean {
        return x >= r.x && y >= r.y && x <= r.x + r.width && y <= r.y + r.height
    }

    private fun startLinePointExist(): Boolean {
        return _tempLineStartX != -1 && _tempLineStartY != -1
    }

    private fun cleanTempLineCoordinates() {
        _tempLineStartX = -1
        _tempLineStartY = -1
        _tempLineEndX = -1
        _tempLineEndY = -1
        if (_lines != null) {
            val itr: MutableIterator<*> = _lines!!.iterator()
            while (itr.hasNext()) {
                val line = itr.next() as Line
                if (!line.fixed) itr.remove()
            }
        }
        if (_itemsList != null) {
            for (item in _itemsList!!) if (!item.locked) item.selected = false
        }
        _resultString = ""
    }

    private fun setTextSizeForWidth(
        paint: Paint, desiredWidth: Float,
        text: String
    ): Float {
        // Pick a reasonably large value for the test. Larger values produce
        // more accurate results, but may cause problems with hardware
        // acceleration. But there are workarounds for that, too; refer to
        // http://stackoverflow.com/questions/6253528/font-size-too-large-to-fit-in-cache
        val testTextSize = 48f

        // Get the bounds of the text, using our testTextSize.
        paint.textSize = testTextSize
        val bounds = Rect()
        paint.getTextBounds(text, 0, text.length, bounds)
        // Calculate the desired size as a proportion of our testTextSize.
        val desiredTextSize = testTextSize * desiredWidth / bounds.width()
        // Set the paint for that size.
        paint.textSize = desiredTextSize
        return desiredTextSize
    }

    //endregion
    //region ValueAnimator.AnimatorUpdateListener
    override fun onAnimationUpdate(valueAnimator: ValueAnimator) {
        // gets the current value of our animation
        val value = valueAnimator.animatedValue as Int
        setCoordinateForAnimatingView(value, 0, 1, 0)
        setCoordinateForAnimatingView(value, secondDelay, 1, 1)
        setCoordinateForAnimatingView(value, thirdDelay, 1, 2)
        invalidate()
    }

    private fun setCoordinateForAnimatingView(
        value: Int,
        minTime: Int,
        mult: Int,
        orderPosition: Int
    ) {
        var mult = mult
        if (value > minTime) {
            val newValue = value - minTime
            val position = newValue / animateTime
            var positionInList = position * 3 + orderPosition
            if (positionInList >= _itemsList!!.size) {
                positionInList -= _itemsList!!.size
                mult = -1
            }
            var result = newValue % animateTime / animateDiff * mult
            if (positionInList < _itemsList!!.size) {
                if (mult == -1) result = _itemSize - _itemsList!![positionInList].minSize + result
                _itemsList!![positionInList].extraPadding = result
                return
            }
        }
    }

    //endregion
    //region private properties
    //changable values
    //when you change word
    private var _itemCount = 0
    private var _angle = 0

    //center container size
    private var _containerSize = 0

    //padding from left and right sides
    private var _contentPaddding = 0

    //one item size
    private var _itemSize = 0
    private var _xCoordinate = 0

    //middle of container without content padding
    private var _centerCoordinate = 0
    private var _tempOpenedString: String? = null

    //draw temporary line from one point
    private var _tempLineStartX = -1
    private var _tempLineStartY = -1
    private var _tempLineEndX = -1
    private var _tempLineEndY = -1
    private var _textOffset = 0f
    private var _textSize = 0f

    //colors
    var lineColor = 0
    var textColor = 0
    var circleColor = 0
    var borderButtonColor = 0

    //
    var lineWidth = 0f
    var circleWidth = 0f
    var cornerRadius = 0f
    var cornerWidth = 0f
    private var mAnimator: ValueAnimator? = null

    //
    private var _resultString: String? = ""
    private var _keyboardResult: IKeybordCircleResult? = null
    private var _itemsList: ArrayList<Cell>? = ArrayList()
    private var _lines: ArrayList<Line>? = ArrayList()
    private val _texts: ArrayList<TempText>? = ArrayList()
    var linePaint = Paint()
    var buttonNotPressedBorderPaint = Paint()
    var buttonBcgPaint = Paint()
    var buttonPressedBorderPaint = Paint()
    var titlePaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
    var circlePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    var rect = Rect() //endregion

    companion object {
        //animation properties
        //time for animate one view in one way
        private const val animateTime = 250
    }
}

internal class Cell(var title: Char, var isSolved: Boolean, var paint: Paint) {
    var x = 0
    var y = 0
    var height = 0
    var width = 0
    var openedByHint = false
    var extraPadding = 0
    var minSize = 1000
    var linearGradient: LinearGradient? = null

    fun setGradient() {
        linearGradient = if(isSolved) LinearGradient(0f, y.toFloat(), 0f, (y + height).toFloat(), Color.parseColor("#2A5F86"), Color.parseColor("#041929"), Shader.TileMode.MIRROR)
            else LinearGradient(0f, y.toFloat(), 0.toFloat(), (y + height).toFloat(), Color.parseColor("#2A5F86"), Color.parseColor("#ff00e8"), Shader.TileMode.MIRROR)
    }

    var selected = false
    var locked = false
    fun isSelected(): Boolean {
        return selected
    }

}

internal class Line(
    var startX: Int,
    var startY: Int,
    var endX: Int,
    var endY: Int,
    var fixed: Boolean
)

internal class TempText(var title: String, var x: Int, var y: Int, var paint: Paint)
