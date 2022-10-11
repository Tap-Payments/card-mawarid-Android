package company.tap.tapcardsdk.internal.logic.api.models

import androidx.annotation.RestrictTo
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import company.tap.tapcardsdk.internal.logic.api.enums.AmountModificatorType
import java.io.Serializable
import java.math.BigDecimal

/**
 * Created by AhlaamK on 7/24/22.

Copyright (c) 2022    Tap Payments.
All rights reserved.
 **/

/**
 * The type Amount modificator.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
open class AmountModificator(
    @SerializedName("type")
    @Expose var amnttype: AmountModificatorType? = null,

    @SerializedName("value")
    @Expose var bigvalue: BigDecimal? = null
): Serializable {





    /**
     * Gets type.
     *
     * @return the type
     */
    fun getType(): AmountModificatorType? {
        return amnttype
    }

    /**
     * Gets value.
     *
     * @return the value
     */
    fun getValue(): BigDecimal? {
        return bigvalue
    }

    /**
     * Gets normalized value.
     *
     * @return the normalized value
     */
    fun getNormalizedValue(): BigDecimal? {
        return if (this.amnttype !== AmountModificatorType.PERCENTAGE) {
            this.bigvalue
        } else this.bigvalue?.multiply(BigDecimal.valueOf(0.01))
        //why
    }
    init {
        this.amnttype = amnttype
        this.bigvalue = bigvalue
    }
}