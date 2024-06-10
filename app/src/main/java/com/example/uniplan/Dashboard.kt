package com.example.uniplan

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.main.builder.api.RequestsFileManager
import com.main.builder.generic.JSONBuilder
import com.main.uniplan.MainActivity
import com.objects.Subject

class Dashboard : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val allSubs = getAllSubject();
        if (allSubs.isEmpty()) {
            setContentView(R.layout.activity_plan_empty);
            findViewById<TextView>(R.id.textView6).text = "DASHBOARD";
        } else {
            setContentView(R.layout.activity_dashboard);
            fun setFirst(subject: Subject) {
                val sub = findViewById<TextView>(R.id.subject)
                val dates = findViewById<TextView>(R.id.dates)
                val progressBar = findViewById<ProgressBar>(R.id.progressBar)
                val toDo = findViewById<TextView>(R.id.to_do)
                val done = findViewById<TextView>(R.id.done)

                sub.text = subject.getSubject()
                dates.text = "${subject.getRequestDate()}                                                                   ${subject.getExamDate()}"

                val occList = JSONBuilder(applicationContext, subject).buildOccurrenceFromJson()
                val totalOcc = occList.size
                val doneOcc = occList.count { it.getDone() }

                val progressPercentage = if (totalOcc > 0) (doneOcc * 100) / totalOcc else 0
                progressBar.progress = progressPercentage

                done.text = "DONE TOPICS: $doneOcc"
                //toDo.text = "TO DO TOPICS: ${occList.size - doneOcc}"
                toDo.text = "${occList[0]}"
            }
            setFirst(allSubs[0])
            /*setContentView(R.layout.activity_plan_empty);
            val main = findViewById<TextView>(R.id.textView16);
            val res = JSONBuilder(applicationContext, allSubs[0])
            main.text = res;*/
        }
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

    private fun getAllSubject(): MutableList<Subject> {
        val fileList = RequestsFileManager(applicationContext).getFilesNames();
        val res = mutableListOf<Subject>();
        for (sub in fileList) {
            res.add(Subject(sub));
        }
        return res;
    }

}