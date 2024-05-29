package com.monetizationlib.data.base.view.viewHolders

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

abstract class BaseViewHolder<in T>(open val binder: ViewDataBinding) :
    RecyclerView.ViewHolder(binder.root) {
    abstract fun bind(data: T, position:Int)
}
