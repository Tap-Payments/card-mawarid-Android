package company.tap.tapcardsdk.open

import android.app.DatePickerDialog
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Parcelable
import android.text.*
import android.util.AttributeSet
import android.view.*
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
import company.tap.tapcardsdk.databinding.CardInputFormBinding
import company.tap.tapcardsdk.internal.logic.api.models.CreateTokenCard
import company.tap.tapcardsdk.internal.ui.`interface`.BaseCardInput
import company.tap.tapcardsdk.internal.ui.`interface`.CardInputListener
import company.tap.tapcardsdk.internal.ui.`interface`.CardInputListener.FocusField.Companion.FOCUS_CARD
import company.tap.tapcardsdk.internal.ui.`interface`.CardInputListener.FocusField.Companion.FOCUS_CVC
import company.tap.tapcardsdk.internal.ui.`interface`.CardInputListener.FocusField.Companion.FOCUS_EXPIRY
import company.tap.tapcardsdk.internal.ui.`interface`.CardInputListener.FocusField.Companion.FOCUS_HOLDERNAME
import company.tap.tapcardsdk.internal.ui.`interface`.CardValidCallback
import company.tap.tapcardsdk.internal.ui.brandtypes.Card
import company.tap.tapcardsdk.internal.ui.brandtypes.CardBrand
import company.tap.tapcardsdk.internal.ui.brandtypes.CardBrandSingle
import company.tap.tapcardsdk.internal.ui.utils.DateUtils
import company.tap.tapcardsdk.internal.ui.utils.TextValidator
import company.tap.tapcardsdk.internal.ui.views.CardNumberEditText
import company.tap.tapcardsdk.internal.ui.widget.BackUpFieldDeleteListener
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibrary.fontskit.enums.TapFont
import company.tap.tapuilibrary.themekit.ThemeManager
import company.tap.tapuilibrary.uikit.atoms.TapTextInput
import company.tap.tapuilibrary.uikit.utils.TapTextWatcher
import java.util.*
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
    BaseCardInput{
    private val viewBinding = CardInputFormBinding.inflate(
            LayoutInflater.from(context),
            this,true
    )

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
    val cardDetailsText = viewBinding.cardDetailsText
    val datePicker = viewBinding.datePickerActions

    private var cardNumber: String? = null
    private var cardHolderName: String? = "CARD HOLDER NAME"
    private var expiryDate: String? = null
    private var cvvNumber: String? = null
    private var day_of_month: Int? = null
    private var year: Int? = null
    private var month: Int? = null
    lateinit var monthVal:String

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
       cardNumberEditText.addTextChangedListener(object : TextValidator(cardNumberEditText){
           override fun validate(cardNumberEditText: CardNumberEditText?, text: String?) {
           }

       })
    }

    /**
     * The switchCardEnabled field is disabled by default. Will be enabled from parent class
     * on cardform completeion
     */
    var checkedSaveCardEnabled: Boolean by Delegates.observable(
        BaseCardInput.DEFAULT_SWITCH
    ) { _, _, isEnabled ->

    }


    /**
     * Set the card number. Method does not change text field focus.
     *
     * @param cardNumber card number to be set
     */
    override fun setCardNumber(cardNumber: String?) {
        println("setCardNumber value>>>"+cardNumber)
        cardNumberEditText.setText(cardNumber)
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
        cvcNumberEditText.setText(cvcCode)
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
        cvcNumberEditText.addTextChangedListener(cvcNumberTextWatcher)
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

/*    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action != MotionEvent.ACTION_DOWN) {
            return super.onInterceptTouchEvent(ev)
        }

        return getFocusRequestOnTouch(ev.x.toInt())?.let {
            it.requestFocus()
            true
        } ?: super.onInterceptTouchEvent(ev)
    }*/

    override fun onSaveInstanceState(): Parcelable {
        return Bundle().apply {
            putParcelable(STATE_SUPER_STATE, super.onSaveInstanceState())
            putBoolean(STATE_CARD_VIEWED, cardNumberIsViewed)

        }
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        super.onRestoreInstanceState(state)

    }




    private fun initView(attrs: AttributeSet?) {
        attrs?.let { applyAttributes(it) }
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
                DataConfiguration.getListener()?.cardDataValidation(CardValidation.INVALID)
               // expiryDateEditText.visibility = View.VISIBLE
              //  cvcNumberEditText.visibility = View.VISIBLE
            }
        }

        expiryDateEditText.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {

                cardInputListener?.onFocusChange(FOCUS_EXPIRY)
                DataConfiguration.getListener()?.cardDataValidation(CardValidation.INVALID)
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
                            DataConfiguration.getListener()?.cardDataValidation(CardValidation.VALID)

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
           // println("cardNumberEditText is????"+cardNumberEditText.cardNumber)
            cardNumber = cardNumberEditText.cardNumber
           // scrollEnd()
            cardInputListener?.onCardComplete()

            if(cardNumberEditText.isCardNumberValid){
                cvcNumberEditText.isEnabled = true
            }

        }

        cardNumberEditText.brandChangeCallback = { brand ->
            hiddenCardText = createHiddenCardText(brand)
            updateIcon()
            cvcNumberEditText.updateBrand(brand)
        }

        expiryDateEditText.completionCallback = {
            cvcNumberEditText.requestFocus()
            cardInputListener?.onExpirationComplete()
            expiryDate  = expiryDateEditText.fieldText
        }

        cvcNumberEditText.completionCallback = {
            holderNameEditText.requestFocus()
            cvvNumber  = cvcNumberEditText.fieldText
        }

        holderNameEditText.completionCallback={
            cardHolderName = holderNameEditText.holderName
        }


