package com.timenoteco.timenote.view.searchFlow

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.timenoteco.timenote.R
import com.timenoteco.timenote.view.homeFlow.TimenoteAddressArgs
import kotlinx.android.synthetic.main.fragment_timenote_address.*

class TimenoteAddressSearch : Fragment() {

    private val args: TimenoteAddressSearchArgs by navArgs()

    private var googleMap: GoogleMap? = null
    private val callback = OnMapReadyCallback { googleMap ->
        this.googleMap = googleMap
        this.googleMap?.uiSettings?.setAllGesturesEnabled(false)
        this.googleMap?.moveCamera(CameraUpdateFactory.newLatLng(LatLng(args.timenoteInfoDTO?.location?.latitude!!, args.timenoteInfoDTO?.location?.longitude!!)))
        this.googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(args.timenoteInfoDTO?.location?.latitude!!, args.timenoteInfoDTO?.location?.longitude!!), 15F))
        this.googleMap?.addMarker(MarkerOptions().position(LatLng(args.timenoteInfoDTO?.location?.latitude!!, args.timenoteInfoDTO?.location?.longitude!!)))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_timenote_address, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map_timenote) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
        timenote_address_toolbar.text = args.timenoteInfoDTO?.location?.address?.address?.plus(", ")?.plus(args.timenoteInfoDTO?.location?.address?.city)?.plus(" ")?.plus(args.timenoteInfoDTO?.location?.address?.country)
    }

}