package company.tap.tapcardsdk.internal.logic.api.enums

import com.google.gson.annotations.SerializedName

/**
 * Created by AhlaamK on 3/23/22.

Copyright (c) 2022    Tap Payments.
All rights reserved.
 **/

/**
 * The enum Token type.
 */
enum class TokenType {
    /**
     * Card token type.
     */
    @SerializedName("CARD")
    CARD,

    /**
     * Saved card token type.
     */
    @SerializedName("SAVED_CARD")
    SAVED_CARD
}