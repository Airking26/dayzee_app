package com.timenoteco.timenote.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.timenoteco.timenote.R
import com.timenoteco.timenote.model.CommentInfoDTO
import com.timenoteco.timenote.model.TimenoteInfoDTO

class CommentPagingAdapter(
    diffCallback: DiffUtil.ItemCallback<CommentInfoDTO>,
    val commentPicUserListener: CommentAdapter.CommentPicUserListener,
    val commentMoreListener: CommentAdapter.CommentMoreListener) :
    PagingDataAdapter<CommentInfoDTO, CommentAdapter.CommentViewHolder>(diffCallback) {

    override fun onBindViewHolder(holder: CommentAdapter.CommentViewHolder, position: Int) {
        holder.bindComment(getItem(position)!!, commentPicUserListener, commentMoreListener)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CommentAdapter.CommentViewHolder =
        CommentAdapter.CommentViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false))
}

object CommentComparator : DiffUtil.ItemCallback<CommentInfoDTO>(){

    override fun areItemsTheSame(oldItem: CommentInfoDTO, newItem: CommentInfoDTO): Boolean {
        return oldItem.createdAt == newItem.createdAt
    }

    override fun areContentsTheSame(oldItem: CommentInfoDTO, newItem: CommentInfoDTO): Boolean {
        return oldItem == newItem
    }

}