//        allFields.forEach { it.addTextChangedListener(inputChangeTextWatcher) }

        cardNumberEditText.requestFocus()

         initLocals()
        initTheme()
        clickAction()



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

    private fun clickAction() {
        datePicker.setOnClickListener {
            datePickerAction()
        }
    }

    private fun initTheme() {
        containerLayout.setBackgroundColor(Color.parseColor(ThemeManager.getValue("cardView.containter.backgroundColor")))
        cardNumberEditText.setHintTextColor(Color.parseColor(ThemeManager.getValue("cardView.textFields.placeholderColor")))
        cardNumberEditText.setTextColor(Color.parseColor(ThemeManager.getValue("cardView.textFields.textColor")))
        expiryDateEditText.setHintTextColor(Color.parseColor(ThemeManager.getValue("cardView.textFields.placeholderColor")))
        expiryDateEditText.setTextColor(Color.parseColor(ThemeManager.getValue("cardView.textFields.textColor")))
        cvcNumberEditText.setHintTextColor(Color.parseColor(ThemeManager.getValue("cardView.textFields.placeholderColor")))
        cvcNumberEditText.setTextColor(Color.parseColor(ThemeManager.getValue("cardView.textFields.textColor")))
        holderNameEditText.setHintTextColor(Color.parseColor(ThemeManager.getValue("cardView.textFields.placeholderColor")))
        holderNameEditText.setTextColor(Color.parseColor(ThemeManager.getValue("cardView.textFields.textColor")))
        cardDetailsText.setTextColor(Color.parseColor(ThemeManager.getValue("cardView.titleLabel.textColor")))
        cardDetailsText.textSize = ThemeManager.getFontSize("cardView.titleLabel.font").toFloat()

        val cardDetText : String = LocalizationManager.getValue("title","cardForm")
        cardDetailsText.text = cardDetText


        cardNumberEditText.textSize = ThemeManager.getFontSize("cardView.textFields.font").toFloat()
        holderNameEditText.textSize = ThemeManager.getFontSize("cardView.textFields.font").toFloat()
        cvcNumberEditText.textSize = ThemeManager.getFontSize("cardView.textFields.font").toFloat()
        expiryDateEditText.textSize = ThemeManager.getFontSize("cardView.textFields.font").toFloat()


        checkBoxSaveCard.setTextColor(Color.parseColor(ThemeManager.getValue("cardView.saveLabel.textColor")))
       // checkBoxSaveCard.backgroundTintList = ColorStateList.valueOf(Color.parseColor(ThemeManager.getValue("cardView.saveLabel.textColor")))
        checkBoxSaveCard.textSize = ThemeManager.getFontSize("cardView.saveLabel.font").toFloat()
        if (context?.let { LocalizationManager.getLocale(it).language } == "en") setFontsEnglish() else setFontsArabic()
        checkBoxSaveCard.setOnCheckedChangeListener { buttonView, isChecked ->
            checkedSaveCardEnabled = isChecked
        }

    }

   private fun datePickerAction(){


       val calendar = Calendar.getInstance()

        year = calendar.get(Calendar.YEAR)
        month = calendar.get(Calendar.MONTH)
        day_of_month = calendar.get(Calendar.DAY_OF_MONTH)
       calendar.set(year!!, month!!, day_of_month!!)

       //Note hiding date works only with Theme_Holo_Dialog style
       val dpd = DatePickerDialog(
           context,
           android.R.style.Theme_Holo_Dialog,
           DatePickerDialog.OnDateSetListener { datePicker, selyear, monthOfYear, dayOfMonth ->

               day_of_month = dayOfMonth
               month = monthOfYear + 1
               year = selyear

               val yearVal = selyear.toString().substring(2)

               if(month!! <10){
                   monthVal = "0$month"
               }else monthVal = month.toString()

               if(::monthVal.isInitialized)
               expiryDateEditText.setText ("$monthVal/$year")

           }, year!!, month!!, day_of_month!!
       )

       dpd.show()
       //Work around to hide day from datepicker
       val dayPicker = dpd.datePicker.findViewById<View>(Resources.getSystem().getIdentifier("android:id/day", null, null))

       if (dayPicker != null) {
           dayPicker.visibility = View.GONE
       }



    }



    private fun initLocals() {
        val holderNameHint :String = LocalizationManager.getValue("cardNamePlaceholder","cardForm")
        val cardNumberHint :String = LocalizationManager.getValue("cardNumberPlaceholder","cardForm")
        val expiryHint :String = LocalizationManager.getValue("cardExpiryPlaceholder","cardForm")
        val cvvHint :String = LocalizationManager.getValue("cardCVVPlaceholder","cardForm")
        val checkBoxText :String = LocalizationManager.getValue("saveCard","cardForm")

        holderNameEditText.hint = holderNameHint
        cardNumberEditText.hint = cardNumberHint
        expiryDateEditText.hint = expiryHint
        cvcNumberEditText.hint = cvvHint
        checkBoxSaveCard.text = checkBoxText
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
    fun getCard(): CreateTokenCard? {
        val number: String? = cardNumber
        val expiryDate: String? = expiryDate
        val cvc: String? = cvvNumber



        //  println("expiryDate in getcard" + expiryDate)
        //  println("cardHolderName >> "+ cardHolderName)

        return if (number == null || expiryDate == null || cvc == null) {
            null
        } else {

            val monthString: String?
            val yearString: String?

            if(expiryDate.contains("/")){
                monthString = expiryDate.split("/")[0]
                yearString = expiryDate.split("/")[1]
            } else {
                val expiryDateArray: CharArray = expiryDate.toCharArray()
                monthString = expiryDateArray[0].toString()+ expiryDateArray[1].toString()
                yearString = expiryDateArray[2].toString()+ expiryDateArray[3].toString()
            }

            //     println("monthString"+monthString)
            //    println("yearString"+yearString)

            return CreateTokenCard(
                number.replace(" ", ""),
                monthString,
                yearString,
                cvc,
                cardHolderName, null
            )


        }

        // TODO: Add address handling here.
    }

   private fun setFontsEnglish() {
        cardNumberEditText.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.TajawalLight
            )
        )

        holderNameEditText.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.TajawalLight
            )
        )

        cvcNumberEditText.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.TajawalLight
            )
        )
       expiryDateEditText.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.TajawalLight
            )
        )
       checkBoxSaveCard.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.TajawalRegular
            )
        )
       cardDetailsText.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.TajawalRegular
            )
        )

    }
    private  fun setFontsArabic() {
        cardNumberEditText.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.TajawalLight
            )
        )

        cvcNumberEditText.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.TajawalLight
            )
        )

        expiryDateEditText.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.TajawalLight
            )
        )
        holderNameEditText.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.TajawalLight
            )
        )
        checkBoxSaveCard.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.TajawalRegular
            )
        )
        cardDetailsText.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.TajawalRegular
            )
        )

    }




    }

