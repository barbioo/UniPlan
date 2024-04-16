package com.builder.generic;

import android.content.Context
import objects.*;
import java.io.IOException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.util.Calendar
import java.util.Properties

class JSONBuilder(
    private var json: String,
    private val applicationContext: Context
    ) {

    constructor(applicationContext: Context, examSubject: String, examDate: String) : this(
        readFileContent(applicationContext, examSubject, examDate),
        applicationContext
    );

    companion object {
        private fun readFileContent(applicationContext: Context, examSubject: String, examDate: String): String {
            return try {
                val inp = BufferedReader(FileReader(File(applicationContext.getExternalFilesDir("data_responses"),  "$examSubject-$examDate.json")));
                return inp.readText();
            } catch (e: IOException) {
                return e.message.toString();
            }
        }
    }

    fun getJson(): String {
        return json;
    }

    fun buildOccurrence(subject: String): MutableList<Occurrence> {
        val res = mutableListOf<Occurrence>();

        fun applyAttribute(o: Occurrence, i: Int, inp: String) {
            require(i <= 3);
            when (i) {
                0 -> o.setUser(inp);
                1 -> o.setSubject(inp);
                2 -> o.setDate(inp);
                3 -> o.setTopic(inp);
            }
        }

        fun getEnvVariable(k: String): String {
            return try {
                val config = applicationContext.assets.open("settings.env")
                val properties = Properties()
                properties.load(config)
                config.close()
                properties.getProperty(k)
            } catch (e: IOException) {
                e.printStackTrace()
                "default"
            }
        }

        fun getPrettyDate(date: String): String {
            val tmp = date.split("/");
            var newDate = tmp[1] + "-" + tmp[0] + "-";
            newDate += Calendar.getInstance().get(Calendar.YEAR);
            return newDate;
        }

        fun getTopicAtDate(date: String): String {
            return try {
                val jsonObject = JSONObject(getRespondContent());
                val topic = jsonObject.getJSONArray(date).getString(0);
                topic;
            } catch (e: Exception) {
                ""
            }
        }

        val dates = getTopicDates(getRespondContent());

        for (date in dates) {
            val tmp = Occurrence();

            //user setting
            applyAttribute(tmp, 0, getEnvVariable("USERNAME"));

            //subject setting
            applyAttribute(tmp, 1, subject);

            //date setting
            applyAttribute(tmp, 2, getPrettyDate(date));

            //topic setting
            applyAttribute(tmp, 3, getTopicAtDate(date));

            res.add(tmp);
        }
        return res;
    }

    private fun getRespondContent(): String {
        return try {
            val respond = JSONObject(json);
            val content = respond.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");
            content
        } catch (e: Exception) {
            ""
        }
    }

    private fun getTopicDates(content: String): MutableList<String> {
        val jContent: JSONObject = try {
            JSONObject(content);
        } catch (e: Exception) {
            JSONObject("");
        }
        val keys = jContent.keys()
        val res = mutableListOf<String>();
        for(nodeName in keys) {
            res.add(nodeName);
        }
        return res;
    }

}