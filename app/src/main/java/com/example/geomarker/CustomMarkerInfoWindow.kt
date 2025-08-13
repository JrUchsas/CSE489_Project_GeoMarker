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

class CustomMarkerInfoWindow(mapView: MapView) : MarkerInfoWindow(R.layout.marker_info_window, mapView) {

    override fun onOpen(item: Any?) {
        if (item is Marker) {
            val entity = item.relatedObject as? Entity
            if (entity != null) {
                mView.findViewById<TextView>(R.id.info_title).text = entity.title
                mView.findViewById<TextView>(R.id.info_description).text = "Lat: ${entity.lat}, Lon: ${entity.lon}"

                val imageView = mView.findViewById<ImageView>(R.id.info_image)
                if (!entity.imageUrl.isNullOrEmpty()) {
                    imageView.visibility = View.VISIBLE
                    Glide.with(imageView.context)
                        .asBitmap()
                        .load(entity.imageUrl)
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
                } else {
                    imageView.visibility = View.GONE
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