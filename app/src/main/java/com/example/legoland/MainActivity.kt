package com.example.legoland

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.example.legoland.creatingProject.NewInventoryActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private val tableLayout by lazy { TableLayout(this) }

    private lateinit var dbAdapter: DbAdapter


    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        SettingsManager.readSettings(filesDir)
        fab.setOnClickListener {
            val i = Intent(this, NewInventoryActivity::class.java)
            startActivity(i)
        }

        val lp = TableLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        )

        tableLayout.apply {
            layoutParams = lp
            isShrinkAllColumns = true
            textAlignment = View.TEXT_ALIGNMENT_CENTER
        }

        val pl = loadInventories()

        createTable(pl)
    }

    private fun loadInventories() : ArrayList<Project>{
        dbAdapter = DbAdapter(this.baseContext).open()!!
        val res = dbAdapter.getInventories()
        dbAdapter.close()
        return res
    }



    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> {
                val i = Intent(this, SettingsActivity::class.java)
                startActivity(i)
                true}
            else -> super.onOptionsItemSelected(item)
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private fun createTable(results: List<Project>) {
        for ((index, result) in results.withIndex()) {
            val row = TableRow(this)
            row.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            )
            row.textAlignment = View.TEXT_ALIGNMENT_CENTER
            if(index%2 != 0) row.setBackgroundColor(Color.LTGRAY)

            val rowText = TextView(this)
            rowText.apply {
                layoutParams = TableRow.LayoutParams(
                        TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.MATCH_PARENT
                )
                text = result.name
                textAlignment = View.TEXT_ALIGNMENT_CENTER
                textSize = 30F
                setPadding(18,18,18,18)
            }
            row.addView(rowText)
            tableLayout.addView(row)
            row.setOnClickListener{
                val i = Intent(this, ProjectActivity::class.java)
                i.putExtra("id", result.id)
                i.putExtra("name", result.name)
                startActivity(i)
            }
        }
        linearLayout.addView(tableLayout)
    }

}