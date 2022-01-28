package com.team1.trivia

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