package br.edu.ifsp.moodly.controller
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import br.edu.ifsp.moodly.model.MoodEntry
import br.edu.ifsp.moodly.model.MoodState

class MoodDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "moodly.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_MOOD = "mood_entries"
        private const val COL_ID = "id"
        private const val COL_DATE = "date"
        private const val COL_MOOD = "mood"
        private const val COL_NOTE = "note"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_MOOD (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_DATE TEXT NOT NULL,
                $COL_MOOD INTEGER NOT NULL,
                $COL_NOTE TEXT
            )
        """.trimIndent()
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MOOD")
        onCreate(db)
    }

    fun insertMood(entry: MoodEntry): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_DATE, entry.date)
            put(COL_MOOD, entry.mood.code)
            put(COL_NOTE, entry.note)
        }
        return db.insert(TABLE_MOOD, null, values)
    }

    fun getAllMoods(): List<MoodEntry> {
        val db = readableDatabase
        val list = mutableListOf<MoodEntry>()
        val cursor = db.query(
            TABLE_MOOD,
            arrayOf(COL_ID, COL_DATE, COL_MOOD, COL_NOTE),
            null, null, null, null,
            "$COL_DATE DESC"
        )
        cursor.use {
            while (it.moveToNext()) {
                val id = it.getLong(it.getColumnIndexOrThrow(COL_ID))
                val date = it.getString(it.getColumnIndexOrThrow(COL_DATE))
                val moodCode = it.getInt(it.getColumnIndexOrThrow(COL_MOOD))
                val note = it.getString(it.getColumnIndexOrThrow(COL_NOTE))

                val moodState = MoodState.fromCode(moodCode)
                list.add(MoodEntry(id, date, moodState, note))
            }
        }
        return list
    }

    fun updateMood(entry: MoodEntry): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_DATE, entry.date)
            put(COL_MOOD, entry.mood.code)
            put(COL_NOTE, entry.note)
        }
        Log.e("Menu","Updating $entry")
        return db.update(TABLE_MOOD, values, "$COL_ID = ?", arrayOf(entry.id.toString()))
    }

    fun deleteMood(id: Long): Int {
        val db = writableDatabase
        return db.delete(TABLE_MOOD, "$COL_ID = ?", arrayOf(id.toString()))
    }
}
