package company.tap.tapcardsdk.internal.logic.api.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by AhlaamK on 7/27/22.

Copyright (c) 2022   Tap Payments.
All rights reserved.
 **/
data class CardIssuer(
    @SerializedName("id") @Expose
    var id: String,

    @SerializedName("name")
    @Expose
    val name: String,

    @SerializedName("country")
    @Expose
    val country: String
):Serializable