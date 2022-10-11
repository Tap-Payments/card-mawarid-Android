/*
 * Created By AhlaamK 8/5/22, 7:13 PM
 * Copyright (c) 2022    Tap Payments.
 * All rights reserved.
 */

package company.tap.cardsdk

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.textfield.TextInputEditText

import company.tap.tapcardformkit.open.enums.CardType
import kotlin.properties.Delegates

class SelectChoiceActivity : AppCompatActivity() {

     private lateinit var radioGroup:RadioGroup
     private lateinit var radioGroup2:RadioGroup


      private lateinit var selectedUserLanguage :String
      private lateinit var selectedUserTheme :String

      private  var defaultCardHolderName : String?=null
    var toggleButtonGroup : MaterialButtonToggleGroup?=null

    var selectedCurrency : String?=null
    var selectedCardType : String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_choice)
        initializeViews()
        saveTheSelectedValue()
    }

    private fun initializeViews() {
        radioGroup = findViewById(R.id.radioGroup)
        radioGroup2 = findViewById(R.id.radioGroup2)

        toggleButtonGroup = findViewById(R.id.toggleButtonGroup)



        selectedCurrency= "KWD"
        toggleButtonGroup?.addOnButtonCheckedListener { toggleButtonGroup, checkedId, isChecked ->

            if (isChecked) {
                when (checkedId) {
                    R.id.btnKuwait -> selectedCurrency = "KWD"
                    R.id.btnUae -> selectedCurrency = "AED"
                    R.id.btnSaudi -> selectedCurrency = "SAR"
                }
            } else {
                if (toggleButtonGroup.checkedButtonId == View.NO_ID) {
                    showToast("Nothing Selected")
                }
            }
        }


    }

    private fun saveTheSelectedValue() {
        if (radioGroup.checkedRadioButtonId == R.id.lang_english) {
            selectedUserLanguage = "en"
        }
        if (radioGroup.checkedRadioButtonId == R.id.lang_arabic) {
            selectedUserLanguage = "ar"
        }
        radioGroup.setOnCheckedChangeListener { _, _ ->
            // This will get the radiobutton that has changed in its check state .
            if (radioGroup.checkedRadioButtonId == R.id.lang_english) {
                selectedUserLanguage = "en"
            }
            if (radioGroup.checkedRadioButtonId == R.id.lang_arabic) {
                selectedUserLanguage = "ar"
            }
        }
        if (radioGroup2.checkedRadioButtonId == R.id.theme_dark)
        {
            selectedUserTheme = "darktheme"
        }
        if (radioGroup2.checkedRadioButtonId == R.id.theme_light)
        {
            selectedUserTheme = "lighttheme"
        }
        radioGroup2.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->

            if (radioGroup2.checkedRadioButtonId == R.id.theme_dark)
            {
                selectedUserTheme = "darktheme"
            }
            if (radioGroup2.checkedRadioButtonId == R.id.theme_light)
            {
                selectedUserTheme = "lighttheme"
            }
        })




    }

    fun startTokenizationactivity(view: View){
        if(::selectedUserLanguage.isInitialized && ::selectedUserTheme.isInitialized) {
            println("defaultCardHolderName is"+defaultCardHolderName)
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("languageSelected", selectedUserLanguage)
            intent.putExtra("themeSelected", selectedUserTheme)

            intent.putExtra("selectedCurrency", selectedCurrency)

            finish()
            startActivity(intent)

        } else{
            Toast.makeText(this, "Please make your selection", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showToast(str: String) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menumain, menu)
        return true
    }

    // actions on click menu items
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {

        R.id.action_CreateCustomer -> {
            val intent = Intent(this, CustomerActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            recreate()
            startActivity(intent)

            true

        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }
}