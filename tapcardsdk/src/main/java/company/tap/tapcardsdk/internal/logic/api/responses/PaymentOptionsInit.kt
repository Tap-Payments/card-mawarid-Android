package company.tap.tapcardsdk.internal.logic.api.responses

import androidx.annotation.NonNull
import androidx.annotation.RestrictTo
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import company.tap.tapcardsdk.internal.logic.api.models.PaymentOption
import company.tap.tapcardsdk.internal.logic.api.models.SupportedCurrencies
import company.tap.tapcardsdk.internal.logic.api.responses.MerchantData
import java.io.Serializable

/**
 * Created by AhlaamK on 7/24/22.

Copyright (c) 2022    Tap Payments.
All rights reserved.
 **/
@RestrictTo(RestrictTo.Scope.LIBRARY)
data class PaymentOptionsInit (@SerializedName("id")
                               @Expose
                               @NonNull var id: String,

                               @SerializedName("object")
                               @Expose
                               @NonNull val `object`: String,

                               @SerializedName("payment_methods")
                               @Expose
                               @NonNull var paymentOptions: ArrayList<PaymentOption>,
                               @SerializedName("currency")
                               @Expose
                               @NonNull var currency: String,
                               @SerializedName("country")
                               @Expose
                               @NonNull var country: String,
                               @SerializedName("settlement_currency")
                               @Expose
                               @NonNull var settlement_currency: String,
                               @SerializedName("supported_currencies")
                               @Expose
                               @NonNull val supportedCurrencies: ArrayList<SupportedCurrencies>,
                               @SerializedName("api_version")
                               @Expose
                               @NonNull var apiVersion: String,
                               @SerializedName("merchant")
                               @Expose
                               @NonNull var merchant: MerchantData,

                               ): Serializable




