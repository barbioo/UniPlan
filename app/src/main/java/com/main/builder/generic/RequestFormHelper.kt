package com.main.builder.generic

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate

class RequestFormHelper() {
    fun subjectControl(sub: String): String {
        var corrected = sub;
        if (sub.contains("-")) {
            corrected = corrected.replace("-", " ");
        }
        return corrected;
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun dateControl(d: String): Boolean {
        if ((d.count {it == '-'}) != 2) {
            return false;
        }

        val tmp = d.split("-");
        val day = tmp[0]; val month = tmp[1]; val year = tmp[2];
        try {
            Integer.parseInt(day); Integer.parseInt(month); Integer.parseInt(year);
        } catch (_: Exception) {
            return false;
        }
        val intD = Integer.parseInt(day); val intM = Integer.parseInt(month); val intY= Integer.parseInt(year);
        if (intD < 0 || intD > 31) {
            return false;
        }
        if (intM < 0 || intM > 12) {
            return false;
        }
        val currentY = LocalDate.now().year;
        if (currentY > intY) {
            return false;
        }
        return true;
    }


}