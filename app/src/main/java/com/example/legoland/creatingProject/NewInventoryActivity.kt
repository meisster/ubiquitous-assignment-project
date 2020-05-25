package com.example.legoland.creatingProject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import com.example.legoland.MainActivity
import com.example.legoland.ProjectActivity
import com.example.legoland.R
import kotlinx.android.synthetic.main.activity_new_inventory.*

class NewInventoryActivity : AppCompatActivity() {

    private lateinit var creatingProjectManager: CreatingProjectManager
    private lateinit var validatingProjectManager: ValidatingProjectManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_inventory)

        validatingProjectManager =
            ValidatingProjectManager(
                this.baseContext,
                "-1"
            )

        id.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                validatingProjectManager.validated = 0
                addInventory.isEnabled = false
            }
        })

        checkInventory.setOnClickListener {
            if (name.text.isEmpty() || id.text.isEmpty()) {
                Toast.makeText(this, "Name and number cannot be empty!", Toast.LENGTH_LONG).show()
            } else {
                checkInventory(id.text.toString())
                while (validatingProjectManager.validated == 0) {
                    Toast.makeText(this, "Waiting for validation!", Toast.LENGTH_LONG).show()
                }
                if (validatingProjectManager.validated == 1) {
                    addInventory.isEnabled = true
                    Toast.makeText(
                        this,
                        "Validated. Now You can add this Project",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(this, "Cannot Find this Project !", Toast.LENGTH_LONG).show()
                }
            }
        }
        addInventory.setOnClickListener {
            addNewInventory(id.text.toString())

            Toast.makeText(this, "Adding Project", Toast.LENGTH_LONG).show()
            val i = Intent(this, MainActivity::class.java)

            startActivity(i)
        }
    }

    private fun checkInventory(nr: String) {
        validatingProjectManager =
            ValidatingProjectManager(
                this.baseContext,
                nr
            )
        validatingProjectManager.execute()
    }

    private fun addNewInventory(nr: String) {
        creatingProjectManager =
            CreatingProjectManager(this.baseContext, nr, name.text.toString())
        creatingProjectManager.execute()

    }


}

