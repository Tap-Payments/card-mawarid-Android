package company.tap.tapcardsdk.internal.logic.api.enums

import com.google.gson.annotations.SerializedName

/**
 * Created by AhlaamK on 7/27/22.

Copyright (c) 2022    Tap Payments.
All rights reserved.
 **/

/**
 * The enum Url status.
 */
enum class URLStatus {
    /**
     * Pending url status.
     */
    @SerializedName("PENDING")
    PENDING,

    /**
     * Success url status.
     */
    @SerializedName("SUCCESS")
    SUCCESS,

    /**
     * Failed url status.
     */
    @SerializedName("FAILED")
    FAILED
}