package company.tap.tapcardsdk.internal.logic.interfaces

import company.tap.tapcardformkit.open.Receipt
import company.tap.tapcardformkit.open.Reference
import company.tap.tapcardsdk.internal.logic.api.models.CardIssuer
import company.tap.tapcardsdk.open.TapCustomer

import java.util.HashMap

/**
 * Created by AhlaamK on 7/28/22.

Copyright (c) 2022    Tap Payments.
All rights reserved.
 **/
interface IPaymentDataProvider {




    /**
     * Gets payment options order id.
     *
     * @return the payment options order id
     */
    fun getPaymentOptionsOrderID(): String?

    /**
     * Gets post url.
     *
     * @return the post url
     */
    fun getPostURL(): String?

    /**
     * Gets customer.
     *
     * @return the customer
     */
    fun getCustomer(): TapCustomer?

    /**
     * Gets payment description.
     *
     * @return the payment description
     */
    fun getPaymentDescription(): String?

    /**
     * Gets payment metadata.
     *
     * @return the payment metadata
     */
    fun getPaymentMetadata(): HashMap<String, String>?


    /**
     * Gets payment statement descriptor.
     *
     * @return the payment statement descriptor
     */
    fun getPaymentStatementDescriptor(): String?

    /**
     * Gets requires 3 d secure.
     *
     * @return the requires 3 d secure
     */
    fun getRequires3DSecure(): Boolean

    /**
     * Gets receipt settings.
     *
     * @return the receipt settings
     */
    fun getReceiptSettings(): Receipt?




    /**
     * Gets payment reference.
     *
     * @return the payment reference
     */
    fun getPaymentReference(): Reference?



    /**
     * get CardIssuer object
     */
    fun getCardIssuer(): CardIssuer?


}