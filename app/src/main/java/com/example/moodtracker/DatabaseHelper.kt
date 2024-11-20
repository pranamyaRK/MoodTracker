package com.example.moodtracker

import com.google.gson.Gson
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "UserDB.db"
        private const val DATABASE_VERSION = 3
        private const val TABLE_USERS = "Users"
        private const val TABLE_JOURNAL_ENTRIES = "JournalEntries"
        private const val TABLE_ACTIVITY_ENTRIES = "ActivityEntries"

        // Columns for Users table
        private const val COLUMN_ID = "ID"
        private const val COLUMN_USERNAME = "Username"
        private const val COLUMN_PASSWORD = "Password"
        private const val COLUMN_PHONE = "PhoneNumber"
        private const val COLUMN_EMAIL = "Email"

        // Columns for Journal Entries table
        private const val COLUMN_JOURNAL_ENTRY_ID = "EntryID"
        private const val COLUMN_JOURNAL_ENTRY = "Entry"
        private const val COLUMN_JOURNAL_DATE = "Date"
        private const val COLUMN_MOOD = "Mood"

        //Columns for Activity Entries table
        private const val COLUMN_ACTIVITY_ENTRY_ID = "ActivityID"
        private const val COLUMN_ACTIVITY_ENTRY = "Entry"
        private const val COLUMN_ACTIVITY_DATE = "Date"
        private const val COLUMN_ACTIVITIES = "Activities"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = ("CREATE TABLE $TABLE_USERS ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COLUMN_USERNAME TEXT, $COLUMN_PHONE TEXT, $COLUMN_EMAIL TEXT, $COLUMN_PASSWORD TEXT)")
        db?.execSQL(createTable)

        // Create Journal Entries table
        val createJournalEntriesTable = ("CREATE TABLE $TABLE_JOURNAL_ENTRIES ($COLUMN_JOURNAL_ENTRY_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COLUMN_JOURNAL_ENTRY TEXT, $COLUMN_JOURNAL_DATE TEXT, $COLUMN_MOOD TEXT)")
        db?.execSQL(createJournalEntriesTable)

        //Create Activities Entries table
        val createActivitiesEntriesTable = ("CREATE TABLE $TABLE_ACTIVITY_ENTRIES ($COLUMN_ACTIVITY_ENTRY_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_ACTIVITY_ENTRY TEXT, $COLUMN_ACTIVITY_DATE TEXT, $COLUMN_ACTIVITIES TEXT)")
        db?.execSQL(createActivitiesEntriesTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion < newVersion) {
            db?.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
            db?.execSQL("DROP TABLE IF EXISTS $TABLE_JOURNAL_ENTRIES")
            db?.execSQL("DROP TABLE IF EXISTS $TABLE_ACTIVITY_ENTRIES")
            onCreate(db)
        }
    }

    fun insertUser(username: String, phone: String, email: String, password: String): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_USERNAME, username)
            put(COLUMN_PHONE, phone)
            put(COLUMN_EMAIL, email)
            put(COLUMN_PASSWORD, password)
        }

        return try {
            val result = db.insert(TABLE_USERS, null, contentValues)
            if (result == -1L) {
                Log.e("DatabaseHelper", "Insertion failed for user: $username")
            } else {
                Log.d("DatabaseHelper", "User $username inserted successfully with ID: $result")
            }
            result
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error inserting user: ${e.message}")
            -1L
        }
    }

    fun checkUser(username: String, password: String): Boolean {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_USERS WHERE $COLUMN_USERNAME = ? AND $COLUMN_PASSWORD = ?"
        val cursor = db.rawQuery(query, arrayOf(username, password))
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    fun insertJournalEntry(entry: String, date: String, mood: String): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_JOURNAL_ENTRY, entry)
            put(COLUMN_JOURNAL_DATE, date)
            put(COLUMN_MOOD, mood)
        }

        return try {
            val result = db.insert(TABLE_JOURNAL_ENTRIES, null, contentValues)
            if (result == -1L) {
                Log.e("DatabaseHelper", "Insertion failed for journal entry")
            }
            result
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error inserting journal entry: ${e.message}")
            -1L
        }
    }

    fun getJournalEntryByDate(date: String): String? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT $COLUMN_JOURNAL_ENTRY FROM $TABLE_JOURNAL_ENTRIES WHERE $COLUMN_JOURNAL_DATE = ?", arrayOf(date))
        return if (cursor.moveToFirst()) {
            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_JOURNAL_ENTRY))
        } else {
            null
        }.also {
            cursor.close()
        }
    }

    fun updateJournalEntry(entry: String, date: String, mood: String): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_JOURNAL_ENTRY, entry)
            put(COLUMN_MOOD, mood)
        }
        val updatedRows = db.update(TABLE_JOURNAL_ENTRIES, contentValues, "$COLUMN_JOURNAL_DATE = ?", arrayOf(date))
        return updatedRows > 0
    }

    fun retrieveEmail(username: String): String? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT $COLUMN_EMAIL FROM $TABLE_USERS WHERE $COLUMN_USERNAME = ?", arrayOf(username))
        var email: String? = null
        if (cursor.moveToFirst()) {
            // Retrieve the email from the cursor
            email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL))
        }
        cursor.close()  // Always close the cursor
        return email
    }

    fun getAllJournalEntries(): List<JournalEntry> {
        val journalList = mutableListOf<JournalEntry>()
        val db = this.readableDatabase
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val cursor = db.rawQuery("SELECT * FROM $TABLE_JOURNAL_ENTRIES WHERE $COLUMN_JOURNAL_DATE != ? ORDER BY $COLUMN_JOURNAL_DATE DESC", arrayOf(currentDate))
        while(cursor.moveToNext()) {
            val extractId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_JOURNAL_ENTRY_ID))
            val extractDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_JOURNAL_DATE))
            val extractEntry = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_JOURNAL_ENTRY))
            val extractMood = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MOOD))

            val entry = JournalEntry(extractId, extractEntry, extractDate, extractMood)
            journalList.add(entry)
        }
        cursor.close()
        db.close()
        return journalList
    }

    fun insertActivityEntry(entry: String, date: String, activities: List<String>): Long {
        val db = this.writableDatabase
        val activitiesJson = Gson().toJson(activities)

        val contentValues = ContentValues().apply {
            put(COLUMN_ACTIVITY_ENTRY, entry)
            put(COLUMN_ACTIVITY_DATE, date)
            put(COLUMN_ACTIVITIES, activitiesJson)
        }

        return try {
            val result = db.insert(TABLE_ACTIVITY_ENTRIES, null, contentValues)
            if (result == -1L) {
                Log.e("DatabaseHelper", "Insertion failed for journal entry")
            }
            result
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error inserting journal entry: ${e.message}")
            -1L
        }
    }

    fun getActivityEntryByDate(date: String): String? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT $COLUMN_ACTIVITY_ENTRY FROM $TABLE_ACTIVITY_ENTRIES WHERE $COLUMN_ACTIVITY_DATE = ?", arrayOf(date))
        return if (cursor.moveToFirst()) {
            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ACTIVITY_ENTRY))
        } else {
            null
        }.also {
            cursor.close()
        }
    }

    fun updateActivityEntry(entry: String, date: String, activities: List<String>): Boolean {
        val db = this.writableDatabase
        val activitiesJson = Gson().toJson(activities)
        val contentValues = ContentValues().apply {
            put(COLUMN_ACTIVITY_ENTRY, entry)
            put(COLUMN_ACTIVITIES, activitiesJson)
        }
        val updatedRows = db.update(TABLE_ACTIVITY_ENTRIES, contentValues, "$COLUMN_ACTIVITY_DATE = ?", arrayOf(date))
        return updatedRows > 0
    }

    fun getMoodData(): List<Pair<String, Int>> {
        val moodList = mutableListOf<Pair<String, Int>>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT $COLUMN_JOURNAL_DATE, $COLUMN_MOOD FROM $TABLE_JOURNAL_ENTRIES", null)

        if (cursor.moveToFirst()) {
            do {
                val date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_JOURNAL_DATE))
                val moodLevel = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MOOD))
                var moodVal = 0
                when (moodLevel.lowercase()) {
                    "joyful" -> moodVal = 5
                    "happy" -> moodVal = 4
                    "meh" -> moodVal = 3
                    "sad" -> moodVal = 2
                    "crying" -> moodVal = 1
                }
                moodList.add(Pair(date, moodVal))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return moodList
    }

    fun getActivityData(): Map<String, Int> {
        val db = this.readableDatabase
        val activityCountMap = mutableMapOf<String, Int>()
        val cursor = db.rawQuery("SELECT $COLUMN_ACTIVITIES FROM $TABLE_ACTIVITY_ENTRIES", null)

        if (cursor.moveToFirst()) {
            do {
                val activitiesJsonString = cursor.getString(cursor.getColumnIndexOrThrow("Activities"))
                val activitiesList: List<String> = Gson().fromJson(activitiesJsonString, Array<String>::class.java).toList()

                for (activity in activitiesList) {
                    activityCountMap[activity] = activityCountMap.getOrDefault(activity, 0) + 1
                }
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()

        return activityCountMap
    }
}
