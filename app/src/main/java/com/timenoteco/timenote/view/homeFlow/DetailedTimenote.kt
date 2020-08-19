package com.timenoteco.timenote.view.homeFlow

import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.timenoteco.timenote.R
import com.timenoteco.timenote.adapter.ItemTimenoteToComeAdapter
import com.timenoteco.timenote.adapter.ScreenSlideTimenotePagerAdapter
import com.timenoteco.timenote.common.RoundedCornersTransformation
import com.timenoteco.timenote.model.StatusTimenote
import com.timenoteco.timenote.viewModel.TimenoteViewModel
import kotlinx.android.synthetic.main.fragment_detailed_fragment.*
import kotlinx.android.synthetic.main.item_timenote.view.*

class DetailedTimenote : Fragment(), View.OnClickListener {

    private val timenoteViewModel: TimenoteViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
         inflater.inflate(R.layout.fragment_detailed_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        timenoteViewModel.getSpecificTimenote("").observe(viewLifecycleOwner, Observer {})

        val screenSlideCreationTimenotePagerAdapter =  ScreenSlideTimenotePagerAdapter(this, mutableListOf("https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
            "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
            "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg"), true){ i: Int, i1: Int -> }

        detailed_timenote_vp.adapter = screenSlideCreationTimenotePagerAdapter
        detailed_timenote_indicator.setViewPager(detailed_timenote_vp)
        screenSlideCreationTimenotePagerAdapter.registerAdapterDataObserver(detailed_timenote_indicator.adapterDataObserver)

        val test = "Saved by Ronny Dahan and thousands of other people"

        val p = Typeface.create("sans-serif-light", Typeface.NORMAL)
        val m = Typeface.create("sans-serif", Typeface.NORMAL)
        val o = ItemTimenoteToComeAdapter.CustomTypefaceSpan(p)
        val k = ItemTimenoteToComeAdapter.CustomTypefaceSpan(m)

        val t = SpannableStringBuilder(test)
        t.setSpan(o, 0, 8, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
        t.setSpan(k, 9, test.length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)

        detailed_timenote_added_by.text = t

        val desc = "#Beach#Sunset#Love A very good place to be also known for his cold drinks a good music open all day and night come join us we are waiting for you"
        val h = SpannableStringBuilder(desc)
        h.setSpan(k, 0, 17, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
        h.setSpan(o, 18, desc.length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
        detailed_timenote_username_desc.text = h

        Glide
            .with(this)
            .load("https://media.istockphoto.com/photos/beautiful-woman-posing-against-dark-background-picture-id638756792")
            .apply(RequestOptions.bitmapTransform(RoundedCornersTransformation(requireContext(), 90, 0, getString(0 + R.color.colorBackground), 4)))
            .into(detailed_timenote_pic_participant_one)
        Glide
            .with(this)
            .load("https://wl-sympa.cf.tsp.li/resize/728x/jpg/f6e/ef6/b5b68253409b796f61f6ecd1f1.jpg")
            .apply(RequestOptions.bitmapTransform(RoundedCornersTransformation(requireContext(), 90, 0, getString(0 + R.color.colorBackground), 4)))
            .into(detailed_timenote_pic_participant_two)
        Glide
            .with(this)
            .load("https://www.fc-photos.com/wp-content/uploads/2016/09/fc-photos-Weynacht-0001.jpg")
            .apply(RequestOptions.bitmapTransform(RoundedCornersTransformation(requireContext(), 90, 0, getString(0 + R.color.colorBackground), 4)))
            .into(detailed_timenote_pic_participant_three)

        Glide
            .with(this)
            .load("https://media.istockphoto.com/photos/beautiful-woman-posing-against-dark-background-picture-id638756792")
            .apply(RequestOptions.circleCropTransform())
            .into(detailed_timenote_pic_user)

        detailed_timenote_comment.setOnClickListener(this)
        detailed_timenote_btn_back.setOnClickListener { findNavController().popBackStack() }
    }

    override fun onClick(v: View?) {
        when(v){
            detailed_timenote_comment -> findNavController().navigate(DetailedTimenoteDirections.actionDetailedTimenoteToComments())
        }
    }

}