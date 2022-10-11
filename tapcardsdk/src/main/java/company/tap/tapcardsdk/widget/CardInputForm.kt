package company.tap.tapcardsdk.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import android.text.*
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.animation.Animation
import android.view.animation.Transformation
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.annotation.ColorInt
import androidx.annotation.IdRes
import androidx.annotation.IntRange
import androidx.annotation.VisibleForTesting
import androidx.core.view.AccessibilityDelegateCompat
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import company.tap.tapcardsdk.R

import company.tap.tapcardsdk.utils.DateUtils
import company.tap.tapcardsdk.utils.TextValidator
import company.tap.tapcardsdk.views.CardNumberEditText
import company.tap.tapcardsdk.`interface`.BaseCardInput
import company.tap.tapcardsdk.`interface`.CardInputListener
import company.tap.tapcardsdk.`interface`.CardInputListener.FocusField.Companion.FOCUS_CARD
import company.tap.tapcardsdk.`interface`.CardInputListener.FocusField.Companion.FOCUS_CVC
import company.tap.tapcardsdk.`interface`.CardInputListener.FocusField.Companion.FOCUS_EXPIRY
import company.tap.tapcardsdk.`interface`.CardInputListener.FocusField.Companion.FOCUS_HOLDERNAME
import company.tap.tapcardsdk.`interface`.CardValidCallback
import company.tap.tapcardsdk.brandtypes.Card
import company.tap.tapcardsdk.brandtypes.CardBrand
import company.tap.tapcardsdk.brandtypes.CardBrandSingle
import company.tap.tapcardsdk.brandtypes.CardInputUIStatus
import company.tap.tapcardsdk.databinding.CardInputFormBinding
import company.tap.tapuilibrary.themekit.ThemeManager
import company.tap.tapuilibrary.uikit.atoms.TapTextInput
import company.tap.tapuilibrary.uikit.utils.TapTextWatcher
import company.tap.tapuilibrary.uikit.views.TapAlertView
import kotlin.properties.Delegates

/**
 * A card input widget that handles all animation on its own.
 *
 * The individual `EditText` views of this widget can be styled by defining a style
 * `Tap.CardInputWidget.EditText` that extends `Tap.Base.CardInputWidget.EditText`.
 */
