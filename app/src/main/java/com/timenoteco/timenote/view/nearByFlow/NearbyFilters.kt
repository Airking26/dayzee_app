package com.timenoteco.timenote.view.nearByFlow

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.afollestad.materialdialogs.list.listItems
import com.timenoteco.timenote.R
import kotlinx.android.synthetic.main.fragment_nearby_filters.*

class NearbyFilters : Fragment(), View.OnClickListener {

    private lateinit var nearbyFilterCategoryTv: TextView
    private lateinit var nearbyFilterFromTv: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_nearby_filters, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        nearbyFilterCategoryTv = nearby_filter_category_tv
        nearbyFilterFromTv = nearby_filter_from_tv

        nearby_filter_from.setOnClickListener(this)
        nearby_filter_category.setOnClickListener(this)
        nearby_filter_done_btn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v){
            nearby_filter_category -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                title(R.string.category)
                listItems(items = listOf("Judaisme", "Bouddhisme", "Techno", "Pop", "Football", "Tennis",
                    "Judaisme", "Bouddhisme", "Techno", "Pop", "Football", "Tennis", "Judaisme", "Bouddhisme",
                    "Techno", "Pop", "Football", "Tennis")){_, index, text ->
                    nearbyFilterCategoryTv.text = text.toString()
                }
                lifecycleOwner(this@NearbyFilters)
            }
            nearby_filter_from -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                title(R.string.from)
                listItems(null, listOf("Public", "Private", "Public and Private")) { dialog, index, text ->
                    nearbyFilterFromTv.text = text.toString()
                }
                lifecycleOwner(this@NearbyFilters)
            }
            nearby_filter_done_btn -> findNavController().navigate(NearbyFiltersDirections.actionNearbyFiltersToNearBy())
        }
    }

}
