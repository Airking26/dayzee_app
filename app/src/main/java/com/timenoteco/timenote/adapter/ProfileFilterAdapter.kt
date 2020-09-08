package com.timenoteco.timenote.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.timenoteco.timenote.R
import com.timenoteco.timenote.model.TimenoteInfoDTO
import kotlinx.android.synthetic.main.item_timenote.view.*
import kotlinx.android.synthetic.main.item_timenote_root.view.*

class ProfileFilterAdapter(
    private val timenotes: List<TimenoteInfoDTO>,
    private val fragment: Fragment
): RecyclerView.Adapter<ProfileFilterAdapter.ProfileFilterHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileFilterHolder =
        ProfileFilterHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_timenote, parent, false))

    override fun getItemCount(): Int = timenotes.size


    override fun onBindViewHolder(holder: ProfileFilterHolder, position: Int) {
        holder.bindTimenotesAndChips(timenotes[position], fragment)
    }

    class ProfileFilterHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bindTimenotesAndChips(
            timenote: TimenoteInfoDTO,
            fragment: Fragment
        ) {
            Glide
                .with(itemView)
                .load(timenote.createdBy.pictureURL)
                .apply(RequestOptions.circleCropTransform())
                .into(itemView.timenote_pic_user_imageview)

            val screenSlideCreationTimenotePagerAdapter = ScreenSlideTimenotePagerAdapter(fragment, timenote.pictures, true){ i: Int, i1: Int -> }
            itemView.timenote_vp.adapter = screenSlideCreationTimenotePagerAdapter
            itemView.timenote_indicator.setViewPager(itemView.timenote_vp)
            if(timenote.pictures.size == 1) itemView.timenote_indicator.visibility = View.GONE
            screenSlideCreationTimenotePagerAdapter.registerAdapterDataObserver(itemView.timenote_indicator.adapterDataObserver)

            itemView.timenote_username.text = timenote.createdBy.givenName
            itemView.timenote_place.text = timenote.location.address.address
            itemView.timenote_username_desc.text = timenote.description
            itemView.timenote_title.text = timenote.title
            //itemView.timenote_year.text = timenote.year
            //itemView.timenote_day_month.text = timenote.month
            //itemView.timenote_time.text = timenote.date
            itemView.timenote_day_month.setOnClickListener {
                itemView.separator_1.visibility = View.GONE
                itemView.separator_2.visibility = View.GONE
                itemView.timenote_day_month.visibility = View.GONE
                itemView.timenote_time.visibility = View.GONE
                itemView.timenote_year.visibility = View.GONE
            }
        }

    }
}