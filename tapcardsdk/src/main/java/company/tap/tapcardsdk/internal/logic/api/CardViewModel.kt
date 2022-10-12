package company.tap.tapcardsdk.internal.logic.api

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.RestrictTo
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import company.tap.tapcardsdk.internal.logic.api.models.CreateTokenCard
import company.tap.tapcardsdk.internal.logic.api.models.TapConfigRequestModel
import company.tap.tapcardsdk.open.CardInputForm

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by AhlaamK on 3/23/22.

Copyright (c) 2022    Tap Payments.
All rights reserved.
 **/
@RestrictTo(RestrictTo.Scope.LIBRARY)
class CardViewModel : ViewModel() {

    private val repository = CardRepository()
    private val compositeDisposable = CompositeDisposable()

    @SuppressLint("StaticFieldLeak")
    private lateinit var context: Context
    private val liveData = MutableLiveData<Resource<CardViewState>>()


    init {
        compositeDisposable.add(repository.resultObservable
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { liveData.value = Resource.Loading() }
            .doOnTerminate { liveData.value = Resource.Finished() }
            .subscribe(
                { data -> liveData.value = Resource.Success(data) },
                { error -> liveData.value = error.message?.let { Resource.Error(it) } }
            ))

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun getInitData(
        cardViewModel: CardViewModel?,
        _context: Context?
    ) {
        if (_context != null) {
            this.context = _context
            repository.getInitData(_context, cardViewModel)
        }


    }


    @RequiresApi(Build.VERSION_CODES.N)
    private fun getConfigData(
        cardViewModel: CardViewModel?,
        _context: Context?,
        tapConfigRequestModel: TapConfigRequestModel?,
        tapCardInputView: CardInputForm?
    ) {
        if (_context != null) {
            this.context = _context
            repository.getConfigData(_context, cardViewModel,tapConfigRequestModel,tapCardInputView)
        }


    }


    @RequiresApi(Build.VERSION_CODES.N)
    fun processEvent(
        event: CardViewEvent,
        cardDataRequest: CreateTokenCard?,
        context: Context? = null,
        tapCardInputView: CardInputForm?,
        configRequestModel: TapConfigRequestModel?, binValue: String? = null, activity: AppCompatActivity?, savedResponseId:String?=null
    ) {
        when (event) {
            CardViewEvent.ConfigEvent -> getConfigData( this ,context,configRequestModel,tapCardInputView)
            CardViewEvent.InitEvent -> getInitData( this ,context)
            CardViewEvent.CreateTokenEvent -> createTokenWithEncryptedCard(cardDataRequest,
                    tapCardInputView,activity )
            CardViewEvent.RetreiveBinLookupEvent -> retrieveBinlookup(this,binValue)
            CardViewEvent.RetreiveSaveCardEvent -> retrieveSaveCard(this,savedResponseId,context)
        }
    }
    @RequiresApi(Build.VERSION_CODES.N)
    private fun retrieveSaveCard(viewModel: CardViewModel, savedResponseId: String?, context: Context?) {
        repository.retrieveSaveCard(viewModel,savedResponseId,context)

    }

        @RequiresApi(Build.VERSION_CODES.N)
        private fun createTokenWithEncryptedCard(createTokenWithEncryptedDataRequest: CreateTokenCard?, cardInputForm:CardInputForm?, activity: AppCompatActivity?) {
            //  println("createTokenWithEncryptedDataRequest>>."+createTokenWithEncryptedDataRequest)
          //  if (createTokenWithEncryptedDataRequest != null) {
                repository.createTokenWithEncryptedCard(createTokenWithEncryptedDataRequest, cardInputForm,activity)
         //   }

        }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun retrieveBinlookup(viewModel: CardViewModel, binValue: String?) {
        repository.retrieveBinLookup(viewModel,binValue)

    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }


}