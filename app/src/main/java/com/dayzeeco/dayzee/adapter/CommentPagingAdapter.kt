package com.dayzeeco.dayzee.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.dayzeeco.dayzee.R
import com.dayzeeco.dayzee.model.CommentInfoDTO

class CommentPagingAdapter(
    diffCallback: DiffUtil.ItemCallback<CommentInfoDTO>,
    private val commentPicUserListener: CommentAdapter.CommentPicUserListener,
    private val commentMoreListener: CommentAdapter.CommentMoreListener,
    private val userTaggedListener: CommentAdapter.UserTaggedListener) :
    PagingDataAdapter<CommentInfoDTO, CommentAdapter.CommentViewHolder>(diffCallback) {

    override fun onBindViewHolder(holder: CommentAdapter.CommentViewHolder, position: Int) {
        holder.bindComment(
            getItem(position)!!,
            commentPicUserListener,
            commentMoreListener,
            userTaggedListener
        )
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