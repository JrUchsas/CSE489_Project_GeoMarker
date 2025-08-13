package com.example.geomarker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide

class ImageDetailFragment : Fragment() {

    private val args: ImageDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_image_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageUrl = args.imageUrl
        val title = args.title

        val imageView: ImageView = view.findViewById(R.id.image_detail_view)
        val titleView: TextView = view.findViewById(R.id.image_detail_title)

        titleView.text = title

        Glide.with(this)
            .load(imageUrl)
            .into(imageView)
    }
}