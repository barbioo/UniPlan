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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.main.builder.api.RequestBuilder
import com.main.builder.api.RequestSender
import com.main.builder.api.RequestsFileManager
import com.main.builder.api.ResponseWriter
import com.main.builder.generic.JSONBuilder
import com.main.uniplan.MainActivity
import com.objects.Subject

class AddSubjects : AppCompatActivity() {
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
            buttonSend.setOnClickListener {
                val success = sendRequest()
                setContentView(R.layout.request_success) // Imposta il layout prima di trovare il TextView

                val textField = findViewById<TextView>(R.id.textView15)
                textField.text = success
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
    fun sendRequest(): String {
        return try {
        var subject = ""; var date = ""; var occurences = "";
        val editText1 = findViewById<EditText>(R.id.editText)
        val editText2 = findViewById<EditText>(R.id.editText2)
        val editText3 = findViewById<EditText>(R.id.editText3)

        subject = editText1.text.toString();
        date = editText2.text.toString();
        occurences = editText3.text.toString();

        val sub = Subject(subject, date);
        val rBuilder = RequestBuilder(applicationContext, sub);

        val s = Thread {
            rBuilder.buildFile();
        }
        s.start();

        fun getList(s: String): MutableList<String> {
            val tmp = s.split(";");
            return tmp.toMutableList();
        }

        val list = getList(occurences);
        val request = rBuilder.build(list);

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