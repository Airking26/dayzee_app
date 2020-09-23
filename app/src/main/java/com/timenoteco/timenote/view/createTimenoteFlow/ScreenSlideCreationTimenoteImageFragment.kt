package com.timenoteco.timenote.view.createTimenoteFlow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.timenoteco.timenote.R
import com.timenoteco.timenote.listeners.TimenoteCreationPicListeners
import com.timenoteco.timenote.model.AWSFile
import kotlinx.android.synthetic.main.timenote_view_image.*

private const val ARG_PARAM1 = "position"
private const val ARG_PARAM2 = "aws"
private const val ARG_PARAM3 = "hideIcons"
private const val ARG_PARAM4 = "fromDuplicateOrEdit"
private const val ARG_PARAM5 = "picture"

class ScreenSlideCreationTimenoteImageFragment: Fragment() {
    private var param1: Int? = null
    private var param2: AWSFile? = null
    private var param3: Boolean? = null
    private var param4: Boolean? = null
    private var param5: String? =null

    private lateinit var timenoteCreationPicListeners: TimenoteCreationPicListeners

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getInt(ARG_PARAM1)
            param2 = it.getParcelable(ARG_PARAM2)
            param3 = it.getBoolean(ARG_PARAM3)
            param4 = it.getBoolean(ARG_PARAM4)
            param5 = it.getString(ARG_PARAM5)
        }
    }

    fun setListener(timenoteCreationPicListeners: TimenoteCreationPicListeners){
        this.timenoteCreationPicListeners = timenoteCreationPicListeners
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.timenote_view_image, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        if(param4!! && !param5.isNullOrBlank()){
            Glide.with(view)
                .load(param5)
                .into(create_timenote_pic)
        } else Glide.with(view)
            .load(param2?.uri)
            .into(create_timenote_pic)

        if(param3!! || param4!!){
            timenote_crop_pic.visibility = View.GONE
            timenote_add_pic.visibility = View.GONE
            timenote_delete_pic.visibility = View.GONE
        } else {
            timenote_crop_pic.visibility = View.VISIBLE
            timenote_add_pic.visibility = View.VISIBLE
            timenote_delete_pic.visibility = View.VISIBLE
        }
        timenote_crop_pic.setOnClickListener { timenoteCreationPicListeners.onCropPicClicked(param2) }
        timenote_add_pic.setOnClickListener { timenoteCreationPicListeners.onAddClicked() }
        timenote_delete_pic.setOnClickListener { timenoteCreationPicListeners.onDeleteClicked(param2) }
    }

    companion object {
        @JvmStatic
        fun newInstance(
            param1: Int,
            awsFile: AWSFile?,
            context: Fragment,
            hideIcons: Boolean,
            fromDuplicateOrEdit: Boolean,
            picture: String?
        ) =
            ScreenSlideCreationTimenoteImageFragment()
                .apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, param1)
                    putParcelable(ARG_PARAM2, awsFile)
                    putBoolean(ARG_PARAM3, hideIcons)
                    putBoolean(ARG_PARAM4, fromDuplicateOrEdit)
                    putString(ARG_PARAM5, picture)
                    if(context is CreateTimenote) setListener(context as TimenoteCreationPicListeners)
                }
            }
    }
}