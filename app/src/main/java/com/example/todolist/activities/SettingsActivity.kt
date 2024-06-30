package com.example.todolist.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.todolist.R
import com.example.todolist.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private val prefsName = "settings"
    private val hideTasksText = "hide_tasks"
    private val minutesText = "minutes"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val minutes = arrayOf("1 min", "2 min", "3 min", "5 min", "10 min", "30 min")

        makeAdapter(minutes)

        val prefs = getSharedPreferences(prefsName, Context.MODE_PRIVATE)

        binding.checkbox.isChecked = prefs.getString(hideTasksText, false.toString()).toBoolean()

        binding.notificationSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                prefs.edit().putString(minutesText, minutes[position]).apply()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        binding.checkbox.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putString(hideTasksText, isChecked.toString()).apply()
        }

        binding.saveButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun makeAdapter(minutes: Array<String>) {
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, minutes)

        binding.notificationSpinner.adapter = arrayAdapter
    }
}