package com.example.crosswordkotlin

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout

class CustomViewCoinHint (context: Context, attrs: AttributeSet): LinearLayout(context, attrs) {
    var textView: TextView ?= null
    var text : String ?= null
    set(value) {
        field = value
        textView?.text = value
    }

    init {
        inflate(context, R.layout.view_hint_coin, this)

        val imageView: ImageView = findViewById(R.id.image)
        textView = findViewById(R.id.text)

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.CustomViewCoinHint)
        imageView.setImageDrawable(attributes.getDrawable(R.styleable.CustomViewCoinHint_CNImage))
        textView?.text = attributes.getString(R.styleable.CustomViewCoinHint_CNText)
        attributes.recycle()
    }
}