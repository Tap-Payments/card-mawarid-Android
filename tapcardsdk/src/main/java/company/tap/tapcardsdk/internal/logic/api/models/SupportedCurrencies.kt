package company.tap.tapcardsdk.internal.logic.api.models

import androidx.annotation.RestrictTo
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

/**
 * Created by AhlaamK on 7/24/22.

Copyright (c) 2022    Tap Payments.
All rights reserved.
 **/
@RestrictTo(RestrictTo.Scope.LIBRARY)
data class SupportedCurrencies(
    @SerializedName("symbol")
    @Expose
    var symbol: String? = null,
    @SerializedName("name")
    @Expose
    var name: String? = null,
    @SerializedName("flag")
    @Expose
    var flag: String? = null,
    @SerializedName("decimal_digit")
    @Expose
    var decimal_digit: Int? = null,
    @SerializedName("selected")
    @Expose
    var selected: Boolean? = false,
    @SerializedName("currency")
    @Expose
    var currency: String? = null,
    @SerializedName("rate")
    @Expose
    var rate: Double? = null,
    @SerializedName("amount")
    @Expose
    var amount: BigDecimal,
)

