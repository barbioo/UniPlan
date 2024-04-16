package com.main.uniplan

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.uniplan.Plan
import com.example.uniplan.R
import com.example.uniplan.Summaries
import java.util.Calendar


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
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