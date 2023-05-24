package com.example.gamestalker

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ReviewAdapter(private var reviewList: List<ReviewData>) :
    RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    inner class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val logo: ImageView = itemView.findViewById(R.id.logoIv)
        val titleTv: TextView = itemView.findViewById(R.id.titleTv)
        val reviewText: TextView = itemView.findViewById(R.id.reviewText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.review_list_item, parent, false)
        return ReviewViewHolder(view)
    }

    private val databaseGamesReference = FirebaseDatabase.getInstance().getReference("Games")

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.reviewText.text = reviewList[position].text

        val review = reviewList[position]

        databaseGamesReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (gameSnapshot in snapshot.children) {
                        val game = gameSnapshot.getValue(GameData::class.java)
                        if (game?.id == review.gameId) {
                            holder.titleTv.text = game?.title
                            holder.logo.setImageResource(game?.logo!!)
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("error", "Error")
            }
        })
    }

    override fun getItemCount(): Int {
        return reviewList.size
    }
}