package company.tap.tapcardsdk.internal.logic.api.models

import androidx.annotation.RestrictTo
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import company.tap.tapcardsdk.internal.logic.interfaces.CurrenciesSupport
import company.tap.tapcardsdk.internal.logic.api.enums.PaymentType
import company.tap.tapcardvalidator_android.CardBrand
import java.io.Serializable
import java.util.ArrayList

/**
 * Created by AhlaamK on 7/24/22.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
@RestrictTo(RestrictTo.Scope.LIBRARY)
data class PaymentOption(
    @SerializedName("id") @Expose
    var id: String? = null,

    @SerializedName("name")
    @Expose
    val brand: CardBrand? = null,

    @SerializedName("image")
    @Expose
    val image: String? = null,

    @SerializedName("payment_type")
    @Expose
    val paymentType: PaymentType? = null,

    @SerializedName("source_id")
    @Expose
    val sourceId: String? = null,

    @SerializedName("supported_card_brands")
    @Expose
    val supportedCardBrands: ArrayList<CardBrand>? = null,

    @SerializedName("extra_fees")
    @Expose
    var extraFees: ArrayList<ExtraFee>? = null,

    @SerializedName("supported_currencies")
    @Expose
    private val supportedCurrencies: ArrayList<String>,

    @SerializedName("order_by")
    @Expose

    val orderBy: Int = 0,

    @SerializedName("threeDS")
    @Expose
    val threeDS: String? = null,

    @SerializedName("asynchronous")
    @Expose
    val asynchronous: Boolean = false,
    @SerializedName("cc_markup")
    @Expose
    val cc_markup: Int = 0
): CurrenciesSupport, Serializable{
/// Comparable<PaymentOption>,{
    override fun getSupportedCurrencies(): ArrayList<String>? {
        return supportedCurrencies
    }

/*    override fun compareTo(other: PaymentOption): Int {
        return orderBy - other.orderBy
    }*/

}
