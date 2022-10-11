package company.tap.tapcardsdk.internal.logic.viewmodels

/**
 * Created by AhlaamK on 3/31/22.

Copyright (c) 2022    Tap Payments.
All rights reserved.
 **/
interface MvvmCustomViewModel<T: MvvmCustomViewState> {
    var state: T?
}