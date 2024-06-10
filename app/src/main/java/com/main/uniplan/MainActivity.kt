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
import com.google.android.material.snackbar.Snackbar
import com.main.builder.api.RequestsFileManager
import com.main.builder.generic.JSONBuilder
import com.objects.Subject
import objects.Occurrence
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
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

        /*val t = Thread {
            if (!isOnline()) {
                Snackbar.make(findViewById(android.R.id.content), "No internet connection\nSome features will not be viable", Snackbar.LENGTH_LONG).show()
            }
        }
        t.start();*/
        /*val to = Thread {
            val list = applicationContext.getExternalFilesDir("data_respond")?.listFiles();
            list?.forEach { file ->
                file.delete()
            }
            var out = BufferedWriter(FileWriter(File(applicationContext.getExternalFilesDir("user_requests"), "user_requests.txt")));
            out.write("");
            out.flush();
            out.close();
            Snackbar.make(findViewById(android.R.id.content), "Thread completed", Snackbar.LENGTH_LONG).show()
        }
        to.start(); to.join();*/
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

    private fun isOnline(): Boolean {
        val client = OkHttpClient();
        val request = Request.Builder()
            .url("https://jsonplaceholder.typicode.com/posts")
            .get()
            .build();
        return try {
            client.newCall(request).execute().toString()
            true
        } catch (e: Exception) {
            false
        }
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
            val occList = JSONBuilder(applicationContext, sub).buildOccurrenceFromJson()
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