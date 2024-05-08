package com.main.uniplan

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.uniplan.AddSubjects
import com.example.uniplan.Dashboard
import com.example.uniplan.Plan
import com.example.uniplan.R
import com.main.builder.api.RequestsFileManager
import com.main.builder.generic.JSONBuilder
import com.objects.Subject
import objects.Occurrence
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.time.LocalDate


class MainActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val params = v.layoutParams as ViewGroup.MarginLayoutParams
            params.topMargin = systemBars.top
            v.layoutParams = params
            insets
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

    fun addSubjects(view: View?) {
        val intent = Intent(this, AddSubjects::class.java)
        startActivity(intent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun thereIsAOccurence(): MutableList<Occurrence> {
        val res = mutableListOf<Occurrence>()

        @RequiresApi(Build.VERSION_CODES.O)
        fun todayDate(): String {
            return try {
                val tmp = LocalDate.now()
                "${tmp.dayOfMonth}-${tmp.month.value}-${tmp.year}"
            } catch (e: Exception) {
                e.message.toString()
            }
        }

        val man = RequestsFileManager(applicationContext);
        val requests = man.getFilesNames();

        if (requests.isEmpty()) {
            return res;
        }
        for (request in requests) {
            val sub = Subject(request)
            val occList = JSONBuilder(applicationContext, sub).buildOccurenceFromJson()
            var found = false; var index = 0;
            while(!found || index == occList.size) {
                if (occList[index].getDate() == todayDate()) {
                    res.add(occList[index])
                    found = true;
                }
            }
        }
        return res;
    }

}