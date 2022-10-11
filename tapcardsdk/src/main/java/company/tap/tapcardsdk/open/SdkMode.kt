package company.tap.tapcardformkit.open

import com.google.gson.annotations.SerializedName

/**
 * Created by AhlaamK on 3/23/22.

Copyright (c) 2022    Tap Payments.
All rights reserved.
 **/
enum class SdkMode {
    /**
     * Sandbox is for testing purposes
     */
    @SerializedName("Sandbox")
    SAND_BOX,
    /**
     * Production is for live
     */
    @SerializedName("Production")
    PRODUCTION
}