package com.example.gamestalker

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Reviews.newInstance] factory method to
 * create an instance of this fragment.
 */
class Reviews : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var recyclerView: RecyclerView
    private lateinit var noReviewFound: TextView
    private lateinit var myReviews: TextView
    private var reviewList = ArrayList<ReviewData>()
    private lateinit var adapter: ReviewAdapter
    private lateinit var databaseReviewsReference: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reviews, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Friends.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Reviews().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        noReviewFound = view.findViewById(R.id.textNoReviews)
        myReviews = view.findViewById(R.id.myReviews)

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)

        reviewList = arrayListOf()
        getReviewData()
    }


    private fun getReviewData() {
        adapter = ReviewAdapter(reviewList)

        databaseReviewsReference = FirebaseDatabase.getInstance().getReference("Reviews")

        databaseReviewsReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (reviewSnapshot in snapshot.children) {
                        val retrievedReview = reviewSnapshot.getValue(ReviewData::class.java)

                        val firebaseAuth = FirebaseAuth.getInstance()
                        if (retrievedReview?.userID == firebaseAuth.currentUser?.uid) {
                            reviewList.add(retrievedReview!!)
                        }
                    }
                    val firebaseAuth = FirebaseAuth.getInstance()

                    // Showing different messages if there are no reviews found or if the user is not logged in
                    if (firebaseAuth.currentUser == null) {
                        noReviewFound.text = "Cannot see reviews without logging in!"
                        recyclerView.visibility = View.GONE
                        myReviews.visibility = View.GONE
                        noReviewFound.visibility = View.VISIBLE
                    }
                    else if (reviewList.isEmpty()) {
                        noReviewFound.text = "No reviews to show"
                        noReviewFound.visibility = View.VISIBLE
                    }
                    else {
                        noReviewFound.visibility = View.GONE
                    }
                    recyclerView.adapter = adapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Failed retrieving review!", Toast.LENGTH_SHORT).show()
            }
        })
    }
}