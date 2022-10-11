package company.tap.tapcardsdk.internal.logic.api.responses

import androidx.annotation.RestrictTo
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by AhlaamK on 7/24/22.

Copyright (c) 2022    Tap Payments.
All rights reserved.
 **/
@RestrictTo(RestrictTo.Scope.LIBRARY)
data class TapConfigResponseModel(
    @SerializedName("token")
    var token:String
) : Serializable