class CardInputForm @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr),
    BaseCardInput {
    private val viewBinding = CardInputFormBinding.inflate(
            LayoutInflater.from(context),
            this,true
    )

  //  private val containerLayout1 = viewBinding.container1
    private val containerLayout = viewBinding.container




    @JvmSynthetic
    internal val cardNumberEditText = viewBinding.cardNumberEditText

    @JvmSynthetic
    internal val expiryDateEditText = viewBinding.expiryDateEditText

    @JvmSynthetic
    internal val cvcNumberEditText = viewBinding.cvcEditText

    @JvmSynthetic
    internal val holderNameEditText = viewBinding.holderNameEditText

    @JvmSynthetic
    internal val checkBoxSaveCard = viewBinding.checkBoxSaveCard

    private var cardInputListener: CardInputListener? = null
    private var cardValidCallback: CardValidCallback? = null
    lateinit var alertView :TapAlertView
    lateinit var nfcButton :ImageView
    lateinit var scannerButton :ImageView
    lateinit var closeButton :ImageView
    lateinit var linearIconsLayout :LinearLayout

    private val frameStart: Int
        get() {
            val isLtr = context.resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_LTR
            return if (isLtr) {
                containerLayout.left
            } else {
                containerLayout.right
            }
        }
    private val cardValidTextWatcher = object : TapTextWatcher() {
        override fun afterTextChanged(s: Editable?) {
            super.afterTextChanged(s)
            cardValidCallback?.onInputChanged(invalidFields.isEmpty(), invalidFields)
        }
    }

    private val invalidFields: Set<CardValidCallback.Fields>
        get() {
            return listOfNotNull(
                    CardValidCallback.Fields.Number.takeIf {
                        cardNumberEditText.cardNumber == null
                    },
                    CardValidCallback.Fields.Expiry.takeIf {
                        expiryDateEditText.validDateFields == null
                    },
                    CardValidCallback.Fields.Cvc.takeIf {
                        this.cvcValue == null
                    }
            ).toSet()
        }

    @VisibleForTesting
    internal var shouldShowErrorIcon = false
        private set(value) {
            val isValueChange = field != value
            field = value

            if (isValueChange) {
                updateIcon()
            }
        }


    @JvmSynthetic
    internal var cardNumberIsViewed = true

    private var initFlag: Boolean = false

    @JvmSynthetic
    internal var layoutWidthCalculator: LayoutWidthCalculator =
        DefaultLayoutWidthCalculator()

    internal val placementParameters: PlacementParameters =
        PlacementParameters()

    private val holderNameValue: String?
        get() {
            return holderNameEditText.holderName

        }

    private val cvcValue: String?
        get() {
            return cvcNumberEditText.cvcValue
        }

    private val brand: CardBrand
        get() {
            return cardNumberEditText.cardBrand
        }

    var shouldChangeIcon = true

    @VisibleForTesting
    @JvmSynthetic
    internal val requiredFields: List<TapTextInput>
    private val allFields: List<TapTextInput>

    /**
     * The [TapEditText] fields that are currently enabled and active in the UI.
     */
    @VisibleForTesting
    internal val currentFields: List<TapTextInput>
        @JvmSynthetic
        get() {
            return requiredFields

                    .filterNotNull()
        }

    /**
     * Gets a [Card] object from the user input, if all fields are valid. If not, returns
     * `null`.
     *
     * @return a valid [Card] object based on user input, or `null` if any field is
     * invalid
     */
    override val card: Card?
        get() {
            return cardBuilder?.build()
        }

    override val cardBuilder: Card.Builder?
        get() {
            val cardNumber = cardNumberEditText.cardNumber
            val cardDate = expiryDateEditText.validDateFields
            val cvcValue = this.cvcValue

            cardNumberEditText.shouldShowError = cardNumber == null
            expiryDateEditText.shouldShowError = cardDate == null
            cvcNumberEditText.shouldShowError = cvcValue == null
          //  cvcNumberEditText.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
            holderNameEditText.shouldShowError =
                   // holderNameRequired &&
                            holderNameEditText.holderName.isNullOrBlank()

            // Announce error messages for accessibility
            currentFields
                    .filter { it.shouldShowError }
                    .forEach { editText ->
                        editText.fieldErrorMessage?.let { errorMessage ->
                            editText.announceForAccessibility(errorMessage)
                        }
                    }

            when {
                cardNumber == null -> {
                    cardNumberEditText.shouldShowError
                    cardNumberEditText.requestFocus()
                }
                cardDate == null -> {
                    expiryDateEditText.requestFocus()
                }
                cvcValue == null -> {
                    cvcNumberEditText.requestFocus()
                  //  cvcNumberEditText.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD

                }
                holderNameEditText.shouldShowError -> {
                    holderNameEditText.requestFocus()
                }
                else -> {
                    println("cardNumber is"+cardNumber)
                    shouldShowErrorIcon = false
                    return Card.Builder(
                        cardNumber,
                        cardDate.first,
                        cardDate.second,
                        cvcValue
                    )
                            .addressZip(holderNameValue)
                            .loggingTokens(setOf(LOGGING_TOKEN))
                }
            }

            shouldShowErrorIcon = true

            return null
        }

    private val frameWidth: Int
        get() = frameWidthSupplier()

    @JvmSynthetic
    internal var frameWidthSupplier: () -> Int




    init {
        // This ensures that onRestoreInstanceState is called
        // during rotations.
        if (id == View.NO_ID) {
            id =
                DEFAULT_READER_ID
        }
        orientation = HORIZONTAL
        minimumWidth = resources.getDimensionPixelSize(R.dimen.card_widget_min_width)

        frameWidthSupplier = { containerLayout.width }

        requiredFields = listOf(
                cardNumberEditText, cvcNumberEditText, expiryDateEditText
        )

        allFields = requiredFields.plus(holderNameEditText)
       // allFields = requiredFields


        initView(attrs)
    }



    override fun onFinishInflate() {
        super.onFinishInflate()
    }

    override fun setCardValidCallback(callback: CardValidCallback?) {
        this.cardValidCallback = callback
        requiredFields.forEach { it.removeTextChangedListener(cardValidTextWatcher) }

        // only add the TextWatcher if it will be used
        if (callback != null) {
            requiredFields.forEach { it.addTextChangedListener(cardValidTextWatcher) }
        }

        // call immediately after setting
        cardValidCallback?.onInputChanged(invalidFields.isEmpty(), invalidFields)
    }

    /**
     * Set a [CardInputListener] to be notified of card input events.
     *
     * @param listener the listener
     */
    override fun setCardInputListener(listener: CardInputListener?) {
        cardInputListener = listener
    }

    override fun setSingleCardInput(cardBrand: CardBrandSingle, iconUrl : String?) {
        shouldChangeIcon = false
      //  cardBrandView.showBrandIconSingle(cardBrand, shouldShowErrorIcon)

    }

    override fun setCardNumberApiTextWatcher(cardApiNumberTextWatcher: TextValidator) {
       cardNumberEditText.addTextChangedListener(object :TextValidator(cardNumberEditText){
           override fun validate(cardNumberEditText: CardNumberEditText?, text: String?) {
           }

       })
    }

    /**
     * The switchCardEnabled field is disabled by default. Will be enabled from parent class
     * on cardform completeion
     */
    var switchCardEnabled: Boolean by Delegates.observable(
        BaseCardInput.DEFAULT_SWITCH
    ) { _, _, isEnabled ->
          if (isEnabled) {
            //  mainSwitchInline.visibility = View.VISIBLE

          } else {
              //mainSwitchInline.visibility = View.GONE

          }
    }


    /**
     * Set the card number. Method does not change text field focus.
     *
     * @param cardNumber card number to be set
     */
    override fun setCardNumber(cardNumber: String?) {
        println("setCardNumber value>>>"+cardNumber)
        cardNumberEditText.setText(cardNumber)
       // println("maskCardNumber>>>"+maskCardNumber(fieldText))
        this.cardNumberIsViewed = !cardNumberEditText.isCardNumberValid
    }

    override fun setCardHolderName(cardHolderName: String?) {
        holderNameEditText.setText(cardHolderName)
    }

    override fun setCardHint(cardHint: String) {
        cardNumberEditText.hint = cardHint
    }

    override fun setCardHolderHint(cardHolderHint: String) {
        holderNameEditText.hint = cardHolderHint
    }

    override fun setCVVHint(cvvHint: String) {
        cvcNumberEditText.hint =cvvHint
    }

    override fun setExpiryHint(expiryHint: String) {
        expiryDateEditText.hint =expiryHint
    }

    /**
     * Set the expiration date. Method invokes completion listener and changes focus
     * to the CVC field if a valid date is entered.
     *
     * Note that while a four-digit and two-digit year will both work, information
     * beyond the tens digit of a year will be truncated. Logic elsewhere in the SDK
     * makes assumptions about what century is implied by various two-digit years, and
     * will override any information provided here.
     *
     * @param month a month of the year, represented as a number between 1 and 12
     * @param year a year number, either in two-digit form or four-digit form
     */
    override fun setExpiryDate(
            @IntRange(from = 1, to = 12) month: Int,
            @IntRange(from = 0, to = 9999) year: Int
    ) {
        expiryDateEditText.setText(DateUtils.createDateStringFromIntegerInput(month, year))
    }

    /**
     * Set the CVC value for the card. Note that the maximum length is assumed to
     * be 3, unless the brand of the card has already been set (by setting the card number).
     *
     * @param cvcCode the CVC value to be set
     */
    override fun setCvcCode(cvcCode: String?) {
      //  cvcNumberEditText.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
        cvcNumberEditText.setText(cvcCode)
    }

    @SuppressLint("SetTextI18n")
    override fun setSavedCardDetails(cardDetails: Any?, cardInputUIStatus: CardInputUIStatus) {
        cardDetails as Card
        initFlag = true
        cardNumberIsViewed = false
       // onTouchHandling()
        cvcNumberEditText.requestFocus()
        cvcNumberEditText.setHint("Enter CVV")
       // cvcNumberEditText.getBackground().setColorFilter(getResources().getColor(R.color.red_error), PorterDuff.Mode.SRC_ATOP)
        cvcNumberEditText.isEnabled = true


        cardNumberEditText.setText(cardDetails.number.toString())
        cardNumberEditText.isEnabled = false
        expiryDateEditText.setText(cardDetails.expMonth.toString()+"/"+cardDetails?.expYear.toString())
        expiryDateEditText.isEnabled = false

        nfcButton.visibility= View.GONE
        scannerButton.visibility= View.GONE

    }

    @JvmSynthetic
    internal fun setHolderName(holderName: String?) {
        holderNameEditText.setText(holderName)
    }

    /**
     * Clear all text fields in the CardInputWidget.
     */
    override fun clear() {
//        if (currentFields.any { it.hasFocus() } || this.hasFocus()) {
//        }
        cardNumberEditText.requestFocus()
        invalidate()
        currentFields.forEach { it.text?.clearSpans() }
        currentFields.forEach { it.text?.clear() }
        currentFields.forEach { it.setText("") }
        cardNumberEditText.isEnabled = true
        cvcNumberEditText.isEnabled = true
        expiryDateEditText.isEnabled = true

        nfcButton.visibility= View.VISIBLE
        scannerButton.visibility= View.VISIBLE
    }

    /**
     * Enable or disable text fields
     *
     * @param isEnabled boolean indicating whether fields should be enabled
     */
    override fun setEnabled(isEnabled: Boolean) {
        currentFields.forEach { it.isEnabled = isEnabled }
    }

    /**
     * Set a `TextWatcher` to receive card number changes.
     */
    override fun setCardNumberTextWatcher(cardNumberTextWatcher: TextWatcher?) {
        cardNumberEditText.addTextChangedListener(cardNumberTextWatcher)
    }
    /**
     * Remove a `TextWatcher` to receive card number changes.
     */
    override fun removeCardNumberTextWatcher(cardNumberTextWatcher: TextWatcher?) {
        cardNumberEditText.removeTextChangedListener(cardNumberTextWatcher)
    }

    /**
     * Set a `TextWatcher` to receive expiration date changes.
     */
    override fun setExpiryDateTextWatcher(expiryDateTextWatcher: TextWatcher?) {
        expiryDateEditText.addTextChangedListener(expiryDateTextWatcher)
    }

    /**
     * Set a `TextWatcher` to receive CVC value changes.
     */
    override fun setCvcNumberTextWatcher(cvcNumberTextWatcher: TextWatcher?) {
      //  cvcNumberEditText.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
        cvcNumberEditText.addTextChangedListener(cvcNumberTextWatcher)
    }
    /**
     * Set a `Delegate` to receive switch changes.
     */
    override fun setSwitchSaveCardListener(switchListener: CompoundButton.OnCheckedChangeListener?) {
       //mainSwitchInline.switchSaveCard?.setOnCheckedChangeListener(switchListener)
    }

    /**
     * Set a `TextWatcher` to receive postal code changes.
     */
    override fun setHolderNameTextWatcher(holderNameTextWatcher: TextWatcher?) {
        holderNameEditText.addTextChangedListener(holderNameTextWatcher)
    }

    /**
     * Override of [View.isEnabled] that returns `true` only
     * if all three sub-controls are enabled.
     *
     * @return `true` if the card number field, expiry field, and cvc field are enabled,
     * `false` otherwise
     */
    override fun isEnabled(): Boolean {
        return requiredFields.all { it.isEnabled }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action != MotionEvent.ACTION_DOWN) {
            return super.onInterceptTouchEvent(ev)
        }

        return getFocusRequestOnTouch(ev.x.toInt())?.let {
            it.requestFocus()
            true
        } ?: super.onInterceptTouchEvent(ev)
    }

    override fun onSaveInstanceState(): Parcelable {
        return Bundle().apply {
            putParcelable(STATE_SUPER_STATE, super.onSaveInstanceState())
            putBoolean(STATE_CARD_VIEWED, cardNumberIsViewed)

        }
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        super.onRestoreInstanceState(state)

    }

    /**
     * Checks on the horizontal position of a touch event to see if
     * that event needs to be associated with one of the controls even
     * without having actually touched it. This essentially gives a larger
     * touch surface to the controls. We return `null` if the user touches
     * actually inside the widget because no interception is necessary - the touch will
     * naturally give focus to that control, and we don't want to interfere with what
     * Android will naturally do in response to that touch.
     *
     * @param touchX distance in pixels from the start side of this control
     * @return a [TapEditText] that needs to request focus, or `null`
     * if no such request is necessary.
     */
    @VisibleForTesting
    internal fun getFocusRequestOnTouch(touchX: Int): View? {
        //check this as it was constraint.left
        val frameStart :Int= this.frameStart
        println("cardNumberIsViewed >>>"+cardNumberIsViewed)
        return when {
            cardNumberIsViewed -> {
                // Then our view is
                // |CARDVIEW||space||DATEVIEW|

                when {
                    touchX < frameStart + placementParameters.cardWidth -> // Then the card edit view will already handle this touch.
                        null
                    touchX < placementParameters.cardTouchBufferLimit -> // Then we want to act like this was a touch on the card view
                        cardNumberEditText
                    touchX < placementParameters.dateStartPosition -> // Then we act like this was a touch on the date editor.
                        expiryDateEditText
                    touchX < placementParameters.cvcStartPosition -> // We need to act like this was a touch on the cvc editor.
                        cvcNumberEditText
                    else -> // Then the date editor will already handle this touch.
                        null
                }
            }

            else -> {
              //  println("else block called"+placementParameters.cvcStartPosition)
               // println("else block called"+placementParameters.dateStartPosition)
              //  println("else block called"+placementParameters.dateEndTouchBufferLimit)
              //  println("else block called"+touchX)
                // Our view is
                // |PEEK||space||DATE||space||CVC|
                when {
                    touchX < frameStart + placementParameters.peekCardWidth -> // This was a touch on the card number editor, so we don't need to handle it.
                        null
                    touchX <placementParameters.cardTouchBufferLimit -> // Then we need to act like the user touched the card editor
                        cardNumberEditText
                    touchX < placementParameters.dateStartPosition-> // Then we need to act like this was a touch on the date editor
                        expiryDateEditText
                  touchX < placementParameters.dateStartPosition + placementParameters.dateWidth -> // Just a regular touch on the date editor.
                       null
                   touchX < placementParameters.dateEndTouchBufferLimit -> // We need to act like this was a touch on the date editor
                       null
                    touchX < placementParameters.cvcStartPosition   -> // We need to act like this was a touch on the cvc editor.
                        cvcNumberEditText
                    else -> null
                }
            }
        }
    }



    private fun updateFieldLayout(view: View, width: Int, marginStart: Int) {
        view.layoutParams = (view.layoutParams as LinearLayout.LayoutParams).apply {
            this.width = width
            this.marginStart = marginStart
        }
    }

    private fun getDesiredWidthInPixels(text: String, editText: TapTextInput): Int {
        return layoutWidthCalculator.calculate(text, editText.paint)
    }

    private fun initView(attrs: AttributeSet?) {
        attrs?.let { applyAttributes(it) }
        //backArrow.visibility = View.GONE
        ViewCompat.setAccessibilityDelegate(
                cardNumberEditText,
                object : AccessibilityDelegateCompat() {
                    override fun onInitializeAccessibilityNodeInfo(
                            host: View,
                            info: AccessibilityNodeInfoCompat
                    ) {
                        super.onInitializeAccessibilityNodeInfo(host, info)
                        // Avoid reading out "1234 1234 1234 1234"
                        info.hintText = null

                    }
                })
        ViewCompat.setAccessibilityDelegate(
            cvcNumberEditText,
            object : AccessibilityDelegateCompat() {
                override fun onInitializeAccessibilityNodeInfo(
                    host: View,
                    info: AccessibilityNodeInfoCompat
                ) {
                    super.onInitializeAccessibilityNodeInfo(host, info)
                    // Avoid reading out "CVV"
                    info.hintText = null

                }
            })
        cardNumberIsViewed = true

        @ColorInt var errorColorInt = cardNumberEditText.defaultErrorColorInt
      // cardBrandView.tintColorInt = cardNumberEditText.hintTextColors.defaultColor
        var cardHintText: String? = null
        if (attrs != null) {
            val a = context.theme.obtainStyledAttributes(
                    attrs,
                R.styleable.CardInputView,
                    0, 0)
/*
            try {
                cardBrandView.tintColorInt = a.getColor(
                    R.styleable.CardInputView_cardTint,
                        cardBrandView.tintColorInt
                )
                errorColorInt = a.getColor(R.styleable.CardInputView_cardTextErrorColor, errorColorInt)
                cardHintText = a.getString(R.styleable.CardInputView_cardHintText)
            } finally {
                a.recycle()
            }*/
        }

        cardHintText?.let {
            cardNumberEditText.hint = it
        }

     //   currentFields.forEach { it.setErrorColor(errorColorInt) }

        cardNumberEditText.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
              //  scrollStart()
                cardInputListener?.onFocusChange(FOCUS_CARD)
               // expiryDateEditText.visibility = View.VISIBLE
              //  cvcNumberEditText.visibility = View.VISIBLE
            }
        }

        expiryDateEditText.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {

                cardInputListener?.onFocusChange(FOCUS_EXPIRY)

            }

        }

        expiryDateEditText.setDeleteEmptyListener(
            BackUpFieldDeleteListener(
                cardNumberEditText
            )
        )
        cvcNumberEditText.setDeleteEmptyListener(
            BackUpFieldDeleteListener(
                expiryDateEditText
            )
        )
        cardNumberEditText.setDeleteEmptyListener(
            BackUpFieldDeleteListener(
                holderNameEditText
            )
        )

        cvcNumberEditText.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {

                cardInputListener?.onFocusChange(FOCUS_CVC)

            }
            updateIconCvc(hasFocus, cvcValue)
        }

        holderNameEditText.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {

                cardInputListener?.onFocusChange(FOCUS_HOLDERNAME)
            }
          // updateIconCvc(hasFocus, cvcValue)
        }

        cvcNumberEditText.setAfterTextChangedListener(
                object : TapTextInput.AfterTextChangedListener {
                    override fun onTextChanged(text: String) {
                        if (brand.isMaxCvc(text)) {
                            cardInputListener?.onCvcComplete()

                        }
                        updateIconCvc(cvcNumberEditText.hasFocus(), text)

                    }
                }
        )

     /*   cardBrandView.onScanClicked = {
            Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show()
        }*/

        cardNumberEditText.displayErrorCallback = {
            shouldShowErrorIcon = it
        }

        cardNumberEditText.completionCallback = {
            expiryDateEditText.visibility = View.VISIBLE
            cvcNumberEditText.visibility = View.VISIBLE
            println("cardNumberEditText is????"+cardNumberEditText.cardNumber)
           // scrollEnd()
            cardInputListener?.onCardComplete()
        }

        cardNumberEditText.brandChangeCallback = { brand ->
            hiddenCardText = createHiddenCardText(brand)
            updateIcon()
            cvcNumberEditText.updateBrand(brand)
        }

        expiryDateEditText.completionCallback = {
            cvcNumberEditText.requestFocus()
            cardInputListener?.onExpirationComplete()
        }

        cvcNumberEditText.completionCallback = {
            holderNameEditText.requestFocus()

        }

