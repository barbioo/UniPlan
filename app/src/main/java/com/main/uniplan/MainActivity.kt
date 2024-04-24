package com.main.uniplan

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.TextField
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.builder.api.RequestBuilder
import com.builder.api.RequestSender
import com.builder.api.RequestsFileManager
import com.example.uniplan.Plan
import com.example.uniplan.R
import com.example.uniplan.Summaries
import kotlinx.serialization.json.JsonBuilder
import java.util.Calendar


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

    fun Plan(view: View?) {
        val intent = Intent(this, Plan::class.java)
        startActivity(intent)
    }

    //rifaccio calendar, errore ed esce dall'app
    fun Calendar(view: View?) {
        val intent = Intent(this, Calendar::class.java)
        startActivity(intent)
    }

    fun mySummaries(view: View?) {
        val intent = Intent(this, Summaries::class.java)
        startActivity(intent)
    }
}