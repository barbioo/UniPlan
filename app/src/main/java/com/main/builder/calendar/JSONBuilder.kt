package com.main.builder.calendar;

import android.content.Context
import com.objects.Subject
import objects.*;
import org.json.JSONArray
import org.json.JSONException
import java.io.IOException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

class JSONBuilder(
    private var json: String,
    private val applicationContext: Context
    ) {

    constructor(applicationContext: Context, examSubject: String, requestDate: String, examDate: String) : this(
        readFileContent(applicationContext, "$examSubject-$requestDate-$examDate.json"),
        applicationContext
    );

    constructor(applicationContext: Context, sub: Subject): this(
        readFileContent(applicationContext, "${sub.getSubject()}-${sub.getRequestDate()}-${sub.getExamDate()}.json"),
        applicationContext
    )

    constructor(applicationContext: Context, fileName: String): this(
        readFileContent(applicationContext, fileName),
        applicationContext
    )

    fun setJson(applicationContext: Context, examSubject: String, requestDate: String, examDate: String) {
        json = readFileContent(applicationContext, "$examSubject-$requestDate-$examDate.json");
    }

    companion object {
        private fun readFileContent(applicationContext: Context, fileName: String): String {
            return try {
                val f = File(applicationContext.getExternalFilesDir("data_responses"),  fileName);
                if (f.exists()){
                    val inp = BufferedReader(FileReader(f));
                    inp.readText();
                } else {
                    throw IOException("No such file")
                }
            } catch (e: IOException) {
                e.message.toString();
            }
        }

        fun isJson(json: String): Boolean {
            return try {
                if (json.isBlank()) {
                    false
                } else {
                    JSONObject(json)
                    true
                }
            } catch (e: JSONException) {
                false
            }
        }

    }

    fun getJson(): String {
        return json;
    }


    fun buildOccurrenceFromResponse(subject: String): MutableList<Occurrence> {
        val res = mutableListOf<Occurrence>();

        fun applyAttribute(o: Occurrence, i: Int, inp: String) {
            require(i <= 3);
            when (i) {
                0 -> o.setDone(inp.toBoolean());
                1 -> o.setSubject(inp);
                2 -> o.setDate(inp);
                3 -> o.setTopic(inp);
            }
        }

        fun getRespondContent(): String {
            return try {
                val respond = JSONObject(json);
                val content = respond.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");
                content
            } catch (e: Exception) {
                e.message.toString()
            }
        }

        fun getTopicAtDate(date: String): String {
            return try {
                val jsonObject = JSONObject(getRespondContent());
                val topic = jsonObject.getString(date);
                topic;
            } catch (e: Exception) {
                e.message.toString()
            }
        }

        val dates = getTopicDates(getRespondContent());

        for (date in dates) {
            val tmp = Occurrence();

            //user setting
            applyAttribute(tmp, 0, "false");

            //subject setting
            applyAttribute(tmp, 1, subject);

            //date setting
            applyAttribute(tmp, 2, date);

            //topic setting
            applyAttribute(tmp, 3, getTopicAtDate(date));

            res.add(tmp);
        }
        return res;
    }

    fun buildJsonOfOccurences(list: MutableList<Occurrence>): String {
        return try {
            val res = JSONArray();
            list.forEach {occ ->
                val tmp = JSONObject();
                tmp.put("subject", occ.getSubject());
                tmp.put("date", occ.getDate());
                tmp.put("topic", occ.getTopic());
                tmp.put("done", occ.getDone());
                res.put(tmp);
            }
            res.toString();
        } catch (e: JSONException) {
            e.message.toString()
        }
    }

    fun buildOccurenceFromJson(): MutableList<Occurrence> {
        val res = mutableListOf<Occurrence>();

        val list = try {
            JSONArray(json)
        } catch (_: JSONException) {
            throw Exception("Error in building occurence list")
        }

        for(i in 0 until list.length()) {
            val o = Occurrence();
            val tmp = list.getJSONObject(i);
            o.setSubject(tmp.getString("subject"));
            o.setTopic(tmp.getString("topic"));
            o.setDate(tmp.getString("date"));
            o.setDone(tmp.getBoolean("done"));
            res.add(o);
        }

        return res;
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