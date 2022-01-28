package com.team1.trivia

import jdk.incubator.jpackage.internal.Log
import java.io.*
import javax.naming.Context
interface CursorFactory

/***
 * Helper singleton class to manage SQLiteDatabase Create and Restore
 *
 * Modified from SQLiteDatabaseAdapter by Alessandro Franzi
 * http://code.google.com/p/almanac/source/browse/trunk/Almanac/#Almanac%2Fres%2Fxml
 *
 */
class DatabaseAdapter : SQLiteOpenHelper {
    private var context: Context?
    private var DB_PATH: String? = DB_PATH_PREFIX + "com.team1.trivia" + DB_PATH_SUFFIX

    /***
     * Constructor
     *
     */
    private constructor(
        context: Context?, name: String?,
        factory: CursorFactory?, version: Int
    ) : super() {
        this.context = context
        Log.info(TAG)
    }

    internal constructor(c: Context?) : super(c, DATABASE_NAME, null, 0) {
        context = c
        Log.i(TAG, "Create or Open database : " + DATABASE_NAME)
    }

    fun onCreate(db: SQLiteDatabase?) {
        Log.d(TAG, "onCreate : nothing to do")
    }

    fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        Log.d(TAG, "onUpgrade : nothing to do")
    }

    /***
     * Method to Copy the database from asset directory to application's data
     * directory
     */
    @Throws(IOException::class)
    private fun copyDataBase(databaseName: String?) {
        copyDataBase(context, databaseName)
    }

    /***
     * Method to check if database exists in application's data directory
     */
    fun checkDatabase(databaseName: String?): Boolean {
        return checkDatabase(context, databaseName)
    }

    fun getDB_PATH(): String? {
        return DB_PATH
    }

    fun setDB_PATH(dB_PATH: String?) {
        DB_PATH = dB_PATH
    }

    companion object {
        // private String DB_PATH_PACKAGE = context.getPackageName();
        private var myDb: SQLiteDatabase? = null
        private var instance: DatabaseAdapter? = null
        private val DB_PATH_PREFIX: String? = "/data/data/"
        private val DB_PATH_SUFFIX: String? = "/databases/"
        private const val DATABASE_VERSION = 1
        private val DATABASE_NAME: String? = "movie_time"
        private val TAG: String? = "DatabaseAdapter"
        private val PHRASE_TABLE: String? = "phrase"

        /***
         * Initialize method
         */
        private fun initialize(context: Context?, databaseName: String?) {
            if (instance == null) {
                /**
                 * Try to check if there is an copy of DB in Database
                 * Directory
                 */
                if (!checkDatabase(context, databaseName)) {
                    // if not exist, I try to copy from asset dir                                 
                    try {
                        copyDataBase(context, databaseName)
                    } catch (e: IOException) {
                        Log.e(
                            TAG, "Database " + databaseName
                                    + " does not exist and there is no Original Version in Asset dir"
                        )
                    }
                }
                Log.i(TAG, "Try to create instance of database ($databaseName)")
                instance = DatabaseAdapter(
                    context, databaseName,
                    null, DATABASE_VERSION
                )
                myDb = instance.getWritableDatabase()
                Log.i(TAG, "instance of database ($databaseName) created !")
            }
        }

        /***
         * Static method for getting singleton instance
         */
        fun getInstance(
            context: Context?, databaseName: String?
        ): DatabaseAdapter? {
            initialize(context, databaseName)
            return instance
        }

        /***
         * Method to get database instance
         */
        fun getDatabase(): SQLiteDatabase? {
            return myDb
        }

        /***
         * Static method for copy the database from asset directory to application's
         * data directory
         */
        @Throws(IOException::class)
        private fun copyDataBase(aContext: Context?, databaseName: String?) {
            // Open your local db as the input stream
            val myInput: InputStream = aContext.getAssets().open(databaseName)

            // Path to the just created empty db
            val outFileName = getDatabasePath(aContext, databaseName)
            Log.i(
                TAG, "Check if create dir : " + DB_PATH_PREFIX
                        + aContext.getPackageName() + DB_PATH_SUFFIX
            )

            // if the path doesn't exist first, create it
            val f = File(
                DB_PATH_PREFIX + aContext.getPackageName()
                    .toString() + DB_PATH_SUFFIX
            )
            if (!f.exists()) f.mkdir()
            Log.i(TAG, "Trying to copy local DB to : $outFileName")

            // Open the empty db as the output stream                 
            val myOutput: OutputStream = FileOutputStream(outFileName)
            // transfer bytes from the inputfile to the outputfile
            val buffer = ByteArray(1024)
            var length: Int
            while (myInput.read(buffer).also { length = it } > 0) {
                myOutput.write(buffer, 0, length)
            }

            // Close the streams
            myOutput.flush()
            myOutput.close()
            myInput.close()
            Log.i(TAG, "DB ($databaseName) copied!")
        }

        /***
         * Static Method to check if database exists in application's data directory
         */
        fun checkDatabase(aContext: Context?, databaseName: String?): Boolean {
            var checkDB: SQLiteDatabase? = null
            try {
                val myPath = getDatabasePath(aContext, databaseName)
                Log.i(TAG, "Trying to connect to : $myPath")
                checkDB = SQLiteDatabase.openDatabase(
                    myPath, null,
                    SQLiteDatabase.OPEN_READONLY
                )
                Log.i(TAG, "Database $databaseName found!")
                checkDB.close()
            } catch (e: SQLiteException) {
                Log.i(TAG, "Database $databaseName does not exist!")
            }
            return if (checkDB != null) true else false
        }

        /***
         * Static Method that returns database path in the application's data
         * directory
         */
        private fun getDatabasePath(aContext: Context?, databaseName: String?): String? {
            return DB_PATH_PREFIX + aContext.getPackageName().toString() + DB_PATH_SUFFIX
                .toString() + databaseName
        }

        // Get the phrases and titles
        fun getTitles(qry: String?): QuizQuestion? {
            val qq = QuizQuestion(null, null, null)
            val id = ArrayList<Int?>()
            val phrase = ArrayList<String?>()
            val title: MutableList<String?> = ArrayList()
            myDb = getDatabase()
            val results: Cursor = myDb.rawQuery(qry, null)
            if (results.getCount() > 0) {
                var i = 0
                while (results.moveToNext()) {
                    id.add(i, results.getInt(0))
                    phrase.add(i, results.getString(1))
                    title.add(i, results.getString(2))
                    i++
                }
            }
            qq._id = id
            qq.phrase = phrase
            qq.title = title
            results.close()
            return qq
        }

        // Get the number of rows in the Phrase table
        fun getCount(): Int {
            myDb = getDatabase()

            // Find the number of rows in the Phrase table.
            val count: Long = DatabaseUtils.queryNumEntries(myDb, PHRASE_TABLE)
            return count.toInt()
        } // end getCount
    }
}