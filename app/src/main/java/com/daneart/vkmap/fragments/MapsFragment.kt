package com.daneart.vkmap.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.daneart.vkmap.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.firestore.FirebaseFirestore


class MapsFragment : Fragment(), OnMapReadyCallback {
    private val REQUEST_LOCATION_PERMISSION = 1
    private lateinit var map: GoogleMap
    private lateinit var coordinates: LatLng
    private var number = 0L
    val colors = mapOf<String, Float>(
        "\uD83D\uDE0C Хорошее" to 335.6F, //Хорошее
        "Активное" to 202.5F, // Активное
        "\uD83D\uDE1C Гиперактивное" to 30F,    // Гиперактивное
        "\uD83D\uDE10 Спокойное" to 50.3F,  // Спокойное
        "\uD83D\uDE41 Беспокойное" to 158.4F, // Беспокойное
        "\uD83D\uDE34 Усталое" to 183.4F, // Усталое
        "Расслабленное" to 104.7F, // Расслабленное
        "Негативное" to 72.1F,  // Негативное
        "НАСТРОЕНИЕ" to 50.4F
    )

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root: View = inflater.inflate(R.layout.fragment_maps, container, false)

        val db = FirebaseFirestore.getInstance()
        db.collection("posts")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        Log.d("TAG", "i m here")
                        var lat: Double
                        var long: Double
                        Log.d("TAG", document.id + " => " + document.data)
                        val mapp = document.data
                        lat = mapp.get("latitude") as Double
                        long = mapp.get("longitude") as Double
                        createMarker(
                            map,
                            LatLng(lat, long),
                            mapp.get("emotion").toString(),
                            mapp.get("author").toString(),
                            colors.getOrDefault(mapp.get("author").toString(), 50.4F)
                        )
                    }
                } else {
                    Log.w("TAG", "Error getting documents.", task.exception)
                }
            }



        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

    }

    private fun sticker() {

    }


    private fun setMapLongClick() {
        map.setOnMapLongClickListener { latLng ->
            map.addMarker(MarkerOptions().position(latLng))
            coordinates = latLng
        }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        enableMyLocation()
        setMapLongClick()
        /*  createMarker(
              googleMap,
              LatLng(59.968771, 30.293110),
              "hint",
              "category",
              colors[0]
          )*/
        val overlaySize = 10000f
        val androidOverlay = GroundOverlayOptions()
            .image(BitmapDescriptorFactory.fromResource(R.drawable.emo1))
            .position(LatLng(59.968771, 30.293110), overlaySize)
        map.addGroundOverlay(androidOverlay)


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
                CameraPosition.Builder().target(point).zoom(12f).build()
            )
        )
    }

    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun enableMyLocation() {
        if (isPermissionGranted()) {
            if (ActivityCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            map.isMyLocationEnabled = true
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                enableMyLocation()
            }
        }
    }


}