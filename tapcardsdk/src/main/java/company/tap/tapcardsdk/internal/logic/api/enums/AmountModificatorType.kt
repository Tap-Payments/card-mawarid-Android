package company.tap.tapcardsdk.internal.logic.api.enums

import com.google.gson.annotations.SerializedName

/**
 * Created by AhlaamK on 3/23/22.

Copyright (c) 2022    Tap Payments.
All rights reserved.
 **/
enum class AmountModificatorType (val amountModificatorType:String){
    /**
     * Percentage amount modificator type.
     */
    @SerializedName("P")
    PERCENTAGE("P"),
    /**
     * Fixed amount modificator type.
     */
    @SerializedName("F")
    FIXED("F")
}