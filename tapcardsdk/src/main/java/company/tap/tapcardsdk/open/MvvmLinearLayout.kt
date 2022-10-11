package company.tap.tapcardsdk.open

import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.lifecycle.LifecycleOwner

import company.tap.tapcardsdk.internal.logic.LifecycleOwnerNotFoundException
import company.tap.tapcardsdk.internal.logic.viewmodels.MvvmCustomView
import company.tap.tapcardsdk.internal.logic.viewmodels.MvvmCustomViewModel
import company.tap.tapcardsdk.internal.logic.viewmodels.MvvmCustomViewState
import company.tap.tapcardsdk.internal.logic.viewmodels.MvvmCustomViewStateWrapper

/**
 * Created by AhlaamK on 3/31/22.

Copyright (c) 2022    Tap Payments.
All rights reserved.
 **/
abstract class MvvmLinearLayout<V: MvvmCustomViewState, T: MvvmCustomViewModel<V>>(
    context: Context,
    attributeSet: AttributeSet?
): LinearLayout(context, attributeSet), MvvmCustomView<V, T> {
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val lifecycleOwner = context as? LifecycleOwner ?: throw LifecycleOwnerNotFoundException()
        onLifecycleOwnerAttached(lifecycleOwner)
    }

    override fun onSaveInstanceState() = MvvmCustomViewStateWrapper(super.onSaveInstanceState(), viewModel.state)

    @Suppress("UNCHECKED_CAST")
    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is MvvmCustomViewStateWrapper) {
            viewModel.state = state.state as V?
            super.onRestoreInstanceState(state.superState)
        }
    }
}