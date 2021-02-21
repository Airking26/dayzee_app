package com.dayzeeco.dayzee.viewModel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.dayzeeco.dayzee.model.CommentCreationDTO
import com.dayzeeco.dayzee.paging.CommentPagingSource
import com.dayzeeco.dayzee.webService.repo.DayzeeRepository
import kotlinx.coroutines.flow.flow

class CommentViewModel : ViewModel() {

    private val commentService = DayzeeRepository().getCommentService()

    fun getComments(token: String, id : String, sharedPreferences: SharedPreferences) = Pager(PagingConfig(pageSize = 1)){CommentPagingSource(token, id, commentService, sharedPreferences)}.flow.cachedIn(viewModelScope)
    fun postComment(token: String, commentCreationDTO: CommentCreationDTO) = flow { emit(commentService.postComment("Bearer $token", commentCreationDTO))}.asLiveData(viewModelScope.coroutineContext)
    fun deleteComment(token: String, id: String) = flow { emit(commentService.deleteComment("Bearer $token", id)) }.asLiveData(viewModelScope.coroutineContext)

}