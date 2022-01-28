package com.team1.trivia

import java.util.*

class QueryBuilder : TriviaActivity() {
    private var dbCount: Int? = 0
    fun getQuestions(): String? {
        dbCount = DatabaseAdapter.Companion.getCount()

        // First we get a set of random numbers
        randomize(dbCount)
        val cats = categories()
        qry = assembleQuery(cats)
        return qry
    }

    // Set up the WHERE clause for the categories
    fun categories(): String? {
        var catFilter = ""
        for (s in OpeningActivity.Companion.cat!!) {
            if (s !== "Select All") {    // If the user selects 'Select All' we don't need to build
                // our WHERE clause.
                for (i in 1..3) {
                    catFilter = if (catFilter === "") {
                        "genre_$i=\"$s\""
                    } else {
                        "genre_$i=\"$s\" OR $catFilter"
                    }
                }
            } else {                    // so we make sure the filter is empty and exit the for loop.
                catFilter = ""
                break
            }
        }
        if (catFilter !== "") {
            catFilter = "$catFilter);"
        }
        return catFilter
    }

    protected fun assembleQuery(cats: String?): String? {
        val sb = StringBuffer()
        val sqry = "SELECT phrase._id, phrase, title FROM phrase, movie WHERE ("
        var rn = "" // Initialize a String for the random numbers
        sb.append(sqry)

        // Assemble the clause for the 6 random numbers
        for (i in numbers!!) {
            rn = if (rn === "") {
                "phrase._id=" + i.toString()
            } else {
                rn + " OR phrase._id=" + i.toString()
            }
        }
        rn = "$rn)"
        sb.append(rn)

        // Add the categories if any are selected
        if (cats !== "") {
            sb.append(" AND ($cats")
        }
        sb.append("AND movie._id=phrase.movieID;")
        return sb.toString()
    }

    // Create an array with random numbers 
    fun randomize(count: Int?) {
        for (i in 0..5) {
            // Add a random number between 0 and the number of rows in the Phrase table.
            val randomNumber = Random()
            numbers?.add(count?.let { randomNumber.nextInt(it) })
        }
    }

    companion object {
        var qry: String? = null

        // Create a List to hold the random numbers
        protected var numbers: MutableList<Int?>? = ArrayList()
    }
}