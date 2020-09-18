package com.daneart.vkmap.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.daneart.vkmap.R
import com.daneart.vkmap.SpinnerAdapter
import com.daneart.vkmap.database.Emotion
import com.daneart.vkmap.database.FBStorage
import com.daneart.vkmap.database.Post
import com.daneart.vkmap.database.Theme
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

    private val emojiArr = mutableListOf(
        "НАСТРОЕНИЕ",
        "\uD83D\uDE0C",
        "\uD83D\uDE03",
        "\uD83D\uDE1C",
        "\uD83D\uDE10",
        "\uD83D\uDE41",
        "\uD83D\uDE34"
    )

    val themeArr = mutableListOf(
        "ТЕМАТИКА"
    ).apply {
        addAll(Theme.values().map { it.rus })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        btn_check.setOnClickListener {
//            FBStorage.addPost(
//                Post.TextPost(
//                    id = UUID.randomUUID(),
//                    emotion = Emotion.values()[btn_emotion.selectedItemPosition],
//                    theme =,
//                    author =,
//                    latLng =,
//                    textContent =,
//                )
//            )
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment EditorFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EditorFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}