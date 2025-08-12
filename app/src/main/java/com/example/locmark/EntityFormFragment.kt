package com.example.locmark

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.locmark.viewmodel.EntityFormViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class EntityFormFragment : Fragment() {
    private lateinit var editTitle: EditText
    private lateinit var textLat: TextView
    private lateinit var textLon: TextView
    private lateinit var btnGetLocation: Button
    private lateinit var imagePreview: ImageView
    private lateinit var btnSelectImage: Button
    private lateinit var btnSave: Button
    private var imageUri: Uri? = null
    private var lat: Double? = null
    private var lon: Double? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val viewModel: EntityFormViewModel by viewModels()

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            imageUri = it
            imagePreview.setImageURI(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_entity_form, container, false)
        editTitle = view.findViewById(R.id.editTitle)
        textLat = view.findViewById(R.id.textLat)
        textLon = view.findViewById(R.id.textLon)
        btnGetLocation = view.findViewById(R.id.btnGetLocation)
        imagePreview = view.findViewById(R.id.imagePreview)
        btnSelectImage = view.findViewById(R.id.btnSelectImage)
        btnSave = view.findViewById(R.id.btnSave)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnSelectImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }
        btnGetLocation.setOnClickListener {
            getLocation()
        }
        btnSave.setOnClickListener {
            val title = editTitle.text.toString()
            val latValue = lat
            val lonValue = lon
            if (title.isBlank() || latValue == null || lonValue == null || imageUri == null) {
                Toast.makeText(requireContext(), "Fill all fields and select an image", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.createEntity(title, latValue, lonValue, imageUri)
        }
        viewModel.saveResult.observe(viewLifecycleOwner) { success ->
            if (success == true) {
                Toast.makeText(requireContext(), "Entity saved!", Toast.LENGTH_SHORT).show()
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
        viewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            errorMsg?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                lat = location.latitude
                lon = location.longitude
                textLat.text = "Latitude: $lat"
                textLon.text = "Longitude: $lon"
            } else {
                Toast.makeText(requireContext(), "Unable to get location", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
