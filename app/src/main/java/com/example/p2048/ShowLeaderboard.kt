package com.example.p2048

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue

class ShowLeaderboard : AppCompatActivity() {
    val database = FirebaseDatabase.getInstance("https://p2048-9b74d-default-rtdb.europe-west1.firebasedatabase.app/")
    val databaseRef = database.getReference().child("Users").orderByValue().limitToLast(5)//ascending sort


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_leaderboard)

        var names: MutableList<TextView> = mutableListOf()
        var scores: MutableList<TextView> = mutableListOf()

        //reversed sequence to get descending order
        names.add(findViewById<TextView>(R.id.name5))
        names.add(findViewById<TextView>(R.id.name4))
        names.add(findViewById<TextView>(R.id.name3))
        names.add(findViewById<TextView>(R.id.name2))
        names.add(findViewById<TextView>(R.id.name1))
        scores.add(findViewById<TextView>(R.id.score5))
        scores.add(findViewById<TextView>(R.id.score4))
        scores.add(findViewById<TextView>(R.id.score3))
        scores.add(findViewById<TextView>(R.id.score2))
        scores.add(findViewById<TextView>(R.id.score1))

        // Read from the database
        databaseRef.addValueEventListener(object: ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                var i = 0
                for (dataSnapshot in snapshot.children) {
                    var key : String? = dataSnapshot.key
                    names[i].setText(key)
                    scores[i].setText(dataSnapshot.getValue().toString())
                    if (i == 4) break
                    i++
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(getApplicationContext(), "Failed to read data from database", Toast.LENGTH_SHORT).show() //notification
            }
        })

    }
}