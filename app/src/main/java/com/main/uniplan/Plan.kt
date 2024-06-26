package com.main.uniplan

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.graphics.Color;
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.main.uniplan.R
import com.main.builder.api.RequestsFileManager
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
            first.text =
                "${list[0].getSubject()}\n\n${list[0].getRequestDate()}                                                       ${list[0].getExamDate()}"
            first.setOnClickListener {
                val intent = Intent(applicationContext, Occurrence::class.java);
                intent.putExtra("subject", list[0].serialize());
                if (applicationContext !is Activity) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                applicationContext.startActivity(intent);
            }
            addButtons(applicationContext, list);
        }

    }

    fun occurrence(view: View?) {
        val intent = Intent(this, Occurrence::class.java)
        startActivity(intent)
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

    private fun addButtons(context: Context, list: MutableList<Subject>) {
        val buttonStyle = androidx.appcompat.R.style.Widget_AppCompat_Button_Colored
        for (sub in list.drop(1)) {
            val layout = findViewById<LinearLayout>(R.id.rootLayout);
            val newBtn = Button(ContextThemeWrapper(context, buttonStyle), null, buttonStyle)
            newBtn.text =
                "${sub.getSubject()}\n\n${sub.getRequestDate()}                                                       ${sub.getExamDate()}"
            newBtn.backgroundTintList = ColorStateList.valueOf(Color.rgb(96, 60, 154))
            newBtn.setOnClickListener {
                val intent = Intent(context, Occurrence::class.java);
                intent.putExtra("subject", sub.serialize());
                if (context !is Activity) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                applicationContext.startActivity(intent);
            }
            layout.addView(newBtn)
        }
    }
}