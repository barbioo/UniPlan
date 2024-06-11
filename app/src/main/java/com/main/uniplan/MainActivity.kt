package com.main.uniplan

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.uniplan.R
import com.google.android.material.snackbar.Snackbar
import com.main.builder.api.RequestsFileManager
import com.main.builder.api.ResponseWriter
import com.main.builder.generic.JSONBuilder
import com.objects.Subject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import objects.Occurrence
import okhttp3.OkHttpClient
import okhttp3.Request
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

        GlobalScope.launch {
            val occTodaysList = thereIsAOccurrences()
            if (occTodaysList.isNotEmpty()) {
                buildButtons(occTodaysList);
            }
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
    private suspend fun thereIsAOccurrences(): MutableList<Occurrence> {
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

        val man = RequestsFileManager(applicationContext)
        val requests = man.getFilesNames()

        if (requests.isEmpty()) {
            return res
        }

        val deferredResults = requests.map { request ->
            GlobalScope.async {
                val sub = Subject(request)
                val occList = JSONBuilder(applicationContext, sub).buildOccurrenceFromJson()
                occList.firstOrNull { it.getDate() == todayDate() }
            }
        }

        deferredResults.forEach { deferred ->
            val occurrence = deferred.await()
            occurrence?.let { res.add(it) }
        }

        return res
    }

    private fun buildButtons(occList: MutableList<Occurrence>) {
        val buttonStyle = androidx.appcompat.R.style.Widget_AppCompat_Button_Colored;
        val content = findViewById<LinearLayout>(R.id.subjects);
        content.removeAllViews()

        for(occ in occList) {
            val button = Button(ContextThemeWrapper(applicationContext, buttonStyle), null, buttonStyle)
            button.text = "${occ.getSubject()}\n\n${occ.getTopic()}"
            button.backgroundTintList = ColorStateList.valueOf(Color.rgb(96, 60, 154))
            content.addView(button);

            button.setOnClickListener {
                val t = Thread() {
                    occ.setDone(true);
                    addAsDone(occ);
                }
                t.start()
            }
        }
    }

    private fun addAsDone(occ: Occurrence) {

        fun findCandidates(occ: Occurrence): MutableList<String> {
            val manager = RequestsFileManager(applicationContext)
            val list = manager.getFilesNames()
            val candidates = mutableListOf<String>()

            for (s in list) {
                if (s.contains(occ.getSubject())) {
                    candidates.add(s)
                }
            }

            return candidates
        }

        fun findSubject(o: Occurrence, candidates: MutableList<String>): Subject {
            for (s in candidates) {
                val builder = JSONBuilder(applicationContext, s);

                val occList = builder.buildOccurrenceFromJson();

                for(occ in occList) {
                    if (occ.equals(o)) {
                        return Subject(s);
                    }
                }
            }
            throw Exception("No math found")
        }

        fun findTheOne(o: Occurrence, s: Subject): MutableList<Occurrence> {
            val builder = JSONBuilder(applicationContext, s);

            val occList = builder.buildOccurrenceFromJson();

            for(occ in occList) {
                if (occ.equals(o)) {
                    return occList;
                }
            }
            throw Exception("No match found");
        }

        val candidates = findCandidates(occ);

        val subject = findSubject(occ, candidates)

        val list = findTheOne(occ, subject);
        for (i in 0 until list.size) {
            if (occ.equals(list[i])) list[i] = occ;
        }
        val res = JSONBuilder(applicationContext, subject).buildJsonOfOccurrences(list);
        ResponseWriter(applicationContext, subject).printJson(res);
    }

}