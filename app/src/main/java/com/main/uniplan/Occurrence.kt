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
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.ContextThemeWrapper
import android.widget.Button
import android.widget.LinearLayout
import androidx.compose.material3.TextField
import com.main.builder.calendar.CalendarBuilder
import com.main.builder.generic.JSONBuilder


class Occurrence : AppCompatActivity() {
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
        val subject = intent.getStringExtra("subject")?.let { Subject.deserialize(it) };
        if (subject != null) {
            buildButtons(applicationContext, subject)
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

    private fun buildButtons(context: Context, subject: Subject) {
        val occList = JSONBuilder(applicationContext, subject).buildOccurrenceFromJson();
        val buttonStyle = androidx.appcompat.R.style.Widget_AppCompat_Button_Colored

        val layout = findViewById<LinearLayout>(R.id.occurrences_container);
        for (occ in occList) {
            val newBtn = Button(ContextThemeWrapper(context, buttonStyle), null, buttonStyle)
            newBtn.text =
                "${occ.getTopic()}\n\n${occ.getDate()}"
            newBtn.backgroundTintList = ColorStateList.valueOf(Color.rgb(96, 60, 154))
            newBtn.setOnClickListener {
                CalendarBuilder(applicationContext, occ).insertEvent();
            }
            layout.addView(newBtn)
        }
    }
}
