package company.tap.tapcardsdk.internal.logic.interfaces

import java.util.ArrayList

/**
 * Created by AhlaamK on 7/24/22.

Copyright (c) 2022    Tap Payments.
All rights reserved.
 **/
interface CurrenciesSupport {
    /**
     * Gets supported currencies.
     *
     * @return the supported currencies
     */
    fun getSupportedCurrencies(): ArrayList<String>?
}