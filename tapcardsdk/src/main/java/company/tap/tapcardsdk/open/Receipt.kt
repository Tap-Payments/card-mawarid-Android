package company.tap.tapcardformkit.open

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.io.Serializable

/**
 * Created by AhlaamK on 7/27/22.

Copyright (c) 2022   Tap Payments.
All rights reserved.
 **/

data class Receipt(
    @SerializedName("id") @Expose
    var id: Boolean,

    @SerializedName("email")
    @Expose
    val email: Boolean = false,

    @SerializedName("sms")
    @Expose
    private val sms: Boolean = false
) : Serializable
