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
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController // Added import
import com.example.locmark.viewmodel.EntityFormViewModel
import com.example.locmark.viewmodel.MapViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.util.Log // Added import
import androidx.lifecycle.lifecycleScope
import com.example.locmark.model.Entity
import kotlinx.coroutines.launch

class EntityFormFragment : Fragment() {
    private lateinit var editTitle: EditText
    private lateinit var editLat: EditText
    private lateinit var editLon: EditText
    private lateinit var imagePreview: ImageView
    private lateinit var btnSelectImage: Button
    private lateinit var btnSave: Button
    private var imageUri: Uri? = null
    private var lat: Double? = null
    private var lon: Double? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val viewModel: EntityFormViewModel by viewModels()
    private val sharedMapViewModel: MapViewModel by activityViewModels()

    private var currentEntityId: Int = -1 // To store the ID of the entity being edited

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
        editLat = view.findViewById(R.id.editLat)
        editLon = view.findViewById(R.id.editLon)
        imagePreview = view.findViewById(R.id.imagePreview)
        btnSelectImage = view.findViewById(R.id.btnSelectImage)
        btnSave = view.findViewById(R.id.btnSave)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve entityId if editing
        arguments?.let { bundle ->
            currentEntityId = EntityFormFragmentArgs.fromBundle(bundle).entityId
            if (currentEntityId != -1) {
                // Fetch entity details and pre-fill form
                lifecycleScope.launch {
                    val entity = viewModel.getEntityById(currentEntityId)
                    entity?.let {
                        editTitle.setText(it.title)
                        editLat.setText(it.lat.toString())
                        editLon.setText(it.lon.toString())
                        imageUri = Uri.parse(it.imagePath)
                        imagePreview.setImageURI(imageUri)
                    }
                }
            }
        }

        btnSelectImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }
        btnSave.setOnClickListener {
            Log.d("EntityFormFragment", "Save button clicked.") // Added log
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
                // Update existing entity
                viewModel.updateEntity(currentEntityId, title, latValue, lonValue, image)
            } else {
                // Create new entity
                viewModel.createEntity(title, latValue, lonValue, image)
            }
        }
        viewModel.saveResult.observe(viewLifecycleOwner) { success ->
            Log.d("EntityFormFragment", "saveResult observed: $success") // Added log
            if (success == true) {
                Toast.makeText(requireContext(), "Entity saved!", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.mapFragment)
            }
        }
        viewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            Log.e("EntityFormFragment", "Error observed: $errorMsg") // Added log
            errorMsg?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }
}

