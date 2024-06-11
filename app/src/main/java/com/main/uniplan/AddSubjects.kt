package com.main.uniplan

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.uniplan.R
import com.google.android.material.snackbar.Snackbar
import com.main.builder.api.NRequestsManager
import com.main.builder.api.RequestBuilder
import com.main.builder.api.RequestSender
import com.main.builder.api.RequestsFileManager
import com.main.builder.api.ResponseWriter
import com.main.builder.generic.JSONBuilder
import com.main.builder.generic.RequestFormHelper
import com.objects.Subject
import kotlinx.coroutines.InternalCoroutinesApi
import org.json.JSONArray
import org.json.JSONObject

class AddSubjects : AppCompatActivity() {
    @OptIn(InternalCoroutinesApi::class)
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_subjects)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val buttonSend = findViewById<Button>(R.id.button4)
        buttonSend.setOnClickListener {
            val sub = buildSubject()                   //metodi che costruiscono rispettivamente la materia, la data e la lista delle occorrenze.
            val date = buildDate();
            if (date == "Unsuccessful") {
                Snackbar.make(findViewById(android.R.id.content), "Bad date format", Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val subject = Subject(sub, date)
            val occ = buildOccList()

            setContentView(R.layout.loading);
            try {
                val t = Thread {
                    val response = sendRequest(subject, occ);

                    runOnUiThread {
                        if (response == "true") {
                            // Ottieni la risposta JSON dal sender (ad esempio, da un file o direttamente dalla stringa di risposta)
                            val jsonResponse = "" // Sostituisci con la logica per ottenere la risposta JSON
                            val intent = Intent(this, Occurrence::class.java)
                            intent.putExtra("responseJson", jsonResponse)
                            startActivity(intent)
                        } else {
                            Snackbar.make(findViewById(android.R.id.content), "Request failed: $response", Snackbar.LENGTH_LONG).show()
                        }
                    }
                }
                t.start();
            } catch (e: Exception) {
                setContentView(R.layout.request_success)
                val textView = findViewById<TextView>(R.id.textView15)
                textView.text = "Something went wrong with your request, please check your setup and retry"
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


    @RequiresApi(Build.VERSION_CODES.O)
    private fun buildSubject(): String {
        return try {
            val help = RequestFormHelper();
            var subject = "";
            val editText1 = findViewById<EditText>(R.id.editText)
            subject = help.subjectControl(editText1.text.toString());
            subject;
        } catch (e: Exception) {
            e.message.toString()
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun buildDate(): String {
        val help = RequestFormHelper();
        val editText2 = findViewById<EditText>(R.id.editText2);
        val date = editText2.text.toString()
        return if (!help.dateControl(date)) {
            "Unsuccessful"
        } else {
            date
        }
    }

    private fun buildOccList(): MutableList<String> {
        val editText3 = findViewById<EditText>(R.id.editText3)
        val occurrences = editText3.text.toString()
        val tmp = occurrences.split(";");
        return tmp.toMutableList();
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun sendRequest(sub: Subject, occ: MutableList<String>): String {
        return try {

        val rBuilder = RequestBuilder(applicationContext, sub);

        val s = Thread {
            rBuilder.buildFile();
        }
        s.start();

        val request = rBuilder.build(occ);

        val sender = RequestSender();
        sender.setUserRequest(request);

        var respond = "";
        val t = Thread {
            respond = sender.sendStandardCall();
        }
        t.start(); t.join();
        val out = ResponseWriter(applicationContext, sub);
        out.printJson(respond);

        RequestsFileManager(applicationContext).addRequestToList(sub);

        val j = JSONBuilder(applicationContext, sub);
        val occList = j.buildOccurrenceFromResponse(sub.getSubject())
        val jsonList = j.buildJsonOfOccurrences(occList);
        out.renewWriter(applicationContext, sub);
        out.printJson(jsonList);
            "true"
        } catch (e: Exception) {
            e.message.toString()
        }
    }

    private fun getResponseJson(): String {
        // Implementa la logica per ottenere la risposta JSON
        // Può essere una lettura da file o direttamente dalla stringa di risposta
        // In questo esempio, restituisce una stringa JSON simulata
        return """
            [
                {"subject": "Math", "date": "2023-05-28", "topic": "Algebra"},
                {"subject": "Math", "date": "2023-05-28", "topic": "Geometry"},
                {"subject": "Science", "date": "2023-05-28", "topic": "Physics"},
                {"subject": "Science", "date": "2023-05-28", "topic": "Chemistry"}
            ]
        """
    }

    private fun parseResponse(responseJson: String): List<Subject> {
        val subjects = mutableListOf<Subject>()
        try {
            val jsonArray = JSONArray(responseJson)
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val subjectName = jsonObject.getString("subject")
                val date = jsonObject.getString("date")
                val topic = jsonObject.getString("topic")

                val subject = subjects.find { it.getName() == subjectName && it.getDate() == date } ?: Subject(subjectName, date).also { subjects.add(it) }
                subject.addTopic(topic)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return subjects
    }

    private fun displaySubjectsAndTopics(subjects: List<Subject>) {
        val textView = findViewById<TextView>(R.id.textViewSubjects)
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

}