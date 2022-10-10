package company.tap.tapcardsdk.views

import android.animation.Animator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import androidx.core.graphics.drawable.DrawableCompat
import com.bumptech.glide.Glide
import company.tap.tapcardsdk.brandtypes.CardBrand
import company.tap.tapcardsdk.brandtypes.CardBrandSingle
import company.tap.tapcardsdk.databinding.CardBrandViewBinding

internal class CardBrandView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private val viewBinding: CardBrandViewBinding = CardBrandViewBinding.inflate(
        LayoutInflater.from(context),
        this
    )
    private val iconView = viewBinding.icon

    private var animationApplied = false

    @ColorInt
    internal var tintColorInt: Int = 0

    internal var onScanClicked: () -> Unit = {}

    init {
        isFocusable = false
        setScanClickListener()
    }

    internal fun showBrandIcon(brand: CardBrand, shouldShowErrorIcon: Boolean) {
        iconView.setOnClickListener(null)
        if (shouldShowErrorIcon) {
            iconView.setImageResource(brand.errorIcon)
        } else {
            if (animationApplied) {
                animationApplied = false
                animateImageChange(brand.icon)
            } else
                iconView.setImageResource(brand.icon)
            if (brand == CardBrand.Unknown) {
                applyTint(false)
                setScanClickListener()
            }
        }
    }

    internal fun showBrandIconSingle(brand: CardBrandSingle, shouldShowErrorIcon: Boolean) {
        iconView.setOnClickListener(null)
        if (shouldShowErrorIcon) {
            iconView.setImageResource(brand.errorIcon)
        } else {
            if (animationApplied) {
                animationApplied = false
                animateImageChange(brand.icon)
            } else
                iconView.setImageResource(brand.icon)
            if (brand.name == CardBrand.Unknown.name) {
                applyTint(false)
                setScanClickListener()
            }
        }
    }

    internal fun showBrandIconSingle(brand: CardBrandSingle, iconUrl : String, shouldShowErrorIcon: Boolean) {
        iconView.setOnClickListener(null)
        if (shouldShowErrorIcon) {
            iconView.setImageResource(brand.errorIcon)
        } else {
            if (animationApplied) {
                animationApplied = false
                animateImageChange(brand.icon)
            } else
                Glide.with(context).load(iconUrl).into(iconView)
              //  iconView.setImageURI(brand.icon)
            if (brand.name == CardBrand.Unknown.name) {
                applyTint(false)
                setScanClickListener()
            }
        }
    }


    private fun setScanClickListener() {
        iconView.setOnClickListener { onScanClicked() }
    }

    internal fun showCvcIcon(brand: CardBrand) {
        if (animationApplied) return

        if (brand == CardBrand.AmericanExpress) {
            iconView.setImageResource(brand.cvcIcon)
            applyTint(true)
            return
        }

        animationApplied = true
        animateImageChange(brand.cvcIcon)
        iconView.setOnClickListener(null)
    }

    private fun animateImageChange(cvcIcon: Int) {
        iconView.rotationY = 0f
        iconView.animate().setDuration(300).rotationY(90f)
            .setListener(object : Animator.AnimatorListener {

                override fun onAnimationEnd(animation: Animator?) {
                    iconView.setImageResource(cvcIcon)
                    iconView.rotationY = 270f
                    iconView.animate().rotationY(360f).setListener(null)
                    if (animationApplied)
                        applyTint(true)
                }

                override fun onAnimationRepeat(animation: Animator?) {}
                override fun onAnimationCancel(animation: Animator?) {}
                override fun onAnimationStart(animation: Animator?) {}

            })
    }

    internal fun applyTint(apply: Boolean) {
        if (!apply)
            return
        val icon = iconView.drawable
        val compatIcon = DrawableCompat.wrap(icon)
       // DrawableCompat.setTint(compatIcon.mutate(), tintColorInt)
        iconView.setImageDrawable(DrawableCompat.unwrap(compatIcon))
    }
}
