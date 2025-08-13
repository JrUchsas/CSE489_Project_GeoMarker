package com.example.geomarker

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.geomarker.model.Entity
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow
import androidx.navigation.Navigation
import android.os.Bundle
import com.example.geomarker.R

class CustomMarkerInfoWindow(mapView: MapView) : MarkerInfoWindow(R.layout.marker_info_window, mapView) {

    private val BASE_IMAGE_URL = "https://labs.anontech.info/cse489/t3/"

    override fun onOpen(item: Any?) {
        if (item is Marker) {
            val entity = item.relatedObject as? Entity
            if (entity != null) {
                mView.findViewById<TextView>(R.id.info_title).text = entity.title

                val imageView = mView.findViewById<ImageView>(R.id.info_image)
                if (!entity.imageUrl.isNullOrEmpty()) {
                    imageView.visibility = View.VISIBLE
                    val fullImageUrl = BASE_IMAGE_URL + entity.imageUrl
                    Glide.with(imageView.context)
                        .asBitmap()
                        .load(fullImageUrl)
                        .override(150, 150) // Adjust size as needed for info window
                        .into(object : CustomTarget<Bitmap>() {
                            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                imageView.setImageBitmap(resource)
                                // This is crucial to make sure the info window resizes to fit the image
                                (mView.parent as? View)?.requestLayout()
                            }

                            override fun onLoadCleared(placeholder: Drawable?) {
                                imageView.setImageDrawable(placeholder)
                            }
                        })

                    imageView.setOnClickListener { view ->
                        val navController = Navigation.findNavController(view)
                        val action = R.id.action_mapFragment_to_imageDetailFragment
                        val bundle = Bundle().apply {
                            putString("imageUrl", fullImageUrl)
                            putString("title", entity.title)
                        }
                        navController.navigate(action, bundle)
                    }
                } else {
                    imageView.visibility = View.GONE
                    imageView.setOnClickListener(null) // Remove listener if no image
                }
            }
        }
    }

    override fun onClose() {
        super.onClose()

        mView.findViewById<ImageView>(R.id.info_image).setImageDrawable(null)
        mView.findViewById<ImageView>(R.id.info_image).visibility = View.GONE
    }
}