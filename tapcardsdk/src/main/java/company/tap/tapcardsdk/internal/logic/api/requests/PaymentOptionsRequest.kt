package company.tap.tapcardsdk.internal.logic.api.requests

import androidx.annotation.Nullable
import androidx.annotation.RestrictTo
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import company.tap.tapcardsdk.internal.logic.utils.AmountCalculator
import company.tap.tapcardsdk.internal.logic.api.models.PaymentItem
import company.tap.tapcardsdk.internal.logic.api.models.Shipping
import company.tap.tapcardsdk.internal.logic.api.models.Tax
import company.tap.tapcardsdk.internal.logic.api.enums.TransactionMode
import company.tap.tapcardsdk.internal.logic.api.models.TopUp
import java.math.BigDecimal
import java.util.*

/**
 * Created by AhlaamK on 6/15/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
@RestrictTo(RestrictTo.Scope.LIBRARY)
open class PaymentOptionsRequest(
    transactionMode: TransactionMode?,
    amount: BigDecimal?,
    items: ArrayList<PaymentItem>?,
    shipping: ArrayList<Shipping>?,
    taxes: ArrayList<Tax>?,
    currency: String?,
    customer: String?,
    merchant_id: String?,
    payment_type: String?,
    topup : TopUp?

) {
    @SerializedName("transaction_mode")
    @Expose
    private val transactionMode: TransactionMode = transactionMode ?: TransactionMode.TOKENIZE_CARD

    @SerializedName("items")
    @Expose
    private var items: ArrayList<PaymentItem>? = null

    @SerializedName("shipping")
    @Expose
    private val shipping: ArrayList<Shipping>? = shipping

    @SerializedName("taxes")
    @Expose
    private val taxes: ArrayList<Tax>? = taxes

    @SerializedName("customer")
    @Expose
    private val customer: String? = customer

    @SerializedName("currency")
    @Expose
    private val currency: String? = currency

    @SerializedName("total_amount")
    @Expose
    private var totalAmount: BigDecimal? = null

    @SerializedName("merchant_id")
    @Expose
    @Nullable private val merchant_id: String? = merchant_id

    @SerializedName("payment_type")
    @Expose
    private val payment_type: String? = payment_type

    @SerializedName("topup")
    @Expose
    private val topup: TopUp? = null
    /**
     * Gets transaction mode.
     *
     * @return the transaction mode
     */
    fun getTransactionMode(): TransactionMode {
        return transactionMode
    }

    /**
     * Get payment option request info string.
     *
     * @return the string
     */
    fun getPaymentOptionRequestInfo(): String {
        return "trx_mode : " + transactionMode + " /n " +
                "shipping : " + shipping + " /n " +
                "taxes : " + taxes + " /n " +
                "currency : " + currency + " /n " +
                "customer : " + customer + " /n " +
                "total_amout : " + totalAmount + " /n " +
                "merchant_id : " + merchant_id + " /n " +
                "payment_type : " + payment_type + " /n "+
                "topup : " + topup + " /n "
    }

    /**
     * Instantiates a new Payment options request.
     *
     * @param transactionMode the transaction mode
     * @param amount          the amount
     * @param items           the items
     * @param shipping        the shipping
     * @param taxes           the taxes
     * @param currency        the currency
     * @param topUp        the topup
     */
    init {

        if (items != null && items.size > 0) {
            this.items = items
            totalAmount = AmountCalculator.calculateTotalAmountOf(items, taxes, shipping)
        } else {
            this.items = null
            val plainAmount = amount ?: BigDecimal.ZERO
            totalAmount = AmountCalculator.calculateTotalAmountOf(ArrayList(), taxes, shipping)?.add(plainAmount)
        }
    }
}
