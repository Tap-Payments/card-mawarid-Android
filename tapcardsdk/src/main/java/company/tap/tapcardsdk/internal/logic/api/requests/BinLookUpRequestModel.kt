package company.tap.tapcardsdk.internal.logic.api.requests

import androidx.annotation.Nullable
import androidx.annotation.RestrictTo
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by AhlaamK on 7/24/22.

Copyright (c) 2022    Tap Payments.
All rights reserved.
 **/
@RestrictTo(RestrictTo.Scope.LIBRARY)
class BinLookUpRequestModel (
    @SerializedName("bin")
    @Expose
    @Nullable var bin: String?
)
