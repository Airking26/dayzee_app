package com.dayzeeco.dayzee.listeners

import android.location.Address

interface PlacePickerListener {
    fun onPlacePicked(address: Address)
}