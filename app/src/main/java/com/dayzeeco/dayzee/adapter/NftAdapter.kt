package com.dayzeeco.dayzee.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.dayzeeco.dayzee.R
import kotlinx.android.synthetic.main.item_nft.view.*
import kotlinx.android.synthetic.main.item_timenote.view.*

class NftAdapter(val context : Context, val images: List<String>): BaseAdapter() {
    override fun getCount(): Int = images.size
    override fun getItem(p0: Int): Any = Any()
    override fun getItemId(p0: Int): Long = 0

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val view = LayoutInflater.from(context).inflate(R.layout.item_nft, p2, false)
        Glide
            .with(view)
            .load(images[p0])
            .thumbnail(0.1f)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(view.nft_item)
        return view
    }
}