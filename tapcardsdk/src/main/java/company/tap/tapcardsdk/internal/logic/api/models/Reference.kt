package company.tap.tapcardsdk.internal.logic.api.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by AhlaamK on 7/27/22.

Copyright (c) 2022    Tap Payments.
All rights reserved.
 **/

data class Reference(
    @SerializedName("acquirer") @Expose
    private var acquirer: String? = null,

    @SerializedName("gateway")
    @Expose
    private val gateway: String? = null,

    @SerializedName("payment")
    @Expose
    private val payment: String? = null,

    @SerializedName("track")
    @Expose
    private val track: String? = null,

    @SerializedName("transaction")
    @Expose
    private val transaction: String? = null,

    @SerializedName("order")
    @Expose
    private val order: String? = null,

    @SerializedName("gosell_id")
    @Expose
    private val gosell_id: String? = null
) : Serializable