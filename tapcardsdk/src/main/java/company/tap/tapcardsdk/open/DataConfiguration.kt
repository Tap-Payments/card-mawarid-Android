package company.tap.tapcardsdk.open


import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import company.tap.tapcardsdk.R
import company.tap.tapcardsdk.internal.logic.api.ApiService
import company.tap.tapcardsdk.internal.logic.api.CardViewEvent
import company.tap.tapcardsdk.internal.logic.api.CardViewModel
import company.tap.tapcardsdk.internal.logic.api.enums.TransactionMode
import company.tap.tapcardsdk.internal.logic.api.models.*
import company.tap.tapcardsdk.internal.logic.datamanagers.PaymentDataSource
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapnetworkkit.connection.NetworkApp
import company.tap.tapuilibrary.themekit.ThemeManager
import java.util.*


/**
 * Created by AhlaamK on 3/23/22.

Copyright (c) 2022    Tap Payments.
All rights reserved.
 **/
@SuppressLint("StaticFieldLeak")
object DataConfiguration  {

    @JvmField
    var tapCardInputDelegate: TapCardInputDelegate? = null
    private var defaultCardHolderName: String? = null
    lateinit var _context: Context
    private var defaultScannerBorderColor: Int? = null
    /**
    Handles  the card forum engine with the required theme on demand. It takes below
    - Parameter resources: The local resources required to handle theme //Optional
    - Parameter urlString: The urlString to load theme from URL //Optional
    - Parameter urlPathLocal: The local resources path required to handle theme
    - Parameter fileName: The fileName to identify theme
     */
   fun setTheme(context: Context?, resources: Resources?, urlString:String?, urlPathLocal: Int?, fileName:String?){
       if(resources!=null && urlPathLocal!=null){
           if(fileName!=null && fileName.contains("dark")){
               if (urlPathLocal != null) {
                   ThemeManager.loadTapTheme(resources,urlPathLocal,"darktheme")
               }
           }else {
               if (urlPathLocal != null) {
                   ThemeManager.loadTapTheme(resources,urlPathLocal,"lighttheme")
               }
           }
       }else if(urlString!=null){
           if (context != null) {
               println("urlString>>>"+urlString)
               ThemeManager.loadTapTheme(context,urlString,"darktheme")
           }
       }

   }



    /**
    Handles  the card forum engine with the required locale on demand. It takes below
    - Parameter resources: The local resources required to handle locale //Optional
    - Parameter languageString: The languageString to load locale as user choice URL //Optional
    - Parameter urlPathLocal: The local resources path required to handle locale
    -  Parameter urlString:  The urlString to load locale from URL //Optional
     */
    fun setLocale (context: Context ,languageString: String,urlString: String?,resources: Resources?,urlPathLocal: Int?){
        _context =context
        if(languageString!=null){
            LocalizationManager.setLocale(context, Locale(languageString))
        }else {
            LocalizationManager.setLocale(context, Locale.getDefault())
        }

        if(resources!=null && urlPathLocal!=null) {
            LocalizationManager.loadTapLocale(resources, R.raw.cardlocalisation)
        }
        else if(urlString!=null){
            if (context != null) {
                LocalizationManager.loadTapLocale(context.resources,urlString.toInt())
            }
        }
    }
    /**
    Handles tokenizing the card forum engine with the required data to be able to tokenize on demand. It calls the Tokenize api
    - Parameter tapCardForm: The tapcardform required to handle UI updates
     */

    @RequiresApi(Build.VERSION_CODES.N)
    fun startTokenize(cardInputForm: CardInputForm, activity: AppCompatActivity) {
            startTokenizingCard(cardInputForm,activity)

    }

    /**
    Handles tokenizing the card forum engine with the required data to be able to tokenize on demand. It calls the Tokenize and save api
    - Parameter tapCardForm: The tapcardform required to handle UI updates
     */

