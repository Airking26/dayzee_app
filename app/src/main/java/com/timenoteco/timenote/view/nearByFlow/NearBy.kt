package com.timenoteco.timenote.view.nearByFlow

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.location.Address
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.datetime.datePicker
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.timenoteco.timenote.R
import com.timenoteco.timenote.adapter.ItemTimenoteAdapter
import com.timenoteco.timenote.common.Utils
import com.timenoteco.timenote.listeners.PlacePickerListener
import com.timenoteco.timenote.model.Timenote
import kotlinx.android.synthetic.main.fragment_near_by.*
import java.text.SimpleDateFormat
import java.util.*

class NearBy : Fragment(), ItemTimenoteAdapter.CommentListener, ItemTimenoteAdapter.PlusListener, View.OnClickListener, PlacePickerListener {

    private lateinit var locationManager: LocationManager
    private lateinit var nearbyDateTv: TextView
    private var timenotes: List<Timenote> = listOf()
    private val DATE_FORMAT = "EEE, d MMM yyyy"
    private lateinit var dateFormat : SimpleDateFormat
    private lateinit var timenoteAdapter: ItemTimenoteAdapter
    private lateinit var googleMap: GoogleMap
    private val callback = OnMapReadyCallback { googleMap -> this.googleMap = googleMap }
    private val locationListener: LocationListener = object: LocationListener {
        override fun onLocationChanged(location: Location?) {
            googleMap.addMarker(MarkerOptions().position(LatLng(location?.latitude!!, location.longitude)))
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(location.latitude, location.longitude)))
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(15.0f))

        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        }

        override fun onProviderEnabled(provider: String?) {
        }

        override fun onProviderDisabled(provider: String?) {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_near_by, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        locationManager = requireContext().getSystemService(LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            this.requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), 3)
        } else {
            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null)
        }
        dateFormat = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
        nearbyDateTv = nearby_time

        nearby_place.setOnClickListener(this)
        nearby_time.setOnClickListener(this)
        nearby_filter_btn.setOnClickListener(this)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        timenotes = listOf(
            Timenote(
                "https://media.istockphoto.com/photos/beautiful-woman-posing-against-dark-background-picture-id638756792",
                "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                "Samuel",
                "23 Herzl Street",
                "34 Likes",
                "See 63 comments",
                "#Beach#Sunset#Love#Summer#Dance#Party#Beach#Sunset#Love#Summer#Dance#Party#Beach#Sunset#Love#Summer#Dance#Party#Beach#Sunset#Love#Summer#Dance#Party",
                true,
                "2020",
                "10\nAug",
                "15:30",
                "Beach Party",
                "In 23 days"
            ),
            Timenote(
                "https://media.istockphoto.com/photos/beautiful-woman-posing-against-dark-background-picture-id638756792",
                "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                "Samuel",
                "23 Herzl Street",
                "34 Likes",
                "See 63 comments",
                "#Beach#Sunset#Love#Summer#Dance#Party#Beach#Sunset#Love#Summer#Dance#Party#Beach#Sunset#Love#Summer#Dance#Party#Beach#Sunset#Love#Summer#Dance#Party",
                true,
                "2020",
                "10\nAug",
                "15:30",
                "Beach Party",
                "In 23 days"
            ),
            Timenote(
                "https://media.istockphoto.com/photos/beautiful-woman-posing-against-dark-background-picture-id638756792",
                "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                "Samuel",
                "23 Herzl Street",
                "34 Likes",
                "See 63 comments",
                "#Beach#Sunset#Love#Summer#Dance#Party#Beach#Sunset#Love#Summer#Dance#Party#Beach#Sunset#Love#Summer#Dance#Party#Beach#Sunset#Love#Summer#Dance#Party",
                true,
                "2020",
                "10\nAug",
                "15:30",
                "Beach Party",
                "In 23 days"
            ),
            Timenote(
                "https://media.istockphoto.com/photos/beautiful-woman-posing-against-dark-background-picture-id638756792",
                "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                "Samuel",
                "23 Herzl Street",
                "34 Likes",
                "See 63 comments",
                "#Beach#Sunset#Love#Summer#Dance#Party#Beach#Sunset#Love#Summer#Dance#Party#Beach#Sunset#Love#Summer#Dance#Party#Beach#Sunset#Love#Summer#Dance#Party",
                true,
                "2020",
                "10\nAug",
                "15:30",
                "Beach Party",
                "In 23 days"
            ),
            Timenote(
                "https://media.istockphoto.com/photos/beautiful-woman-posing-against-dark-background-picture-id638756792",
                "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                "Samuel",
                "23 Herzl Street",
                "34 Likes",
                "See 63 comments",
                "#Beach#Sunset#Love#Summer#Dance#Party#Beach#Sunset#Love#Summer#Dance#Party#Beach#Sunset#Love#Summer#Dance#Party#Beach#Sunset#Love#Summer#Dance#Party",
                true,
                "2020",
                "10\nAug",
                "15:30",
                "Beach Party",
                "In 23 days"
            ),
            Timenote(
                "https://media.istockphoto.com/photos/beautiful-woman-posing-against-dark-background-picture-id638756792",
                "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                "Samuel",
                "23 Herzl Street",
                "34 Likes",
                "See 63 comments",
                "#Beach#Sunset#Love#Summer#Dance#Party#Beach#Sunset#Love#Summer#Dance#Party#Beach#Sunset#Love#Summer#Dance#Party#Beach#Sunset#Love#Summer#Dance#Party",
                true,
                "2020",
                "10\nAug",
                "15:30",
                "Beach Party",
                "In 23 days"
            ),
            Timenote(
                "https://media.istockphoto.com/photos/beautiful-woman-posing-against-dark-background-picture-id638756792",
                "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                "Samuel",
                "23 Herzl Street",
                "34 Likes",
                "See 63 comments",
                "#Beach#Sunset#Love#Summer#Dance#Party#Beach#Sunset#Love#Summer#Dance#Party#Beach#Sunset#Love#Summer#Dance#Party#Beach#Sunset#Love#Summer#Dance#Party",
                true,
                "2020",
                "10\nAug",
                "15:30",
                "Beach Party",
                "In 23 days"
            ),
            Timenote(
                "https://media.istockphoto.com/photos/beautiful-woman-posing-against-dark-background-picture-id638756792",
                "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                "Samuel",
                "23 Herzl Street",
                "34 Likes",
                "See 63 comments",
                "#Beach#Sunset#Love#Summer#Dance#Party#Beach#Sunset#Love#Summer#Dance#Party#Beach#Sunset#Love#Summer#Dance#Party#Beach#Sunset#Love#Summer#Dance#Party",
                true,
                "2020",
                "10\nAug",
                "15:30",
                "Beach Party",
                "In 23 days"
            ),
            Timenote(
                "https://media.istockphoto.com/photos/beautiful-woman-posing-against-dark-background-picture-id638756792",
                "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                "Samuel",
                "23 Herzl Street",
                "34 Likes",
                "See 63 comments",
                "#Beach#Sunset#Love#Summer#Dance#Party#Beach#Sunset#Love#Summer#Dance#Party#Beach#Sunset#Love#Summer#Dance#Party#Beach#Sunset#Love#Summer#Dance#Party",
                true,
                "2020",
                "10\nAug",
                "15:30",
                "Beach Party",
                "In 23 days"
            )
        )
        timenoteAdapter = ItemTimenoteAdapter(timenotes, timenotes, false, this, this)
        nearby_rv.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = timenoteAdapter
        }

    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode == 3){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null)
            }
        }
    }

    override fun onCommentClicked() {
        findNavController().navigate(NearByDirections.actionNearByToComments())
    }

    override fun onPlusClicked() {
        findNavController().navigate(NearByDirections.actionNearByToDetailedTimenote())
    }

    override fun onClick(v: View?) {
        when(v){
            nearby_place -> Utils().placePicker(requireContext(), this@NearBy, nearby_place, this)
            nearby_time -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                datePicker { dialog, datetime ->
                    nearbyDateTv.text = dateFormat.format(datetime.time.time)
                }
            }
            nearby_filter_btn -> findNavController().navigate(NearByDirections.actionNearByToNearbyFilters())
        }
    }

    override fun onPlacePicked(address: Address) {
        this.googleMap.addMarker(MarkerOptions().position(LatLng(address.latitude, address.longitude)))
        this.googleMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(address.latitude, address.longitude)))
        this.googleMap.animateCamera(CameraUpdateFactory.zoomTo(13.0f))
    }

}