//        allFields.forEach { it.addTextChangedListener(inputChangeTextWatcher) }

        cardNumberEditText.requestFocus()

        if(ThemeManager.currentTheme!=null) {

         /*   cardNumberEditText.setHintTextColor(Color.parseColor(ThemeManager.getValue("emailCard.textFields.placeHolderColor")))
            cardNumberEditText.setTextColor(Color.parseColor(ThemeManager.getValue("emailCard.textFields.textColor")))
            expiryDateEditText.setHintTextColor(Color.parseColor(ThemeManager.getValue("emailCard.textFields.placeHolderColor")))
            expiryDateEditText.setTextColor(Color.parseColor(ThemeManager.getValue("emailCard.textFields.textColor")))
            cvcNumberEditText.setHintTextColor(Color.parseColor(ThemeManager.getValue("emailCard.textFields.placeHolderColor")))
            cvcNumberEditText.setTextColor(Color.parseColor(ThemeManager.getValue("emailCard.textFields.textColor")))
            holderNameEditText.setHintTextColor(Color.parseColor(ThemeManager.getValue("emailCard.textFields.placeHolderColor")))
            holderNameEditText.setTextColor(Color.parseColor(ThemeManager.getValue("emailCard.textFields.textColor")))*/
        }
        cardNumberEditText.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // do your stuff here
                    println("expiryDateEditText>>>>>>>>"+expiryDateEditText)
                    expiryDateEditText.requestFocus()
                }
                return false
            }
        })

    }

    /**
     * @return a [String] that is the length of a full card number for the current [brand],
     * without the last group of digits when the card is formatted with spaces. This is used for
     * measuring the rendered width of the hidden portion (i.e. when the card number is "peeking")
     * and does not have to be a valid card number.
     *
     * e.g. if [brand] is [CardBrand.Visa], this will generate `"0000 0000 0000 "` (including the
     * trailing space).
     *
     * This should only be called when [brand] changes.
     */
    @VisibleForTesting
    internal fun createHiddenCardText(
        brand: CardBrand,
        cardNumber: String = cardNumberEditText.fieldText
    ): String {
        var lastIndex = 0
        val digits: MutableList<String> = mutableListOf()

        brand.getSpacePositionsForCardNumber(cardNumber)
                .toList()
                .sorted()
                .forEach {
                    repeat(it - lastIndex) {
                        digits.add("0")
                    }
                    digits.add(" ")
                    lastIndex = it + 1
                }

        return digits.joinToString(separator = "")
    }

    private fun applyAttributes(attrs: AttributeSet) {
        val typedArray = context.theme.obtainStyledAttributes(
                attrs, R.styleable.CardElement, 0, 0
        )


    }




    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(hasWindowFocus)
        if (hasWindowFocus && CardBrand.Unknown == brand) {
           // cardBrandView.applyTint(false)
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        if (!initFlag && width != 0) {
            initFlag = true
            placementParameters.totalLengthInPixels = frameWidth


        }
    }

    private var hiddenCardText: String = createHiddenCardText(brand)

    private val cvcPlaceHolder: String
        get() {
            return if (CardBrand.AmericanExpress == brand) {
                CVC_PLACEHOLDER_AMEX
            } else {
                CVC_PLACEHOLDER_COMMON
            }
        }

    private val peekCardText: String
        get() {
            return when (brand) {
                CardBrand.AmericanExpress -> PEEK_TEXT_AMEX
                CardBrand.DinersClub -> PEEK_TEXT_DINERS_14
                else -> PEEK_TEXT_COMMON
            }
        }

    private fun updateIcon() {
        if (!shouldChangeIcon) return
        //cardBrandView.showBrandIcon(brand, shouldShowErrorIcon)
    }

     fun updateIconCvc(
            hasFocus: Boolean,
            cvcText: String?
    ) {
        when {
            shouldShowErrorIcon -> {
                updateIcon()
            }
            shouldIconShowBrand(
                brand,
                hasFocus,
                cvcText
            ) -> {
                updateIcon()
            }
            else -> {
                updateIconForCvcEntry()
            }
        }
    }

    private fun updateIconForCvcEntry() {
        //cardBrandView.showCvcIcon(brand)
    }

    /**
     * A class for tracking the placement and layout of fields
     */
    internal class PlacementParameters {
        internal var totalLengthInPixels: Int = 0

        internal var cardWidth: Int = 0
        internal var hiddenCardWidth: Int = 0
        internal var peekCardWidth: Int = 0
        internal var cardDateSeparation: Int = 0
        internal var dateWidth: Int = 0
        internal var dateCvcSeparation: Int = 0
        internal var cvcWidth: Int = 0
        internal var cvcHolderNameSeparation: Int = 0
        internal var holderNameWidth: Int = 0

        internal var cardTouchBufferLimit: Int = 0
        internal var dateStartPosition: Int = 0
        internal var dateEndTouchBufferLimit: Int = 0
        internal var cvcStartPosition: Int = 0
        internal var cvcEndTouchBufferLimit: Int = 0
        internal var holderNameStartPosition: Int = 0

        private val cardPeekDateStartMargin: Int
            @JvmSynthetic
            get() {
                return peekCardWidth + cardDateSeparation
            }

        private val cardPeekCvcStartMargin: Int
            @JvmSynthetic
            get() {
                return cardPeekDateStartMargin + dateWidth + dateCvcSeparation
            }

        internal val cardPeekHolderNameStartMargin: Int
            @JvmSynthetic
            get() {
                return cardPeekCvcStartMargin + holderNameWidth + cvcHolderNameSeparation
            }

        @JvmSynthetic
        internal fun getDateStartMargin(isFullCard: Boolean): Int {
            return if (isFullCard) {
                cardWidth + cardDateSeparation
            } else {
                cardPeekDateStartMargin
            }
        }

        @JvmSynthetic
        internal fun getCvcStartMargin(isFullCard: Boolean): Int {
            return if (isFullCard) {
                totalLengthInPixels
            } else {
                cardPeekCvcStartMargin
            }
        }

        @JvmSynthetic
        internal fun getHolderNameStartMargin(isFullCard: Boolean): Int {
            return if (isFullCard) {
                totalLengthInPixels
            } else {
                cardPeekHolderNameStartMargin
            }
        }


        override fun toString(): String {
            val touchBufferData = """
                Touch Buffer Data:
                CardTouchBufferLimit = $cardTouchBufferLimit
                DateStartPosition = $dateStartPosition
                DateEndTouchBufferLimit = $dateEndTouchBufferLimit
                CvcStartPosition = $cvcStartPosition
                CvcEndTouchBufferLimit = $cvcEndTouchBufferLimit
                HolderNameStartPosition = $holderNameStartPosition
                """

            val elementSizeData = """
                TotalLengthInPixels = $totalLengthInPixels
                CardWidth = $cardWidth
                HiddenCardWidth = $hiddenCardWidth
                PeekCardWidth = $peekCardWidth
                CardDateSeparation = $cardDateSeparation
                DateWidth = $dateWidth
                DateCvcSeparation = $dateCvcSeparation
                CvcWidth = $cvcWidth
                CvcHolderNameSeparation = $cvcHolderNameSeparation
                HolderNameWidth: $holderNameWidth
                """

            return elementSizeData + touchBufferData
        }
    }

    private abstract class CardFieldAnimation : Animation() {
        init {
            duration =
                ANIMATION_LENGTH
        }

        private companion object {
            private const val ANIMATION_LENGTH = 150L
        }
    }

    private class CardNumberSlideStartAnimation(
            private val view: View
    ) : CardFieldAnimation() {
        init {
            setAnimationListener(object : AnimationEndListener() {
                override fun onAnimationEnd(animation: Animation) {
                    view.requestFocus()
                }
            })
        }

        override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            super.applyTransformation(interpolatedTime, t)
            println("view latout"+view.layoutParams)
            if(view.layoutParams is LinearLayout.LayoutParams){
                view.layoutParams = (view.layoutParams as LinearLayout.LayoutParams).apply {

                    marginStart = (marginStart * (1 - interpolatedTime)).toInt()
                }
            }else
            view.layoutParams = (view.layoutParams as LinearLayout.LayoutParams).apply {
                marginStart = (marginStart * (1 - interpolatedTime)).toInt()
            }
        }
    }

    private class ExpiryDateSlideStartAnimation(
            private val view: View,
            private val startPosition: Int,
            private val destination: Int
    ) : CardFieldAnimation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            super.applyTransformation(interpolatedTime, t)
            view.layoutParams = (view.layoutParams as LinearLayout.LayoutParams).apply {
                marginStart =
                        (interpolatedTime * destination + (1 - interpolatedTime) * startPosition).toInt()
            }
        }
    }

    private class CvcSlideStartAnimation(
            private val view: View,
            private val startPosition: Int,
            private val destination: Int,
            private val newWidth: Int
    ) : CardFieldAnimation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            super.applyTransformation(interpolatedTime, t)
            view.layoutParams = (view.layoutParams as LinearLayout.LayoutParams).apply {
                this.marginStart = (interpolatedTime * destination + (1 - interpolatedTime) * startPosition).toInt()
                this.marginEnd = 0
              //  this.width = newWidth
            }
        }
    }

    private class HolderNameSlideStartAnimation(
            private val view: View,
            private val startPosition: Int,
            private val destination: Int,
            private val newWidth: Int
    ) : CardFieldAnimation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
            super.applyTransformation(interpolatedTime, t)
            view.layoutParams = (view.layoutParams as LinearLayout.LayoutParams).apply {
                this.marginStart =
                        (interpolatedTime * destination + (1 - interpolatedTime) * startPosition).toInt()
                this.marginEnd = 0
                this.width = newWidth
            }
        }
    }

    private class CardNumberSlideEndAnimation(
            private val view: View,
            private val hiddenCardWidth: Int,
            private val focusOnEndView: View
    ) : CardFieldAnimation() {
        init {
            setAnimationListener(object : AnimationEndListener() {
                override fun onAnimationEnd(animation: Animation) {
                    focusOnEndView.requestFocus()
                }
            })
        }

        override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            super.applyTransformation(interpolatedTime, t)
            view.layoutParams = (view.layoutParams as LinearLayout.LayoutParams).apply {
                marginStart = (-0.9f * hiddenCardWidth.toFloat() * interpolatedTime).toInt()
            }
        }
    }

    private class ExpiryDateSlideEndAnimation(
            private val view: View,
            private val startMargin: Int,
            private val destination: Int
    ) : CardFieldAnimation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            super.applyTransformation(interpolatedTime, t)
            view.layoutParams = (view.layoutParams as LinearLayout.LayoutParams).apply {
                marginStart =
                        (interpolatedTime * destination + (1 - interpolatedTime) * startMargin).toInt()
            }
        }
    }

    private class CvcSlideEndAnimation(
            private val view: View,
            private val startMargin: Int,
            private val destination: Int,
            private val newWidth: Int
    ) : CardFieldAnimation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            super.applyTransformation(interpolatedTime, t)
            view.layoutParams = (view.layoutParams as LinearLayout.LayoutParams).apply {
                marginStart =
                        (interpolatedTime * destination + (1 - interpolatedTime) * startMargin).toInt()
                marginEnd = 0
                width = newWidth
            }
        }
    }

    private class HolderNameSlideEndAnimation(
            private val view: View,
            private val startMargin: Int,
            private val destination: Int,
            private val newWidth: Int
    ) : CardFieldAnimation() {

        override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            super.applyTransformation(interpolatedTime, t)
            view.layoutParams = (view.layoutParams as LinearLayout.LayoutParams).apply {
                this.marginStart =
                        (interpolatedTime * destination + (1 - interpolatedTime) * startMargin).toInt()
                this.marginEnd = 0
                this.width = newWidth
            }
        }
    }

    /**
     * A convenience class for when we only want to listen for when an animation ends.
     */
    private abstract class AnimationEndListener : Animation.AnimationListener {
        override fun onAnimationStart(animation: Animation) {
            // Intentional No-op
        }

        override fun onAnimationRepeat(animation: Animation) {
            // Intentional No-op
        }
    }

    internal interface LayoutWidthCalculator {
        fun calculate(text: String, paint: TextPaint): Int
    }

    internal class DefaultLayoutWidthCalculator :
        LayoutWidthCalculator {
        override fun calculate(text: String, paint: TextPaint): Int {
            return Layout.getDesiredWidth(text, paint).toInt()
        }
    }

    internal companion object {
        internal const val LOGGING_TOKEN = "CardInputView"

        private const val PEEK_TEXT_COMMON = "4242"
        private const val PEEK_TEXT_DINERS_14 = "88"
        private const val PEEK_TEXT_AMEX = "34343"

        private const val CVC_PLACEHOLDER_COMMON = "CVV"
        private const val CVC_PLACEHOLDER_AMEX = "2345"

        private const val FULL_SIZING_CARD_TEXT = "4242 4242 4242 4242"
        private const val FULL_SIZING_DATE_TEXT = "MM/MM"
        private const val FULL_SIZING_HOLDER_NAME_TEXT = "Holder Name..."

        private const val STATE_CARD_VIEWED = "state_card_viewed"
        private const val STATE_SUPER_STATE = "state_super_state"
        private const val STATE_POSTAL_CODE_ENABLED = "state_postal_code_enabled"

        // This value is used to ensure that onSaveInstanceState is called
        // in the event that the user doesn't give this control an ID.
        @IdRes
        private val DEFAULT_READER_ID =
            R.id.default_reader_id

        /**
         * Determines whether or not the icon should show the card brand instead of the
         * CVC helper icon.
         *
         * @param brand the [CardBrand] of the card number
         * @param cvcHasFocus `true` if the CVC entry field has focus, `false` otherwise
         * @param cvcText the current content of [cvcNumberEditText]
         *
         * @return `true` if we should show the brand of the card, or `false` if we
         * should show the CVC helper icon instead
         */
        @VisibleForTesting
        internal fun shouldIconShowBrand(
            brand: CardBrand,
            cvcHasFocus: Boolean,
            cvcText: String?
        ): Boolean {
            return !cvcHasFocus || brand.isMaxCvc(cvcText)
        }
    }

    fun onTouchView(){
        onTouchHandling()

    }

    private fun onTouchHandling(){
        expiryDateEditText.visibility = View.VISIBLE
        cvcNumberEditText.visibility = View.VISIBLE

        cardInputListener?.onCardComplete()
    }
    private fun  maskCardNumber(cardInput: String): String {
        val maskLen: Int = cardInput.length - 4
        println("maskLen"+maskLen)
        println("cardInput"+cardInput.length)
        if (maskLen <= 0) return cardInput // Nothing to mask
        return (cardInput).replaceRange(0, maskLen, "•••• ")
    }


    }

