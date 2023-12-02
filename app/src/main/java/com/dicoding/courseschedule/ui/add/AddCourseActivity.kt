package com.dicoding.courseschedule.ui.add

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dicoding.courseschedule.R
import com.dicoding.courseschedule.ui.ViewModelFactory
import com.dicoding.courseschedule.util.TimePickerFragment
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddCourseActivity : AppCompatActivity(), TimePickerFragment.DialogTimeListener {
    private lateinit var viewModel: AddCourseViewModel
    private lateinit var edCourseName: TextInputEditText
    private lateinit var spinnerDay: Spinner
    private lateinit var edLecturer: TextInputEditText
    private lateinit var edNote: TextInputEditText

    private var startTime = ""
    private var endTime = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_course)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val factory = ViewModelFactory.createFactory(this)
        viewModel = ViewModelProvider(this, factory)[AddCourseViewModel::class.java]

        edCourseName = findViewById(R.id.ed_course_name)
        spinnerDay = findViewById(R.id.spinner_day)
        edLecturer = findViewById(R.id.ed_lecturer)
        edNote = findViewById(R.id.ed_note)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_insert -> {
                val courseName = edCourseName.text.toString()
                val selectedDay = spinnerDay.selectedItem.toString()
                val dayNumber = getDayNumberByDayName(selectedDay)
                val lecturer = edLecturer.text.toString()
                val note = edNote.text.toString()

                if (validateInput(courseName, startTime, endTime, dayNumber, lecturer, note)) {
                    viewModel.insertCourse(courseName, dayNumber, startTime, endTime, lecturer, note)
                    finish()
                    true
                } else {
                    Toast.makeText(this, "Please fill in all the fields", Toast.LENGTH_SHORT).show()
                    false
                }
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun validateInput(
        courseName: String,
        startTime: String,
        endTime: String,
        dayNumber: Int,
        lecturer: String,
        note: String
    ): Boolean {
        return courseName.isNotEmpty() && startTime.isNotEmpty() && endTime.isNotEmpty()
                && dayNumber != -1 && lecturer.isNotEmpty() && note.isNotEmpty()
    }

    fun showTimePicker(view: View) {
        val tag = when (view.id) {
            R.id.ib_start_time -> getString(R.string.tag_start_time)
            R.id.ib_end_time -> getString(R.string.tag_end_time)
            else -> ""
        }

        val dialogFragment = TimePickerFragment()
        dialogFragment.show(supportFragmentManager, tag)
    }

    override fun onDialogTimeSet(tag: String?, hour: Int, minute: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        when (tag) {
            getString(R.string.tag_start_time) -> {
                findViewById<TextView>(R.id.tv_start_time).text = timeFormat.format(calendar.time)
                startTime = timeFormat.format(calendar.time)
            }
            getString(R.string.tag_end_time) -> {
                findViewById<TextView>(R.id.tv_end_time).text = timeFormat.format(calendar.time)
                endTime = timeFormat.format(calendar.time)
            }
        }
    }

    private fun getDayNumberByDayName(dayName: String): Int {
        val days = resources.getStringArray(R.array.day)
        return days.indexOf(dayName)
    }
}