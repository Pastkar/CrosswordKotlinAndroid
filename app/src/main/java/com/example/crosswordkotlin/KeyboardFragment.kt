package com.example.crosswordkotlin

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment

class KeyboardFragment : Fragment, View.OnClickListener {
    //region private properties
    private var currentOrientation: EnumOrientation? = null
    private var layout: ConstraintLayout? = null
    //endregion

    //endregion
    //region public methods
    constructor(currentOrientation: EnumOrientation?) :  super(R.layout.fragment_keyboard) {
        this.currentOrientation = currentOrientation
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_keyboard, container, false)
        layout = view.findViewById<View>(R.id.parent_layout) as ConstraintLayout
        val keyBoardCustom = view.findViewById<View>(R.id.keyboard) as KeyBoardCustom
        val editText = view.findViewById<View>(R.id.editText) as EditText
        val buttonLeftOrientation = view.findViewById<View>(R.id.btn_left_orientation) as Button
        buttonLeftOrientation.setOnClickListener(this as View.OnClickListener)
        val buttonCenterOrientation = view.findViewById<View>(R.id.btn_center_orientation) as Button
        buttonCenterOrientation.setOnClickListener(this as View.OnClickListener)
        val buttonRightOrientation = view.findViewById<View>(R.id.btn_right_orientation) as Button
        buttonRightOrientation.setOnClickListener(this as View.OnClickListener)
        changeLayout()
        editText.setRawInputType(InputType.TYPE_CLASS_TEXT)
        editText.setTextIsSelectable(true)
        val ic = editText.onCreateInputConnection(EditorInfo())
        keyBoardCustom.setInputConnection(ic)
        return view
    }

    override fun onClick(view: View) {
        if (view.id == R.id.btn_left_orientation) {
            getActivity()?.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
            currentOrientation = EnumOrientation.Left
        } else if (view.id == R.id.btn_center_orientation) {
            getActivity()?.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            currentOrientation = EnumOrientation.Center
        } else if (view.id == R.id.btn_right_orientation) {
            getActivity()?.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
            currentOrientation = EnumOrientation.Right
        }
    }

     override fun onConfigurationChanged(config: Configuration) {
        super.onConfigurationChanged(config)
        changeLayout()
    }
    //endregion

    //endregion
    //region private methods
    private fun changeConstraintsRight(set: ConstraintSet) {
        set.clear(R.id.keyboard)
        set.connect(R.id.keyboard, ConstraintSet.START, R.id.guideline, ConstraintSet.START, 0)
        set.connect(
            R.id.keyboard,
            ConstraintSet.BOTTOM,
            ConstraintSet.PARENT_ID,
            ConstraintSet.BOTTOM,
            0
        )
        set.connect(R.id.keyboard, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0)
        set.constrainPercentHeight(R.id.keyboard, .6f)
    }

    private fun changeConstraintLeft(set: ConstraintSet) {
        set.clear(R.id.keyboard)
        set.connect(
            R.id.keyboard,
            ConstraintSet.START,
            ConstraintSet.PARENT_ID,
            ConstraintSet.START,
            0
        )
        set.connect(
            R.id.keyboard,
            ConstraintSet.BOTTOM,
            ConstraintSet.PARENT_ID,
            ConstraintSet.BOTTOM,
            0
        )
        set.connect(R.id.keyboard, ConstraintSet.END, R.id.guideline, ConstraintSet.END, 0)
        set.constrainPercentHeight(R.id.keyboard, .6f)
    }

    private fun changeConstraintCenter(set: ConstraintSet) {
        set.clear(R.id.keyboard)
        set.connect(
            R.id.keyboard,
            ConstraintSet.START,
            ConstraintSet.PARENT_ID,
            ConstraintSet.START,
            0
        )
        set.connect(
            R.id.keyboard,
            ConstraintSet.BOTTOM,
            ConstraintSet.PARENT_ID,
            ConstraintSet.BOTTOM,
            0
        )
        set.connect(R.id.keyboard, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0)
        set.constrainPercentHeight(R.id.keyboard, .3f)
    }

    private fun changeLayout() {
        val set = ConstraintSet()
        set.clone(layout)
        when (currentOrientation) {
            EnumOrientation.Left -> changeConstraintLeft(set)
            EnumOrientation.Right -> changeConstraintsRight(set)
            EnumOrientation.Center -> changeConstraintCenter(set)
        }
        set.applyTo(layout)
    }
    //endregion
}