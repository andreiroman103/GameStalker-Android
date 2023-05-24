package com.example.gamestalker

import android.animation.ObjectAnimator
import android.animation.TimeInterpolator
import android.content.Context
import android.content.Intent
import android.util.Log
import android.util.Property
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth

class GameAdapter(private var gameList: List<GameData>, private val context : Context) :
    RecyclerView.Adapter<GameAdapter.GameViewHolder>() {


    inner class GameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val logo: ImageView = itemView.findViewById(R.id.logoIv)
        val titleTv: TextView = itemView.findViewById(R.id.titleTv)
        val nrOfReviews : TextView = itemView.findViewById(R.id.nrOfReviewsTv)
        val addReviewButton : Button = itemView.findViewById(R.id.addReviewButton)
        val link : TextView = itemView.findViewById(R.id.link)
        val expandedLayout : ConstraintLayout = itemView.findViewById(R.id.expandedLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.game_list_item, parent, false)
        return GameViewHolder(view)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        holder.logo.setImageResource(gameList[position].logo)
        holder.titleTv.text = gameList[position].title
        holder.nrOfReviews.text = gameList[position].reviews.size.toString() + " reviews"
        holder.link.text = gameList[position].link

        // changing the visibility of the items from the extended view of the items in the recycler view
        val isVisible : Boolean = gameList[position].visibility
        holder.nrOfReviews.visibility = if (isVisible) View.VISIBLE else View.GONE
        holder.addReviewButton.visibility = if (isVisible) View.VISIBLE else View.GONE
        holder.link.visibility = if (isVisible) View.VISIBLE else View.GONE

        // showing/hiding the extended view when clicking the layout and showing the animation of the logo
        holder.expandedLayout.setOnClickListener {
            gameList[position].visibility = !gameList[position].visibility
            if (gameList[position].visibility) {
                animate(holder.logo, ImageView.ROTATION, 0f, 360f, 500, LinearInterpolator())
            }
            else {
                animate(holder.logo, ImageView.ROTATION, 360f, 0f, 500, LinearInterpolator())
            }
            notifyItemChanged(position)
        }

        // Starting the AddReview activity
        val firebaseAuth = FirebaseAuth.getInstance()
        holder.addReviewButton.setOnClickListener {
            if (firebaseAuth.currentUser != null) {
                val intent = Intent(context, AddReviewActivity::class.java)
                intent.putExtra("id", gameList[position].id.toString())
                intent.putExtra("title", gameList[position].title)
                context.startActivity(intent)
            }
            else {
                Toast.makeText(context, "Cannot add review without signing in!", Toast.LENGTH_SHORT).show()
            }
        }

        // Starting the activity that shows the reviews of the game that was clicked
        holder.nrOfReviews.setOnClickListener {
            val intent = Intent(context, GameReviewsActivity::class.java)
            intent.putExtra("gameId", gameList[position].id.toString())
            intent.putExtra("nrOfReviews", gameList[position].reviews.size.toString())
            context.startActivity(intent)
        }


        // Share intent for sharing the game link
        val link = holder.link.text

        holder.link.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, link)
            val chooser = Intent.createChooser(intent, "Share using...")
            startActivity(context, chooser, null)
        }

        holder.logo.setOnClickListener {
            val link = gameList[position].videoLink
            if (link != null) {
                playYoutubeVideo(link = link)
            }
        }
    }

    override fun getItemCount(): Int {
        return gameList.size
    }

    fun setFilteredList(gameList: List<GameData>) {
        this.gameList = gameList
        notifyDataSetChanged()
    }

    // function that animates the logo image of the game using ObjectAnimator
    private fun animate (
        target: ImageView,
        property: Property<View, Float>,
        from: Float,
        to: Float,
        duration: Long,
        interpolator: TimeInterpolator) {
        val animator = ObjectAnimator.ofFloat(target, property, from, to)
        animator.duration = duration
        animator.interpolator = interpolator
        animator.start()
    }

    private fun playYoutubeVideo(link: String) {
        val intent = Intent(context, VideoPlayerActivity::class.java)
        intent.putExtra("videoLink", link)
        context.startActivity(intent)
    }

    // function that updates the number of reviews shown in the recycler view
    fun updateReviews(gameId: Int, nrOfReviews: Int) {
        val game = gameList.find { it.id == gameId }
        game?.nrOfReviews = nrOfReviews.toString()
        val position = gameList.indexOf(game)
        if (position != -1) {
            notifyItemChanged(position)
        }
    }

}