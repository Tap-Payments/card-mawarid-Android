package company.tap.cardsdk

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import company.tap.tapcardsdk.open.CardInputForm
import company.tap.tapcardsdk.open.DataConfiguration
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibrary.themekit.ThemeManager
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var cardDetailsText:TextView
    lateinit var cardInputForm: CardInputForm
    var dataConfiguration: DataConfiguration = DataConfiguration
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val selectedLanguage:String = intent.getStringExtra("languageSelected").toString()
        val selectedTheme:String = intent.getStringExtra("themeSelected").toString()
        logicToHandleThemeLanguageChange(selectedLanguage,selectedTheme)

        setContentView(R.layout.activity_main)
        cardDetailsText = findViewById(R.id.cardDetailsText)
        cardInputForm = findViewById(R.id.cardInputForm)
        val cardDetText : String = LocalizationManager.getValue("title","cardForm")
        cardDetailsText.text = cardDetText
    }



    @RequiresApi(Build.VERSION_CODES.N)
    fun tokenizeCard(view: View) {
        dataConfiguration.startTokenize(cardInputForm,this)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun saveCard(view: View) {
      //  multiPopup()

    }
    private fun logicToHandleThemeLanguageChange(
        selectedLanguage: String,
        selectedTheme: String

    ) {
        if(selectedTheme!=null && selectedTheme.contains("darktheme")){
            dataConfiguration.setTheme(this, resources, null,
                R.raw.defaultdarktheme, selectedTheme)

        } else if(selectedTheme!=null && selectedTheme.contains("lighttheme")){
            dataConfiguration.setTheme(this, resources, null,
                R.raw.defaultlighttheme, selectedTheme)

        }

        dataConfiguration.setLocale(this, selectedLanguage, null, resources, R.raw.cardlocalisation)

    }
}