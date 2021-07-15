package com.example.p2048

import android.os.Handler
import android.widget.ImageView
import android.widget.LinearLayout
import java.util.*

class Rectangle(var X: Int = 1, var Y: Int = 3)
{
    // Images
    var element: ImageView? = null

    // Position
    var value = 0
    var elementX = 0f
    var elementY = 0f
    var currentX = 0f
    var currentY = 0f
    var timer: Timer = Timer()

    init{
        value = 2
        elementX = 240f * X.toFloat() + 60f
        elementY = 240f * Y.toFloat() + 550f
        currentX = elementX
        currentY = elementY
        element?.setX(currentX)
        element?.setY(currentY)
    }

    fun set(image: ImageView, number: Int)
    {
        when (number) {
            2 -> image.setImageResource(R.drawable.two)
            4 -> image.setImageResource(R.drawable.four)
            8 -> image.setImageResource(R.drawable.eight)
            16 -> image.setImageResource(R.drawable.sixteen)
        }
        element = image
    }

    fun set(number: Int)
    {
        when (number) {
            2 -> element?.setImageResource(R.drawable.two)
            4 -> element?.setImageResource(R.drawable.four)
            8 -> element?.setImageResource(R.drawable.eight)
            16 -> element?.setImageResource(R.drawable.sixteen)
        }
    }

    fun run() {
        timer.schedule(object : TimerTask() {
            override fun run() {
                //handler.post { changePos() }
                changePos()
            }
        }, 0, 10) //20 is speed
    }

    fun move(vx: Int, vy: Int) {
        elementX += vx * 240
        elementY += vy * 240
        X += vx
        Y += vy

    }

    fun getXPos(): Int{
        return X
    }

    fun getYPos(): Int{
        return Y
    }

    private fun changePos() {
        if(currentX < elementX) currentX += 10
        else if(currentX > elementX) currentX -= 10
        else if(currentY < elementY) currentY += 10
        else if(currentY > elementY) currentY -= 10
        element?.setX(currentX)
        element?.setY(currentY)
    }

}