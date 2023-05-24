package com.example.gamestalker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import com.example.gamestalker.databinding.ActivityAddReviewBinding
import com.example.gamestalker.databinding.ActivityVideoPlayerBinding
import java.util.regex.Pattern

class VideoPlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVideoPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityVideoPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val webView = findViewById<WebView>(R.id.webView)
        webView.settings.javaScriptEnabled = true

        val url = intent.getStringExtra("videoLink").toString()
        val embedLink = "https://www.youtube.com/embed/${extractVideoIdFromUrl(url)}"
        val html = "<iframe width=\"100%\" height=\"100%\" src=\"$embedLink\" frameborder=\"0\" allowfullscreen></iframe>"
        webView.loadData(html, "text/html", "utf-8")
    }

    fun extractVideoIdFromUrl(url: String): String? {
        val pattern = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%\u200C\u200B2F|youtu.be%2F|%2Fv%2F)[^#\\&\\?\\n]*"
        val compiledPattern = Pattern.compile(pattern)
        val matcher = compiledPattern.matcher(url) //url is youtube url for which you want to extract video id.
        return if (matcher.find()) {
            matcher.group()
        } else {
            null
        }
    }
}