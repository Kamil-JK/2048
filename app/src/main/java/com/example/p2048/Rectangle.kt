package com.example.p2048

import android.widget.ImageView
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
    var deleted = false

    init{
        value = 2
        elementX = 240f * X.toFloat() + 20f
        elementY = 240f * Y.toFloat() + 20f
        currentX = elementX
        currentY = elementY
        element?.setX(currentX)
        element?.setY(currentY)
    }

    fun set(image: ImageView) {
        image.setImageResource(R.drawable.p2)
        element = image
    }

    fun set(number: Int) {
        when (number) {
            0 -> element?.setImageBitmap(null)
            2 -> element?.setImageResource(R.drawable.p2)
            4 -> element?.setImageResource(R.drawable.p4)
            8 -> element?.setImageResource(R.drawable.p8)
            16 -> element?.setImageResource(R.drawable.p16)
            32 -> element?.setImageResource(R.drawable.p32)
            64 -> element?.setImageResource(R.drawable.p64)
            128 -> element?.setImageResource(R.drawable.p128)
            256 -> element?.setImageResource(R.drawable.p256)
            512 -> element?.setImageResource(R.drawable.p512)
            1024 -> element?.setImageResource(R.drawable.p1024)
            2048 -> element?.setImageResource(R.drawable.p2048)
            4096 -> element?.setImageResource(R.drawable.p4096)
            8192 -> element?.setImageResource(R.drawable.p8192)
        }
    }

    fun double(): Int{
        value *= 2
        set(value)
        return value
    }

    fun run() {
        timer.schedule(object : TimerTask() {
            override fun run() {
                changePos()
            }
        }, 0, 4) //speed
    }

    fun move(vx: Int, vy: Int){
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

    fun delete(){
        deleted = true
        set(0)
    }

    fun isDeleted(): Boolean{
        return deleted
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