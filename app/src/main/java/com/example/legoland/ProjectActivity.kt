package com.example.legoland

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.annotation.RequiresApi
import kotlinx.android.synthetic.main.activity_project.*
import kotlinx.android.synthetic.main.content_main.linearLayout

class ProjectActivity : AppCompatActivity() {

    private val tableLayout by lazy { TableLayout(this) }
    private var dbAdapter: DbAdapter? = null
    private lateinit var project : Project

    @RequiresApi(26)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        Toast.makeText(this, "Loading Project blocks", Toast.LENGTH_LONG).show()

        val bundle = intent.extras
        val id = bundle!!.getInt("id")
        val name = bundle.getString("name")
        export.setOnClickListener {
            exportData()
            Toast.makeText(this, "Exported Data", Toast.LENGTH_LONG).show()
            val i = Intent(this, MainActivity::class.java)
            startActivity(i)
        }
        archive.setOnClickListener {
            archiveProject()
            Toast.makeText(this, "Archived Project", Toast.LENGTH_LONG).show()
            val i = Intent(this, MainActivity::class.java)
            startActivity(i)
        }
        dbAdapter = DbAdapter(this.baseContext)
        getProject(id, name!!)
        createTable()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId: Int = item.itemId
        if (itemId == android.R.id.home) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        return true
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun archiveProject() {
        dbAdapter!!.open()
        dbAdapter!!.archiveProject(project.id)
        dbAdapter!!.close()
    }

    private fun exportData() {
        DataExporter(project, filesDir).export()
    }

    private fun getProject(id : Int, name: String){
        dbAdapter!!.open()
        project = dbAdapter!!.getProject(id, name)
        dbAdapter!!.close()
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private fun createTable() {
        tableLayout.apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        for ((index, block) in project.blocks.withIndex()) {
            val row = TableRow(this)
            row.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            row.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
            if(index%2 != 0) row.setBackgroundColor(Color.LTGRAY)

            val nameText = TextView(this)
            nameText.apply {
                layoutParams = TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT
                )
                inputType = EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE
                isSingleLine = false
                text = "${block.name} [${block.itemCode}] ${block.colorName}"
                textSize = 20F
                setPadding(18,18,18,18)
            }


            val countText = TextView(this)
            countText.apply {
                layoutParams = TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT
                )
                inputType = EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE
                text = "${block.QTYFound} of ${block.QTYInSet}                    "
                textAlignment = View.TEXT_ALIGNMENT_CENTER
                textSize = 20F
                typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                setPadding(18,18,18,18)
            }

            val layout = LinearLayout(this)
            layout.apply{
                layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT)
                orientation = LinearLayout.VERTICAL
            }

            val removeButton = Button(this)
            removeButton.apply {
                layoutParams = TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT
                )
                text = "-"
                textAlignment = View.TEXT_ALIGNMENT_CENTER
                textSize = 20F
            }
            removeButton.setOnClickListener {
                removeItem(block)
            }

            val addButton = Button(this)
            addButton.apply {
                layoutParams = TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT
                )
                text = "+"
                textAlignment = View.TEXT_ALIGNMENT_CENTER
                textSize = 20F
            }
            addButton.setOnClickListener {
                addItem(block)
            }

            layout.addView(nameText)
            layout.addView(countText)
            row.addView(removeButton)
            row.addView(addButton)
            row.addView(layout)
            tableLayout.addView(row)
        }
        linearLayout.addView(tableLayout)
    }

    private fun addItem(block: Block) {
        if(block.QTYFound == block.QTYInSet){
            Toast.makeText(this, "You already have all You need", Toast.LENGTH_LONG).show()
        }else {
            Toast.makeText(this, "Adding Element", Toast.LENGTH_LONG).show()
            dbAdapter!!.open()
            dbAdapter!!.addCount(block)
            dbAdapter!!.close()
            refresh()
        }
    }

    private fun removeItem(block: Block) {
        if(block.QTYFound == 0){
            Toast.makeText(this, "You cannot have less then 0", Toast.LENGTH_LONG).show()
        }else {
            Toast.makeText(this, "Removing Element", Toast.LENGTH_LONG).show()
            dbAdapter!!.open()
            dbAdapter!!.removeCount(block)
            dbAdapter!!.close()
            refresh()
        }
    }
    private fun refresh(){
        val i = Intent(this, ProjectActivity::class.java)
        i.putExtra("id", project.id)
        i.putExtra("name", project.name)
        startActivity(i)
    }
}
