package com.example.moodtracker

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class JournalFragment : Fragment() {
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: JournalEntryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_journal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnRedirectToHome: Button = view.findViewById(R.id.BtnRedirectToEditEntry)
        val btnRedirectToActivity: Button = view.findViewById(R.id.BtnRedirectToEditActivity)
        btnRedirectToHome.setOnClickListener {
            val navController = findNavController(requireActivity(), R.id.NavHostFragment)
            navController.navigate(R.id.action_journalFragment_to_homeFragment)
        }

        btnRedirectToActivity.setOnClickListener {
            val navController = findNavController(requireActivity(), R.id.NavHostFragment)
            navController.navigate(R.id.action_journalFragment_to_activityFragment)
        }

        databaseHelper = DatabaseHelper(requireContext())
        adapter = JournalEntryAdapter(databaseHelper.getAllJournalEntries())
        recyclerView = view.findViewById(R.id.JournalRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        adapter.refreshData(databaseHelper.getAllJournalEntries())
    }

}
