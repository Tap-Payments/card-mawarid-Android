package company.tap.tapcardsdk.internal.ui.`interface`

import android.text.TextWatcher
import android.widget.CompoundButton
import androidx.annotation.IntRange
import company.tap.tapcardsdk.internal.ui.brandtypes.Card
import company.tap.tapcardsdk.internal.ui.brandtypes.CardBrandSingle
import company.tap.tapcardsdk.internal.ui.brandtypes.CardInputUIStatus
import company.tap.tapcardsdk.internal.ui.utils.TextValidator

internal interface BaseCardInput {
    val card: Card?

    val cardBuilder: Card.Builder?

    fun setCardValidCallback(callback: CardValidCallback?)

    fun setCardInputListener(listener: CardInputListener?)

    fun setSingleCardInput(cardBrand: CardBrandSingle, iconUrl : String?=null)

    /**
     * Set a `TextWatcher` to receive card number changes.
     */
    fun setCardNumberApiTextWatcher(cardApiNumberTextWatcher: TextValidator)
    /**
     * Set a `TextWatcher` to receive card number changes.
     */
    fun setCardNumberTextWatcher(cardNumberTextWatcher: TextWatcher?)
    /**
     * Remove a `TextWatcher` to receive card number changes.
     */
    fun removeCardNumberTextWatcher(cardNumberTextWatcher: TextWatcher?)

    /**
     * Set a `TextWatcher` to receive expiration date changes.
     */
    fun setExpiryDateTextWatcher(expiryDateTextWatcher: TextWatcher?)

    /**
     * Set a `TextWatcher` to receive CVC value changes.
     */
    fun setCvcNumberTextWatcher(cvcNumberTextWatcher: TextWatcher?)

    /**
     * Set a `setSwitchSaveCardListener value changes.
     */
    fun setSwitchSaveCardListener(switchListener: CompoundButton.OnCheckedChangeListener?)

    /**
     * Set a `TextWatcher` to receive postal code changes.
     */
    fun setHolderNameTextWatcher(holderNameTextWatcher: TextWatcher?)

    fun setCardHint(cardHint: String)
    fun setCardHolderHint(cardHolderHint: String)
    fun setCVVHint(cvvHint: String)
    fun setExpiryHint(expiryHint: String)

    fun clear()

    fun setCardNumber(cardNumber: String?)

    fun setCardHolderName(cardHolderName: String?)

    fun setExpiryDate(
        @IntRange(from = 1, to = 12) month: Int,
        @IntRange(from = 0, to = 9999) year: Int
    )

    fun setCvcCode(cvcCode: String?)


    companion object {
        internal const val DEFAULT_HOLDER_NAME_ENABLED = false
        internal const val DEFAULT_SWITCH = false
    }
}
