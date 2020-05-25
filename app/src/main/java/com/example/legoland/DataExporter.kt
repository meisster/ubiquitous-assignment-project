package com.example.legoland

import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

class DataExporter(private val project: Project, private val path: File) {

    fun export() {
        val doc = DocumentBuilderFactory.newInstance()
            .newDocumentBuilder()
            .newDocument()
        val root = doc.createElement("INVENTORY")
        for (block in project.blocks) {
            if(block.QTYFound == block.QTYInSet){
                continue
            }
            val item = doc.createElement("ITEM")

            val itemType = doc.createElement("ITEMTYPE")
            itemType.appendChild(doc.createTextNode(block.itemType.toString()))
            item.appendChild(itemType)

            val itemId = doc.createElement("ITEMID")
            itemId.appendChild(doc.createTextNode(block.itemID.toString()))
            item.appendChild(itemId)

            val color = doc.createElement("COLOR")
            color.appendChild(doc.createTextNode(block.colorId.toString()))
            item.appendChild(color)

            val qty = doc.createElement("QTYFILLED")
            qty.appendChild(doc.createTextNode((block.QTYInSet - block.QTYFound).toString()))
            item.appendChild(qty)

            root.appendChild(item)
        }
        doc.appendChild(root)

        val transformer = TransformerFactory.newInstance().newTransformer()
        transformer.setOutputProperty(OutputKeys.INDENT, "yes")
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")
        val outDir = File(path, "Export")
        outDir.mkdir()
        val file = File(outDir, "${project.name}-export.xml")
        transformer.transform(DOMSource(doc), StreamResult(file))

    }

}