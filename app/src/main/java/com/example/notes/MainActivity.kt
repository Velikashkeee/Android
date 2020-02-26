package com.example.notes

import android.app.SearchManager
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.row.view.*

class MainActivity : AppCompatActivity() {

    var listNotes = ArrayList<Note>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Load from DB
        LoadQuery("@")
    }

    override fun onResume() {
        super.onResume()
        LoadQuery("@")
    }

    private fun LoadQuery(title: String) {
        var dbManager = DBManager(this)
        val projections = arrayOf("ID", "Title", "Description")
        val selectionArgs = arrayOf(title)

        val cursor = dbManager.DatabaseHelperNotes(this).Query(
            projections,
            "Title like ?", selectionArgs, "Title"
        )
        listNotes.clear()
        if (cursor.moveToFirst()) {
            do {
                val nodeId = cursor.getInt(cursor.getColumnIndex("ID"))
                val title = cursor.getString(cursor.getColumnIndex("Title"))
                val description = cursor.getString(cursor.getColumnIndex("Description"))
                listNotes.add(Note(nodeId, title, description))
            } while (cursor.moveToNext())
        }

        //set adapter
        notesLv.adapter = MyNotesAdapter(this, listNotes)

        //get total number of tasks from ListView
        val total = notesLv.count

        //actionbar
        val mActionBar = supportActionBar
        if (mActionBar != null) {
            // set to actionbar as subtitle of actionbar
            mActionBar.subtitle = "You have $total note(s) in list..."
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        //searchView
        val sv = menu?.findItem(R.id.app_bar_search)?.actionView as SearchView?
        val sm = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        sv?.setSearchableInfo(sm.getSearchableInfo(componentName))
        sv?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                LoadQuery("%$query%")
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                LoadQuery("%$newText%")
                return false
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item != null) {
            when (item.itemId) {
                R.id.addNote -> {
                    startActivity(Intent(this, AddNoteActivity::class.java))
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    inner class MyNotesAdapter : BaseAdapter {
        var listNotesAdapter = ArrayList<Note>()
        var context: Context? = null

        constructor(context: Context, listNotesAdapter: ArrayList<Note>) : super() {
            this.listNotesAdapter = listNotesAdapter
            this.context = context
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            //inflate layout row.xml
            var myView = layoutInflater.inflate(R.layout.row, null)
            val myNote = listNotesAdapter[position]
            myView.titleTv.text = myNote.nodeName
            myView.descTv.text = myNote.nodeDes
            //delete button click
            myView.deleteBtn.setOnClickListener {
                var dbManager = DBManager(this.context!!)
                val selectionArgs = arrayOf(myNote.nodeID.toString())
                dbManager.DatabaseHelperNotes(this.context!!).delete("ID=?", selectionArgs)
                LoadQuery("%")
            }
            //edit//update button click
            myView.setOnClickListener { GoToUpdateFun(myNote) }
            //copy btn click
            myView.copyBtn.setOnClickListener {
                //get title
                val title = myView.titleTv.text.toString()
                //get description
                val desc = myView.descTv.text.toString()
                //concatenate
                val s = title + "\n" + desc
                val cb = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                cb.text = s //add to clipboard
                Toast.makeText(this@MainActivity, "Copied...", Toast.LENGTH_SHORT).show()
            }
            //share btn click
            myView.shareBtn.setOnClickListener {
                //get title
                val title = myView.titleTv.text.toString()
                //get description
                val desc = myView.descTv.text.toString()
                //concatenate
                val s = title + "\n" + desc
                val shareIntent = Intent()
                shareIntent.action = Intent.ACTION_SEND
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_TEXT, s)
                startActivity(Intent.createChooser(shareIntent, s))
            }

            return myView
        }

        override fun getItem(position: Int): Any {
            return listNotesAdapter[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return listNotesAdapter.size
        }
    }

    private fun GoToUpdateFun(myNote: Note) {
        var intent = Intent(this, AddNoteActivity::class.java)
        intent.putExtra("ID", myNote.nodeID) //put id
        intent.putExtra("name", myNote.nodeName) //put name
        intent.putExtra("des", myNote.nodeDes) //put decription
        startActivity(intent) //start activity
    }
}
