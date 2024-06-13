package com.main.uniplan

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.uniplan.R
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.objects.Subject
import org.json.JSONArray
import android.content.Context



class Occurrence : AppCompatActivity() {
    private val subjects: MutableList<Subject> = mutableListOf()
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_occurrence)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //nuovi valori per metodo parseresponse
        val subjectName = intent.getStringExtra("subjectName") ?: ""
        val subjectDate = intent.getStringExtra("subjectDate") ?: ""
        loadOccurrences(subjectName, subjectDate)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadOccurrences(subjectName: String, subjectDate: String) {
        // Fetch occurrences data from shared preferences or another storage method
        val sharedPref = getSharedPreferences("OccurrencesData", Context.MODE_PRIVATE)
        val responseJson = sharedPref.getString("responseJson", "") ?: ""

        //nuovo per metodo parseresponse
        parseResponse(responseJson, subjectName, subjectDate)
        displaySubjectsAndTopics()
    }

    private fun displaySubjectsAndTopics() {
        val textView: TextView = findViewById(R.id.textViewSubjects)
        val builder = StringBuilder()

        subjects.forEach { subject ->
            builder.append("Subject: ${subject.getName()}, Date: ${subject.getDate()}\n")
            subject.getTopics().forEach { topic ->
                builder.append(" - Topic: $topic\n")
            }
            builder.append("\n")
        }

        textView.text = builder.toString()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun parseResponse(responseJson: String, subjectName: String, subjectDate: String) {
        try {
            val jsonArray = JSONArray(responseJson)
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val subjectName = jsonObject.getString("subject")
                val date = jsonObject.getString("date")
                val topic = jsonObject.getString("topic")

                val subject = findOrCreateSubject(subjectName, date)
                subject.addTopic(topic)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun findOrCreateSubject(name: String, date: String): Subject {
        for (subject in subjects) {
            if (subject.getName() == name && subject.getDate() == date) {
                return subject
            }
        }
        val newSubject = Subject(name, date)
        subjects.add(newSubject)
        return newSubject
    }


    fun plan(view: View?) {
        val intent = Intent(this, Plan::class.java)
        startActivity(intent)
    }

    fun dashboard(view: View?) {
        val intent = Intent(this, Dashboard::class.java)
        startActivity(intent)
    }

    fun home(view: View?) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}
