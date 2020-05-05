package com.example.notes

import android.content.ContentValues
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_add_note.*
import kotlinx.android.synthetic.main.row.*
import java.lang.Exception
import java.time.LocalDateTime

class AddNoteActivity : AppCompatActivity() {

    val dbTable = "Notes"
    var id = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)

        try {
            val bundle:Bundle = intent.extras!!
            id = bundle.getInt("ID", 0)
            if (id != 0) {
                titleEt.setText(bundle.getString("name"))
                descEt.setText(bundle.getString("des"))
                createdAt.setText(bundle.getString("createdAt"))
            }
        } catch (ex: Exception){}
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addFunc(view: View) {
        var dbManager = DBManager(this)
        var values = ContentValues()
        values.put("CreatedAt", LocalDateTime.now().toString())
        values.put("Title", titleEt.text.toString())
        values.put("Description", descEt.text.toString())
        if (id == 0) {
            val ID = dbManager.DatabaseHelperNotes(this).insert(values)
            if (ID>0) {
                Toast.makeText(this, "Note is added", Toast.LENGTH_SHORT).show()
                finish()
            }
            else {
                Toast.makeText(this, "Error adding note...", Toast.LENGTH_SHORT).show()
            }
        }
        else {
            var selectionArgs = arrayOf(id.toString())
            val ID = dbManager.DatabaseHelperNotes(this).update(values, "ID=?", selectionArgs)
            if (ID > 0) {
                Toast.makeText(this, "Note is added", Toast.LENGTH_SHORT).show()
                finish()
            }
            else {
                Toast.makeText(this, "Error adding note...", Toast.LENGTH_SHORT).show()
            }
        }
    }
}