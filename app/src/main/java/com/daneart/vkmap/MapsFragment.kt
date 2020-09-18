package com.daneart.vkmap

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class MapsFragment : Fragment(), OnMapReadyCallback {
    private lateinit var Gmap: GoogleMap
    private val colors = arrayOf(
        335.6F,
        202.5F,
        30F,
        50.3F,
        158.4F,
        183.4F,
        104.7F,
        72.1F,
        50.4F
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_maps, container, false)
        /*val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)*/
        val mMapView = root.findViewById<View>(R.id.map) as MapView
        mMapView.onCreate(savedInstanceState)
        mMapView.getMapAsync(this) //this is important


        return root
    }

    override fun onMapReady(map: GoogleMap?) {
        val point1 = LatLng(59.935722, 30.325729)
        val hint = "Здесь дают макбуки"
        val title = "HQ VK"

        if (map != null) {
            //These coordinates represent the latitude and longitude of the Googleplex.
            val latitude = 37.422160
            val longitude = -122.084270
            val zoomLevel = 15f

            val homeLatLng = LatLng(latitude, longitude)
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(homeLatLng, zoomLevel))
            map.addMarker(MarkerOptions().position(homeLatLng))
            Gmap = map
            map.addMarker(
                MarkerOptions().position(point1).title(title).snippet(hint)
            )
            createMarker(
                googleMap = map,
                point = point1,
                hint = hint,
                title = title,
                color = colors[0]
            )
        }

    }


    private fun createMarker(
        googleMap: GoogleMap,
        point: LatLng,
        hint: String,
        title: String,
        color: Float
    ) {

        googleMap.addMarker(
            MarkerOptions().position(point).title(title).snippet(hint).icon(
                BitmapDescriptorFactory.defaultMarker(color)
            )
        )
        googleMap.animateCamera(
            CameraUpdateFactory.newCameraPosition(
                CameraPosition.Builder().target(point).zoom(14f).build()
            )
        )
    }

    private fun setMapLongClick() {
        Gmap.setOnMapLongClickListener { latLng ->
            Gmap.addMarker(
                MarkerOptions()
                    .position(latLng).title(latLng.toString())
            )
        }
    }
}