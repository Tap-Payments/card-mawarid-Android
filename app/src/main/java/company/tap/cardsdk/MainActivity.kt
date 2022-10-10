package company.tap.cardsdk

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
}