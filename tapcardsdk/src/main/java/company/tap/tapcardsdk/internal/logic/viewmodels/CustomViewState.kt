package company.tap.tapcardsdk.internal.logic.viewmodels

import company.tap.cardscanner.TapCard
import company.tap.nfcreader.open.reader.TapEmvCard
import kotlinx.android.parcel.Parcelize

/**
 * Created by AhlaamK on 3/31/22.

Copyright (c) 2022    Tap Payments.
All rights reserved.
 **/
@Parcelize
data class CustomViewState(
    val card: TapCard?,
    val nfcCard :TapEmvCard?
): MvvmCustomViewState {

}
