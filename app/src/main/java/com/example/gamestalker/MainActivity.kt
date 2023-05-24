package com.example.gamestalker

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.gamestalker.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        changeFragment(Games())

        firebaseAuth = FirebaseAuth.getInstance()

        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.games -> changeFragment(Games())
                R.id.profile -> if (firebaseAuth.currentUser != null)
                                    changeFragment(ProfileAfterSignIn())
                                else
                                    changeFragment(Profile())
                R.id.reviews -> changeFragment(Reviews())
                else -> {

                }
            }
            true
        }


        // Adding the games list only once in the database
        // saved a boolean in the shared preferences so i can add the game list only once to the database
        val sharedPreferences = getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("areGamesAdded", false)
        editor.apply()

        val areGamesAdded = sharedPreferences.getBoolean("areGamesAdded", false)

        if (!areGamesAdded) {
            val gamesReference = FirebaseDatabase.getInstance().getReference("Games")

            val games = mutableListOf(
                GameData("Team Fortress 2", R.drawable.tf2, link = "https://store.steampowered.com/app/440/Team_Fortress_2/", videoLink = "https://www.youtube.com/watch?v=C4cfo0f88Ug", id = 0, reviews = mutableListOf()),
                GameData("The Last of Us Part I", R.drawable.the_last_of_us, link = "https://store.steampowered.com/app/1888930/The_Last_of_Us_Part_I/", videoLink = "https://www.youtube.com/watch?v=WxjeV10H1F0", id = 1, reviews = mutableListOf()),
                GameData("CS:GO", R.drawable.csgo, link = "https://store.steampowered.com/app/730/CounterStrike_Global_Offensive/", videoLink = "https://www.youtube.com/watch?v=edYCtaNueQY&t=102s", id = 2, reviews = mutableListOf()),
                GameData("FIFA 2022", R.drawable.fifa22, link = "https://www.ea.com/games/fifa/fifa-22", videoLink = "https://www.youtube.com/watch?v=o1igaMv46SY", id = 3, reviews = mutableListOf()),
                GameData("Battlefield 1", R.drawable.bf1, link = "https://www.ea.com/games/battlefield/battlefield-1", videoLink = "https://www.youtube.com/watch?v=c7nRTF2SowQ", id = 4, reviews = mutableListOf()),
                GameData("GTA V", R.drawable.gtav, link = "https://www.rockstargames.com/gta-v", videoLink = "https://www.youtube.com/watch?v=QkkoHAzjnUs", id = 5, reviews = mutableListOf()),
                GameData("Overwatch 2", R.drawable.overwatch2, link = "https://overwatch.blizzard.com/en-us/", videoLink = "https://www.youtube.com/watch?v=GKXS_YA9s7E", id = 6, reviews = mutableListOf()),
                GameData("League of Legends", R.drawable.lol, link = "https://store.epicgames.com/en-US/p/league-of-legends", videoLink = "https://www.youtube.com/watch?v=aR-KAldshAE", id = 7, reviews = mutableListOf()),
                GameData("DOTA 2", R.drawable.dota2, link = "https://store.steampowered.com/app/570/Dota_2/", videoLink = "https://www.youtube.com/watch?v=-cSFPIwMEq4", id = 8, reviews = mutableListOf()),
                GameData("Rainbow Six Siege", R.drawable.rainbow_six_siege, link = "https://store.steampowered.com/app/359550/Tom_Clancys_Rainbow_Six_Siege/", videoLink = "https://www.youtube.com/watch?v=6wlvYh0h63k", id = 9, reviews = mutableListOf()),
                GameData("Red Dead Redemption 2", R.drawable.rdr2, link = "https://store.steampowered.com/app/1174180/Red_Dead_Redemption_2/", videoLink = "https://www.youtube.com/watch?v=gmA6MrX81z4", id = 10, reviews = mutableListOf())
            )

            for (game in games) {
                gamesReference.child(game.title.toString()).setValue(game)
            }

            sharedPreferences.edit().putBoolean("areGamesAdded", true).apply()
        }
    }

    private fun changeFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
    }
}