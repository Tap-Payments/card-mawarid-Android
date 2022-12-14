package company.tap.tapcardsdk.internal.ui.`interface`

interface CardValidCallback {
    /**
     * @param isValid if the current input is valid
     * @param invalidFields if the current input is invalid, this [Set] will be populated with the
     * fields that are invalid, represented by [Fields]; if the current input is valid,
     * this [Set] will be empty
     */
    fun onInputChanged(isValid: Boolean, invalidFields: Set<Fields>)

    enum class Fields {
        Number,
        Expiry,
        Cvc
    }
}
