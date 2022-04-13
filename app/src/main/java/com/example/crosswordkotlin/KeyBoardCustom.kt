package com.example.crosswordkotlin

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.SparseArray
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputConnection
import android.widget.Button
import android.widget.FrameLayout
import androidx.core.content.res.ResourcesCompat

class KeyBoardCustom : FrameLayout, View.OnClickListener {
    //region private properties
    private var mPaddingTop = 0f
    private var mWidthResolution = 0
    private val keyBoard = charArrayOf(
        'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p',
        'a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l',
        '/', 'z', 'x', 'c', 'v', 'b', 'n', 'm', '?'
    )
    private val buttonsKeyBoard = arrayOfNulls<Button>(keyBoard.size)
    private val keyValues = SparseArray<String>()
    private var inputConnection: InputConnection? = null
    //endregion

    //endregion
    //region public methods
    constructor(context: Context) :  super(context) {
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs){
        val a = context.theme.obtainStyledAttributes(
            attrs, R.styleable.Keyboard, 0, 0
        )
        mPaddingTop = a.getDimensionPixelSize(R.styleable.Keyboard_padding_top, 0).toFloat()
        a.recycle()
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)  : super(context, attrs, defStyleAttr) {
        init(context)
    }

    override fun onClick(view: View) {
        if (inputConnection == null) return
        if (view.id == '?'.toInt()) {
            val selectedText = inputConnection!!.getSelectedText(0)
            if (TextUtils.isEmpty(selectedText)) {
                inputConnection!!.deleteSurroundingText(1, 0)
            } else {
                inputConnection!!.commitText("", 1)
            }
        } else if (false) {
            //view.getId() == R.id.button_caps
        } else {
            val value = keyValues[view.id]
            inputConnection!!.commitText(value, 1)
        }
    }

    fun setInputConnection(ic: InputConnection?) {
        inputConnection = ic
    }
    //endregion

    //endregion
    //region private methods
    private fun init(context: Context) {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val metrics = DisplayMetrics()
        display.getMetrics(metrics)
        val width: Int
        val height: Int
        if (metrics.widthPixels < metrics.heightPixels) {
            width = metrics.widthPixels * 8 / 100
            height = metrics.heightPixels * 5 / 100
        } else {
            width = metrics.heightPixels * 8 / 100
            height = metrics.widthPixels * 5 / 100
        }
        for (i in keyBoard.indices) {
            createButton(i, context, width, height)
        }
    }

    private fun createButton(i: Int, context: Context, width: Int, height: Int) {
        buttonsKeyBoard[i] = Button(context)
        buttonsKeyBoard[i]!!.id = keyBoard[i].toInt()
        buttonsKeyBoard[i]!!.setBackgroundResource(R.drawable.bcg_btn_keyboard_selector)
        buttonsKeyBoard[i]!!.text = keyBoard[i].toString()
        if (keyBoard[i] != '/' && keyBoard[i] != '?') buttonsKeyBoard[i]!!.layoutParams =
            FrameLayout.LayoutParams(width, height) else buttonsKeyBoard[i]!!.layoutParams =
            FrameLayout.LayoutParams(
                (width * 1.5).toInt(), height
            )
        buttonsKeyBoard[i]?.setTextColor(getResources().getColor(R.color.white))
        buttonsKeyBoard[i]!!.setTypeface(ResourcesCompat.getFont(context, R.font.arvo))
        buttonsKeyBoard[i]!!.setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            getResources().getDimension(R.dimen.keyboard_text_size)
        )
        buttonsKeyBoard[i]!!.setOnClickListener(this as View.OnClickListener)
        keyValues.put(keyBoard[i].toInt(), keyBoard[i].toString())
        addView(buttonsKeyBoard[i])
    }
    //endregion

    //endregion
    //region protected methods
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        measureChildren(widthMeasureSpec, heightMeasureSpec)
        val first: View = getChildAt(0)
        val size = first.measuredWidth + first.measuredHeight
        val width = ViewGroup.resolveSize(size, widthMeasureSpec)
        val height = ViewGroup.resolveSize(size, heightMeasureSpec)
        mWidthResolution = width
        setMeasuredDimension(width, height)
    }

    override fun onLayout(b: Boolean, i: Int, i1: Int, i2: Int, i3: Int) {
        val first: View = getChildAt(0)
        val childWidth = first.measuredWidth
        val childHeight = first.measuredHeight
        val marginBetweenButtons =
            getContext().getResources().getDimension(R.dimen.margin_between_button_width).toInt()
        val marginHeight =
            getContext().getResources().getDimension(R.dimen.margin_between_button_height).toInt()
        val marginLeft = ((mWidthResolution - childWidth * 10) / 4)
        for (a in 0 until getChildCount()) {
            val child: View = getChildAt(a)
            var x = 0
            var y = 0
            if (a < 10) {
                x = a * (childWidth + marginBetweenButtons) + marginLeft
                y = mPaddingTop.toInt()
            } else if (a >= 10 && a < 19) {
                x = (a - 10) * (childWidth + marginBetweenButtons) + marginLeft + childWidth / 2
                y = mPaddingTop.toInt() + childHeight + marginHeight
            } else {
                x =
                    if (a - 20 < 1) (a - 19) * marginBetweenButtons + (1.5 * childWidth).toInt() * (a - 19) + marginLeft else (a - 19) * marginBetweenButtons + (1.5 * childWidth).toInt() + (a - 19 - 1) * childWidth + marginLeft
                y = mPaddingTop.toInt() + 2 * (childHeight + marginHeight)
            }
            child.layout(x, y, x + child.measuredWidth, y + child.measuredHeight)
        }
    }
    //endregion

}