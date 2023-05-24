package com.example.gamestalker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gamestalker.databinding.ActivityGameReviewsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class GameReviewsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var noReviewFound: TextView
    private lateinit var myReviews: TextView
    private var reviewList = ArrayList<ReviewData>()
    private lateinit var adapter: GameReviewAdapter
    private lateinit var databaseReviewsReference: DatabaseReference
    private lateinit var binding: ActivityGameReviewsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGameReviewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val nrOfReviews = intent.getStringExtra("nrOfReviews")?.toInt()

        recyclerView = binding.recyclerView
        noReviewFound = binding.textNoReviews
        myReviews = binding.myReviews

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)

        reviewList = arrayListOf()
        getReviewData()

        if (nrOfReviews != 0) {
            noReviewFound.visibility = View.GONE
        }
        else {
            noReviewFound.visibility = View.VISIBLE
        }
    }

    private fun getReviewData() {
        val gameId = intent.getStringExtra("gameId").toString()
        adapter = GameReviewAdapter(reviewList)

        databaseReviewsReference = FirebaseDatabase.getInstance().getReference("Reviews")

        databaseReviewsReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (reviewSnapshot in snapshot.children) {
                        val retrievedReview = reviewSnapshot.getValue(ReviewData::class.java)

                        if (retrievedReview?.gameId == gameId.toInt()) {
                            reviewList.add(retrievedReview)
                        }
                    }
                    recyclerView.adapter = adapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@GameReviewsActivity, "Failed retrieving review!", Toast.LENGTH_SHORT).show()
            }
        })
    }
}