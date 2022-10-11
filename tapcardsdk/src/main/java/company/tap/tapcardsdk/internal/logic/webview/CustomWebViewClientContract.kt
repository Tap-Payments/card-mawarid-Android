package company.tap.tapcardsdk.internal.logic.webview

/**
 * Created by OlaMonir on 7/27/20.

Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/

interface CustomWebViewClientContract {
    fun submitResponseStatus( success : Boolean)
    fun getRedirectedURL(url : String)
    fun showLoading(showLoading: Boolean , authId:String?=null)

}