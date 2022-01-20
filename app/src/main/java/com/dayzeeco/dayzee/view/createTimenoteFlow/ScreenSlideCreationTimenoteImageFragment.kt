package com.dayzeeco.dayzee.view.createTimenoteFlow

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.dayzeeco.dayzee.R
import com.dayzeeco.dayzee.common.*
import com.dayzeeco.dayzee.listeners.TimenoteCreationPicListeners
import kotlinx.android.synthetic.main.timenote_view_image.*

class ScreenSlideCreationTimenoteImageFragment: Fragment() {
    private var position: Int? = null
    private var aws: String? = null
    private var hideIcons: Boolean? = null
    private var fromDuplicateOrEdit: Boolean? = null
    private var picture: String? =null

    private lateinit var timenoteCreationPicListeners: TimenoteCreationPicListeners

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            position = it.getInt(com.dayzeeco.dayzee.common.position)
            aws = it.getString(com.dayzeeco.dayzee.common.aws)
            hideIcons = it.getBoolean(hide_icons)
            fromDuplicateOrEdit = it.getBoolean(from_duplicate_or_edit)
            picture = it.getString(com.dayzeeco.dayzee.common.picture)
        }
    }

    fun setListener(timenoteCreationPicListeners: TimenoteCreationPicListeners){
        this.timenoteCreationPicListeners = timenoteCreationPicListeners
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.timenote_view_image, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        if(!picture.isNullOrBlank()){
            Glide.with(view)
                .load(picture)
                .fitCenter()
                .into(create_timenote_pic)
        } else Glide.with(view)
            .load(Uri.parse(aws))
            .fitCenter()
            .into(create_timenote_pic)

        if(hideIcons!! || fromDuplicateOrEdit!!){
            timenote_crop_pic.visibility = View.GONE
            timenote_add_pic.visibility = View.GONE
            timenote_delete_pic.visibility = View.GONE
        } else {
            timenote_crop_pic.visibility = View.GONE
            timenote_add_pic.visibility = View.VISIBLE
            timenote_delete_pic.visibility = View.VISIBLE
        }
        timenote_crop_pic.setOnClickListener { timenoteCreationPicListeners.onCropPicClicked(Uri.parse(
            aws
        )) }
        timenote_add_pic.setOnClickListener { timenoteCreationPicListeners.onAddClicked() }
        timenote_delete_pic.setOnClickListener { timenoteCreationPicListeners.onDeleteClicked(Uri.parse(
            aws
        )) }
    }

    companion object {
        @JvmStatic
        fun newInstance(
            param1: Int,
            uri: String?,
            context: Fragment,
            hideIcons: Boolean,
            fromDuplicateOrEdit: Boolean,
            picture: String?
        ) =
            ScreenSlideCreationTimenoteImageFragment()
                .apply {
                arguments = Bundle().apply {
                    putInt(com.dayzeeco.dayzee.common.position, param1)
                    putString(com.dayzeeco.dayzee.common.aws, uri)
                    putBoolean(hide_icons, hideIcons)
                    putBoolean(from_duplicate_or_edit, fromDuplicateOrEdit)
                    putString(com.dayzeeco.dayzee.common.picture, picture)
                    if(context is CreateTimenote) setListener(context as TimenoteCreationPicListeners)
                }
            }
    }
}