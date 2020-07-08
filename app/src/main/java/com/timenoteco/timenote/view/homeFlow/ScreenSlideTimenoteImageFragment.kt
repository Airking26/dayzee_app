package com.timenoteco.timenote.view.homeFlow

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.timenoteco.timenote.R
import com.timenoteco.timenote.listeners.TimenoteCreationPicListeners
import com.timenoteco.timenote.view.createTimenoteFlow.CreateTimenote
import kotlinx.android.synthetic.main.timenote_view_image.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val ARG_PARAM3 = "param3"

class ScreenSlideTimenoteImageFragment: Fragment() {
    private var param1: Int? = null
    private var param2: String? = null
    private var param3: Boolean? = null
    private lateinit var timenoteCreationPicListeners: TimenoteCreationPicListeners

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getInt(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            param3 = it.getBoolean(ARG_PARAM3)
        }
    }

    fun setListener(timenoteCreationPicListeners: TimenoteCreationPicListeners){
        this.timenoteCreationPicListeners = timenoteCreationPicListeners
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.timenote_view_image, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Glide.with(this).load(Uri.parse(param2)).into(create_timenote_pic)
        if(param3!!){
            timenote_change_pic.visibility = View.GONE
            timenote_crop_pic.visibility = View.GONE
            timenote_add_pic.visibility = View.GONE
            timenote_delete_pic.visibility = View.GONE
        } else {
            timenote_change_pic.visibility = View.VISIBLE
            timenote_crop_pic.visibility = View.VISIBLE
            timenote_add_pic.visibility = View.VISIBLE
            timenote_delete_pic.visibility = View.VISIBLE
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(
            param1: Int,
            url: String?,
            context: Fragment,
            hideIcons: Boolean
        ) =
            ScreenSlideTimenoteImageFragment()
                .apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, url)
                    putBoolean(ARG_PARAM3, hideIcons)
                    if(context is CreateTimenote) setListener(context as TimenoteCreationPicListeners)
                }
            }
    }
}