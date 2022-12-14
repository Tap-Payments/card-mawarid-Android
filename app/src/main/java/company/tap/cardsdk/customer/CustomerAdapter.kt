/*
 * Created By AhlaamK 8/5/22, 7:09 PM
 * Copyright (c) 2022    Tap Payments.
 * All rights reserved.
 */

package company.tap.cardsdk.customer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import company.tap.cardsdk.R


/**
 * Created by AhlaamK on 9/6/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
class CustomerAdapter(customers: List<CustomerViewModel>, listener: OnClickListenerInterface) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var customers: List<CustomerViewModel>
    var listener: OnClickListenerInterface
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return CustomerViewHolder(view, listener)
    }


    private fun getCustomer(index: Int): CustomerViewModel {
        return customers[index]
    }

    override fun getItemCount(): Int {
        return customers.size
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.customer_layout
    }

    interface OnClickListenerInterface {
        fun onClick(customerViewModel: CustomerViewModel?)
    }

    init {
        this.customers = customers
        this.listener = listener
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as CustomerViewHolder).bindData(getCustomer(position))
    }
}