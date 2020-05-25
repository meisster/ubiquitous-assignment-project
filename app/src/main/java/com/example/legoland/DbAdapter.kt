package com.example.legoland

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.legoland.SettingsManager.showArchived
import java.time.LocalDateTime


class DbAdapter(c: Context) {
    private val context: Context = c
    private var db: SQLiteDatabase? = null
    private var dbHelper: DbHelper? = null
    private var tableCategories = "Categories"
    private var tableCodes = "Codes"
    private var tableColors = "Colors"
    private var tableTypes = "ItemTypes"
    private var tableParts = "Parts"
    private var tableInventories = "Inventories"
    private var tableInventoriesParts = "InventoriesParts"


    fun open(): DbAdapter? {
        dbHelper = DbHelper(context)
        db = dbHelper!!.readableDatabase
        return this
    }

    fun close() {
        dbHelper!!.close()
    }

    fun getInventories(): ArrayList<Project> {
        val result = ArrayList<Project>()
        val selection = if(showArchived) null else "Active == 1"
        val cursor =
            db!!.query(
                tableInventories,
                arrayOf("id", "Name"),
                selection,
                null,
                null,
                null,
                "LastAccessed"
            )
        if (cursor.moveToFirst()) {
            for (i in 1..cursor.count) {
                result.add(
                    Project(
                        cursor.getInt(0),
                        cursor.getString(1)
                    )
                )
                if (!cursor.moveToNext()) {
                    break
                }
            }
        } else {
            println("There is no record")
        }
        cursor.close()
        return result
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun saveNewProject(data: Project) {
        if(projectExists(data.id)){
            return
        }
        val cv = ContentValues()
        cv.put("Name", data.name)
        cv.put("id", data.id)
        val date = LocalDateTime.now().year.toString() + LocalDateTime.now().dayOfYear.toString()
        cv.put("LastAccessed", date.toInt())
        db!!.insert(tableInventories, null, cv)
        data.blocks.forEach { block -> saveBlock(block) }
    }

    private fun projectExists(id: Int): Boolean {
        return db!!.query(
                tableInventories,
                null,
                "id == ?1",
                arrayOf(id.toString()),
                null,
                null,
                null
            ).moveToFirst()
    }

    private fun saveBlock(block: Block) {
        val cv = ContentValues()
        val id = findNextId()
        cv.put("id", id)
        cv.put("InventoryID", block.inventoryId)
        block.itemType = findTypeId(block.itemTypeCode)
        cv.put("TypeID", block.itemType)
        block.itemID = findItemId(block.itemCode)
        cv.put("ItemID", block.itemID)
        cv.put("QuantityInSet", block.QTYInSet)
        cv.put("ColorID", block.colorId)
        val res = db!!.insert(tableInventoriesParts, null, cv)
        if (res == -1L) {
            print("error")
        }
    }

    private fun findNextId(): Int {
        val cursor =
            db!!.query(tableInventoriesParts, arrayOf("id"), null, null, null, null, null)
        val count = cursor.count + 1
        cursor.close()
        return count
    }

    private fun findItemId(itemCode: String): Int {
        var result = -1
        val cursor =
            db!!.query(
                tableParts,
                arrayOf("CategoryID"),
                "Code == ?1",
                arrayOf(itemCode),
                null,
                null,
                null
            )
        if (cursor.moveToFirst()) {
            result = cursor.getInt(0)
        } else {
            println("There is no record")
        }
        cursor.close()
        return result
    }

    private fun findTypeId(itemTypeCode: String): Int {
        var result = -1
        val cursor =
            db!!.query(
                tableTypes,
                arrayOf("id"),
                "Code == ?1",
                arrayOf(itemTypeCode),
                null,
                null,
                null
            )
        if (cursor.moveToFirst()) {
            result = cursor.getInt(0)
        } else {
            println("There is no record")
        }
        cursor.close()
        return result
    }

    fun getProject(id: Int, name: String): Project {
        val project = Project(id, name)
        project.blocks = getBlocks(id)
        return project
    }

    private fun getBlocks(id: Int): java.util.ArrayList<Block> {
        val blocks = ArrayList<Block>()
        val cursor =
            db!!.query(
                tableInventoriesParts,
                arrayOf("ColorID", "ItemID", "QuantityInStore", "QuantityInSet"),
                "InventoryID == ?1",
                arrayOf(id.toString()),
                null,
                null,
                null
            )
        if (cursor.moveToFirst()) {
            for (i in 1..cursor.count) {
                var block = Block()
                block.inventoryId = id
                block.colorId = cursor.getInt(0)
                block.itemID = cursor.getInt(1)
                block.QTYFound = cursor.getInt(2)
                block.QTYInSet = cursor.getInt(3)
                block = getBlockName(block)
                block.colorName = getColorName(block.colorId)
                blocks.add(block)
                if (!cursor.moveToNext()) {
                    break
                }
            }
        } else {
            println("There is no record")
        }
        cursor.close()
        return blocks
    }

    private fun getColorName(colorId: Int): String {
        var result = ""
        val cursor =
            db!!.query(
                tableColors,
                arrayOf("Name"),
                "Code == ?1",
                arrayOf(colorId.toString()),
                null,
                null,
                null
            )
        if (cursor.moveToFirst()) {
            result = cursor.getString(0)
        } else {
            println("Nie ma żadnego rekordu")
        }
        cursor.close()
        return result
    }

    private fun getBlockName(block: Block): Block {
        val itemId = block.itemID

        val cursor =
            db!!.query(
                tableParts,
                arrayOf("Name", "Code"),
                "CategoryID == ?1",
                arrayOf(itemId.toString()),
                null,
                null,
                null
            )
        if (cursor.moveToFirst()) {
            block.name = cursor.getString(0)
            block.itemCode = cursor.getString(1)

        } else {
            println("There is no record")
        }
        cursor.close()
        return block
    }

    fun addCount(block: Block) {
        val cv = ContentValues()
        cv.put("QuantityInStore", block.QTYFound + 1)

        val res = db!!.update(tableInventoriesParts, cv, "ItemID =" + block.itemID, null)
        if (res == -1) {
            print("error")
        }
    }

    fun removeCount(block: Block) {
        val cv = ContentValues()
        cv.put("QuantityInStore", block.QTYFound - 1)

        val res = db!!.update(tableInventoriesParts, cv, "ItemID =" + block.itemID, null)
        if (res == -1) {
            print("error")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun archiveProject(id: Int) {
        val cv = ContentValues()
        cv.put("Active", 0)
        val date = LocalDateTime.now().year.toString() + LocalDateTime.now().dayOfYear.toString()
        cv.put("LastAccessed", date.toInt())
        val res = db!!.update(tableInventories, cv, "id == $id", null)
        if (res == -1) {
            print("error")
        }
    }


}
