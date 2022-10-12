package company.tap.tapcardsdk.internal.logic.datamanagers


import company.tap.tapcardsdk.internal.logic.api.models.CardIssuer
import company.tap.tapcardsdk.internal.logic.interfaces.IPaymentDataProvider
import company.tap.tapcardsdk.internal.logic.api.models.Receipt
import company.tap.tapcardsdk.internal.logic.api.models.Reference
import company.tap.tapcardsdk.internal.logic.api.models.TapCustomer
import java.util.*

/**
 * Created by AhlaamK on 7/28/22.

Copyright (c) 2022    Tap Payments.
All rights reserved.
 **/
internal class PaymentDataProvider: IPaymentDataProvider {
    private var externalDataSource: PaymentDataSource = PaymentDataSource



    /////////////////////////////////////////  ########### End of Singleton section ##################
    /**
     * Gets external data source.
     *
     * @return the external data source
     */
    fun getExternalDataSource(): PaymentDataSource {
        return this.externalDataSource
    }

    /**
     * Sets external data source.
     *
     * @param externalDataSource the external data source
     */
    fun setExternalDataSource(externalDataSource: PaymentDataSource) {
        println("externalDataSource<<<<>>>>" + externalDataSource)
        this.externalDataSource = externalDataSource

    }

    override fun getPaymentOptionsOrderID(): String? {
        return ""
    }

    override fun getPostURL(): String? {
        return PaymentDataSource.getPostURL()
    }

    override fun getCustomer(): TapCustomer? {
        return PaymentDataSource.getCustomer()
    }

    override fun getPaymentDescription(): String? {
        return PaymentDataSource.getPaymentDescription()
    }

    override fun getPaymentMetadata(): HashMap<String, String>? {
        return PaymentDataSource.getPaymentMetadata()
    }

    override fun getPaymentReference(): Reference? {
        return PaymentDataSource.getPaymentReference()
    }

    override fun getPaymentStatementDescriptor(): String? {
        return PaymentDataSource.getPaymentStatementDescriptor()
    }

    override fun getRequires3DSecure(): Boolean {
        /**
         * Stop checking SDKsettings in SDK to decide if  the payment will be 3DSecure or not
         * instead always send value configured by the Merchant.
         */

        return PaymentDataSource.getRequires3DSecure()
    }

    override fun getReceiptSettings(): Receipt? {
        return PaymentDataSource.getReceiptSettings()
    }



    override fun getCardIssuer(): CardIssuer? {
        return PaymentDataSource.getCardIssuer()
    }
}