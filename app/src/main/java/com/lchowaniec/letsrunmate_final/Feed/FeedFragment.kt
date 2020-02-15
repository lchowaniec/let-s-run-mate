package com.lchowaniec.letsrunmate_final.Feed


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.lchowaniec.letsrunmate_final.R

/**
 * A simple [Fragment] subclass.
 */
class FeedFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_feed, container, false)

        return view
    }


}
