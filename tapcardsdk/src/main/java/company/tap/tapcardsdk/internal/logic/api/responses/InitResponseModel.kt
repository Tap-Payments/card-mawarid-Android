package company.tap.tapcardsdk.internal.logic.api.responses

import androidx.annotation.NonNull
import androidx.annotation.RestrictTo
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import company.tap.tapcardsdk.internal.logic.api.enums.Permission
import java.io.Serializable
import java.util.ArrayList

/**
 * Created by AhlaamK on 7/24/22.

Copyright (c) 2022    Tap Payments.
All rights reserved.
 **/
@RestrictTo(RestrictTo.Scope.LIBRARY)
data class InitResponseModel(@SerializedName("payment_options") @Expose
                             @NonNull var paymentOptionsInit: PaymentOptionsInit,

                             @SerializedName("merchant")
                             @Expose
                             @NonNull val merchant: MerchantData
): Serializable
data class MerchantData(
    @SerializedName("background")
    @Expose
    var background: Object? = null,

    @SerializedName("encryption_key")
    @Expose
    val encryptionKey: String? = null,

    @SerializedName("country_code")
    @Expose
    val countryCode: String? = null,

    @SerializedName("logo")
    @Expose
    val logo: String,
    @SerializedName("name")
    @Expose
    val name: String,
    @SerializedName("sdk_settings")
    @Expose
    val sdkSettings: SDKSettingsData? = null,
    @SerializedName("permissions")
    @Expose
    val permissions: ArrayList<Permission>? = null,
    @SerializedName("live_mode")
    @Expose
    val live_mode: Boolean? = false,
    @SerializedName("livemode")
    @Expose
    val liveMode: Boolean? = false,
    @SerializedName("verified_application")
    @Expose
    val verifiedApplication: Boolean? = false,
    @SerializedName("device_id")
    @Expose
    val deviceId: String? = null,

    ): Serializable

data class SDKSettingsData(
    @SerializedName("status_display_duration")
    var statusDisplayDuration:Int,
    @SerializedName("otp_resend_interval")
    @Expose
    var otpResendInterval: Int,
    @SerializedName("otp_resend_attempts")
    @Expose
    var otpResendAttempts: Int,
) : Serializable
