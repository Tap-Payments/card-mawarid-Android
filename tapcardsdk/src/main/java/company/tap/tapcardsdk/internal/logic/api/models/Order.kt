package company.tap.tapcardsdk.internal.logic.api.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by AhlaamK on 7/27/22.

Copyright (c) 2022    Tap Payments.
All rights reserved.
 **/
data class Order(
    @SerializedName("id") @Expose
    var id: String? = null,
    @SerializedName("reference")
    @Expose
    val reference: String? = null,

    @SerializedName("store_url")
    @Expose
    val store_url: String? = null
) : Serializable
