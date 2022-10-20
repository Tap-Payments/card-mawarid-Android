package company.tap.tapcardsdk.internal.logic.utils


import company.tap.tapcardsdk.internal.logic.api.enums.AmountModificatorType
import company.tap.tapcardsdk.internal.logic.api.models.*
import company.tap.tapcardsdk.internal.logic.datamanagers.PaymentDataSource
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode


/**
 * The type Amount calculator.
 */
object AmountCalculator {
    /**
     * Calculate taxes on big decimal.
     *
     * @param amount the amount
     * @param taxes  the taxes
     * @return the big decimal
     */
    fun calculateTaxesOn(amount: BigDecimal, taxes: java.util.ArrayList<Tax>?): BigDecimal? {
        var result = BigDecimal.ZERO
        if (taxes == null) {
            return result
        }
        for (tax in taxes) {
            when (tax?.amount?.getType()) {
                AmountModificatorType.PERCENTAGE -> {
                    result = result.add(amount.multiply(tax.amount.getNormalizedValue()))
                    result = result.add(tax.amount.getValue())
                }
                AmountModificatorType.FIXED -> result = result.add(tax.amount.getValue())
            }
        }
        return result
    }

    /**
     * Calculate total amount of big decimal.
     *
     * @param items     the items
     * @param taxes     the taxes
     * @param shippings the shippings
     * @return the big decimal
     */
    open fun calculateTotalAmountOf(
        items: List<PaymentItem>,
        taxes: java.util.ArrayList<Tax>?,
        shippings: java.util.ArrayList<Shipping>?
    ): BigDecimal? {
        var itemsPlainAmount = BigDecimal.ZERO
        var itemsDiscountAmount = BigDecimal.ZERO
        var itemsTaxesAmount = BigDecimal.ZERO
        for (item in items) {
            itemsPlainAmount = itemsPlainAmount.add(item.getPlainAmount())
            itemsDiscountAmount = itemsDiscountAmount.add(item.getDiscountAmount())
            itemsTaxesAmount = itemsTaxesAmount.add(item.getTaxesAmount())
        }
        val discountedAmount = itemsPlainAmount.subtract(itemsDiscountAmount)
        var shippingAmount = BigDecimal.ZERO
        if (shippings != null) {
            for (shipping in shippings) {
                shippingAmount = shippingAmount.add(shipping.amount)
            }
        }
        val taxesAmount = calculateTaxesOn(discountedAmount.add(shippingAmount), taxes)
        val totalTaxesAmount = itemsTaxesAmount.add(taxesAmount)
        return discountedAmount.add(shippingAmount).add(totalTaxesAmount)
    }



    private fun getAmountedCurrency(
            amountedCurrencies: ArrayList<SupportedCurrencies>?,
            currency: String
        ): SupportedCurrencies? {
        if (amountedCurrencies != null) {
            for (amountedCurrency in amountedCurrencies) {
                if (amountedCurrency?.currency.equals(currency)) {
                    return amountedCurrency
                }
            }
        }
        return null
    }

}