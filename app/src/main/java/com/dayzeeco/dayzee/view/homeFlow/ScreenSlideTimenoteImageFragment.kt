package com.dayzeeco.dayzee.view.homeFlow

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.target.Target.SIZE_ORIGINAL
import com.dayzeeco.dayzee.R
import com.dayzeeco.dayzee.common.hide_icons
import com.dayzeeco.dayzee.common.is_null_or_empty
import com.dayzeeco.dayzee.listeners.DoubleClickListener
import kotlinx.android.synthetic.main.timenote_view_image.*

class ScreenSlideTimenoteImageFragment : Fragment() {
    private lateinit var itemClickListener: (Int, Int) -> Unit
    private var position: Int? = null
    private var url: String? = null
    private var hideIcons: Boolean? = null
    private var isNullOrEmpty: Boolean? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            position = it.getInt(com.dayzeeco.dayzee.common.position)
            url = it.getString(com.dayzeeco.dayzee.common.url)
            hideIcons = it.getBoolean(hide_icons)
            isNullOrEmpty = it.getBoolean(is_null_or_empty)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =


        inflater.inflate(R.layout.timenote_view_image, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        if(isNullOrEmpty!!){
            if(!url.isNullOrEmpty() && !url.isNullOrBlank()) create_timenote_pic.setBackgroundColor((Color.parseColor(if(url?.contains("#")!!) url else "#${url}")))
        } else {

            val glideUrl = GlideUrl(url, LazyHeaders.Builder()
                .addHeader("User-Agent",
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_2) AppleWebKit / 537.36(KHTML, like Gecko) Chrome  47.0.2526.106 Safari / 537.36")
                .build())

            create_timenote_pic.layout(0, 0, 0, 0)
            Glide.with(this)
                .load(url)
                .fitCenter()
                .thumbnail(0.1f)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(create_timenote_pic)}

        create_timenote_pic.setOnClickListener(object: DoubleClickListener(){
            override fun onSimpleClick() {
                itemClickListener(position!!, 0)
            }

            override fun onDoubleClick() {
                itemClickListener(position!!, 1)
            }

        })
        if (hideIcons!!) {
            timenote_crop_pic.visibility = View.GONE
            timenote_add_pic.visibility = View.GONE
            timenote_delete_pic.visibility = View.GONE
        } else {
            timenote_crop_pic.visibility = View.VISIBLE
            timenote_add_pic.visibility = View.VISIBLE
            timenote_delete_pic.visibility = View.VISIBLE
        }
    }

    fun setImageClickListener(itemClickListener: (Int, Int) -> Unit){
        this.itemClickListener = itemClickListener
    }

    companion object {
        @JvmStatic
        fun newInstance(
            position: Int,
            url: String?,
            hideIcons: Boolean,
            isNullOrEmpty: Boolean,
            itemClickListener: (Int, Int) -> Unit
        ) =
            ScreenSlideTimenoteImageFragment().apply {
                    arguments = Bundle().apply {
                        putInt(com.dayzeeco.dayzee.common.position, position)
                        putString(com.dayzeeco.dayzee.common.url, url)
                        putBoolean(hide_icons, hideIcons)
                        putBoolean(is_null_or_empty, isNullOrEmpty)
                        setImageClickListener(itemClickListener)
                    }
                }
    }
}