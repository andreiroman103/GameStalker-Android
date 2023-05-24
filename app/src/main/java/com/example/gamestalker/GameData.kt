package com.example.gamestalker

data class GameData(val title: String? = null,
                    val logo: Int,
                    val reviews : MutableList<ReviewData> = mutableListOf(),
                    var nrOfReviews : String? = null,
                    var visibility : Boolean = false,
                    val link: String? = null,
                    val videoLink: String? = null,
                    var id: Int = -1) {

    constructor() : this(null, 0, reviews = mutableListOf<ReviewData>(), null, false)
}
