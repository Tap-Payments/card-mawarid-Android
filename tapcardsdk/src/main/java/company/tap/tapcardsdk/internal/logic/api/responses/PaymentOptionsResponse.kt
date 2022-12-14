package company.tap.tapcardsdk.internal.logic.api.responses

import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import company.tap.tapcardsdk.internal.logic.api.models.PaymentOption
import company.tap.tapcardsdk.internal.logic.api.models.SavedCard
import company.tap.tapcardsdk.internal.logic.api.models.SupportedCurrencies
import company.tap.tapcardsdk.internal.logic.interfaces.BaseResponse

import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by AhlaamK on 6/15/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
data class PaymentOptionsResponse(
    @SerializedName("id") @Expose
    @NonNull  var id: String,

    @SerializedName("order_id")
    @Expose
    @NonNull  val orderID: String,

    @SerializedName("object")
    @Expose
    @NonNull  val `object`: String,

    @SerializedName("payment_methods")
    @Expose
    @NonNull  var paymentOptions: ArrayList<PaymentOption>,

    @SerializedName("currency")
    @Expose
    @NonNull  val currency: String,

    @SerializedName("supported_currencies")
    @Expose
    @NonNull  val supportedCurrencies: ArrayList<SupportedCurrencies>,

    @SerializedName("cards")
    @Expose
    @NonNull  val cards: ArrayList<SavedCard>,

    @SerializedName("settlement_currency")
    @Expose
    @Nullable  val settlement_currency: String? = null
) : BaseResponse
