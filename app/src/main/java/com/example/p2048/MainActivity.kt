package com.example.p2048

import android.app.Dialog
import android.app.PendingIntent.getActivity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.lang.Math.abs
import com.google.firebase.database.DatabaseError

import com.google.firebase.database.DataSnapshot

import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity(), GestureDetector.OnGestureListener {

    val database = FirebaseDatabase.getInstance("https://p2048-9b74d-default-rtdb.europe-west1.firebasedatabase.app/")
    val databaseRef = database.getReference().child("Users")

    var score = 0
    lateinit var gestureDetector: GestureDetector

    //to przenieść
    var checkMove = false
    var myLayout: LinearLayout? = null
    var scoreText: TextView? = null
    var scoreTextOnDialog: TextView? = null
    var editName: EditText? = null
    var dialog: Dialog? = null

    companion object{
        const val MIN = 100
    }

    var rectangles: MutableList<Rectangle> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        gestureDetector = GestureDetector(this, this)
        myLayout = findViewById<LinearLayout>(R.id.myLayout)
        scoreText = findViewById<TextView>(R.id.scoreNumber)
        scoreTextOnDialog = findViewById<TextView>(R.id.textScore)
        dialog = Dialog(this)

        var button: Button = findViewById(R.id.retryButton)
        button.setOnClickListener {
            restart()
        }

        val overButton = findViewById(R.id.overButton) as Button
        overButton.setOnClickListener {
            gameOverDialog()
        }

        makeNew()
    }

    fun makeNew(){
        if(rectangles.size >= 16) return
        val imageView = ImageView(this)
        imageView.layoutParams= LinearLayout.LayoutParams(240, 240)
        imageView.x= 20F // setting margin from left
        imageView.y= 20F // setting margin from top
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
        rec.set(imageView)
        rec.run()
        rectangles.add(rec)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        gestureDetector.onTouchEvent(event)
        var check = true
        var x1:Float = 0.0f
        var x2:Float = 0.0f
        var y1:Float = 0.0f
        var y2:Float = 0.0f
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
                else check = false
                if(check && checkMove==true) { //Jeśli był swipe to stwórz nowy i sprawdź usunięte
                    makeNew()
                    clear()
                    scoreText?.setText(score.toString())
                    checkMove = false
                    if (rectangles.size == 16) { //Jeśli jest 16 kwadratow -> game over
                        if(isGameOver()) {
                            gameOverDialog()
                        }
                    }
                }
            }
        }
        return super.onTouchEvent(event)
    }

    private fun clear() {
        //Usun oznaczone
        var size = rectangles.size
        for (i in 0..size + 1) {
            if (i < rectangles.size) {
                if (rectangles[i].isDeleted()) {
                    rectangles[i].set(0) //index out of bounds
                    rectangles.remove(rectangles[i])
                }
            }
        }
    }

    private fun onRight() {
        for(row in 0..3) {//od góry do dołu
            var col = 4
            repeat(4){//od prawej kolumny do lewej
                col--
                for(rectangle in rectangles){ //pętla po kwadratach
                    var x2 = 3
                    var x3 = 0
                    var check = false
                    var blockPos = 0
                    if(!rectangle.isDeleted() && rectangle.getXPos()==col && rectangle.getYPos()==row) { //znajduje wybrany istniejact kwadrat
                        for(r in rectangles){
                            if(!r.isDeleted() && r.getYPos()==row && r.getXPos()>col && r.getXPos()<=x2){ //jeśli ten sam rząd i r po prawej od rec
                                x2 = r.getXPos() - 1
                            }
                            if(!r.isDeleted() && r.getYPos()==row && r.getXPos()<col && r.getXPos()>=x3){ //szuka po lewej najbliższego takiego samego
                                if(r.value==rectangle.value) {
                                    x3 = r.getXPos()
                                    check = true    //czy po lewej znaleziono kwadrat z taką samą liczbą?
                                }
                                else blockPos = r.getXPos()
                            }
                        }
                        rectangle.move(x2-col,0)//jeśli nie to kwadrat idzie w prawo do napotkanego innego
                        if(x2-col != 0) checkMove = true
                        if(check && x3>=blockPos){
                            for(r in rectangles) {
                                if(!r.isDeleted() && r.getYPos()==row && r.getXPos()==x3) {//usuwanie po lewej  i podwajanie
                                    r.delete()
                                    score += rectangle.double()
                                    checkMove = true
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun onLeft() {
        for(row in 0..3) {//od góry do dołu
            var col = -1
            repeat(4){//od prawej kolumny do lewej
                col++
                for(rectangle in rectangles){ //pętla po kwadratach
                    var x2 = 0
                    var x3 = 3
                    var check = false
                    var blockPos = 3
                    if(!rectangle.isDeleted() && rectangle.getXPos()==col && rectangle.getYPos()==row) { //znajduje wybrany istniejact kwadrat
                        for(r in rectangles){
                            if(!r.isDeleted() && r.getYPos()==row && r.getXPos()<col && r.getXPos()>=x2){ //jeśli ten sam rząd i r po lewej od rec
                                x2 = r.getXPos() + 1
                            }
                            if(!r.isDeleted() && r.getYPos()==row && r.getXPos()>col && r.getXPos()<=x3){ //szuka po lewej najbliższego takiego samego
                                if(r.value==rectangle.value) {
                                    x3 = r.getXPos()
                                    check = true
                                }
                                else blockPos = r.getXPos()
                            }
                        }
                        rectangle.move(x2-col,0)
                        if(x2-col != 0) checkMove = true
                        if(check  && x3<=blockPos){
                            for(r in rectangles) {//usuwanie po prawej i podwajanie
                                if(!r.isDeleted() && r.getYPos()==row && r.getXPos()==x3) {
                                    r.delete()
                                    score += rectangle.double()
                                    checkMove = true
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun onTop() {
        for(col in 0..3) {
            var row = -1
            repeat(4){
                row++
                for(rectangle in rectangles){
                    var x2 = 0
                    var x3 = 3
                    var check = false
                    var blockPos = 3
                    if(!rectangle.isDeleted() && rectangle.getXPos()==col && rectangle.getYPos()==row) {
                        for(r in rectangles){
                            if(!r.isDeleted() && r.getXPos()==col && r.getYPos()<row && r.getYPos()>=x2){
                                x2 = r.getYPos() + 1
                            }
                            if(!r.isDeleted() && r.getXPos()==col && r.getYPos()>row && r.getYPos()<=x3){
                                if(r.value==rectangle.value) {
                                    x3 = r.getYPos()
                                    check = true
                                }
                                else blockPos = r.getYPos()
                            }
                        }
                        rectangle.move(0, x2-row)
                        if(x2-row != 0) checkMove = true
                        if(check && x3<=blockPos){
                            for(r in rectangles) {//usuwanie po prawej i podwajanie
                                if(!r.isDeleted() && r.getXPos()==col && r.getYPos()==x3) {
                                    r.delete()
                                    score += rectangle.double()
                                    checkMove = true
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun onBottom() {
        for(col in 0..3) {
            var row = 4
            repeat(4){
                row--
                for(rectangle in rectangles){
                    var x2 = 3
                    var x3 = 0
                    var check = false
                    var blockPos = 0
                    if(!rectangle.isDeleted() && rectangle.getXPos()==col && rectangle.getYPos()==row) {
                        for(r in rectangles){
                            if(!r.isDeleted() && r.getXPos()==col && r.getYPos()>row && r.getYPos()<=x2){
                                x2 = r.getYPos() - 1
                            }
                            if(!r.isDeleted() && r.getXPos()==col && r.getYPos()<row && r.getYPos()>=x3){
                                if(r.value==rectangle.value) {
                                x3 = r.getYPos()
                                check = true}
                                else blockPos = r.getYPos()
                            }
                        }
                        rectangle.move(0, x2-row)
                        if(x2-row != 0) checkMove = true
                        if(check && x3>=blockPos){
                            for(r in rectangles) {
                                if(!r.isDeleted() && r.getXPos()==col && r.getYPos()==x3) {
                                    r.delete()
                                    score += rectangle.double()
                                    checkMove = true
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun isGameOver(): Boolean {
        for (row in 0..3) {//od góry do dołu
            var col = -1
            repeat(4) {//od prawej kolumny do lewej
                col++
                for (rectangle in rectangles) { //pętla po kwadratach
                    if (!rectangle.isDeleted() && rectangle.getXPos() == col && rectangle.getYPos() == row) { //znajduje wybrany istniejact kwadrat
                        for (r in rectangles) {
                            if ( r.value == rectangle.value && (r.getYPos() == row + 1 || r.getYPos() == row - 1 || r.getXPos() == col + 1 || r.getXPos() == col - 1 )) { //jeśli jest obok jakiś kwadrat z taką samą wartością to gra toczy się dalej
                                return false
                            }
                        }
                    }
                }
            }
        }
        return true
    }

    private fun gameOverDialog() {
        dialog?.setContentView(R.layout.game_over_layout)
        dialog?.show()
        scoreTextOnDialog = dialog?.findViewById<TextView>(R.id.textScore)
        editName = dialog?.findViewById<EditText>(R.id.editName)
        scoreTextOnDialog?.setText(score.toString())
        val saveButton = dialog?.findViewById(R.id.saveButton) as Button

        saveButton.setOnClickListener {
            var name = editName?.getText().toString()
            databaseRef.child(name).setValue(score).addOnCompleteListener {      //add record to the database
                 Toast.makeText(getApplicationContext(), "High score saved", Toast.LENGTH_SHORT).show() //notification
            }
        }

        val vieButton = dialog?.findViewById(R.id.vieButton) as Button
        vieButton.setOnClickListener {
            dialog?.dismiss()
            startActivity(Intent(getApplicationContext(), ShowLeaderboard::class.java))
        }

        val newGameButton = dialog?.findViewById(R.id.newGameButton) as Button
        newGameButton.setOnClickListener {
            dialog?.dismiss()
            restart()
        }
    }

    private fun restart() {
        val intent = intent
        finish()
        startActivity(intent)
    }


    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
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