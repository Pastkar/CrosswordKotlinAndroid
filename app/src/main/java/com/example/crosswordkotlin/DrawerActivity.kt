package com.example.crosswordkotlin

import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.FrameLayout
import androidx.annotation.DimenRes
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.drawerlayout.widget.DrawerLayout.SimpleDrawerListener

fun Context.px(@DimenRes dimen: Int): Int = resources.getDimension(dimen).toInt()

class DrawerActivity : AppCompatActivity() , View.OnClickListener {
    private var orientation = EnumOrientation.Center
    var rightBcg: BcgQuestionView? = null
    var leftBcg: BcgQuestionView? = null
    var widthSideButtonLandscape = 0
    var heightSideButtonLandscape:Int = 0
    var widthSideButtonPortrait:Int = 0
    var heightSideButtonPortrait:Int = 0
    var widthSideButton = 0
    var heightSideButton:Int = 0
    var width:Int = 0
    var height:Int = 0
    var radiusBorder:Int = 0
    var leftSideView: SideQuestionView ?= null
    var rightSideView:SideQuestionView ?= null
    var marginBcgQuestions = 0
    var marginTopQuestionBtnLandscape:Int = 0
    var customDrawerLayout: CustomDrawerLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crossword)
        val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val metrics = DisplayMetrics()
        display.getMetrics(metrics)
        width = metrics.widthPixels
        height = metrics.heightPixels

        widthSideButtonLandscape = px(R.dimen.width_questions_side_view_landscape)
        heightSideButtonLandscape = px(R.dimen.height_questions_side_view_landscape)
        widthSideButtonPortrait = px(R.dimen.width_questions_side_view_portrait)
        heightSideButtonPortrait = px(R.dimen.height_questions_side_view_portrait)
        marginBcgQuestions = px(R.dimen.margin_top_guestions_view)
        radiusBorder = px(R.dimen.radius_fragment_question)
        marginTopQuestionBtnLandscape = px(R.dimen.margin_top_questions_btn_landscape)

        leftSideView = findViewById<View>(R.id.left_side_view) as SideQuestionView?
        rightSideView = findViewById<View>(R.id.right_side_view) as SideQuestionView?
        leftBcg = findViewById<View>(R.id.left_bcg_questions) as BcgQuestionView?
        rightBcg = findViewById<View>(R.id.right_bcg_questions) as BcgQuestionView?

        val drawerLayout = findViewById<View>(R.id.drawer_layout) as DrawerLayout?
        drawerLayout?.setScrimColor(Color.TRANSPARENT)
        val buttonLeftOrientation = findViewById<View>(R.id.btn_left_orientation) as Button
        buttonLeftOrientation.setOnClickListener(this as View.OnClickListener)
        val buttonCenterOrientation = findViewById<View>(R.id.btn_center_orientation) as Button
        buttonCenterOrientation.setOnClickListener(this as View.OnClickListener)

        customDrawerLayout = CustomDrawerLayout(leftSideView!!,rightSideView!!,orientation)
        changeOrientation()

        drawerLayout?.addDrawerListener(customDrawerLayout!!)
    }

    override fun onConfigurationChanged(config: Configuration) {
        super.onConfigurationChanged(config)
        changeOrientation()
    }

    override fun onClick(view: View) {
        if (view.id == R.id.btn_left_orientation) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
            orientation = EnumOrientation.Left
        } else if (view.id == R.id.btn_center_orientation) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            orientation = EnumOrientation.Center
        }
    }

    private fun leftOrientation() {
        widthSideButton = widthSideButtonLandscape
        heightSideButton = heightSideButtonLandscape

        leftSideView?.layoutParams = FrameLayout.LayoutParams(widthSideButton, heightSideButton)
        rightSideView?.layoutParams = FrameLayout.LayoutParams(widthSideButton, heightSideButton)

        leftSideView?.y = marginTopQuestionBtnLandscape.toFloat()
        leftSideView?.x  =0f
        leftSideView?.paddingLine = 30f

        rightSideView?.y = marginTopQuestionBtnLandscape.toFloat()
        rightSideView?.x = height - widthSideButton.toFloat()
        rightSideView?.paddingLine = 30f

        rightBcg?.lineStartY = rightSideView?.getY()!!.toFloat()
        leftBcg?.lineStartY = leftSideView?.getY()!!.toFloat()

        rightBcg?.setMargin(0f, 0f, 0f, 0f)
        leftBcg?.setMargin(0f, 0f, 0f, 0f)

        leftBcg?.lineHeight = heightSideButton.toFloat()
        rightBcg?.lineHeight = heightSideButton.toFloat()

        rightBcg?.radiusBorder = 0f
        leftBcg?.radiusBorder = 0f
    }

    private fun centerOrientation() {
        widthSideButton = widthSideButtonPortrait
        heightSideButton = heightSideButtonPortrait

        leftSideView?.layoutParams = FrameLayout.LayoutParams(widthSideButton, heightSideButton)
        rightSideView?.layoutParams = FrameLayout.LayoutParams(widthSideButton, heightSideButton)

        leftSideView?.x = (-1 * widthSideButton / 2).toFloat()
        leftSideView?.y = (height / 2 - heightSideButton).toFloat()
        leftSideView?.paddingLine = 10f

        rightSideView?.y = (height / 2 - heightSideButton).toFloat()
        rightSideView?.x = (width - widthSideButton / 2).toFloat()
        rightSideView?.paddingLine = 10f

        rightBcg?.lineStartY = rightSideView?.getY()!!.toFloat()
        leftBcg?.lineStartY = leftSideView?.getY()!!.toFloat()

        rightBcg?.setMargin(0f, marginBcgQuestions.toFloat(), 0f, marginBcgQuestions.toFloat())
        leftBcg?.setMargin(0f, marginBcgQuestions.toFloat(), 0f, marginBcgQuestions.toFloat())

        leftBcg?.lineHeight = heightSideButton.toFloat()
        rightBcg?.lineHeight = heightSideButton.toFloat()

        rightBcg?.radiusBorder  = radiusBorder.toFloat()
        leftBcg?.radiusBorder = radiusBorder.toFloat()
    }

    private fun changeOrientation() {
        when (orientation) {
            EnumOrientation.Center -> centerOrientation()
            EnumOrientation.Left -> leftOrientation()
        }
        customDrawerLayout?.leftPosition = leftSideView?.getX()!!.toFloat()
        customDrawerLayout?.rightPosition = rightSideView?.getX()!!.toFloat()
        customDrawerLayout?.orientation = orientation
    }
}
class CustomDrawerLayout(private val viewLeft: SideQuestionView,private val viewRight: SideQuestionView,var orientation: EnumOrientation = EnumOrientation.Center) : SimpleDrawerListener() {
    var rightPosition = 0f
    var leftPosition = 0f
    override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
        super.onDrawerSlide(drawerView, slideOffset)
        var widthItem: Int = if (orientation === EnumOrientation.Center) viewLeft.measuredWidth / 2 else 0
        if (drawerView.id == R.id.right_view) {
            if (slideOffset != 0f)  viewRight.x = rightPosition - drawerView.measuredWidth * slideOffset - widthItem
            else viewRight.x = rightPosition
        } else if (drawerView.id == R.id.left_view) {
            if (slideOffset != 0f)
                viewLeft.x = drawerView.measuredWidth * slideOffset + leftPosition + widthItem
            else viewLeft.x = leftPosition
        }
    }
}