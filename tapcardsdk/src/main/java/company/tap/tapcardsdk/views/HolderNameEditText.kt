package company.tap.tapcardsdk.views

import android.content.Context
import android.os.Build
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.method.TextKeyListener
import android.util.AttributeSet
import android.view.View
import androidx.annotation.StringRes
import com.google.android.material.textfield.TextInputLayout
import company.tap.tapcardsdk.R
import company.tap.tapuilibrary.uikit.atoms.TapTextInput
import company.tap.tapuilibrary.uikit.utils.TapTextWatcher

class HolderNameEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = androidx.appcompat.R.attr.editTextStyle
) : TapTextInput(context, attrs, defStyleAttr) {


    internal val holderName: String?
        get() {
            return fieldText
        }

    init {
        setErrorMessage(resources.getString(R.string.invalid_holder_name))
        maxLines = 1

        addTextChangedListener(object : TapTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                shouldShowError = false
            }
        })

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setAutofillHints(View.AUTOFILL_HINT_POSTAL_CODE)
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        configureField()
    }

    /**
     * Configure the field
     */
    private fun configureField() {
        hint = resources.getString(R.string.holder_name_hint)
        filters = arrayOf(InputFilter.LengthFilter(MAX_LENGTH), InputFilter.AllCaps())
        keyListener = TextKeyListener.getInstance()
        inputType = InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS
    }

    /**
     * If a `TextInputLayout` is an ancestor of this view, set the hint on it. Otherwise, set
     * the hint on this view.
     */
    private fun updateHint(@StringRes hintRes: Int) {
        getTextInputLayout()?.let {
            if (it.isHintEnabled) {
                it.hint = resources.getString(hintRes)
            } else {
                setHint(hintRes)
            }
        }
    }

    /**
     * Copied from `TextInputEditText`
     */
    private fun getTextInputLayout(): TextInputLayout? {
        var parent = parent
        while (parent is View) {
            if (parent is TextInputLayout) {
                return parent
            }
            parent = parent.getParent()
        }
        return null
    }


    private companion object {
        private const val MAX_LENGTH = 22
    }
}
