package com.example.moodtracker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val databaseHelper: DatabaseHelper = DatabaseHelper(application)

    fun addJournalEntry(entry: String, mood: String): LiveData<Boolean> {
        val successLiveData = MutableLiveData<Boolean>()
        Thread {
            val result = databaseHelper.insertJournalEntry(entry, System.currentTimeMillis().toString(), mood)
            val success = result != -1L  // Check if the insertion was successful
            successLiveData.postValue(success)
        }.start()
        return successLiveData
    }
}