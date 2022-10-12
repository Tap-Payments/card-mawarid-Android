package company.tap.cardsdk.activities

import android.content.DialogInterface
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import company.tap.cardsdk.R
import company.tap.tapcardformkit.open.SdkMode
import company.tap.tapcardsdk.internal.logic.api.models.Charge
import company.tap.tapcardsdk.internal.logic.api.models.TapCardDataConfiguration
import company.tap.tapcardsdk.internal.logic.api.models.Token
import company.tap.tapcardsdk.open.CardInputForm
import company.tap.tapcardsdk.open.DataConfiguration
import company.tap.tapcardsdk.open.TapCardInputDelegate
import company.tap.tapnetworkkit.exception.GoSellError

class MainActivity : AppCompatActivity() , TapCardInputDelegate {

    lateinit var cardInputForm: CardInputForm
    var dataConfiguration: DataConfiguration = DataConfiguration
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val selectedLanguage:String = intent.getStringExtra("languageSelected").toString()
        val selectedTheme:String = intent.getStringExtra("themeSelected").toString()
        val selectedCurrency:String = intent.getStringExtra("selectedCurrency").toString()
        logicToHandleThemeLanguageChange(selectedLanguage,selectedTheme)
        setContentView(R.layout.activity_main)
        cardInputForm = findViewById(R.id.cardInputForm)

        initializeForm(selectedLanguage,selectedTheme,selectedCurrency)


    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun initializeForm(
        selectedLanguage: String,
        selectedTheme: String,
        selectedCurrency: String
    ) {
        dataConfiguration.addCardInputDelegate(this) //** Required **
        dataConfiguration.initCardForm(this, TapCardDataConfiguration("sk_test_kovrMB0mupFJXfNZWx6Etg5y","company.tap.goSellSDKExample" ,null, SdkMode.SAND_BOX,selectedLanguage,
            selectedCurrency,
            ),cardInputForm)

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
        if(selectedTheme.contains("darktheme")){
            dataConfiguration.setTheme(this, resources, null,
                R.raw.defaultdarktheme, selectedTheme)

        } else if(selectedTheme.contains("lighttheme")){
            dataConfiguration.setTheme(this, resources, null,
                R.raw.defaultlighttheme, selectedTheme)

        }

        dataConfiguration.setLocale(this, selectedLanguage, null, resources, R.raw.cardlocalisation)

    }

    override fun cardTokenizedSuccessfully(token: Token, saveCardEnabled:Boolean) {
        println("Card Tokenized Succeeded : ")
        println("Token card : " + token.card?.firstSix.toString() + " **** " + token.card?.lastFour)
        println("Token card : " + token.card?.fingerprint.toString() + " **** " + token.card?.funding)
        println("Token card : " + token.card?.id.toString() + " ****** " + token.card?.name)
        println("Token card : " + token.card?.address.toString() + " ****** " + token.card?.`object`)
        println("Token card : " + token.card?.expirationMonth.toString() + " ****** " + token.card?.expirationYear)
        println("Token card saveCardEnabled : " + saveCardEnabled)
        showDialogAlert("cardTokenizedSuccessfully","token is >> "+ token.id)
    }

    override fun cardTokenizedFailed(goSellError: GoSellError?) {
        println("sdkError> errorMessage>>>>" + goSellError?.errorMessage)
        println("sdkError errorBody>>>>>" + goSellError?.errorBody)
        showDialogAlert("Tokenizationfailed" , "Token failed reason >>"+ goSellError?.errorMessage)
    }

    override fun backendUnknownError(message: String?) {
        println("backendUnknownError> errorMessage>>>>" + message)
        showDialogAlert("backendUnknownError" , "backendUnknownError" + message)
    }


    override fun cardFormIsGettingReady() {
        println("<<<<<<<<<<cardFormIsGettingReady>>>>>>>>>")
    }

    override fun cardFormIsReady() {
        println("<<<<<<<<<<cardFormIsReady>>>>>>>>>")

    }

    override fun saveCardSelected(saveCardEnabled: Boolean) {
        println(" user has selected to saveCardSelected$saveCardEnabled")
    }

    override fun cardFormDataIsValid(cardFormDataValid: Boolean) {
        println("cardFormDataIsValid >>>>$cardFormDataValid")
        println("You can tokenize now >>>>")
    }


    private fun showDialogAlert(title : String? ,message: String?){
        val dialogBuilder = AlertDialog.Builder(this)
        // set message of alert dialog
        dialogBuilder.setMessage(message)
            // if the dialog is cancelable
            .setCancelable(false)
            // positive button text and action
            .setPositiveButton("OK", DialogInterface.OnClickListener {
                    dialog, id -> finish()
            })
            // negative button text and action
            .setNegativeButton("Cancel", DialogInterface.OnClickListener {
                    dialog, id -> dialog.cancel()
            })

        // create dialog box
        val alert = dialogBuilder.create()
        // set title for alert dialog box
        alert.setTitle(title)
        // show alert dialog
        alert.show()
    }
}