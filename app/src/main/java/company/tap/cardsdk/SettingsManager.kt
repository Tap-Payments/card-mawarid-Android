package company.tap.cardsdk

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import company.tap.cardsdk.customer.CustomerViewModel


import company.tap.tapcardsdk.internal.logic.api.models.PhoneNumber
import company.tap.tapcardsdk.internal.logic.api.models.TapCustomer
import kotlin.collections.ArrayList

/**
 * Created by AhlaamK on 9/6/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
@SuppressLint("StaticFieldLeak")
object SettingsManager {
    private var pref: SharedPreferences? = null
    private var context: Context? = null
    fun setPref(ctx: Context?) {
        context = ctx
        if (pref == null) pref = PreferenceManager.getDefaultSharedPreferences(ctx)
    }

    fun saveCustomer(
        ref:String?,
        name: String,
        middle: String,
        last: String,
        email: String,
        sdn: String,
        mobile: String,
        ctx: Context
    ) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(ctx)
        val gson = Gson()
        val response = preferences.getString("customer", "")
        var customersList: ArrayList<CustomerViewModel?>? = gson.fromJson(
            response,
            object : TypeToken<List<CustomerViewModel?>?>() {}.getType()
        )
        if (customersList == null) customersList = ArrayList<CustomerViewModel?>()
        customersList.add(last.let {

            CustomerViewModel(ref, name, middle, it, email, sdn, mobile)

        })
        val data: String = gson.toJson(customersList)
        writeCustomersToPreferences(data, preferences)
    }




    fun editCustomer(
        oldCustomer: CustomerViewModel?,
        newCustomer: CustomerViewModel,
        ctx: Context?
    ) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(ctx)
        val gson = Gson()
        val response = preferences.getString("customer", "")
        val customersList: ArrayList<CustomerViewModel> = gson.fromJson(
            response,
            object : TypeToken<List<CustomerViewModel?>?>() {}.getType()
        )
        if (customersList != null) {
            val customerRef: String? = customersList[0].getRef()
            customersList.clear()
            newCustomer.getMiddleName()?.let {
                CustomerViewModel(
                    customerRef,
                    newCustomer.getName(),
                    it,
                    newCustomer.getLastName()!!,
                    newCustomer.getEmail(),
                    newCustomer.getSdn()!!,
                    newCustomer.getMobile()
                )
            }?.let {
                customersList.add(
                    it
                )
            }
            val data: String = gson.toJson(customersList)
            writeCustomersToPreferences(data, preferences)
        } else {
            if (ctx != null) {
                newCustomer?.getSdn()?.let {
                    saveCustomer(
                        newCustomer.getRef(),
                        newCustomer.getName(),
                        newCustomer?.getMiddleName()!!,
                        newCustomer?.getLastName()!!,
                        newCustomer.getEmail(),
                        it,
                        newCustomer.getMobile(), ctx
                    )
                }
            }
        }
    }


    private fun writeCustomersToPreferences(data: String, preferences: SharedPreferences) {
        val editor = preferences.edit()
        editor.putString("customer", data)
        editor.commit()
    }



    fun getRegisteredCustomers(ctx: Context?): List<CustomerViewModel> {
        val preferences = PreferenceManager.getDefaultSharedPreferences(ctx)
        val gson = Gson()
        val response = preferences.getString("customer", "")
        val customersList: ArrayList<CustomerViewModel>? = gson.fromJson(
            response,
            object : TypeToken<List<CustomerViewModel?>?>() {}.type
        )
        return customersList ?: ArrayList<CustomerViewModel>()
    }

    //////////////////////////////////////   Get Payment Settings ////////////////////////////////
    fun getCustomer(): TapCustomer? {
        val customer: TapCustomer
        val gson = Gson()
        val response = pref?.getString("customer", "")
        println(" get customer: $response")
        val customersList = gson.fromJson<ArrayList<CustomerViewModel>>(
            response,
            object : TypeToken<List<CustomerViewModel?>?>() {}.type
        )

        // check if customer id is in pref.
      //  customer =
        if (customersList != null) {
            println("preparing data source with customer ref :" + customersList[0].getRef())
            customer= TapCustomer(
                customersList[0].getRef(),
                customersList[0].getName(),
                customersList[0].getMiddleName(),
                customersList[0].getLastName(),
                customersList[0].getEmail(),
                PhoneNumber(customersList[0].getSdn(), customersList[0].getMobile()),
                "meta"
            )
        } else {
            println(" paymentResultDataManager.getCustomerRef(context) null")
            //65562630
            customer= TapCustomer(
                "cus_TS012520211349Za012907577", "ahlaam", "middlename",
                "lastname", "abcd@gmail.com",
                PhoneNumber("00965", "66175090"), "description",
            )
        }
        return customer
        //  65562630

    }


    //////////////////////////////////////////////////  General ////////////////////////////////

}
