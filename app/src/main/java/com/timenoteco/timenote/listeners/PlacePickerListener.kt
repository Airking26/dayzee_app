package com.timenoteco.timenote.listeners

import android.location.Address

interface PlacePickerListener {
    fun onPlacePicked(address: Address)
}