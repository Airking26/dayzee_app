package com.timenoteco.timenote.common

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.timenoteco.timenote.R
import com.timenoteco.timenote.listeners.TimenoteCreationPicListeners
import kotlinx.android.synthetic.main.timenote_view_image.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ScreenSlideTimenoteImageFragment: Fragment() {
    private var param1: Int? = null
    private var param2: Bitmap? = null
    private lateinit var timenoteCreationPicListeners: TimenoteCreationPicListeners

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getInt(ARG_PARAM1)
            param2 = it.getParcelable(ARG_PARAM2)
        }
    }

    fun setListener(timenoteCreationPicListeners: TimenoteCreationPicListeners){
        this.timenoteCreationPicListeners = timenoteCreationPicListeners
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.timenote_view_image, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        create_timenote_pic.setImageBitmap(param2)
        timenote_change_pic.setOnClickListener { timenoteCreationPicListeners.onChangePicClicked(param1!!) }
        timenote_crop_pic.setOnClickListener { timenoteCreationPicListeners.onCropPicClicked(param2!!, param1!!) }
        timenote_add_pic.setOnClickListener { timenoteCreationPicListeners.onAddClicked() }
        timenote_delete_pic.setOnClickListener { timenoteCreationPicListeners.onDeleteClicked(param1!!) }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: Int, bitmap: Bitmap?, context: Fragment) =
            ScreenSlideTimenoteImageFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, param1)
                    putParcelable(ARG_PARAM2, bitmap)
                    setListener(context as TimenoteCreationPicListeners)
                }
            }
    }
}