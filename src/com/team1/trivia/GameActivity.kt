package com.team1.trivia

import sun.jvm.hotspot.ui.Editor
import java.io.InputStream
import java.util.*
import javax.swing.text.html.ListView

class GameActivity : ListActivity() {
    private val titleAdapter: CursorAdapter? = null
    private var myDb: SQLiteDatabase? = null
    private val qb: QueryBuilder? = QueryBuilder()
    private var titleListView: ListView? = null
    var aDatabaseAdapter: DatabaseAdapter? = null
    var mPrefs: SharedPreferences? = null

    // Check to see if this is the first time the application has been run
    fun getFirstRun(): Boolean {
        mPrefs = this.getSharedPreferences(
            TriviaActivity.Companion.GAME_PREFERENCES, MODE_PRIVATE
        )
        return mPrefs.getBoolean("firstRun", true)
    }

    // Set first run status
    fun setRunned() {
        val edit: Editor = mPrefs.edit()
        edit.putBoolean("firstRun", false)
        edit.commit()
    }

    // Called when the activity is first created.
    fun onCreate(savedInstanceState: Bundle?) {
        super(savedInstanceState)
        // copyDataBase();

        // Set view
        setContentView(R.layout.game)

        // Identify our List View
        titleListView = findViewById(android.R.id.list) as ListView?
        aDatabaseAdapter = DatabaseAdapter.Companion.getInstance(this, DB_NAME)

        // Create a background gradient
        val grad = GradientDrawable(Orientation.TOP_BOTTOM, intArrayOf(Color.WHITE, Color.BLUE))
        this.getWindow().setBackgroundDrawable(grad)
        if (getFirstRun()) {
            myDb = DatabaseAdapter.Companion.getDatabase()
            setRunned()
        } else {
            myDb = aDatabaseAdapter.getWritableDatabase()
        }

        // Return an ArrayList of QuizQuestion
        val qq = newGame()

        // Break out the individual arrays from the qq
        val id = qq.get_id()
        val phrase = qq.getPhrase()
        val title = qq.getTitle()
        val answer = SelectCorrectAnswer()
        val correctId = id[answer]
        val correctPhrase = phrase[answer]
        ScoringActivity.setCorrectId(correctId)
        val tvPhrase: TextView? = findViewById(R.id.phraseTextView) as TextView?
        tvPhrase.setText(correctPhrase)

        // Create an ArrayAdapter that puts the titles into the TextView field of our ListView
        val adapter: ArrayAdapter<String?> = ArrayAdapter<String?>(
            getApplicationContext(),
            R.layout.list_item, R.id.titleTextView, title
        )

        // Using the adapter
        (findViewById(android.R.id.list) as ListView?).setAdapter(adapter)

        // Set our item click listener for the ListView
        titleListView.setOnItemClickListener(titleListener)
    }

    private fun SelectCorrectAnswer(): Int {
        val maxTitles: Int = mPrefs.getInt("maxTitles", 6)
        // Add a random number between 0 and the number of rows in the Phrase table.
        val randomNumber = Random()
        return randomNumber.nextInt(maxTitles)
    }

    private fun copyDataBase() {
        DB_PATH = DB_PATH_PREFIX + DB_PACKAGE + DB_PATH_SUFFIX
        try {
            //Open the file in the assets directory as the input stream
            val am: AssetManager = getAssets()
            val myInput: InputStream
            myInput = am.open(DB_NAME)

            // Path to the just created empty db 
            val outFileName = DB_PATH + DB_NAME
            val myFile = File(outFileName)
            if (!myFile.exists()) {
                myFile.canWrite()
                myFile.createNewFile()
            }

            //Open the empty db file as the output stream 
            val myOutput = BufferedOutputStream(FileOutputStream(outFileName))

            //transfer bytes from the input file to the output file 
            val buffer = ByteArray(1024)
            var length: Int
            while (myInput.read(buffer).also { length = it } > 0) {
                myOutput.write(buffer, 0, length)
            }

            //Close the streams 
            myOutput.flush()
            myOutput.close()
            myInput.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun newGame(): QuizQuestion? {
        // TODO Set up user name and scoring
        qb.getQuestions()
        return DatabaseAdapter.Companion.getTitles(QueryBuilder.Companion.qry)
    }

    protected fun onStop() {
        val cursor: Cursor = titleAdapter.getCursor()
        if (cursor != null) cursor.deactivate()
        titleAdapter.changeCursor(null)
        super.onStop()
    }

    // Create the Options menu, displayed when Menu button is pressed
    fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = getMenuInflater()
        inflater.inflate(R.menu.game_menu, menu)
        return true
    }

    fun onOptionsItemSelected(item: MenuItem?): Boolean {
        // Handle menu item selection
        return when (item.getItemId()) {
            R.id.new_game -> {
                newGame()
                true
            }
            R.id.help -> {
                showHelp()
                true
            }
            R.id.exit -> {
                exitGame()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // event listener that responds to the user touching a title
    // in the ListView
    var titleListener: OnItemClickListener? = object : OnItemClickListener() {
        fun onItemClick(
            arg0: AdapterView<*>?, arg1: View?, arg2: Int,
            arg3: Long
        ) {
            // create an Intent to launch the Scoring Activity
            val scoring = Intent(this@GameActivity, ScoringActivity::class.java)

            // pass the selected title's row ID as an extra with the Intent
            scoring.putExtra(ROW_ID, arg3)
            startActivity(scoring) // start the Scoring Activity
        } // end method onItemClick
    } // end titleListener

    private fun exitGame() {
        // TODO Create logic to settle storage before exit.
    }

    private fun showHelp() {
        // Show the instructions dialog
        showDialog(DIALOG_INSTRUCTIONS_ID)
    }

    // Standard onCreateDialog logic
    protected fun onCreateDialog(id: Int): Dialog? {
        val instructionDialog = Builder(this)
        val instructionInflater: LayoutInflater? = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?

        // Uses a pre-constructed layout instead of an AlertDialog
        val instructions: View = instructionInflater.inflate(R.layout.instructions_dialog, null)
        instructionDialog.setCancelable(true)
        instructionDialog.setPositiveButton(R.string.ok, object : OnClickListener() {
            fun onClick(dialog: DialogInterface?, which: Int) {
                // Do nothing except close the dialog
            } // end method onClick
        }) // end call to dialogBuilder.setPositiveButton
        // end onCreateDialog
        instructionDialog.setView(instructions)
        instructionDialog.show()
        return null
    }

    companion object {
        const val DIALOG_INSTRUCTIONS_ID = 4
        val ROW_ID: String? = "row_id"
        var context: Context? = getContext()
        private val DB_NAME: String? = "movie_time.db"
        private val DB_PATH_PREFIX: String? = "/data/data/"
        private val DB_PATH_SUFFIX: String? = "/databases/"
        private val DB_PACKAGE: String? = "com.team1.trivia"
        private var DB_PATH: String? = null
        fun getContext(): Context? {
            return context
        }
    }
}