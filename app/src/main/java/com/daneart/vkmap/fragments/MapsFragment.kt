package com.daneart.vkmap.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daneart.vkmap.R
import com.daneart.vkmap.ThemesAdapter
import com.daneart.vkmap.database.LatGroup
import com.daneart.vkmap.database.Post
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_maps.*
import java.util.*
import kotlin.math.*


class MapsFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private val REQUEST_LOCATION_PERMISSION = 1
    private lateinit var map: GoogleMap
    private lateinit var coordinates: LatLng
    private var groups = arrayListOf<LatGroup>()
    private var groupedGroups = hashMapOf<LatLng, ArrayList<LatGroup>>()
    private var number = 0L
    private lateinit var themeAdapter: ThemesAdapter
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root: View = inflater.inflate(R.layout.fragment_maps, container, false)

        themeAdapter = ThemesAdapter()
        val rv = root.findViewById<RecyclerView>(R.id.rv_themes)
        rv.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)

        themeAdapter.setOnClickListener { view, position ->

        }
        rv.adapter = themeAdapter

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
                        val post = Post(
                            UUID.fromString(mapp["id"] as String),
                            mapp["content"] as String?,
                            mapp["author"] as String,
                            mapp["theme"] as String,
                            mapp["emotion"] as String,
                            mapp["latitude"] as Double,
                            mapp["longitude"] as Double
                        )
                        updateGroups(post)

                    }


                    for (g in groupedGroups) {

                        val sortedGroups = g.value.sortedBy { it.posts.count() }.withIndex()
                            .filter { it.index < 3 }.map { it.value }
                        for (sG in sortedGroups) {
                            val rand = Random()
                            val latDif = if (rand.nextBoolean()) {
                                Random().nextDouble() / 300
                            } else {
                                Random().nextDouble() / 300 * -1
                            }
                            sG.topThemes.sortBy { it.second }
                            createMarker(
                                map,
                                LatLng(
                                    sG.mainPost.latitude+latDif,
                                    sG.mainPost.longitude+latDif
                                ),
                                sG.emotion,
                                sG.mainPost.author,
                                colors.getOrDefault(sG.emotion, 72.1F),
                                when(sG.topThemes.last().first){
                                    "Арт" -> R.drawable.group_art
                                            "Фото" -> R.drawable.group_photo
                                            "Наука" -> R.drawable.group_it
                                            "Спорт" -> R.drawable.group_sport
                                            "Туризм" -> R.drawable.group_travel
                                            "IT" -> R.drawable.group_it
                                            "Юмор" -> R.drawable.group_humour
                                            "Игры" -> R.drawable.group_games
                                            "Кино" -> R.drawable.group_cinema
                                            "Стиль" -> R.drawable.group_style
                                            "Музыка" -> R.drawable.group_music
                                            "Фокус" -> R.drawable.group_focus
                                            "Коронавирус" -> R.drawable.group_covid
                                    "Арт"->R.drawable.group_art
                                    "Кино"->R.drawable.group_cinema
                                    else ->R.drawable.btm_marker

                                }
                            )
                        }

                    }
                    map.moveCamera(CameraUpdateFactory.newLatLng(map.cameraPosition.target))

                } else {
                    Log.w("TAG", "Error getting documents.", task.exception)
                }
            }



        return root
    }


    private fun calcDistance(lat1: Double, long1: Double, lat2: Double, long2: Double): Double {
        val lat1Rad = lat1 / (180 / PI)
        val long1Rad = long1 / (180 / PI)

        val lat2Rad = lat2 / (180 / PI)
        val long2Rad = long2 / (180 / PI)

        var distance = 3963.0 * acos(
            sin(lat1Rad) * sin(lat2Rad) + cos(lat1Rad) * cos(lat2Rad) * cos(long2Rad - long1Rad)
        )
        distance = 2 * asin(sqrt(distance))
        val R = 6371
        distance *= R
        return distance
    }

    private fun updateGroups(newPost: Post) {
        try {
            if (groups.isEmpty()) {
                val group = LatGroup(
                    emotion = newPost.emotion,
                    mainPost = newPost,
                    posts = arrayListOf(newPost),
                    topThemes = arrayListOf(newPost.theme to 1)
                )

                groups.add(
                    group
                )
                if (groupedGroups[LatLng(newPost.latitude, newPost.longitude)] != null) {
                    groupedGroups[LatLng(newPost.latitude, newPost.longitude)]!!.add(group)
                } else {
                    groupedGroups[LatLng(newPost.latitude, newPost.longitude)] = arrayListOf(group)
                }
            } else {
                for (g in groups) {
                    val distance = calcDistance(
                        newPost.latitude,
                        newPost.longitude,
                        g.mainPost.latitude,
                        g.mainPost.longitude
                    )
                    if (distance < 5000 && g.emotion == newPost.emotion) {
                        g.posts.add(newPost)
                        if (!g.topThemes.map { it.first }.contains(newPost.theme)) {
                            g.topThemes.add(newPost.theme to 1)
                        } else {
                            val index = g.topThemes.map { it.first }
                                .indexOf(newPost.theme)
                            val elemCount = g.topThemes[index].second + 1
                            g.topThemes[index] = g.topThemes[index].first to elemCount

                        }
                    } else {
                        val group = LatGroup(
                            emotion = newPost.emotion,
                            mainPost = newPost,
                            posts = arrayListOf(newPost),
                            topThemes = arrayListOf(newPost.theme to 1)
                        )
                        groups.add(group)
                        val nearestGroupStack = groupedGroups.filterKeys {
                            calcDistance(
                                group.mainPost.latitude,
                                group.mainPost.longitude,
                                it.latitude,
                                it.longitude
                            ) < 5000
                        }.toSortedMap(Comparator<LatLng> { current, other ->
                            val dist1 = calcDistance(
                                group.mainPost.latitude,
                                group.mainPost.longitude,
                                current.latitude,
                                current.longitude
                            )
                            val dist2 = calcDistance(
                                group.mainPost.latitude,
                                group.mainPost.longitude,
                                other.latitude,
                                other.longitude
                            )

                            if (dist1 < dist2) {
                                -1
                            } else if (dist1 == dist2) {
                                0
                            } else {
                                1
                            }
                        })
                        if (groupedGroups[nearestGroupStack.firstKey()] != null) {
                            groupedGroups[nearestGroupStack.firstKey()]!!.add(group)
                        } else {
                            groupedGroups[nearestGroupStack.firstKey()] = arrayListOf(group)
                        }

                    }

                }
            }
        } catch (t: Throwable) {
        }
    }

    override fun onResume() {
        super.onResume()
        map.clear()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        et_search.doOnTextChanged { text, start, before, count ->
            themeAdapter.filterTheme(text = text.toString())
        }
    }

    private fun setMapLongClick() {
        map.setOnMapLongClickListener { latLng ->
            map.addMarker(MarkerOptions().position(latLng))
            coordinates = latLng
        }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        googleMap.clear()
        googleMap.setOnMarkerClickListener(this);

        enableMyLocation()

        /*   setMapLongClick()
       val ovlaySize = 10000f
       val androidOverlay = GroundOverlayOptions()
         .image(BitmapDescriptorFactory.fromResource(R.drawable.emo1))
         .position(LatLng(59.968771, 30.293110), overlaySize)
       map.addGroundOverlay(androidOverlay)*/


    }

    private fun createMarker(
        googleMap: GoogleMap,
        point: LatLng,
        hint: String,
        title: String,
        color: Float,
        resId: Int
    ) {

        googleMap.addMarker(
            MarkerOptions().position(point).title(title).snippet(hint).icon(
                BitmapDescriptorFactory.fromResource(resId)
            )
        )
//googleMap.animateCamera(
//  CameraUpdateFactory.newCameraPosition(
//      CameraPosition.Builder().target(point).zoom(12f).build()
//  )
//)
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

    override fun onMarkerClick(p0: Marker?): Boolean {
        view?.findNavController()?.navigate(R.id.action_mapsFragment_to_feedFragment)
        return true
    }


}