package com.daneart.vkmap.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.daneart.vkmap.R
import com.daneart.vkmap.SpinnerAdapter
import com.daneart.vkmap.database.Post
import com.daneart.vkmap.database.Theme
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_editor.*
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [EditorFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EditorFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var isPosted: Boolean = false

    private val emojiArr = mutableListOf(
        "НАСТРОЕНИЕ",
        "\uD83D\uDE0C Хорошее",
        "\uD83D\uDE03 Активное",
        "\uD83D\uDE1C Гиперактивное",
        "\uD83D\uDE10 Спокойное",
        "\uD83D\uDE41 Беспокойное",
        "\uD83D\uDE34 Усталое",
        "\uD83E\uDD24 Расслабленное",
        "\uD83D\uDE21 Негативное"
    )

    val themeArr = mutableListOf(
        "ТЕМАТИКА"
    ).apply {
        addAll(Theme.values().map { it.rus })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isPosted = savedInstanceState?.getBoolean("isPosted") ?: false
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onResume() {
        super.onResume()
        isPosted = false
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean("isPosted", isPosted)
        super.onSaveInstanceState(outState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        btn_check.setOnClickListener {

            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    0
                )
            } else {
                val locationManager =
                    getSystemService<LocationManager>(requireContext(), LocationManager::class.java)
                if (locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    val locationListener: LocationListener = MyLocationListener()

                    locationManager?.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER, 5000, 10f, locationListener
                    )
                } else {
                    Toast.makeText(requireContext(), "Включите GPS", Toast.LENGTH_LONG).show()
                }


            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            0 -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ) {
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                    val locationManager =
                        getSystemService<LocationManager>(
                            requireContext(),
                            LocationManager::class.java
                        )
                    if (locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        val locationListener: LocationListener = MyLocationListener()

                        if (ActivityCompat.checkSelfPermission(
                                requireContext(),
                                Manifest.permission.ACCESS_FINE_LOCATION
                            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                                requireContext(),
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            return
                        }
                        locationManager?.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER, 5000, 10f, locationListener
                        )
                    } else {
                        Toast.makeText(requireContext(), "Включите GPS", Toast.LENGTH_LONG).show()
                    }

                } else {
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }


    /*---------- Listener class to get coordinates ------------- */
    inner class MyLocationListener : LocationListener {
        override fun onLocationChanged(loc: Location) {
try {

    val longitude = loc.longitude

    val latitude = loc.latitude

    val post = Post(
        id = UUID.randomUUID(),
        content = et_main.text.toString(),
        author = "Маруся",
        theme = btn_theme.selectedItem as String,
        emotion = btn_emotion.selectedItem as String,
        latitude = latitude,
        longitude = longitude
    )

    addPost(post)

}catch (e:Throwable){}
        }

        override fun onProviderDisabled(provider: String) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onStatusChanged(
            provider: String,
            status: Int,
            extras: Bundle
        ) {
        }
    }

    fun addPost(post: Post) {
        try {
            if (!isPosted) {
                val jsonPost = mapOf(
                    "id" to post.id.toString(),
                    "content" to post.content,
                    "author" to post.author,
                    "theme" to post.theme,
                    "emotion" to post.emotion,
                    "latitude" to post.latitude,
                    "longitude" to post.longitude
                )
                val dataBase = FirebaseFirestore.getInstance()
                dataBase.collection("posts")
                    .add(jsonPost)
                    .addOnCompleteListener {
                        Log.d("TAG", "Completed adding")
                    }
                    .addOnCanceledListener {
                        Log.d("TAG", "Canceled adding")
                    }
                isPosted = true
                requireView().findNavController()
                    .navigate(R.id.action_editorFragment_to_feedFragment)
            }
        } catch (e: Throwable) {
            Toast.makeText(requireContext(), "Пост не отправлен", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_editor, container, false)

        val adapter = SpinnerAdapter(
            requireActivity(), R.layout.spinner_item, R.id.txt_spinner_item, emojiArr
        )
        val spinnerEmoji = view.findViewById<Spinner>(R.id.btn_emotion)
        spinnerEmoji.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                adapter.activePos = position

            }

        }
        spinnerEmoji.setAdapter(adapter)

        val themeAdapter = SpinnerAdapter(
            requireActivity(), R.layout.spinner_item, R.id.txt_spinner_item, themeArr
        )
        val spinnerTheme = view.findViewById<Spinner>(R.id.btn_theme)
        spinnerTheme.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                themeAdapter.activePos = position
            }
        }
        spinnerTheme.adapter = themeAdapter

        return view
    }
}