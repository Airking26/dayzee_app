package com.timenoteco.timenote.view.homeFlow

import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.datetime.dateTimePicker
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.afollestad.materialdialogs.list.listItems
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.timenoteco.timenote.R
import com.timenoteco.timenote.adapter.CommentAdapter
import com.timenoteco.timenote.adapter.ItemTimenoteToComeAdapter
import com.timenoteco.timenote.adapter.ScreenSlideTimenotePagerAdapter
import com.timenoteco.timenote.common.RoundedCornersTransformation
import com.timenoteco.timenote.listeners.TimenoteOptionsListener
import com.timenoteco.timenote.model.*
import com.timenoteco.timenote.viewModel.TimenoteViewModel
import kotlinx.android.synthetic.main.fragment_detailed_fragment.*
import kotlinx.android.synthetic.main.item_timenote_root.*


class DetailedTimenote : Fragment(), View.OnClickListener, CommentAdapter.CommentPicUserListener,
    CommentAdapter.CommentMoreListener {

    private val timenoteViewModel: TimenoteViewModel by activityViewModels()
    private lateinit var prefs: SharedPreferences
    private lateinit var commentAdapter: CommentAdapter
    private var comments : List<CommentModel> = listOf()
    val TOKEN: String = "TOKEN"
    private var tokenId: String? = null
    private val args : DetailedTimenoteArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
        //tokenId = prefs.getString(TOKEN, null)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
         inflater.inflate(R.layout.fragment_detailed_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        timenote_see_more.visibility = View.GONE
        //timenoteViewModel.getSpecificTimenote(tokenId!!, "").observe(viewLifecycleOwner, Observer {})

        comments_edittext.requestFocus()

        comments = listOf(
            CommentModel("", "Ronny Dahan", "Nice place, i love the food and the view, i would love to go back there", "2h"),
            CommentModel("", "Ronny Dahan", "Nice place, i love the food and the view, i would love to go back there", "2h"),
            CommentModel("", "Ronny Dahan", "Nice place, i love the food and the view, i would love to go back there", "2h"),
            CommentModel("", "Ronny Dahan", "Nice place, i love the food and the view, i would love to go back there", "2h"),
            CommentModel("", "Ronny Dahan", "Nice place, i love the food and the view, i would love to go back there", "2h Ago"),
            CommentModel("", "Ronny Dahan", "Nice place, i love the food and the view, i would love to go back there", "2h Ago"),
            CommentModel("", "Ronny Dahan", "Nice place, i love the food and the view, i would love to go back there", "2h Ago"),
            CommentModel("", "Ronny Dahan", "Nice place, i love the food and the view, i would love to go back there", "2h Ago"),
            CommentModel("", "Ronny Dahan", "Nice place, i love the food and the view, i would love to go back there", "2h Ago"),
            CommentModel("", "Ronny Dahan", "Nice place, i love the food and the view, i would love to go back there", "2h Ago"),
            CommentModel("", "Ronny Dahan", "Nice place, i love the food and the view, i would love to go back there", "2h Ago"),
            CommentModel("", "Ronny Dahan", "Nice place, i love the food and the view, i would love to go back there", "2h Ago"),
            CommentModel("", "Ronny Dahan", "Nice place, i love the food and the view, i would love to go back there", "2h Ago")

        )

        commentAdapter = CommentAdapter(comments, this, this)

        detailed_timenote_comments_rv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = commentAdapter
        }


        val screenSlideCreationTimenotePagerAdapter =  ScreenSlideTimenotePagerAdapter(this, mutableListOf("https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
            "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
            "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg"), true){ i: Int, i1: Int -> }

        timenote_vp.adapter = screenSlideCreationTimenotePagerAdapter
        timenote_indicator.setViewPager(timenote_vp)
        screenSlideCreationTimenotePagerAdapter.registerAdapterDataObserver(timenote_indicator.adapterDataObserver)

        val test = "Saved by Ronny Dahan and thousands of other people"

        val p = Typeface.create("sans-serif-light", Typeface.NORMAL)
        val m = Typeface.create("sans-serif", Typeface.NORMAL)
        val o = ItemTimenoteToComeAdapter.CustomTypefaceSpan(p)
        val k = ItemTimenoteToComeAdapter.CustomTypefaceSpan(m)

        val t = SpannableStringBuilder(test)
        t.setSpan(o, 0, 8, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
        t.setSpan(k, 9, test.length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)

        timenote_added_by.text = t

        val desc = "#Beach#Sunset#Love A very good place to be also known for his cold drinks a good music open all day and night come join us we are waiting for you"
        val h = SpannableStringBuilder(desc)
        h.setSpan(k, 0, 17, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
        h.setSpan(o, 18, desc.length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
        timenote_username_desc.text = h
        timenote_username_desc.maxLines = Int.MAX_VALUE

        Glide
            .with(this)
            .load("https://media.istockphoto.com/photos/beautiful-woman-posing-against-dark-background-picture-id638756792")
            .apply(RequestOptions.bitmapTransform(RoundedCornersTransformation(requireContext(), 90, 0, getString(0 + R.color.colorBackground), 4)))
            .into(timenote_pic_participant_one)
        Glide
            .with(this)
            .load("https://wl-sympa.cf.tsp.li/resize/728x/jpg/f6e/ef6/b5b68253409b796f61f6ecd1f1.jpg")
            .apply(RequestOptions.bitmapTransform(RoundedCornersTransformation(requireContext(), 90, 0, getString(0 + R.color.colorBackground), 4)))
            .into(timenote_pic_participant_two)
        Glide
            .with(this)
            .load("https://www.fc-photos.com/wp-content/uploads/2016/09/fc-photos-Weynacht-0001.jpg")
            .apply(RequestOptions.bitmapTransform(RoundedCornersTransformation(requireContext(), 90, 0, getString(0 + R.color.colorBackground), 4)))
            .into(timenote_pic_participant_three)

        Glide
            .with(this)
            .load("https://media.istockphoto.com/photos/beautiful-woman-posing-against-dark-background-picture-id638756792")
            .apply(RequestOptions.circleCropTransform())
            .into(detailed_timenote_pic_user)

        timenote_comment.setOnClickListener(this)
        detailed_timenote_btn_more.setOnClickListener(this)
        detailed_timenote_btn_back.setOnClickListener { findNavController().popBackStack() }
    }

    override fun onClick(v: View?) {
        when(v){
            timenote_comment -> {
                comments_edittext.requestFocus()
                val imm = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.showSoftInput(comments_edittext, InputMethodManager.SHOW_IMPLICIT)
            }
            detailed_timenote_btn_more -> createOptionsOnTimenote(requireContext(), true)
        }
    }

    private fun createOptionsOnTimenote(context: Context, isMine: Boolean){
        val listItems: MutableList<String>
        if(isMine) listItems = mutableListOf(context.getString(R.string.duplicate), context.getString(R.string.alarm), context.getString(R.string.report))
        else listItems = mutableListOf(context.getString(R.string.duplicate), context.getString(R.string.delete), context.getString(R.string.alarm),  context.getString(R.string.mask_user))
        MaterialDialog(context, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            title(R.string.posted_false)
            listItems (items = listItems){ dialog, index, text ->
                when(text.toString()){
                    context.getString(R.string.duplicate) -> findNavController().navigate(DetailedTimenoteDirections.actionDetailedTimenoteToCreateTimenote(true, "",
                        TimenoteBody("", CreatedBy("", "", "", "", "", "", ""),
                            "", "", listOf(), "", Location(0.0, 0.0, Address("", "", "", "")),
                            Category("",""), "", "", listOf(), "", 0, ""), args.from))
                    context.getString(R.string.report) -> Toast.makeText(
                        requireContext(),
                        "Reported. Thank You.",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    context.getString(R.string.alarm) -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                        dateTimePicker { dialog, datetime ->

                        }
                        lifecycleOwner(this@DetailedTimenote)
                    }
                }
            }
        }
    }

    override fun onPicUserCommentClicked() {
        findNavController().navigate(DetailedTimenoteDirections.actionDetailedTimenoteToProfile().setWhereFrom(true).setFrom(args.from))
    }

    override fun onCommentMoreClicked() {
        MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            listItems (items = listOf(getString(R.string.report), getString(R.string.delete))){ dialog, index, text ->  }
            lifecycleOwner(this@DetailedTimenote)
        }
    }

}