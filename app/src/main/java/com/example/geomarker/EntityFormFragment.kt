package com.example.geomarker

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.geomarker.viewmodel.EntityFormViewModel
import com.example.geomarker.viewmodel.MapViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import android.util.Log

class EntityFormFragment : Fragment() {
    private lateinit var editTitle: EditText
    private lateinit var editLat: EditText
    private lateinit var editLon: EditText
    private lateinit var imagePreview: ImageView
    private lateinit var btnSelectImage: Button
    private lateinit var btnSave: Button
    private lateinit var btnGetCurrentLocation: Button
    private var imageUri: Uri? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val viewModel: EntityFormViewModel by viewModels()
    private val sharedMapViewModel: MapViewModel by activityViewModels()

    private var currentEntityId: Int = -1

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            imageUri = it
            imagePreview.setImageURI(it)
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                fetchCurrentLocation()
            } else {
                Toast.makeText(requireContext(), "Location permission denied.", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_entity_form, container, false)
        editTitle = view.findViewById(R.id.editTitle)
        editLat = view.findViewById(R.id.editLat)
        editLon = view.findViewById(R.id.editLon)
        imagePreview = view.findViewById(R.id.imagePreview)
        btnSelectImage = view.findViewById(R.id.btnSelectImage)
        btnSave = view.findViewById(R.id.btnSave)
        btnGetCurrentLocation = view.findViewById(R.id.btnGetCurrentLocation)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.mapFragment)
            }
        })

        arguments?.let { bundle ->
            currentEntityId = EntityFormFragmentArgs.fromBundle(bundle).entityId
            if (currentEntityId != -1) {
                lifecycleScope.launch {
                    val entity = viewModel.getEntityById(currentEntityId)
                    entity?.let {
                        editTitle.setText(it.title)
                        editLat.setText(it.lat.toString())
                        editLon.setText(it.lon.toString())
                        imageUri = Uri.parse(it.imageUrl)
                        imagePreview.setImageURI(imageUri)
                    }
                }
            }
        }

        btnGetCurrentLocation.setOnClickListener {
            when {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED -> {
                    fetchCurrentLocation()
                }
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            }
        }

        btnSelectImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        btnSave.setOnClickListener {
            Log.d("EntityFormFragment", "Save button clicked.")
            val title = editTitle.text.toString()
            val latStr = editLat.text.toString()
            val lonStr = editLon.text.toString()
            val image = imageUri
            val latValue = latStr.toDoubleOrNull()
            val lonValue = lonStr.toDoubleOrNull()
            if (title.isBlank() || latValue == null || lonValue == null || image == null) {
                Toast.makeText(requireContext(), "Fill all fields and select an image", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (latValue !in -90.0..90.0 || lonValue !in -180.0..180.0) {
                Toast.makeText(requireContext(), "Latitude must be between -90 and 90, Longitude between -180 and 180", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (currentEntityId != -1) {
                viewModel.updateEntity(currentEntityId, title, latValue, lonValue, image)
            } else {
                viewModel.createEntity(title, latValue, lonValue, image)
            }
        }

        viewModel.saveResult.observe(viewLifecycleOwner) { success ->
            Log.d("EntityFormFragment", "saveResult observed: $success")
            if (success == true) {
                Toast.makeText(requireContext(), "Entity saved!", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.mapFragment)
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            Log.e("EntityFormFragment", "Error observed: $errorMsg")
            errorMsg?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchCurrentLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    editLat.setText(location.latitude.toString())
                    editLon.setText(location.longitude.toString())
                } else {
                    Toast.makeText(requireContext(), "Could not get location.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
