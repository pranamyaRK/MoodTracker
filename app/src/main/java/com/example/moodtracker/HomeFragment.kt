package com.example.moodtracker

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation.findNavController
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the ViewModel
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        databaseHelper = DatabaseHelper(requireContext())

        // Get references to the UI elements
        val journalEntryEditText: EditText = view.findViewById(R.id.journalEntryEditText)
        val submitButton: Button = view.findViewById(R.id.BtnSaveJournal)

        val btnRedirectToActivity: Button = view.findViewById(R.id.BtnRedirectToActivity)
        btnRedirectToActivity.setOnClickListener {
            val navController = findNavController(requireActivity(), R.id.NavHostFragment)
            navController.navigate(R.id.action_homeFragment_to_activityFragment)
        }

        // Username TextView
        val usernameTextView: TextView = view.findViewById(R.id.usernameGreeting)

        // Get the stored username from SharedPreferences
        val sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "Guest")  // Default to "Guest" if not found
        usernameTextView.text = getString(R.string.text_hello, username)  // Set the username to the TextView

        val moodButtons = listOf<ImageButton>(
            view.findViewById(R.id.BtnJoyful),
            view.findViewById(R.id.BtnHappy),
            view.findViewById(R.id.BtnMeh),
            view.findViewById(R.id.BtnSad),
            view.findViewById(R.id.BtnCrying)
        )

        var selectedMood = ""

        // Get the current date
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val existingEntry = databaseHelper.getJournalEntryByDate(currentDate)

        // Load existing entry into the EditText if it exists
        if (existingEntry != null) {
            journalEntryEditText.setText(existingEntry)
            Toast.makeText(requireContext(), "Loaded existing entry for today", Toast.LENGTH_SHORT).show()
        }

        moodButtons.forEach { button ->
            button.setOnClickListener {
                selectedMood = button.contentDescription.toString()
                Toast.makeText(requireContext(), "Selected mood: $selectedMood", Toast.LENGTH_SHORT).show()
            }
        }

        submitButton.setOnClickListener {
            val journalEntry = journalEntryEditText.text.toString()

            if (journalEntry.isNotEmpty() && selectedMood.isNotEmpty()) {
                if (existingEntry != null) {
                    // Update existing entry
                    val success = databaseHelper.updateJournalEntry(journalEntry, currentDate, selectedMood)
                    if (success) {
                        Toast.makeText(requireContext(), "Entry updated successfully!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Failed to update entry.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Insert a new entry
                    val result = databaseHelper.insertJournalEntry(journalEntry, currentDate, selectedMood)
                    if (result != -1L) {
                        Toast.makeText(requireContext(), "Entry saved successfully!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Failed to save entry.", Toast.LENGTH_SHORT).show()
                    }
                }
                selectedMood = "" // Reset mood selection
            } else {
                Toast.makeText(requireContext(), "Please write an entry and select a mood before saving.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
