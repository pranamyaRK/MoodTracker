package com.example.moodtracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class JournalEntryAdapter(private var entries: List<JournalEntry>) : RecyclerView.Adapter<JournalEntryAdapter.JournalViewHolder>() {

    class JournalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateTextView: TextView = itemView.findViewById(R.id.currentDate)
        val moodImageView: ImageView = itemView.findViewById(R.id.currentMood)
//        val journalButton: Button = itemView.findViewById(R.id.btnCurrentViewJournal)
//        val activityButton: Button = itemView.findViewById(R.id.btnCurrentViewActivity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JournalViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_prev_journal_entry, parent, false)
        return JournalViewHolder(view)
    }

    override fun onBindViewHolder(holder: JournalViewHolder, position: Int) {
        val entry = entries[position]
        holder.dateTextView.text = entry.Date
        when (entry.Mood.lowercase()) {
            "joyful" -> holder.moodImageView.setImageResource(R.drawable.joyful)
            "happy" -> holder.moodImageView.setImageResource(R.drawable.happy)
            "meh" -> holder.moodImageView.setImageResource(R.drawable.meh)
            "sad" -> holder.moodImageView.setImageResource(R.drawable.sad)
            "crying" -> holder.moodImageView.setImageResource(R.drawable.crying)
            else -> holder.moodImageView.setImageResource(R.drawable.meh) // fallback image
        }
    }

    override fun getItemCount(): Int = entries.size

    fun refreshData(newEntries: List<JournalEntry>) {
        entries = newEntries
        notifyDataSetChanged()
    }
}

