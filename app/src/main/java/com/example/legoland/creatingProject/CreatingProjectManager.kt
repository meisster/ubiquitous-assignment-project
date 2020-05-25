package com.example.legoland.creatingProject

import android.content.Context
import android.os.AsyncTask
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.legoland.Block
import com.example.legoland.DbAdapter
import com.example.legoland.Project
import com.example.legoland.SettingsManager.prefix
import org.w3c.dom.Element
import org.w3c.dom.NodeList
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.URL
import javax.xml.parsers.DocumentBuilderFactory

class CreatingProjectManager(private val context: Context, private val number: String, private val name: String) :AsyncTask<String,Int,String> (){

    private var dbAdapter = DbAdapter(context)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun doInBackground(vararg params: String?): String {
        val url = URL("http://$prefix$number.xml")
        val connection = url.openConnection()
        connection.connect()
        val isStream = url.openStream()
        saveToFile(isStream)
        isStream.close()
        val data = loadDataFromXML(number)
        dbAdapter.open()
        dbAdapter.saveNewProject(data)
        dbAdapter.close()
        return ""
    }

    private fun saveToFile(stream: InputStream) {
        val testDirectory = File(context.filesDir!!.toString() + "/projects")
        if(!testDirectory.exists()) testDirectory.mkdir()
        val fos = FileOutputStream("$testDirectory/$number.xml")
        stream.copyTo(fos)
        fos.flush()
        fos.close()
        stream.close()
    }


    private fun loadDataFromXML(nr : String) : Project {
        val xmlFile = File(context.filesDir!!.toString() + "/projects/$nr.xml")
        val xmlDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlFile)
        xmlDoc.documentElement.normalize()
        val items : NodeList = xmlDoc.getElementsByTagName("ITEM")
        val blocks = ArrayList<Block>()
        for(i in 0 until items.length){
            val itemNode = items.item(i)
            val elem = itemNode as Element
            val children = elem.childNodes
            val block = Block()
            block.inventoryId = nr.toInt()
            for(j in 0 until children.length){
                val node = children.item(j)
                if(node is Element){
                    when(node.nodeName){
                        "ITEMTYPE" -> {
                            block.itemTypeCode = node.textContent
                        }
                        "ITEMID" -> {
                            block.itemCode = node.textContent
                        }
                        "QTY" -> {
                            block.QTYInSet = node.textContent.toInt()
                        }
                        "COLOR" -> {
                            block.colorId = node.textContent.toInt()
                        }
                        "ALTERNATE" -> {
                            if(node.textContent != "N"){
                                block.toSave = false
                            }
                        }
                    }
                }
            }
            blocks.add(block)
        }
        val project = Project(number.toInt(), name)
        project.blocks = blocks.filter { block -> block.toSave } as ArrayList<Block>
        return project
    }


}