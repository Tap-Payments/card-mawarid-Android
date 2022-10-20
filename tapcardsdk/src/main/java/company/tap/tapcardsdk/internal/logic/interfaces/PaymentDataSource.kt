package company.tap.tapcardsdk.internal.logic.interfaces

import company.tap.tapcardsdk.internal.logic.api.responses.InitResponseModel
import company.tap.tapcardsdk.internal.logic.api.responses.MerchantData
import company.tap.tapcardformkit.open.SdkMode
import company.tap.tapcardsdk.internal.logic.api.models.Receipt
import company.tap.tapcardsdk.internal.logic.api.models.Reference
import company.tap.tapcardsdk.internal.logic.api.enums.TransactionMode
import company.tap.tapcardsdk.internal.logic.api.models.BINLookupResponse
import company.tap.tapcardsdk.internal.logic.api.models.CardIssuer
import company.tap.tapcardsdk.internal.logic.api.models.TapCustomer
import company.tap.tapcardsdk.internal.logic.api.responses.SDKSettings

/**
 * Created by AhlaamK on 3/23/22.

Copyright (c) 2022    Tap Payments.
All rights reserved.
 **/
interface PaymentDataSource {
    fun getMerchantData(): MerchantData?

    /**
     * Defines the SDK mode . Optional. @return the default Sandbox
     */
    fun getSDKMode(): SdkMode?

    /**
     * Defines the TokenConfig for header
     */
    fun getTokenConfig(): String?

    fun getInitOptionsResponse(): InitResponseModel?

    fun getBinLookupResponse(): BINLookupResponse?
    /**
     * Defines if user wants all cards or specific card types.
     */
    fun getCardType(): String?

    fun getSelectedCurrency(): String?
    fun getDefaultCardHolderName(): String?

    fun getTransactionMode() : TransactionMode?
    /**
     * Tap will post to this URL after transaction finishes. Optional. @return the post url
     */
    fun getPostURL(): String?

    /**
     * TapCustomer. @return the customer
     */
    fun getCustomer(): TapCustomer?
    /**
     * Description of the payment. @return the payment description
     */
    fun getPaymentDescription(): String?

    /**
     * If you would like to pass additional information with the payment, pass it here. @return the payment metadata
     */
    fun getPaymentMetadata(): HashMap<String, String>?
    /**
     * Defines the cardIssuer details. Optional. @return the default CardIssuer
     */
    fun getCardIssuer(): CardIssuer?

    /**
     * Receipt dispatch settings. @return the receipt settings
     */
    fun getReceiptSettings(): Receipt?

    /**
     * Defines if 3D secure check is required. @return the requires 3 d secure
     */
    fun getRequires3DSecure(): Boolean

    /**
     * Payment statement descriptor. @return the payment statement descriptor
     */
    fun getPaymentStatementDescriptor(): String?

    /**
     * Payment reference. Implement this property to keep a reference to the transaction on your backend. @return the payment reference
     */
    fun getPaymentReference(): Reference?

    fun getSDKSettings(): SDKSettings?
}