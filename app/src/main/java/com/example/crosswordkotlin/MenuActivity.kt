package com.example.crosswordkotlin

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MenuActivity : AppCompatActivity(), View.OnClickListener {
    //region private properties
    private var buttonNextPage: Button? = null
    private var buttonPortrait: Button? = null
    private var buttonRightLandscape: Button? = null
    private var buttonLeftLandscape: Button? = null
    private var buttonCircleKeyboardPage: Button? = null
    private var orientation = EnumOrientation.Center
    //endregion

    //region override functions
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        buttonNextPage = findViewById<Button>(R.id.nextPage)
        buttonNextPage!!.setOnClickListener(this)
        buttonPortrait = findViewById<Button>(R.id.portrait)
        buttonPortrait!!.setOnClickListener(this)
        buttonRightLandscape = findViewById<Button>(R.id.right_landscape)
        buttonRightLandscape!!.setOnClickListener(this)
        buttonLeftLandscape = findViewById<Button>(R.id.left_landscape)
        buttonLeftLandscape!!.setOnClickListener(this)
        buttonCircleKeyboardPage = findViewById<Button>(R.id.circleKeyboardPage)
        buttonCircleKeyboardPage!!.setOnClickListener(this)
    }
    override fun onClick(view: View) {
        if (view.id == R.id.nextPage) {
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, KeyboardFragment(orientation)).commit()
            clearPage()
        } else if (view.id == R.id.circleKeyboardPage) {
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, CircleKeyboardFragment(orientation)).commit()
            clearPage()
        } else if (view.id == R.id.portrait) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            orientation = EnumOrientation.Center
        } else if (view.id == R.id.right_landscape) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
            orientation = EnumOrientation.Right
        } else if (view.id == R.id.left_landscape) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
            orientation = EnumOrientation.Left
        }
    }
    //endregion
    //region private methods
    fun clearPage(){
        buttonNextPage!!.visibility = View.GONE
        buttonRightLandscape!!.visibility = View.GONE
        buttonPortrait!!.visibility = View.GONE
        buttonLeftLandscape!!.visibility = View.GONE
        buttonCircleKeyboardPage!!.visibility = View.GONE
    }
    //endregion
}