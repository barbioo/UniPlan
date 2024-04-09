package com.builder.generic

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.provider.CalendarContract
import objects.Occurrence
import java.text.SimpleDateFormat

class CalendarBuilder(
    private val applicationContext: Context,
    private val occurrence: Occurrence,
) {

    fun insertEvent(): String {
        return try {
            fun dateInMillS(date: String): Long {
                val sdfInput = SimpleDateFormat("dd-MM-yyyy");
                val datMls = sdfInput.parse(date);
                if (datMls != null) {
                    return datMls.time
                }
                throw java.lang.Exception()
            }

            val intent = Intent(Intent.ACTION_INSERT);
            intent.type = "vnd.android.cursor.item/event";
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(CalendarContract.Events.TITLE, occurrence.getSubject());
            intent.putExtra(CalendarContract.Events.DESCRIPTION, occurrence.getTopic());
            intent.putExtra(CalendarContract.Events.ALL_DAY, true);
            intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, dateInMillS(occurrence.getDate()));
            val basilColor = Color.rgb(151, 178, 78);
            intent.`package` = "com.google.android.calendar"

            applicationContext.startActivity(intent);
            "Successful"
        } catch (e: Exception) {
            e.message.toString()
        }
    }
}