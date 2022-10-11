package company.tap.tapcardsdk.internal.logic.api.models

import com.google.gson.annotations.SerializedName

/**
 * Created by AhlaamK on 7/28/22.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
data class Acquirer(
    @SerializedName("id") var id : String,
    @SerializedName("response") var response : Response,
)
