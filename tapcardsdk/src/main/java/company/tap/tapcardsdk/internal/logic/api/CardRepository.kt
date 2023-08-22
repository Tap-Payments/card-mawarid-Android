package company.tap.tapcardsdk.internal.logic.api

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.annotation.RestrictTo
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.JsonElement
import company.tap.tapcardsdk.internal.logic.api.requests.PaymentOptionsRequest

import company.tap.tapcardsdk.internal.logic.api.requests.BinLookUpRequestModel
import company.tap.tapcardsdk.internal.logic.api.requests.CreateSaveCardRequest
import company.tap.tapcardsdk.internal.logic.api.requests.CreateTokenWithCardDataRequest
import company.tap.tapcardsdk.internal.logic.api.requests.EmptyBody
import company.tap.tapcardsdk.internal.logic.datamanagers.PaymentDataProvider
import company.tap.tapcardsdk.internal.logic.datamanagers.PaymentDataSource
import company.tap.tapcardsdk.internal.logic.interfaces.CurrenciesSupport
import company.tap.tapcardsdk.internal.logic.interfaces.IPaymentDataProvider
import company.tap.tapcardsdk.internal.logic.utils.Utils
import company.tap.tapcardsdk.internal.logic.webview.WebViewActivity
import company.tap.tapcardsdk.internal.logic.webview.WebViewContract
import company.tap.tapcardsdk.internal.logic.api.enums.*
import company.tap.tapcardsdk.internal.logic.api.models.*
import company.tap.tapcardsdk.internal.logic.api.models.Receipt
import company.tap.tapcardsdk.internal.logic.api.models.Reference
import company.tap.tapcardsdk.open.CardInputForm
import company.tap.tapcardsdk.open.DataConfiguration
import company.tap.tapcardsdk.internal.logic.api.models.TapCustomer
import company.tap.tapcardsdk.internal.logic.api.responses.*
import company.tap.tapnetworkkit.connection.NetworkApp
import company.tap.tapnetworkkit.controller.NetworkController
import company.tap.tapnetworkkit.enums.TapMethodType
import company.tap.tapnetworkkit.exception.GoSellError
import company.tap.tapnetworkkit.interfaces.APIRequestCallback
import company.tap.tapuilibrary.uikit.enums.ActionButtonState
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.subjects.BehaviorSubject
import retrofit2.Response
import java.math.BigDecimal


/**
 * Created by AhlaamK on 3/23/22.

Copyright (c) 2022    Tap Payments.
All rights reserved.
 **/
