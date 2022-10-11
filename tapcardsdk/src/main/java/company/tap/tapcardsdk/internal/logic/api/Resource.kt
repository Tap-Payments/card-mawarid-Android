package company.tap.tapcardsdk.internal.logic.api

/**
 * Created by AhlaamK on 3/23/22.

Copyright (c) 2022    Tap Payments.
All rights reserved.
 **/
sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Loading<T> : Resource<T>()
    class Finished<T> : Resource<T>()
    class Error<T>(message: String) : Resource<T>(message = message)
}