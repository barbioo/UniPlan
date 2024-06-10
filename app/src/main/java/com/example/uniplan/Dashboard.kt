package com.example.uniplan

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.Text
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
                toDo.text = "TO DO TOPICS: ${occList.size - doneOcc}"
            }
            setFirst(allSubs[0])
            buildAllDashboards(allSubs)
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

    private fun buildAllDashboards(subList: MutableList<Subject>) {
        val field = findViewById<LinearLayout>(R.id.content)


        fun dpToPx(dp: Int): Int {
            val density = resources.displayMetrics.density
            return (dp * density).toInt()
        }

        for (subject in subList.drop(1)) {
            val subName = TextView(applicationContext).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                textAlignment = View.TEXT_ALIGNMENT_CENTER
                setPadding(0, dpToPx(120), 0, 0)
                textSize = 35f
                text = subject.getSubject()
            }
            field.addView(subName)

            val beginEnd = TextView(applicationContext).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                textAlignment = View.TEXT_ALIGNMENT_CENTER
                setPadding(0, dpToPx(5), 0, 0)
                text = "BEGIN                                                                        EXAM DATE"
            }
            field.addView(beginEnd)

            val dates = TextView(applicationContext).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                textAlignment = View.TEXT_ALIGNMENT_CENTER
                text = "${subject.getRequestDate()}                                                                      ${subject.getRequestDate()}"
            }
            field.addView(dates)

            val occList = JSONBuilder(applicationContext, subject).buildOccurrenceFromJson()
            val totalOcc = occList.size
            val doneOcc = occList.count { it.getDone() }
            val progressPercentage = if (totalOcc > 0) (doneOcc * 100) / totalOcc else 0

            val progressBar = ProgressBar(applicationContext, null, android.R.attr.progressBarStyleHorizontal).apply {
                layoutParams = ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(dpToPx(15), dpToPx(20), dpToPx(15), 0)
                }
                isIndeterminate = false
                max = 100
                progress = progressPercentage
            }
            field.addView(progressBar)

            val done = TextView(applicationContext).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                textAlignment = View.TEXT_ALIGNMENT_CENTER
                text = "DONE TOPICS: $doneOcc"
            }
            field.addView(done)

            val missing = TextView(applicationContext).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                textAlignment = View.TEXT_ALIGNMENT_CENTER
                text = "TO DO TOPICS: ${totalOcc - doneOcc}"
            }
            field.addView(missing)
        }
    }



}