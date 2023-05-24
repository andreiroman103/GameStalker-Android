package com.example.gamestalker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.gamestalker.databinding.ActivityAddReviewBinding
import com.example.gamestalker.databinding.FragmentProfileAfterSignInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue

class AddReviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddReviewBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var reviewDatabase: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance().getReference("Games")
        firebaseAuth = FirebaseAuth.getInstance()

        val gameId = intent.getStringExtra("id").toString()
        val gameTitle = intent.getStringExtra("title")

        // Retrieving the game data for the image and title
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (gameSnapshot in snapshot.children) {
                        val game = gameSnapshot.getValue(GameData::class.java)
                        if (game?.id.toString() == gameId) {
                            if (game != null) {
                                binding.logoIv.setImageResource(game.logo)
                                binding.titleTv.text = game.title
                            }
                            else {
                                Toast.makeText(this@AddReviewActivity, "Game does not exist", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AddReviewActivity, "Failed retrieving game!", Toast.LENGTH_SHORT).show()
            }
        })

        // Adding the review in the database
        binding.button.setOnClickListener {
            val reviewText = binding.reviewEt.text.toString()

            reviewDatabase = FirebaseDatabase.getInstance().getReference("Reviews")

            if (reviewText.isNotEmpty()) {
                val newReview = reviewDatabase.push()
                val id = newReview.key
                val review = ReviewData(id, firebaseAuth.currentUser!!.uid, reviewText, gameId = gameId.toInt())
                reviewDatabase.child(id.toString()).setValue(review)
                    .addOnSuccessListener {
                        binding.reviewEt.text?.clear()
                        Toast.makeText(this, "Review added!", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener() {
                    Toast.makeText(this, "Failed adding review!", Toast.LENGTH_SHORT).show()
                }

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            else {
                Toast.makeText(this, "Review cannot be empty!", Toast.LENGTH_SHORT).show()
            }

        }

    }

}