package com.dayzeeco.dayzee.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.flow

class WalletConnectViewModel : ViewModel() {

    //fun getNFTS(address: String) = flow { emit(MoralisWeb3ApiAccount.getNFTs(address)) }.asLiveData(viewModelScope.coroutineContext)
}