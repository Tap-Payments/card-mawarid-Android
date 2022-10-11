package company.tap.cardsdk

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibrary.themekit.ThemeManager
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ThemeManager.loadTapTheme(this.resources,R.raw.defaultlighttheme,"lighttheme")
        ThemeManager.currentTheme = R.raw.defaultlighttheme.toString()
        LocalizationManager.loadTapLocale(this.resources,R.raw.lang)
        LocalizationManager.setLocale(this, Locale("en"))
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun tokenizeCard(view: View) {
       // dataConfiguration.startTokenize(tapCardInputView,this)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun saveCard(view: View) {
      //  multiPopup()

    }
}