package company.tap.tapcardsdk.internal.logic.viewmodels

import androidx.lifecycle.LifecycleOwner

/**
 * Created by AhlaamK on 3/31/22.

Copyright (c) 2022    Tap Payments.
All rights reserved.
 **/
interface MvvmCustomView<V: MvvmCustomViewState, T: MvvmCustomViewModel<V>> {
    val viewModel: T

    fun onLifecycleOwnerAttached(lifecycleOwner: LifecycleOwner)
}