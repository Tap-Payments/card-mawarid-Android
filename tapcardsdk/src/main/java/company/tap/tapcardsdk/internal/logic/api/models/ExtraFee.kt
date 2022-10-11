package company.tap.tapcardsdk.internal.logic.api.models

import androidx.annotation.RestrictTo
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import company.tap.tapcardsdk.internal.logic.api.models.AmountModificator

/**
 * Created by AhlaamK on 7/24/22.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
@RestrictTo(RestrictTo.Scope.LIBRARY)
data class ExtraFee(
    @SerializedName("currency") @Expose
    var currency: String,
    @SerializedName("minimum_fee")
    @Expose
    val minimum_fee: Double,
    @SerializedName("maximum_fee")
    @Expose
    var maximum_fee: Double,
) : AmountModificator(null,null)
