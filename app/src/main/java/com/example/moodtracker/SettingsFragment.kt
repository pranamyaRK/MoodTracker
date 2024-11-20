package com.example.moodtracker

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class SettingsFragment : Fragment() {

    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        databaseHelper = DatabaseHelper(requireContext())

        val settingsTextUsername: TextView = view.findViewById(R.id.settingsUsername)
        val settingsTextEmail: TextView = view.findViewById(R.id.settingsEmail)

        val sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "Guest")  // Default to "Guest" if not found
        settingsTextUsername.text = getString(R.string.settings_username, username)
        settingsTextEmail.text = getString(R.string.settings_email, databaseHelper.retrieveEmail(username.toString()))
    }

}