package company.tap.tapcardsdk.internal.logic.viewmodels

import android.content.Context
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Build
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import company.tap.cardscanner.*
import company.tap.nfcreader.open.reader.TapEmvCard
import company.tap.nfcreader.open.reader.TapNfcCardReader

import company.tap.tapcardsdk.open.DataConfiguration
import company.tap.tapcardformkit.open.enums.CardType
import company.tap.tapcardsdk.internal.logic.api.CardViewEvent
import company.tap.tapcardsdk.internal.logic.api.CardViewModel
import company.tap.tapcardsdk.internal.logic.api.models.BINLookupResponse
import company.tap.tapcardsdk.internal.logic.datamanagers.PaymentDataSource
import company.tap.tapcardsdk.internal.logic.interfaces.NFCCallback
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibrary.uikit.fragment.NFCFragment
import company.tap.tapuilibrary.uikit.organisms.TapPaymentInput
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposables


/**
 * Created by AhlaamK on 3/31/22.

Copyright (c) 2022    Tap Payments.
All rights reserved.
 **/
class CustomViewModel : MvvmCustomViewModel<CustomViewState>, NfcAdapter.ReaderCallback , TapScannerCallback,
    TapTextRecognitionCallBack {
    private val liveData = MutableLiveData<TapCard>()
    private val liveNFCData = MutableLiveData<TapEmvCard>()
    private var isInlineViewOpened :Boolean= false
    var isNFCOpened :Boolean= false
    private lateinit var context :Context
    private lateinit var scannerFrameLayout :FrameLayout
    private lateinit var inputTapPayment: TapPaymentInput
    private var textRecognitionML: TapTextRecognitionML? = null
//private val inlineViewFragment:InlineViewFragment= InlineViewFragment()
private val inlineViewFragment:CameraFragment= CameraFragment()
private val nfcFragment:NFCFragment= NFCFragment()
    private var tapNfcCardReader: TapNfcCardReader? = null
    var  intent:Intent?=null
    private var NFCCallback : NFCCallback.Companion =
      company.tap.tapcardsdk.internal.logic.interfaces.NFCCallback
    private var cardReadDisposable = Disposables.empty()
    override var state: CustomViewState? = null

    get() = CustomViewState(liveData.value,liveNFCData.value)
    set(value) {
        field = value
        restore(value)
    }

    fun getLiveData(): LiveData<TapCard> = liveData

    fun getLiveNFCData() :LiveData<TapEmvCard> =liveNFCData

    fun onNfcClick(
        context: Context,
        inlinefRamelayout: FrameLayout,
        intent1: Intent,
        tapPaymentInput: TapPaymentInput
    ) {

        isNFCOpened =true
        this.context = context
        scannerFrameLayout =inlinefRamelayout
        inputTapPayment = tapPaymentInput
       /* if (context is AppCompatActivity) {

            (context).supportFragmentManager.beginTransaction().replace(
                    R.id.inline_container1,
                    nfcFragment
            ).commit()
            inlinefRamelayout.visibility = View.VISIBLE
            inputTapPayment.visibility = View.GONE

        }*/

    }
    fun callNFC(
        context: Context,
        inlinefRamelayout: FrameLayout,
        intent1: Intent
    ){
        tapNfcCardReader = TapNfcCardReader(context as AppCompatActivity)
        //  tapNfcCardReader?.enableDispatch()
        tapNfcCardReader?.isSuitableIntent((context as AppCompatActivity).intent)
        println("intent1>>>>>." + intent1.action)
        println("context>>>>>." + (context as AppCompatActivity).intent)
        if (tapNfcCardReader?.isSuitableIntent((context).intent) == true) {
            cardReadDisposable = tapNfcCardReader!!
                .readCardRx2(intent1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    ::showCardInfo
                ) { throwable: Throwable -> displayError(throwable?.message) }
        }

    }

    fun onCardScannerClick(context: Context, inlinefRamelayout: FrameLayout,tapPaymentInput:TapPaymentInput) {
       // inlineViewFragment.setCallBackListener(this)


        // TODO: 4/21/20 reuse same instance if exist instead of instantiating one each time configuration changes
        textRecognitionML = TapTextRecognitionML(this)
        textRecognitionML?.addTapScannerCallback(this)
        DataConfiguration.getDefaultBorderColor()?.let { textRecognitionML?.setFrameColor(it) }

        isInlineViewOpened =true
        this.context = context
        scannerFrameLayout =inlinefRamelayout
        inputTapPayment = tapPaymentInput
      /*  if (context is AppCompatActivity) {
                (context).supportFragmentManager.beginTransaction().replace(
                        R.id.inline_container1,
                        inlineViewFragment
                ).commit()
            inlinefRamelayout.visibility = View.VISIBLE
            inputTapPayment.visibility = View.GONE

        }*/

    }

    private fun restore(state: CustomViewState?) {
        liveData.value = state?.card
    }

 /*   override fun onScanCardFailed(e: Exception?) {
        println("exception e" + e)
        if(isInlineViewOpened){
            (context as AppCompatActivity).supportFragmentManager.beginTransaction().remove(
                    inlineViewFragment
            ).commit()
            scannerFrameLayout.visibility = View.GONE
            inputTapPayment.visibility = View.VISIBLE
        }
    }*/

/*    @RequiresApi(Build.VERSION_CODES.N)
    override fun onScanCardFinished(
            card: Card?,
            cardImage: ByteArray?
    ) {
        liveData.value=card
        if(isInlineViewOpened){
            (context as AppCompatActivity).supportFragmentManager.beginTransaction().remove(
                    inlineViewFragment
            ).commit()
            scannerFrameLayout.visibility = View.GONE
            inputTapPayment.visibility = View.VISIBLE
        }
       // println("liveData"+card)
        callBinLookupApi(card?.cardNumber?.toString()?.substring(0, 6))

        Handler().postDelayed({
            val binLookupResponse: BINLookupResponse? = PaymentDataSource.getBinLookupResponse()
            if (PaymentDataSource.getCardType() != null && PaymentDataSource.getCardType() == CardType.ALL.name) {
               // setScannedCardDetails(card)
                liveData.value=card

            } else {
                if (binLookupResponse != null) {
                   // checkAllowedCardTypes(binLookupResponse)
                  // setScannedCardDetails(card)
                    DataConfiguration.tapCardInputDelegate?.cardNotSupported( LocalizationManager.getValue("alertUnsupportedCardMessage", "AlertBox"))

                }

            }
        }, 300)
    }*/

    @RequiresApi(Build.VERSION_CODES.N)
    private fun callBinLookupApi(card: String?) {
        CardViewModel().processEvent(
            CardViewEvent.RetreiveBinLookupEvent,
            null, null,null,null, card?.trim().toString().replace(" ", ""),
       null )
    }

    override fun onTagDiscovered(p0: Tag?) {
        println("p0 val " + p0)
        if(isInlineViewOpened){
            (context as AppCompatActivity).supportFragmentManager.beginTransaction().remove(
                    inlineViewFragment
            ).commit()
            scannerFrameLayout.visibility = View.GONE
            inputTapPayment.visibility = View.VISIBLE
        }
    }

    private fun showCardInfo(emvCard: TapEmvCard) {
        Log.e("showCardInfo:", emvCard.cardNumber)
        cardReadDisposable.dispose()
        tapNfcCardReader?.disableDispatch()

        liveNFCData.value =emvCard
        if(isNFCOpened){
            (context as AppCompatActivity).supportFragmentManager.beginTransaction().remove(
                    nfcFragment
            ).commit()
            scannerFrameLayout.visibility = View.GONE
            inputTapPayment.visibility = View.VISIBLE
        }
    }

    private fun displayError(message: String?) {
        println("message>>"+message)
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        if(isNFCOpened){
            (context as AppCompatActivity).supportFragmentManager.beginTransaction().remove(
                    nfcFragment
            ).commit()
            scannerFrameLayout.visibility = View.GONE
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onReadSuccess(card: TapCard?) {
        liveData.value=card
        if(isInlineViewOpened){
            (context as AppCompatActivity).supportFragmentManager.beginTransaction().remove(
                inlineViewFragment
            ).commit()
            scannerFrameLayout.visibility = View.GONE
            inputTapPayment.visibility = View.VISIBLE
        }
        // println("liveData"+card)
        if(card!=null && card.cardNumber!=null && card.cardNumber.length>=6){
            callBinLookupApi(card.cardNumber?.trim().toString().substring(0, 6))

        }

        Handler().postDelayed({
            val binLookupResponse: BINLookupResponse? = PaymentDataSource.getBinLookupResponse()
            if (PaymentDataSource.getCardType() != null && PaymentDataSource.getCardType() == CardType.ALL.name) {
                // setScannedCardDetails(card)
                liveData.value=card

            } else {
                if (binLookupResponse != null) {
                    // checkAllowedCardTypes(binLookupResponse)
                    // setScannedCardDetails(card)
                    DataConfiguration.tapCardInputDelegate?.cardNotSupported( LocalizationManager.getValue("alertUnsupportedCardMessage", "AlertBox"))

                }

            }
        }, 300)
    }

    override fun onReadFailure(error: String?) {
        println("exception e" + error)
        if(isInlineViewOpened){
            (context as AppCompatActivity).supportFragmentManager.beginTransaction().remove(
                inlineViewFragment
            ).commit()
            scannerFrameLayout.visibility = View.GONE
            inputTapPayment.visibility = View.VISIBLE
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onRecognitionSuccess(card: TapCard?) {
        onReadSuccess(card)
    }

    override fun onRecognitionFailure(error: String?) {
        onReadFailure(error)
    }
}