    @RequiresApi(Build.VERSION_CODES.N)
   private fun startSaveCard(tapCardInputView: CardInputForm, tapCustomer: TapCustomer, activity: AppCompatActivity, require3DSWebview:Boolean) {
        startSavingCard(tapCardInputView,tapCustomer, activity,require3DSWebview)

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun startSavingCard(tapCardInputView: CardInputForm, tapCustomer: TapCustomer, activity: AppCompatActivity, require3DS:Boolean) {
        PaymentDataSource.setTransactionMode(TransactionMode.SAVE_CARD)
        PaymentDataSource.setCustomer(tapCustomer)
        PaymentDataSource.setReceiptSettings(
            Receipt(
                false,
                false
            )
        )
        PaymentDataSource.setPaymentMetadata(HashMap())
        PaymentDataSource.setRequire3ds(require3DS)
        // Payment Reference
        PaymentDataSource.setPaymentReference(null) // ** Optional ** you can pass null
        // Payment Statement Descriptor
        PaymentDataSource.setPaymentStatementDescriptor("") // ** Optional **
        PaymentDataSource.setPostURL("")
        startTokenizingCard(tapCardInputView,activity)
    }


    @RequiresApi(Build.VERSION_CODES.N)
    private fun startTokenizingCard(cardInputForm: CardInputForm, activity: AppCompatActivity) {
        CardViewModel().processEvent(
            CardViewEvent.CreateTokenEvent,
            cardInputForm.getCard(),
            _context ,
            cardInputForm,null,null,activity
            )
    }


    fun addCardInputDelegate(_tapCardInputDelegate: TapCardInputDelegate) {
        println("_tokenizeDelegate sdk ${_tapCardInputDelegate}")
        tapCardInputDelegate = _tapCardInputDelegate


    }


    fun getListener(): TapCardInputDelegate? {
        return tapCardInputDelegate
    }


    /**
    Handles initializing the card forum engine with the required data to be able to tokenize on demand. It calls the Init api
    - Parameter dataConfig: The data configured by you as a merchant (e.g. secret key, locale, etc.)
     */

    @RequiresApi(Build.VERSION_CODES.N)
    fun initCardForm(
        context: Context,
        dataConfig: TapCardDataConfiguration,
        cardInputForm: CardInputForm
    ) {

        initializeCardForm(context,dataConfig,cardInputForm)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun initializeCardForm(
        context: Context,
        dataConfig: TapCardDataConfiguration,
        cardInputForm: CardInputForm
    ) {
        PaymentDataSource.setSelectedCurrency(dataConfig.selectedCurrency)
      //  PaymentDataSource.setCardType(dataConfig.selectedCardType)

//TODO NOTE enable/ disable debug this true-false for network call
        Log.e("data authtoken",dataConfig.authToken.toString())
        Log.e("data package",dataConfig.packageId.toString())

        cardInputForm.visibility = View.GONE
        NetworkApp.initNetwork(
            context,
            dataConfig.authToken,
            dataConfig.packageId,
            ApiService.BASE_URL,
            "NATIVE",true, context.resources.getString(company.tap.tapnetworkkit_android.R.string.enryptkey),context as AppCompatActivity)
        val requestModel =
            TapConfigRequestModel(dataConfig.authToken?.let {
                Gateway(
                        it, null,
                        Config(NetworkApp.getApplicationInfo())
                    )

            })


        CardViewModel().processEvent(
            CardViewEvent.InitEvent,null
            , context,cardInputForm ,requestModel,null, null
        )
        _context = context
    }

    /*   private fun isInternetConnectionAvailable(): ErrorTypes? {
           val ctx: Context = SDKSession.contextSDK
               ?: return ErrorTypes.SDK_NOT_CONFIGURED_WITH_VALID_CONTEXT
           val connectivityManager =
               ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
           if (connectivityManager != null) {
               val activeNetworkInfo = connectivityManager.activeNetworkInfo
               return if (activeNetworkInfo != null && activeNetworkInfo.isConnected) ErrorTypes.INTERNET_AVAILABLE else ErrorTypes.INTERNET_NOT_AVAILABLE
           }
           return ErrorTypes.CONNECTIVITY_MANAGER_ERROR
       }*/



}
