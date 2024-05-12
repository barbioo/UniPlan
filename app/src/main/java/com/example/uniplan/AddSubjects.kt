package com.example.uniplan

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
import com.main.builder.api.NRequestsManager
import com.main.builder.api.RequestBuilder
import com.main.builder.api.RequestSender
import com.main.builder.api.RequestsFileManager
import com.main.builder.api.ResponseWriter
import com.main.builder.generic.JSONBuilder
import com.main.uniplan.MainActivity
import com.objects.Subject
import kotlinx.coroutines.InternalCoroutinesApi

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
            val sub = buildSubject()
            val occ = buildOccList()

            setContentView(R.layout.loading);
            try {
                val t = Thread {
                    sendRequest(sub, occ);

                    runOnUiThread {
                        NRequestsManager(applicationContext).addOne()
                        setContentView(R.layout.request_success);
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
    private fun buildSubject(): Subject {
        return try {
            var subject = ""; var date = "";
            val editText1 = findViewById<EditText>(R.id.editText)
            val editText2 = findViewById<EditText>(R.id.editText2)

            subject = editText1.text.toString();
            date = editText2.text.toString();

            Subject(subject, date);

        } catch (e: Exception) {
            Subject(e.message.toString(), "");
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
        val jsonList = j.buildJsonOfOccurences(occList);
        out.renewWriter(applicationContext, sub);
        out.printJson(jsonList);
            "true"
        } catch (e: Exception) {
            e.message.toString()
        }
    }


}