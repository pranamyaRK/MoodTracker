package com.example.moodtracker

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation.findNavController
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ActivityFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var buttonStateViewModel: ButtonStateViewModel
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_activity, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        databaseHelper = DatabaseHelper(requireContext())
        buttonStateViewModel = ViewModelProvider(requireActivity()).get(ButtonStateViewModel::class.java)

        val activityEntryEditText: EditText = view.findViewById(R.id.activityEntryEditText)
        val submitButton: Button = view.findViewById(R.id.BtnSaveActivity)

        val btnRedirectToHome = view.findViewById<Button>(R.id.BtnRedirectToHome)
        btnRedirectToHome.setOnClickListener {
            val navController = findNavController(requireActivity(), R.id.NavHostFragment)
            navController.navigate(R.id.action_activityFragment_to_homeFragment)
        }

        val activityButtons = listOf<Button>(
            view.findViewById(R.id.BtnSwimmingActivity),
            view.findViewById(R.id.BtnRunningActivity),
            view.findViewById(R.id.BtnTennisActivity),
            view.findViewById(R.id.BtnTelevisionActivity),
            view.findViewById(R.id.BtnSleepingActivity),
            view.findViewById(R.id.BtnSkateboardActivity),
            view.findViewById(R.id.BtnHockeyActivity),
            view.findViewById(R.id.BtnSoccerActivity),
            view.findViewById(R.id.BtnRugbyActivity),
            view.findViewById(R.id.BtnCricketActivity)
        )

        val activityList = mutableListOf<String>()
        activityButtons.forEach { button ->
            val buttonKey = button.id.toString() // Use a unique key for each button

            // Set initial state
            val isClicked = buttonStateViewModel.getButtonState(buttonKey)
            updateButtonAppearance(button, isClicked)
            button.setOnClickListener {
                buttonStateViewModel.toggleButtonState(buttonKey)
                val newState = buttonStateViewModel.getButtonState(buttonKey)
                updateButtonAppearance(button, newState)

                val selectedActivity = button.contentDescription.toString()
                activityList.add(selectedActivity)
                Toast.makeText(requireContext(), "Selected activity: $selectedActivity", Toast.LENGTH_SHORT).show()
            }
        }

        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val existingActivityEntry = databaseHelper.getActivityEntryByDate(currentDate)

        if (existingActivityEntry != null) {
            activityEntryEditText.setText(existingActivityEntry)
            Toast.makeText(requireContext(), "Loaded existing entry for today", Toast.LENGTH_SHORT).show()
        }

        submitButton.setOnClickListener {
            val activityEntry = activityEntryEditText.text.toString()

            if (activityEntry.isNotEmpty() && activityList.size > 0) {
                if (existingActivityEntry != null) {
                    // Update existing entry
                    val success = databaseHelper.updateActivityEntry(activityEntry, currentDate, activityList)
                    if (success) {
                        Toast.makeText(requireContext(), "Entry updated successfully!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Failed to update entry.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Insert a new entry
                    val result = databaseHelper.insertActivityEntry(activityEntry, currentDate, activityList)
                    if (result != -1L) {
                        Toast.makeText(requireContext(), "Entry saved successfully!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Failed to save entry.", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Please write an entry and select a mood before saving.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateButtonAppearance(button: Button, isClicked: Boolean) {
        if (isClicked) {
            button.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.Amethyst)) // Set a clicked state color
        } else {
            button.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.Magnolia)) // Default color
        }
    }
}
