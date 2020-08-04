package com.timenoteco.timenote.view.homeFlow

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.timenoteco.timenote.R
import com.timenoteco.timenote.listeners.OnRemoveFilterBarListener
import com.timenoteco.timenote.view.profileFlow.ProfileFutureEvents

private const val LATITUDE = "latitude"
private const val LONGITUDE = "longitude"

class TimenoteAddress : Fragment() {

    private var latitude: Double? = null
    private var longitude: Double? = null

    private var googleMap: GoogleMap? = null
    private val callback = OnMapReadyCallback { googleMap ->
        this.googleMap = googleMap
        this.googleMap?.moveCamera(CameraUpdateFactory.newLatLng(LatLng(latitude!!, longitude!!)))
        this.googleMap?.animateCamera(CameraUpdateFactory.zoomTo(13.0f))    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments.let {

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_timenote_address, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map_timenote) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    companion object{
        @JvmStatic
        fun newInstance(latitude: Double, longitude: Double) =
            ProfileFutureEvents().apply {
                arguments = Bundle().apply {
                    putDouble(LATITUDE, latitude)
                    putDouble(LONGITUDE, longitude)
                }
            }
    }}