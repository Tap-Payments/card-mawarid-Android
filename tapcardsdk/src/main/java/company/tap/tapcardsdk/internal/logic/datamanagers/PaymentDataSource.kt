package company.tap.tapcardsdk.internal.logic.datamanagers

import androidx.annotation.RestrictTo
import company.tap.tapcardsdk.internal.logic.api.models.BINLookupResponse
import company.tap.tapcardsdk.internal.logic.api.responses.InitResponseModel
import company.tap.tapcardsdk.internal.logic.api.responses.MerchantData
import company.tap.tapcardformkit.open.SdkMode
import company.tap.tapcardsdk.internal.logic.api.models.CardIssuer
import company.tap.tapcardformkit.open.Receipt
import company.tap.tapcardformkit.open.Reference
import company.tap.tapcardsdk.internal.logic.api.enums.TransactionMode
import company.tap.tapcardsdk.open.TapCustomer

/**
 * Created by AhlaamK on 3/23/22.

Copyright (c) 2022    Tap Payments.
All rights reserved.
 **/
@RestrictTo(RestrictTo.Scope.LIBRARY)
object PaymentDataSource : company.tap.tapcardsdk.internal.logic.interfaces.PaymentDataSource {

    private var sdkMode: SdkMode = SdkMode.SAND_BOX
    private var merchantData: MerchantData? = null
    private var tokenConfig: String? = null
    private var authKeys: String? = null
    private var initResponseModel: InitResponseModel? = null
    private var transactionMode: TransactionMode? = null
    private var binLookupResponse: BINLookupResponse? = null
    private var cardType: String? = null
    private var selectedCurrency: String? = null
    private var _defaultCardHolderName: String = "CARD HOLDER NAME"
    private lateinit var paymentMetadata: java.util.HashMap<String, String>
    private var paymentReference: Reference? = null
    private val paymentStatementDescriptor: String? = null
    private var requires3DSecure = false
    private var receiptSettings: Receipt? = null
    private var postURL: String? = null
    private var paymentDescription: String? = null
    private lateinit var tapCustomer: TapCustomer
    private val cardIssuer: CardIssuer? = null

    /**
     * Set sdkSettings.
     *
     * @param sdkSettings the sdkSettings
     */
    fun setMerchantData(merchantData: MerchantData?) {
        PaymentDataSource.merchantData = merchantData
    }


    fun setCardType(cardType: String?) {
        PaymentDataSource.cardType = cardType
    }

    /**
     * Set sdkMode.
     *
     * @param sdkMode the sdkMode
     */
    fun setSDKMode(sdkMode: SdkMode){
        PaymentDataSource.sdkMode = sdkMode
    }

    /**
     * Set defaultCardHolderName.
     *
     * @param defaultCardHolderName the defaultCardHolderName
     */
    fun setDefaultCardHolderName(defaultCardHolderName: String){
        println("setDefaultCardHolderName"+defaultCardHolderName)
        if(defaultCardHolderName.equals(null)){
            _defaultCardHolderName ="CARD HOLDER NAME"
        }else{
            _defaultCardHolderName = defaultCardHolderName
        }

    }
    /**
     * Set post url.
     *
     * @param postURL the post url
     */
    fun setPostURL(postURL: String?) {
        PaymentDataSource.postURL = postURL
    }

    /**
     * Set payment description.
     *
     * @param paymentDescription the payment description
     */
    fun setPaymentDescription(paymentDescription: String?) {
        PaymentDataSource.paymentDescription = paymentDescription
    }

    /**
     * Set payment metadata.
     *
     * @param paymentMetadata the payment metadata
     */
    fun setPaymentMetadata(paymentMetadata: java.util.HashMap<String, String>) {
        PaymentDataSource.paymentMetadata = paymentMetadata
    }

    /**
     * Set payment reference.
     *
     * @param paymentReference the payment reference
     */
    fun setPaymentReference(paymentReference: Reference?) {
        PaymentDataSource.paymentReference = paymentReference
    }

    /**
     * Set payment statement descriptor.
     *
     * @param paymentDescription the payment description
     */
    fun setPaymentStatementDescriptor(paymentDescription: String?) {
        PaymentDataSource.paymentDescription = paymentDescription
    }

    /**
     * Set initResponseModel.
     *
     * @param initResponseModel the sdkSettings
     */
    fun setInitResponse(initResponseModel: InitResponseModel?) {
        PaymentDataSource.initResponseModel = initResponseModel
    }

    fun setBinLookupResponse(binLookupResponse: BINLookupResponse?){
        PaymentDataSource.binLookupResponse = binLookupResponse
    }

    fun setSelectedCurrency(selectedCurrency: String?){
        PaymentDataSource.selectedCurrency = selectedCurrency
    }
    /**
     * Set tapCustomer.
     *
     * @param tapCustomer the tapCustomer
     */
    fun setCustomer(tapCustomer: TapCustomer) {
        PaymentDataSource.tapCustomer = tapCustomer
    }

    /**
     * set payment receipt
     *
     * @param receipt the receipt
     */
    fun setReceiptSettings(receipt: Receipt?) {
        receiptSettings = receipt
    }

    /**
     * set 3ds
     *
     * @param 3ds
     */
    fun setRequire3ds(requires3DSecure: Boolean) {
        PaymentDataSource.requires3DSecure = requires3DSecure
    }
    /**
     * Set tokenConfig
     *
     * @param _tokenConfig the  tokenConfig
     */
    fun setTokenConfig(_tokenConfig: String?) {
        tokenConfig = _tokenConfig
    }

    fun setTransactionMode(transactionMode: TransactionMode?) {
        PaymentDataSource.transactionMode = transactionMode
    }
    override fun getMerchantData(): MerchantData? {
        return merchantData
    }

    override fun getSDKMode(): SdkMode {
        return  sdkMode
    }

    override fun getTokenConfig(): String? {
        return  tokenConfig
    }

    override fun getInitOptionsResponse(): InitResponseModel? {
        return initResponseModel
    }

    override fun getBinLookupResponse(): BINLookupResponse? {
       return binLookupResponse
    }

    override fun getCardType(): String? {
        return cardType
    }

    override fun getSelectedCurrency(): String? {
       return selectedCurrency
    }

    override fun getDefaultCardHolderName(): String? {
        return _defaultCardHolderName
    }

    override fun getTransactionMode(): TransactionMode? {
       return transactionMode
    }

    override fun getPostURL(): String? {
        return postURL
    }

    override fun getCustomer(): TapCustomer? {
       return tapCustomer
    }

    override fun getPaymentDescription(): String? {
        return paymentDescription
    }

    override fun getPaymentMetadata(): HashMap<String, String>? {
        return paymentMetadata
    }

    override fun getCardIssuer(): CardIssuer? {
        return cardIssuer
    }

    override fun getReceiptSettings(): Receipt? {
        return receiptSettings
    }

    override fun getRequires3DSecure(): Boolean {
        return requires3DSecure
    }

    override fun getPaymentStatementDescriptor(): String? {
        return paymentStatementDescriptor
    }

    override fun getPaymentReference(): Reference? {
        return paymentReference
    }
}