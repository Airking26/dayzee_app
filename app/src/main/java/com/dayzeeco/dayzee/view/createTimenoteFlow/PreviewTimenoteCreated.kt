/*package com.dayzeeco.dayzee.view.createTimenoteFlow

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.gson.Gson
import com.dayzeeco.dayzee.R
import com.dayzeeco.dayzee.adapter.ScreenSlideCreationTimenotePagerAdapter
import com.dayzeeco.dayzee.common.Utils
import com.dayzeeco.dayzee.listeners.ExitCreationTimenote
import com.dayzeeco.dayzee.model.AWSFile
import com.dayzeeco.dayzee.viewModel.CreationTimenoteViewModel
import io.branch.indexing.BranchUniversalObject
import io.branch.referral.util.BranchEvent
import io.branch.referral.util.ContentMetadata
import io.branch.referral.util.LinkProperties
import kotlinx.android.synthetic.main.fragment_preview_timenote_created.*
import kotlinx.android.synthetic.main.item_timenote_root.view.*
import java.io.File


class PreviewTimenoteCreated : Fragment(), View.OnClickListener {

    private lateinit var listener : ExitCreationTimenote
    private val creationTimenoteViewModel: CreationTimenoteViewModel by activityViewModels()
    private lateinit var screenSlideCreationTimenotePagerAdapter: ScreenSlideCreationTimenotePagerAdapter
    private var mutableList : MutableList<String> = mutableListOf()
    private val args : PreviewTimenoteCreatedArgs by navArgs()
    private val utils: Utils = Utils()

    override fun onResume() {
        super.onResume()
        utils.progressDialog(requireContext()).hide()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
         listener = context as ExitCreationTimenote
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_preview_timenote_created, container, false)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        creationTimenoteViewModel.getCreateTimeNoteLiveData().observe(viewLifecycleOwner, Observer {
            if (it.price.price >= 0 && !it.url.isNullOrBlank()) {
                preview_timenote_buy_cl.visibility = View.VISIBLE
                if (it.price .price > 0) preview_created_timenote_buy.text = it.price.price.toString().plus(it.price.currency)
            }
            if (it.pictures?.size == 1) preview_created_timenote_indicator.visibility = View.GONE
            mutableList.clear()
            if (it.pictures != null) {
                for (pic in it.pictures!!) {
                    mutableList.add(pic)
                }
                screenSlideCreationTimenotePagerAdapter = ScreenSlideCreationTimenotePagerAdapter(
                    this,
                    mutableListOf(),
                    true,
                    false,
                    mutableList
                )
                preview_created_timenote_vp.adapter = screenSlideCreationTimenotePagerAdapter
                preview_created_timenote_indicator.setViewPager(preview_created_timenote_vp)
                screenSlideCreationTimenotePagerAdapter.registerAdapterDataObserver(
                    preview_created_timenote_indicator.adapterDataObserver
                )
            }
            else {
                if (!it.colorHex.isNullOrBlank())
                    preview_created_timenote_vp.setBackgroundColor(Color.parseColor(
                        if(it.colorHex?.contains("#")!!)
                            it.colorHex
                        else
                            "#${it.colorHex}"))
            }

            preview_created_timenote_title.text = it.title
            if (!it.startingAt.isBlank()) {
                if(args.modify == 0) preview_created_timenote_year.text = utils.setYearPreview(it.startingAt)
                else preview_created_timenote_year.text = utils.setYear(it.startingAt)
            }
            if (!it.startingAt.isBlank() && !it.endingAt.isBlank()) {
                if (args.modify == 0) preview_created_timenote_day_month.text =
                    utils.setFormatedStartDatePreview(it.startingAt, it.endingAt)
                else preview_created_timenote_day_month.text =
                    utils.setFormatedStartDate(it.startingAt, it.endingAt)
            }
            if (!it.startingAt.isBlank() && !it.endingAt.isBlank()) {
                if (args.modify == 0) preview_created_timenote_time.text =
                    utils.setFormatedEndDatePreview(it.startingAt, it.endingAt)
                else preview_created_timenote_time.text =
                    utils.setFormatedEndDate(it.startingAt, it.endingAt)
            }
            if (it.location != null) preview_created_timenote_place.text =
                it.location?.address?.address?.plus(", ")?.plus(it.location?.address?.city)?.plus(" ")?.plus(it.location?.address?.country)
            else preview_created_timenote_place.visibility = View.GONE
        })

        preview_created_timenote_done_btn.setOnClickListener(this)
        preview_timenote_created_share_btn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v){
            preview_created_timenote_done_btn -> clearPopAndBackHome()
            preview_timenote_created_share_btn -> share()
        }
    }

    private fun share() {

        val linkProperties: LinkProperties = LinkProperties().setChannel("whatsapp").setFeature("sharing")

        val branchUniversalObject = if(!args.timenoteInfoDTO?.pictures?.isNullOrEmpty()!!) BranchUniversalObject()
            .setTitle(args.timenoteInfoDTO?.title!!)
            .setContentDescription(args.timenoteInfoDTO?.description)
            .setContentImageUrl(args.timenoteInfoDTO?.pictures?.get(0)!!)
            .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
            .setContentMetadata(ContentMetadata().addCustomMetadata("timenoteInfoDTO", Gson().toJson(args.timenoteInfoDTO)))
        else BranchUniversalObject()
            .setTitle(args.timenoteInfoDTO?.title!!)
            .setContentDescription(args.timenoteInfoDTO?.description)
            .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
            .setContentMetadata(ContentMetadata().addCustomMetadata("timenoteInfoDTO", Gson().toJson(args.timenoteInfoDTO)))

        branchUniversalObject.generateShortUrl(requireContext(), linkProperties) { url, error ->
            BranchEvent("branch_url_created").logEvent(requireContext())
            val i = Intent(Intent.ACTION_SEND)
            i.type = "text/plain"
            i.putExtra(Intent.EXTRA_TEXT, String.format("Dayzee : %s at %s", args.timenoteInfoDTO?.title, url))
            startActivityForResult(i, 111)
        }


    }

    private fun clearPopAndBackHome() {
        creationTimenoteViewModel.clear()
        findNavController().popBackStack()
        listener.onDone(args.from)
    }


}
*/