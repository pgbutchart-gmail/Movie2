package com.team1.trivia

import android.app.AlertDialog
import java.util.ArrayList

class OpeningActivity : TriviaActivity(), OnClickListener {
    var mPrefs: SharedPreferences? = null

    /** Called when the activity is first created.  */
    fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.opening)

        // Activate check FirstRun
        firstRunPreferences()

        // Set up button for OnClickListener
        val startButton: Button? = findViewById(R.id.startGameButton) as Button?
        val categoriesButton: Button? = findViewById(R.id.categoriesButton) as Button?
        val optionsButton: Button? = findViewById(R.id.optionsButton) as Button?
        val modeButton: Button? = findViewById(R.id.modeButton) as Button?
        val instructionButton: Button? = findViewById(R.id.instructionButton) as Button?

        // Waits for one of the buttons to be clicked
        startButton.setOnClickListener(this)
        categoriesButton.setOnClickListener(this)
        optionsButton.setOnClickListener(this)
        modeButton.setOnClickListener(this)
        instructionButton.setOnClickListener(this)

        // Create a background gradient
        val grad = GradientDrawable(Orientation.TOP_BOTTOM, intArrayOf(Color.WHITE, Color.BLUE))
        this.getWindow().setBackgroundDrawable(grad)
    } // End onCreate

    /**
     * setting up preferences storage
     */
    fun firstRunPreferences() {
        val mContext: Context = this.getApplicationContext()
        mPrefs = mContext.getSharedPreferences(TriviaActivity.Companion.GAME_PREFERENCES, MODE_PRIVATE)
        // 0 = mode private: only this app can read these preferences
    }

    // This method tells the app what to do when a button is clicked
    fun onClick(v: View?) {
        when (v.getId()) {
            R.id.startGameButton -> {
                var playerName = ""

                // Instantiate the Shared Preferences class
                val sp: SharedPreferences =
                    getSharedPreferences(TriviaActivity.Companion.GAME_PREFERENCES, MODE_PRIVATE)
                val ed: Editor = sp.edit()

                // Retrieve the player's name
                val playerNameEditText: EditText? = findViewById(R.id.playerNameEditText) as EditText?
                playerName = playerNameEditText.getText().toString()
                if (playerName != "") {
                    ed.putString(TriviaActivity.Companion.GAME_PREFERENCES_PLAYER, playerName)
                } else {
                    playerName = "Player 1"
                    ed.putString(TriviaActivity.Companion.GAME_PREFERENCES_PLAYER, playerName)
                }
                ed.commit()
                val inflater: LayoutInflater = getLayoutInflater()
                val layout: View = inflater.inflate(
                    R.layout.toast_layout,
                    findViewById(R.id.toast_layout_root) as ViewGroup?
                )
                val image: ImageView = layout.findViewById(R.id.image) as ImageView
                image.setImageResource(R.drawable.sm_logo)
                val text: TextView = layout.findViewById(R.id.text) as TextView
                text.setText(
                    "Get ready to play, " + sp.getString(
                        TriviaActivity.Companion.GAME_PREFERENCES_PLAYER,
                        "Player 1"
                    )
                )
                val toast = Toast(getApplicationContext())
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
                toast.setDuration(Toast.LENGTH_LONG)
                toast.setView(layout)
                toast.show()

                // Call the Game Activity and close the Opening Activity
                startActivity(
                    Intent(
                        this@OpeningActivity,
                        GameActivity::class.java
                    )
                )
                this@OpeningActivity.finish()
            }
            R.id.categoriesButton -> showDialog(DIALOG_CATEGORY_ID)
            R.id.optionsButton -> showDialog(DIALOG_OPTIONS_ID)
            R.id.modeButton -> showDialog(DIALOG_MODE_ID)
            R.id.instructionButton -> showDialog(DIALOG_INSTRUCTIONS_ID)
        }
    } // End onClick

    // This method tells the app the details of what to do to create the dialogs
    protected fun onCreateDialog(id: Int): Dialog? {
        when (id) {
            0, 1 -> {
                val categories = arrayOf<CharSequence?>( // Put the categories here so they are easier to update
                    "Select All", "Action", "Adventure", "Drama",
                    "Westerns", "Mystery", "Horror",
                    "Sci-Fi"
                )
                val categoryStates = booleanArrayOf(
                    false, false, false,
                    false, false, false, false, false
                )

                // Build the dialog
                return Builder(this)
                    .setTitle("Select Categories:")
                    .setCancelable(false)
                    .setPositiveButton("OK", object : OnClickListener() {
                        fun onClick(dialog: DialogInterface?, which: Int) {
                            var i = 0
                            while (i < categories.size) {
                                if (categoryStates[i]) {
                                    cat.add(categories[i] as String?)
                                }
                                i++
                            }
                        } // End onClick method
                    }) // End setPositiveButton
                    // Set up the Category list with checkboxes
                    .setMultiChoiceItems(categories, categoryStates, object : OnMultiChoiceClickListener() {
                        fun onClick(dialog: DialogInterface?, which: Int, isChecked: Boolean) {
                            if (which == 0) {  // If Select All is clicked, check all checkboxes
                                var i = 1
                                while (i < categories.size) {
                                    categoryStates[i] = true
                                    i++
                                }
                            } // End if
                        } // End onClick
                    }) // End setMultipleChoiceItems
                    .create()
            }
            2 -> {
                val options = arrayOf<CharSequence?>("Easy (3 choices)", "Medium (6 choices)", "Hard (9 choices)")

                // Build the options dialog
                return Builder(this)
                    .setTitle("Select Difficulty:")
                    .setCancelable(false)
                    .setPositiveButton("OK", object : OnClickListener() {
                        fun onClick(dialog: DialogInterface?, which: Int) {
                            // Temporary Toast to show that the onClick method works
                            Toast.makeText(
                                getBaseContext(),
                                "Maximum number of answers " + maxAnswers,
                                Toast.LENGTH_LONG
                            ).show()
                        } // End onClick
                    }) // End setPositiveButton
                    // Set up the radio buttons
                    .setSingleChoiceItems(options, -1, object : OnClickListener() {
                        fun onClick(dialog: DialogInterface?, item: Int) {
                            when (item) {
                                0 -> maxAnswers = 3
                                1 -> maxAnswers = 6
                                2 -> maxAnswers = 9
                            }

                            // Instantiate the Shared Preferences class
                            val sp: SharedPreferences =
                                getSharedPreferences(TriviaActivity.Companion.GAME_PREFERENCES, MODE_PRIVATE)
                            val ed: Editor = sp.edit()
                            ed.putInt(TriviaActivity.Companion.GAME_PREFERENCES_DIFFICULTY, maxAnswers)
                            ed.commit()
                        } // End onClick
                    }) // End setSingleChoiceItems
                    .create()
            }
            3 -> {
                val modes = arrayOf<CharSequence?>("Limited", "Unlimited")
                val modeStates = booleanArrayOf(true, false)

                // Build the dialog
                return Builder(this)
                    .setTitle("Select Game Mode")
                    .setPositiveButton("OK", object : OnClickListener() {
                        fun onClick(dialog: DialogInterface?, which: Int) {
                            var i = 0
                            while (i < modes.size) {
                                if (modeStates[i]) {
                                    // Instantiate the Shared Preferences class
                                    val sp: SharedPreferences =
                                        getSharedPreferences(TriviaActivity.Companion.GAME_PREFERENCES, MODE_PRIVATE)
                                    val ed: Editor = sp.edit()
                                    ed.putString(TriviaActivity.Companion.GAME_PREFERENCES_MODE, modes[i].toString())
                                    ed.commit()
                                }
                                i++
                            }
                        }
                    }) // Set up the radio buttons
                    .setSingleChoiceItems(modes, -1, object : OnClickListener() {
                        fun onClick(dialog: DialogInterface?, item: Int) {
                            Toast.makeText(getApplicationContext(), modes[item], Toast.LENGTH_SHORT).show()
                        }
                    })
                    .create()
            }
            4 -> {
                val instructionDialog = Builder(this)
                val instructionInflater: LayoutInflater? =
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?

                // Uses a pre-constructed layout instead of an AlertDialog
                val instructions: View = instructionInflater.inflate(R.layout.instructions_dialog, null)
                instructionDialog.setCancelable(true)
                instructionDialog.setPositiveButton(R.string.ok, object : OnClickListener() {
                    fun onClick(dialog: DialogInterface?, which: Int) {
                        // Do nothing except close the dialog
                    } // end method onClick
                } // end DialogInterface
                ) // end call to dialogBuilder.setPositiveButton
                instructionDialog.setView(instructions)
                instructionDialog.show()
                return null
            }
        }
        return null
    } // End onCreateDialog method

    companion object {
        const val DIALOG_START_ID = 0
        const val DIALOG_CATEGORY_ID = 1
        const val DIALOG_OPTIONS_ID = 2
        const val DIALOG_MODE_ID = 3
        const val DIALOG_INSTRUCTIONS_ID = 4
        protected var maxAnswers = 3
        var cat: MutableList<String?>? = ArrayList()
    }
} // End OpeningActivity class
