package company.tap.tapcardsdk.open


import company.tap.tapcardsdk.internal.logic.api.models.Charge
import company.tap.tapcardsdk.internal.logic.api.models.Token
import company.tap.tapnetworkkit.exception.GoSellError

/**
 * Created by AhlaamK on 3/23/22.

Copyright (c) 2022    Tap Payments.
All rights reserved.
 **/
interface TapCardInputDelegate {
    /**
     * CallBack of  cardTokenizedSuccessfully returns @param token.
     **/
    fun cardTokenizedSuccessfully(token: Token)
    /**
     * CallBack of  cardTokenizedFailed returns @param goSellError.
     **/
    fun cardTokenizedFailed(goSellError: GoSellError?)
    /**
     * CallBack of  backendUnknownError returns @param errorMessage.
     **/
    fun backendUnknownError(message: String?)
    /**
     * CallBack of  cardNotSupported in case not allowed card is passed returns @param errorMessage.
     **/
    fun cardNotSupported(message: String?)
    /**
     * CallBack of  cardFormIsGettingReady.
     **/
    fun cardFormIsGettingReady()
    /**
     * CallBack of  cardFormIsReady.
     **/
    fun cardFormIsReady()
    /**
     * CallBack of  cardSavedSuccessfully returns @param saveCard.
     **/
    fun cardSavedSuccessfully(saveCard: Charge)
    /**
     * CallBack of  cardSavingFailed returns @param chargeError.
     **/
    fun cardSavingFailed(chargeError: Charge)
    
}