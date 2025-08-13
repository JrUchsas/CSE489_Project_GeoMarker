package com.example.geomarker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.geomarker.adapter.EntityListAdapter
import com.example.geomarker.model.Entity
import com.example.geomarker.viewmodel.MapViewModel

class EntityListFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: EntityListAdapter
    private val viewModel: MapViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_entity_list, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.mapFragment)
            }
        })

        val onEditClick: (Entity) -> Unit = { entity ->
            val action = EntityListFragmentDirections.actionEntityListFragmentToEntityFormFragment(entity.id)
            findNavController().navigate(action)
        }

        val onDeleteClick: (Entity) -> Unit = { entity ->
            viewModel.deleteEntity(entity.id)
        }

        adapter = EntityListAdapter(onEditClick, onDeleteClick)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        viewModel.entities.observe(viewLifecycleOwner) { entities ->
            adapter.submitList(entities)
        }
    }
}