@RestrictTo(RestrictTo.Scope.LIBRARY)
class CardRepository : APIRequestCallback , WebViewContract {
    val resultObservable = BehaviorSubject.create<CardViewState>()
    lateinit var tokenResponse: Token
    private var dataProvider: IPaymentDataProvider = PaymentDataProvider()
    lateinit var cardRepositoryContext: Context
    lateinit var _tapCardInputView: CardInputForm
    private lateinit var cardViewModel: CardViewModel
    private var binLookupResponse: BINLookupResponse?=null
    lateinit var saveCardResponse: SaveCard
    private var tokenizeParams: DataConfiguration = DataConfiguration
    private var initResponse: SDKSettings? = null
    private var _context: Context? = null
    private var activity: AppCompatActivity? = null
    lateinit var  jsonString :String
    private var configResponse: TapConfigResponseModel?=null
    @JvmField
    var initResponseModel : InitResponseModel?= null
     var paymentOptionsResponse: PaymentOptionsResponse? =null
    lateinit var paymentOptionsWorker: java.util.ArrayList<PaymentOption>
    @RequiresApi(Build.VERSION_CODES.N)
    fun getInitData(_context: Context, cardViewModel: CardViewModel?,tapCardInputView: CardInputForm?) {
        if (cardViewModel != null) {
            this.cardViewModel = cardViewModel
        }
        NetworkController.getInstance().processRequest(
            TapMethodType.GET,
            ApiService.INIT,
            null,
            this,
            INIT_CODE
        )
        this.cardRepositoryContext = _context
        if (tapCardInputView != null) {
            this._tapCardInputView = tapCardInputView
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun getConfigData(
        _context: Context,
        cardViewModel: CardViewModel?,
        tapConfigRequestModel: TapConfigRequestModel?,
        tapCardInputView: CardInputForm?
    ) {
        this._context =_context
        if (cardViewModel != null) {
            this.cardViewModel = cardViewModel
        }
        if (tapCardInputView != null) {
            this._tapCardInputView =tapCardInputView
        }
      //  DataConfiguration.tapCardInputDelegate?.cardFormIsGettingReady()
        jsonString = Gson().toJson(tapConfigRequestModel)
        NetworkController.getInstance().processRequest(
            TapMethodType.POST,
            ApiService.CONFIG,
            jsonString,
            this,
            CONFIG_CODE
        )
        this.cardRepositoryContext = _context
        this._context =_context
    }


    @RequiresApi(Build.VERSION_CODES.N)
    fun retrieveBinLookup(cardViewModel: CardViewModel, binValue: String?) {
        this.cardViewModel = cardViewModel
        NetworkController.getInstance().processRequest(
            TapMethodType.GET, ApiService.BIN + binValue, null,
            this, BIN_RETRIEVE_CODE
        )
    }
    @RequiresApi(Build.VERSION_CODES.N)
    fun retrieveSaveCard(cardViewModel: CardViewModel, savedResponseId:String?, context: Context?) {
        this.cardViewModel = cardViewModel
      //  println("saveCardResponse is"+saveCardResponse.id)
       this._context =context
        NetworkController.getInstance().processRequest(
            TapMethodType.GET, ApiService.SAVE_CARD_ID +savedResponseId, null, this,
            RETRIEVE_SAVE_CARD_CODE
        )
    }
    @RequiresApi(Build.VERSION_CODES.N)
    fun createTokenWithEncryptedCard(

        createTokenWithCardDataRequest: CreateTokenCard?,
        cardInputForm: CardInputForm?, activity: AppCompatActivity?
    ) {
        this.activity = activity
        val createTokenWithCardDataReq = createTokenWithCardDataRequest?.let {
            CreateTokenWithCardDataRequest(
                it
            )
        }
        val jsonString = Gson().toJson(createTokenWithCardDataReq)

        NetworkController.getInstance().processRequest(
            TapMethodType.POST, ApiService.TOKEN, jsonString,
            this, CREATE_TOKEN_CODE
        )
        if (cardInputForm != null) {
            this._tapCardInputView = cardInputForm
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onSuccess(responseCode: Int, requestCode: Int, response: Response<JsonElement>?) {
     if (requestCode == INIT_CODE) {
            response?.body().let {
                initResponse = Gson().fromJson(it, SDKSettings::class.java)
                PaymentDataSource.setSDKSettings(initResponse)


            }
        }
        else if (requestCode == PAYMENT_OPTIONS_CODE) {
            if (response?.body() != null) {
                response.body().let {
                    paymentOptionsResponse = Gson().fromJson(it, PaymentOptionsResponse::class.java)
                    PaymentDataSource.setPaymentOptionsResponse(paymentOptionsResponse)
                    filterViewModels(PaymentDataSource.getSelectedCurrency().toString())
                    PaymentDataSource.setMerchantData(initResponseModel?.merchant)
                    _tapCardInputView.visibility = View.VISIBLE
                }
                DataConfiguration.tapCardInputDelegate?.cardFormIsReady()

            }else {
               DataConfiguration.getListener()?.backendUnknownError("Session Failed to start")
            }

        }else if(requestCode == BIN_RETRIEVE_CODE){
            response?.body().let {
                binLookupResponse = Gson().fromJson(it, BINLookupResponse::class.java)
                if(binLookupResponse!=null)
                PaymentDataSource.setBinLookupResponse(binLookupResponse)

            }
        }
        else if(requestCode == CREATE_SAVE_CARD){
            println("data in resp body>>" + response?.body())
            response?.body().let {
                saveCardResponse = Gson().fromJson(it, SaveCard::class.java)
                println("data in resp body>>" + saveCardResponse)

                if(saveCardResponse.status?.name == ChargeStatus.INITIATED.name){

                    val intent = Intent(activity, WebViewActivity::class.java)
                    intent.putExtra("webviewURL",saveCardResponse.transaction?.url)
                    activity?.startActivity(intent)

                   // saveCardResponse.transaction?.url?.let { it1 -> WebFragment.newInstance(it1,this,saveCardResponse,_context) }

                }else {
                    handleSaveCardResponse(saveCardResponse)
                }
            }

        }
        else if(requestCode == RETRIEVE_SAVE_CARD_CODE){

            response?.body().let {
                saveCardResponse = Gson().fromJson(it, SaveCard::class.java)
                println("saveCardResponse value is>>>>" + saveCardResponse.status.name)
               WebViewActivity().finishWebActivity()
              //  DataConfiguration.getListener()?.cardSavedSuccessfully(saveCardResponse)

            //    handleSaveCardResponse(saveCardResponse)

            }
        }
        else if (requestCode == CREATE_TOKEN_CODE) {
            response?.body().let {
                tokenResponse = Gson().fromJson(it, Token::class.java)
                if(PaymentDataSource.getTransactionMode() == TransactionMode.SAVE_CARD){
                    createSaveCard(null,tokenResponse.id)
                }else{
                    tokenizeParams.getListener()?.cardTokenizedSuccessfully(tokenResponse ,_tapCardInputView.checkedSaveCardEnabled )
                  //  _tapCardInputView.clearCardInputAction()

                }
            }


        }
    }


    override fun onFailure(requestCode: Int, errorDetails: GoSellError?) {
        errorDetails?.let {
            if (it.throwable != null) {
                resultObservable.onError(it.throwable)
                tokenizeParams.getListener()?.backendUnknownError("Required fields are empty")

            } else
                try {
                    // resultObservable.onError(Throwable(it.errorMessage))
                    RxJavaPlugins.setErrorHandler(Throwable::printStackTrace)
                    if (requestCode == CREATE_TOKEN_CODE){
                        tokenizeParams.getListener()?.cardTokenizedFailed(errorDetails)
                    }else{

                    }


                } catch (e: Exception) {

                }


        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun filterViewModels(currency: String) {
        println("paymentOptionsResponse?.paymentOptions"+paymentOptionsResponse?.paymentOptions)
        if (paymentOptionsResponse?.paymentOptions != null)
            paymentOptionsWorker = paymentOptionsResponse?.paymentOptions?.let {
                java.util.ArrayList<PaymentOption>(
                    it
                )
            }!!
      //  paymentOptionsWorker.sortBy { paymentOption: PaymentOption ->paymentOption.orderBy  }


        val cardPaymentOptions: java.util.ArrayList<PaymentOption> =
            filteredByPaymentTypeAndCurrencyAndSortedList(
                paymentOptionsWorker, PaymentType.CARD, currency
            )
        println("cardPaymentOptions"+cardPaymentOptions)
        val hasCardPaymentOptions = cardPaymentOptions.size > 0
        logicToHandlePaymentDataType(cardPaymentOptions)

    }

    private fun filteredByPaymentTypeAndCurrencyAndSortedList(
        list: java.util.ArrayList<PaymentOption>, paymentType: PaymentType, currency: String
    ): java.util.ArrayList<PaymentOption> {
        var currencyFilter: String? = currency
        val filters: java.util.ArrayList<Utils.List.Filter<PaymentOption>> =
            java.util.ArrayList<Utils.List.Filter<PaymentOption>>()

        /**
         * if trx currency not included inside supported currencies <i.e Merchant pass transaction currency that he is not allowed for>
         * set currency to first supported currency
        </i.e> */
        var trxCurrencySupported = false
        for (amountedCurrency in paymentOptionsResponse?.supportedCurrencies!!) {
            if (amountedCurrency.currency == currencyFilter) {
                trxCurrencySupported = true
                break
            }
        }
        if (!trxCurrencySupported) currencyFilter =
            paymentOptionsResponse?.supportedCurrencies?.get(0)?.currency


        if (currencyFilter != null) {
            this.getCurrenciesFilter<PaymentOption>(currencyFilter)?.let { filters.add(it) }
        }
        //  filters.add(getPaymentOptionsFilter(paymentType))
        //  val filter: CompoundFilter<PaymentOption> = CompoundFilter(filters)
        //  val filtered: ArrayList<PaymentOption> = Utils.List.filter(list)

        var filtered: ArrayList<PaymentOption> =
            list.filter { items ->
                items.paymentType == paymentType && items.getSupportedCurrencies()?.contains(
                    currencyFilter
                ) == true
            } as ArrayList<PaymentOption>

        return list.filter { items ->
            items.paymentType == paymentType && items.getSupportedCurrencies()?.contains(
                currencyFilter
            ) == true
        } as ArrayList<PaymentOption>
    }
    private fun <E : CurrenciesSupport?> getCurrenciesFilter(
        currency: String
    ): Utils.List.Filter<E>? {
        return object : Utils.List.Filter<E> {
            override fun isIncluded(`object`: E): Boolean {
                return `object`?.getSupportedCurrencies()!!.contains(currency)
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.N)
    private fun logicToHandlePaymentDataType(
        cardPaymentOptions: ArrayList<PaymentOption>
    ) {

    }
        @RequiresApi(Build.VERSION_CODES.N)
        private fun createSaveCard(
            selectedPaymentOption: PaymentOption?,
            identifier: String?
        ) {


            if (identifier != null)
                callChargeOrAuthorizeOrSaveCardAPI(
                    SourceRequest(identifier),
                    selectedPaymentOption,
                    null,
                    null
                )
            else
                selectedPaymentOption?.sourceId?.let { SourceRequest(it) }?.let {
                    callChargeOrAuthorizeOrSaveCardAPI(
                        it,
                        selectedPaymentOption,
                        null,
                        null
                    )
                }
        }

        private fun getDataProvider(): IPaymentDataProvider {
            return dataProvider
        }

        @RequiresApi(Build.VERSION_CODES.N)
        private fun callChargeOrAuthorizeOrSaveCardAPI(
            source: SourceRequest,
            paymentOption: PaymentOption?,
            cardBIN: String?, saveCard: Boolean?
        ) {
            Log.e("OkHttp", "CALL CHARGE API OR AUTHORIZE API")
            val provider: IPaymentDataProvider = getDataProvider()
            val orderID: String? = provider.getPaymentOptionsOrderID()
            val postURL: String? = provider.getPostURL()
            val post = if (postURL == null) null else TrackingURL(URLStatus.PENDING, postURL)
            //  val amountedCurrency: AmountedCurrency? = provider.getSelectedCurrency()


            val order = Order(orderID)
            val redirect = TrackingURL(URLStatus.PENDING, ApiService.RETURN_URL)
            val paymentDescription: String? = provider.getPaymentDescription()
            val paymentMetadata: HashMap<String, String>? = provider.getPaymentMetadata()
            val reference: Reference? = provider.getPaymentReference()
            val shouldSaveCard = saveCard ?: false
            val statementDescriptor: String? = provider.getPaymentStatementDescriptor()
            var require3DSecure: Boolean = provider
                .getRequires3DSecure() // this.dataSource.getRequires3DSecure() || this.chargeRequires3DSecure();
            val receipt: Receipt? = provider.getReceiptSettings()
            val customer: TapCustomer? = provider.getCustomer()
            val cardIssuer: CardIssuer? = provider.getCardIssuer()

            /**
             * Condition added for 3Ds for merchant
             */
            if (paymentOption?.threeDS != null) {
                if (paymentOption.threeDS == "N") {
                    require3DSecure = false
                } else if (paymentOption.threeDS == "Y") {
                    require3DSecure = true
                } else if (paymentOption.threeDS == "U") {
                    require3DSecure = provider?.getRequires3DSecure()
                }
            }

            val saveCardRequest =
                customer?.let {
                    CreateSaveCardRequest(
                        "KWD",
                        it,
                        order,
                        redirect,
                        post,
                        source,
                        paymentDescription,
                        paymentMetadata,
                        reference,
                        true,
                        statementDescriptor,
                        require3DSecure,
                        receipt,
                        true,
                        true,
                        true,
                        true,
                        true,
                        cardIssuer
                    )
                }

            val jsonString = Gson().toJson(saveCardRequest)

            NetworkController.getInstance().processRequest(
                TapMethodType.POST,
                ApiService.SAVE_CARD,
                jsonString,
                this,
                CREATE_SAVE_CARD
            )


        }

        private fun handleSaveCardResponse(saveCard: SaveCard) {
            // Log.d("CardRepository"," Cards >> didReceiveSaveCard * * * " + saveCard);
            if (saveCard == null) return
            //   Log.d("CardRepository"," Cards >> didReceiveSaveCard * * * status :" + saveCard.getStatus());
            when (saveCard.status) {
                ChargeStatus.INITIATED -> {
                    val authenticate: Authenticate = saveCard.authenticate
                    if (authenticate != null && authenticate.status === AuthenticationStatus.INITIATED) {
                        when (authenticate.type) {
                            AuthenticationType.BIOMETRICS -> {
                            }
                            AuthenticationType.OTP -> {
                                Log.d("CardRepository", " start otp for save card mode........")
                                // PaymentDataManager.getInstance().setChargeOrAuthorize(saveCard)
                                //  openOTPScreen(saveCard)
                            }
                        }
                    }
                }
                ChargeStatus.CAPTURED, ChargeStatus.AUTHORIZED, ChargeStatus.VALID -> try {
                   // DataConfiguration.getListener()?.cardSavedSuccessfully(saveCard)

                } catch (e: java.lang.Exception) {
                    Log.d(
                        "CardRepository",
                        " Error while calling delegate method cardSaved(saveCard)" + e
                    )

                }
                ChargeStatus.INVALID, ChargeStatus.FAILED, ChargeStatus.ABANDONED, ChargeStatus.CANCELLED, ChargeStatus.DECLINED, ChargeStatus.RESTRICTED -> try {
                //    DataConfiguration.getListener()?.cardSavingFailed(saveCard)
                    /*viewModel?.handleSuccessFailureResponseButton(
                    "failure",
                    saveCard.authenticate,
                    saveCard

                )*/
                } catch (e: java.lang.Exception) {
                    Log.d(
                        "CardRepository",
                        " Error while calling delegate method cardSavingFailed(saveCard)"
                    )

                }
            }
        }

        companion object {
            private const val CONFIG_CODE = 1
            private const val INIT_CODE = 2
            private const val CREATE_TOKEN_CODE = 3
            private const val BIN_RETRIEVE_CODE = 4
            private const val CREATE_SAVE_CARD = 5
            private const val RETRIEVE_SAVE_CARD_CODE = 6
            private const val PAYMENT_OPTIONS_CODE = 7
        }

        override fun redirectLoadingFinished(done: Boolean, charge: Charge?, contextSDK: Context?) {

        }

    @RequiresApi(Build.VERSION_CODES.N)
    fun getPaymentOptions(
        _context: Context,
        cardViewModel: CardViewModel
    ) {
        this.cardViewModel = cardViewModel

        val requestBody = PaymentOptionsRequest(
            PaymentDataSource.getTransactionMode(),
            BigDecimal.ONE,
            null,
            null,
            null,
            PaymentDataSource.getSelectedCurrency(),
            null,
            TapCardDataConfiguration().merchantId,
           null,
            null
        )
        println("getTransactionMode"+PaymentDataSource.getTransactionMode())

        val jsonString = Gson().toJson(requestBody)
        NetworkController.getInstance().processRequest(
            TapMethodType.POST, ApiService.PAYMENT_TYPES, jsonString, this, PAYMENT_OPTIONS_CODE
        )

         this.cardRepositoryContext = _context
    }

    }
