package com.example.uniplan

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.TypedValue
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import android.graphics.Color;
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.main.builder.api.RequestsFileManager
import com.main.uniplan.MainActivity
import com.objects.Subject


class Plan : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val list = this.getAllSubject();
        if (list.isEmpty()) {
            setContentView(R.layout.activity_plan_empty);
        } else {
            setContentView(R.layout.activity_plan)
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
            val first = findViewById<Button>(R.id.button7);
            first.text = "${list[0].getSubject()}\n\n${list[0].getExamDate()}                                                       ${list[0].getRequestDate()}"
            first.setOnClickListener {
                setContentView(R.layout.activity_main)
                ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
                    val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                    insets
                }
            }
            addButtons(applicationContext, list);
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

    fun addButtons(context: Context, list: MutableList<Subject>) {
        val buttonStyle = androidx.appcompat.R.style.Widget_AppCompat_Button_Colored
        for (sub in list.drop(1)) {
            val layout = findViewById<LinearLayout>(R.id.rootLayout);
            val newBtn = Button(ContextThemeWrapper(context, buttonStyle), null, buttonStyle)
            newBtn.text = "${sub.getSubject()}\n\n${sub.getExamDate()}                                                       ${sub.getRequestDate()}"
            newBtn.backgroundTintList = ColorStateList.valueOf(Color.rgb(96, 60, 154))
            newBtn.setOnClickListener {
                setContentView(R.layout.activity_main)
                ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
                    val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                    insets
                }
            }
            layout.addView(newBtn)
        }
    }



}