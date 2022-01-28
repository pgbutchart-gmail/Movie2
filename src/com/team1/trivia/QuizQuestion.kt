package com.team1.trivia

import com.team1.trivia.QueryBuilder
import com.team1.trivia.DatabaseAdapter
import com.team1.trivia.TriviaActivity
import SharedPreferences.Editor
import com.team1.trivia.GameActivity
import com.team1.trivia.QuizQuestion
import com.team1.trivia.ScoringActivity
import java.io.File
import java.io.BufferedOutputStream
import java.io.FileOutputStream
import com.team1.trivia.OpeningActivity
import java.lang.StringBuffer
import kotlin.Throws
import java.io.IOException
import java.util.ArrayList

// Model to contain Quiz Question data
class QuizQuestion(
    _id: ArrayList<Int?>?, phrase: ArrayList<String?>?,
    title: MutableList<String?>?
) {
    private var _id: MutableList<Int?>?
    private var phrase: MutableList<String?>?
    private var title: MutableList<String?>?

    init {
        this._id = _id
        this.phrase = phrase
        this.title = title
    }

    fun get_id(): MutableList<Int?>? {
        return _id
    }

    fun set_id(id: MutableList<Int?>?) {
        _id = id
    }

    fun getPhrase(): MutableList<String?>? {
        return phrase
    }

    fun setPhrase(phrase: MutableList<String?>?) {
        this.phrase = phrase
    }

    fun getTitle(): MutableList<String?>? {
        return title
    }

    fun setTitle(title: MutableList<String?>?) {
        this.title = title
    }
}