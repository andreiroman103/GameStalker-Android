package com.example.gamestalker

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Games.newInstance] factory method to
 * create an instance of this fragment.
 */
class Games : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var recyclerView: RecyclerView
    private lateinit var noGameFound: TextView
    private lateinit var searchView: androidx.appcompat.widget.SearchView
    private var gameList = ArrayList<GameData>()
    private lateinit var adapter: GameAdapter
    private lateinit var databaseReviewsReference: DatabaseReference
    private lateinit var databaseGamesReference: DatabaseReference

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
        return inflater.inflate(R.layout.fragment_games, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Games.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Games().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        noGameFound = view.findViewById(R.id.textNoResult)
        searchView = view.findViewById(R.id.searchView)

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)


        gameList = arrayListOf()
        getGameData()

        for (i in gameList) {
            Log.d("games", gameList.size.toString())
        }

        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }

        })
    }

    // Searching a game in the list and showing a message if nothing is found
    private fun filterList(query: String?) {
        if (query != null) {
            val filteredList = ArrayList<GameData>()
            for (i in gameList) {
                if (i.title?.lowercase(Locale.ROOT)?.contains(query.lowercase()) == true) {
                    filteredList.add(i)
                }
            }

            if (filteredList.isEmpty()) {
                recyclerView.visibility = View.GONE
                noGameFound.visibility = View.VISIBLE
            }
            else {
                recyclerView.visibility = View.VISIBLE
                noGameFound.visibility = View.GONE
                adapter.setFilteredList(filteredList)
            }
        }
    }

    // retrieving the game data from database
    private fun getGameData() {
        adapter = GameAdapter(gameList, requireActivity())

        databaseGamesReference = FirebaseDatabase.getInstance().getReference("Games")

        databaseGamesReference.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.exists()) {
                for (gameSnapshot in snapshot.children) {
                    val retrievedGame = gameSnapshot.getValue(GameData::class.java)

                    // Adding the reviews to the game's reviews list
                    databaseReviewsReference = FirebaseDatabase.getInstance().getReference("Reviews")
                    databaseReviewsReference.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(reviewsSnapshot: DataSnapshot) {
                            if (reviewsSnapshot.exists()) {
                                for (reviewSnapshot in reviewsSnapshot.children) {
                                    val review = reviewSnapshot.getValue(ReviewData::class.java)

                                    if (review?.gameId == retrievedGame?.id) {
                                        retrievedGame?.reviews?.add(review!!)
                                    }
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(context, "Failed adding review to the game's reviews list!", Toast.LENGTH_SHORT).show()
                        }
                    })

                    gameList.add(retrievedGame!!)
                    adapter.updateReviews(retrievedGame.id, retrievedGame.reviews.size)
                }

                recyclerView.adapter = adapter
            }
        }

        override fun onCancelled(error: DatabaseError) {
            Toast.makeText(context, "Failed retrieving game!", Toast.LENGTH_SHORT).show()
        }
        })
    }
}