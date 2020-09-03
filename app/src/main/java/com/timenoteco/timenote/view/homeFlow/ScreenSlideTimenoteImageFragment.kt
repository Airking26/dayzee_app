package com.timenoteco.timenote.view.homeFlow

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.timenoteco.timenote.R
import com.timenoteco.timenote.listeners.DoubleClickListener
import kotlinx.android.synthetic.main.timenote_view_image.*

private const val ARG_PARAM1 = "position"
private const val ARG_PARAM2 = "url"
private const val ARG_PARAM3 = "hideIcons"


class ScreenSlideTimenoteImageFragment : Fragment() {
    private lateinit var itemClickListener: (Int, Int) -> Unit
    private var position: Int? = null
    private var url: String? = null
    private var hideIcons: Boolean? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            position = it.getInt(ARG_PARAM1)
            url = it.getString(ARG_PARAM2)
            hideIcons = it.getBoolean(ARG_PARAM3)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =


        inflater.inflate(R.layout.timenote_view_image, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Glide.with(this).load(Uri.parse(url)).into(create_timenote_pic)
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
            itemClickListener: (Int, Int) -> Unit
        ) =
            ScreenSlideTimenoteImageFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_PARAM1, position)
                        putString(ARG_PARAM2, url)
                        putBoolean(ARG_PARAM3, hideIcons)
                        setImageClickListener(itemClickListener)
                    }
                }
    }
}