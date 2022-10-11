package company.tap.tapcardsdk.internal.logic.viewmodels

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by AhlaamK on 3/31/22.

Copyright (c) 2022    Tap Payments.
All rights reserved.
 **/
@Parcelize
class MvvmCustomViewStateWrapper(
    val superState: Parcelable?,
    val state: MvvmCustomViewState?
): Parcelable