package com.timenoteco.timenote.common

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.timenoteco.timenote.R
import kotlinx.android.synthetic.main.timenote_view_image.*
import java.io.ByteArrayOutputStream

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ScreenSlideTimenoteImageFragment: Fragment() {
    private var param1: Int? = null
    private var param2: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getInt(ARG_PARAM1)
            param2 = it.getParcelable(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.timenote_view_image, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        create_timenote_pic.setImageBitmap(param2)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: Int, bitmap: Bitmap?) =
            ScreenSlideTimenoteImageFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, param1)
                    putParcelable(ARG_PARAM2, bitmap)
                }
            }
    }
}