package company.tap.tapcardsdk.internal.logic.interfaces


import company.tap.nfcreader.open.reader.TapEmvCard
import company.tap.tapcardsdk.internal.logic.api.models.Card
import java.io.Serializable

/**
 * Created by AhlaamK on 3/24/22.

Copyright (c) 2022    Tap Payments.
All rights reserved.
 **/
interface CardScannerNfcCallback : Serializable {
    fun cardScanned(card: Card)
    fun cardNFC(tapEmvCard: TapEmvCard)

}
