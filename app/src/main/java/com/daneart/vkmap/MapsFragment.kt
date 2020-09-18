package com.daneart.vkmap

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsFragment : Fragment(), OnMapReadyCallback {

    val colors = arrayOf(
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



        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap?) {
        val point1: LatLng = LatLng(59.935722, 30.325729)
        val hint = "Здесь дают макбуки"
        val title = "HQ VK"
        if (map != null) {
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

    /*private fun setMapLongClick() {
        map.setOnMapLongClickListener { latLng ->
            map.addMarker(
                MarkerOptions()
                    .position(latLng).title(latLng.toString())
            )
        }
    }*/
}