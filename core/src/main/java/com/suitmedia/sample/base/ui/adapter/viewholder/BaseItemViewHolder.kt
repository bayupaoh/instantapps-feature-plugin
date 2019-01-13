package com.suitmedia.sample.base.ui.adapter.viewholder

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View

abstract class BaseItemViewHolder<in Data>(itemView: View) : RecyclerView.ViewHolder(itemView){
    var baseContext: Context? = null
    abstract fun bind(data: Data?)
}
