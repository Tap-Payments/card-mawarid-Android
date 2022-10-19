package company.tap.cardsdk

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import company.tap.tapcardformkit.open.SdkMode
import company.tap.tapcardsdk.internal.logic.api.models.TapCardDataConfiguration
import company.tap.tapcardsdk.internal.logic.api.models.Token
import company.tap.tapcardsdk.open.CardInputForm
import company.tap.tapcardsdk.open.CardValidation
import company.tap.tapcardsdk.open.DataConfiguration
import company.tap.tapcardsdk.open.TapCardInputDelegate
import company.tap.tapnetworkkit.exception.GoSellError

class BottomSheetFragment : BottomSheetDialogFragment(), TapCardInputDelegate {
    lateinit var cardInputForm: CardInputForm
    lateinit var cardDataStatus: TextView
    lateinit var tokenizeButton: AppCompatButton
    lateinit var saveCardButton: AppCompatButton
    lateinit var _view: View
    var dataConfiguration: DataConfiguration = DataConfiguration
    lateinit var bottomSheetDialog: BottomSheetDialog

    private var topLeftCorner = 18f
    private var topRightCorner = 18f
    private var bottomRightCorner = 0f
    private var bottomLeftCorner = 0f
    var backgroundColor = Color.WHITE

    companion object {

        const val TAG = "CustomBottomSheetDialogFragment"

    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogStyle)
        val selectedLanguage: String = arguments?.getString("languageSelected").toString()
        val selectedTheme: String = arguments?.getString("themeSelected").toString()
        val selectedCurrency: String = arguments?.getString("selectedCurrency").toString()

        logicToHandleThemeLanguageChange(selectedLanguage, selectedTheme)

        _view = inflater.inflate(R.layout.bottom_sheet_dialog_layout, container, false)
        cardInputForm = _view.findViewById(R.id.cardInputForm)
        tokenizeButton = _view.findViewById(R.id.tokenizeButton)
        saveCardButton = _view.findViewById(R.id.saveCardButton)

        initializeForm(selectedLanguage, selectedTheme, selectedCurrency)

        cardDataStatus = _view.findViewById(R.id.cardDataStatus)

        return _view
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        tokenizeButton.setOnClickListener {
            dataConfiguration.startTokenize(cardInputForm, activity as AppCompatActivity)

        }
        saveCardButton.setOnClickListener {
            //  multiPopup()

        }

    }

    private fun logicToHandleThemeLanguageChange(
        selectedLanguage: String,
        selectedTheme: String

    ) {
        if (selectedTheme.contains("darktheme")) {
            dataConfiguration.setTheme(
                context, resources, null,
                R.raw.defaultdarktheme, selectedTheme
            )

        } else if (selectedTheme.contains("lighttheme")) {
            dataConfiguration.setTheme(
                context, resources, null,
                R.raw.defaultlighttheme, selectedTheme
            )

        }

        context?.let {
            dataConfiguration.setLocale(
                it,
                selectedLanguage,
                null,
                resources,
                R.raw.cardlocalisation
            )
        }

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun initializeForm(
        selectedLanguage: String,
        selectedTheme: String,
        selectedCurrency: String
    ) {
        dataConfiguration.addCardInputDelegate(this) //** Required **
        if (::cardInputForm.isInitialized)
            activity?.applicationContext?.let {
                dataConfiguration.initCardForm(
                    it, TapCardDataConfiguration(
                        "sk_test_kovrMB0mupFJXfNZWx6Etg5y",
                        "company.tap.goSellSDKExample",
                        null,
                        SdkMode.SAND_BOX,
                        selectedLanguage,
                        selectedCurrency,
                    ), cardInputForm
                )
            }

    }

    override fun cardTokenizedSuccessfully(token: Token, saveCardEnabled: Boolean) {
        println("Card Tokenized Succeeded : ")
        println("Token card : " + token.card?.firstSix.toString() + " **** " + token.card?.lastFour)
        println("Token card : " + token.card?.fingerprint.toString() + " **** " + token.card?.funding)
        println("Token card : " + token.card?.id.toString() + " ****** " + token.card?.name)
        println("Token card : " + token.card?.address.toString() + " ****** " + token.card?.`object`)
        println("Token card : " + token.card?.expirationMonth.toString() + " ****** " + token.card?.expirationYear)
        println("Token card saveCardEnabled : " + saveCardEnabled)
        showDialogAlert("cardTokenizedSuccessfully", "token is >> " + token.id)
    }

    override fun cardTokenizedFailed(goSellError: GoSellError?) {
        println("sdkError> errorMessage>>>>" + goSellError?.errorMessage)
        println("sdkError errorBody>>>>>" + goSellError?.errorBody)
        showDialogAlert("Tokenizationfailed", "Token failed reason >>" + goSellError?.errorMessage)
    }

    override fun backendUnknownError(message: String?) {
        println("backendUnknownError> errorMessage>>>>" + message)
        showDialogAlert("backendUnknownError", "backendUnknownError" + message)
    }

    override fun cardFormIsGettingReady() {
        println("<<<<<<<<<<cardFormIsGettingReady>>>>>>>>>")

    }

    override fun cardFormIsReady() {
        println("<<<<<<<<<<cardFormIsReady>>>>>>>>>")
    }

    override fun saveCardCheckBoxStatus(saveCardEnabled: Boolean) {
        println(" user has selected to saveCardCheckBoxStatus$saveCardEnabled")
    }

    override fun cardDataValidation(cardValidation: CardValidation) {
        cardDataStatus.text = " Card Data Form >>>" + cardValidation
    }

    private fun showDialogAlert(title: String?, message: String?) {
        val dialogBuilder = context?.let { AlertDialog.Builder(it) }
        // set message of alert dialog
        dialogBuilder?.setMessage(message)
            // if the dialog is cancelable
            ?.setCancelable(false)
            // positive button text and action
            ?.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, id ->
                this.dismiss()
            })
            // negative button text and action
            ?.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, id ->
                dialog.cancel()
            })

        // create dialog box
        val alert = dialogBuilder?.create()
        // set title for alert dialog box
        alert?.setTitle(title)
        // show alert dialog
        alert?.show()
    }


    @RequiresApi(Build.VERSION_CODES.N)
    fun saveCard(view: View) {


    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        bottomSheetDialog.setOnShowListener {
            val dialog = it as BottomSheetDialog
            val bottomSheetLayout =
                dialog.findViewById<FrameLayout>(company.tap.tapuilibrary.R.id.design_bottom_sheet)
            bottomSheetLayout?.background = getBackgroundDrawable()

        }
        bottomSheetDialog.behavior.isDraggable = true
        return bottomSheetDialog
    }

    private fun getBackgroundDrawable(): Drawable {
        val shape = ShapeDrawable(
            RoundRectShape(
                floatArrayOf(
                    topLeftCorner, topLeftCorner,
                    topRightCorner, topRightCorner,
                    bottomRightCorner, bottomRightCorner,
                    bottomLeftCorner, bottomLeftCorner
                ),
                null, null
            )
        )
        shape.paint.color = backgroundColor
        return shape
    }
}