package com.example.p2048

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.lang.Math.abs
import java.util.*


class MainActivity : AppCompatActivity(), GestureDetector.OnGestureListener {

    //var fields = Array(4) {Array(4) {Rectangle} }

    var score = 0;
    lateinit var gestureDetector: GestureDetector
    var x1:Float = 0.0f
    var x2:Float = 0.0f
    var y1:Float = 0.0f
    var y2:Float = 0.0f
    var myLayout: LinearLayout? = null //nie moge inicjalizować

    companion object{
        const val MIN = 100
    }

    var rectangles: MutableList<Rectangle> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(localClassName, "onCreate")
        setContentView(R.layout.activity_main)
        gestureDetector = GestureDetector(this, this)
        myLayout = findViewById<LinearLayout>(R.id.myLayout)
        makeNew()
    }

    fun makeNew(){
        if(rectangles.size >= 16) return
        val imageView = ImageView(this)
        imageView.layoutParams= LinearLayout.LayoutParams(240, 240)
        imageView.x= 20F // setting margin from left
        imageView.y= 20F // setting margin from top
        //imageView.setImageResource(R.drawable.sixteen)
        myLayout?.addView(imageView) // adding image to the layout

        var check = true
        var randomX = 0
        var randomY = 0

        while(true) {
            check = true
            randomX = (0..3).random()
            randomY = (0..3).random()
            for (rectangle in rectangles) {
                if (rectangle.getXPos() == randomX && rectangle.getYPos() == randomY)
                {
                    check = false
                    break
                }
            }
            if(check==true) break
        }

        val rec = Rectangle(randomX, randomY)
        rec.set(imageView, 2)
        rec.run()
        rectangles.add(rec)

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        gestureDetector.onTouchEvent(event)
        when(event?.action)
        {
            //swipe start
            0 -> {
                x1 = event.x
                y1 = event.y
            }
            //swipe end
            1 -> {
                x2 = event.x
                y2 = event.y
                val X: Float = x2 - x1
                val Y: Float = y2 - y1
                if (abs(X) > MIN) {
                    if (x2 > x1) onRight()
                    else onLeft()
                } else if (abs(Y) > MIN) {
                    if (y2 > y1) onBottom()
                    else onTop()
                }
            }
        }
        return super.onTouchEvent(event)
    }

    private fun onRight() {
        //Toast.makeText(this, "right", Toast.LENGTH_SHORT).show()
        for(rectangle in rectangles){
            var xv = rectangle.getXPos()
            var x2 = 3
            var empty = true
            var rec2: Rectangle? = null
            var i = 0
            var index = 0
            for(r in rectangles){
                i++
                if(r.getYPos()==rectangle.getYPos() && r.getXPos()>xv && r.getXPos()<=x2){ //jeśli ten sam rząd, r po prawej od rec
                    x2 = r.getXPos() - 1
                    empty = false
                    rec2 = r
                    index = i
                }
            }
            if(empty) rectangle.move(3-xv,0)
            else{
                rectangle.move(x2-xv,0)
                rec2?.set(4)
                //rectangle.set(4)
                //rectangles.removeAt(index)
            }
        }
        makeNew()
    }

    private fun onLeft() {
        for(rectangle in rectangles){
            var xv = rectangle.getXPos()
            var x2 = 0
            var empty = true
            for(r in rectangles){
                if(r.getYPos()==rectangle.getYPos() && r.getXPos()<xv && r.getXPos()>=x2){
                    x2 = r.getXPos() + 1
                    empty = false
                }
            }
            if(empty) rectangle.move(-xv,0)
            else rectangle.move(x2-xv,0)
        }
        makeNew()
    }

    private fun onTop() {
        for(rectangle in rectangles){
            var yv = rectangle.getYPos()
            var y2 = 0
            var empty = true
            for(r in rectangles){
                if(r.getXPos()==rectangle.getXPos() && r.getYPos()<yv && r.getYPos()>=y2){
                    y2 = r.getYPos() + 1
                    empty = false
                }
            }
            if(empty) rectangle.move(0, -yv)
            else rectangle.move(0, y2-yv)
        }
        makeNew()
    }

    private fun onBottom() {
        for(rectangle in rectangles){
            var yv = rectangle.getYPos()
            var y2 = 3
            var check = true
            for(r in rectangles){
                if(r.getXPos()==rectangle.getXPos() && r.getYPos()>yv && r.getYPos()<=y2){ //jeszcze w tym samym wierszu! i chyba trzeba wyłączyć z tego 1 prostokat
                    y2 = r.getYPos() - 1
                    check = false
                }
            }
            if(check) rectangle.move(0, 3-yv)
            else rectangle.move(0, y2-yv)
        }
        makeNew()
    }

    override fun onStart() {
        super.onStart()
        Log.i(localClassName, "onstart")
    }

    override fun onResume() {
        super.onResume()
        Log.i(localClassName, "onresume")
    }

    override fun onPause() {
        super.onPause()
        Log.i(localClassName, "onpause")
    }

    override fun onStop() {
        super.onStop()
        Log.i(localClassName, "onstop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(localClassName, "ondestroy")
    }

    override fun onDown(e: MotionEvent?): Boolean {
        return false;
    }
    override fun onShowPress(e: MotionEvent?) {}
    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        return false;
    }
    override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent?,
            distanceX: Float,
            distanceY: Float
    ): Boolean {
        return false;
    }
    override fun onLongPress(e: MotionEvent?) {}
    override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent?,
            velocityX: Float,
            velocityY: Float
    ): Boolean {
        return false;
    }


}