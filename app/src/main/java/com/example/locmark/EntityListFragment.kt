package com.example.locmark

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.locmark.adapter.EntityListAdapter
import com.example.locmark.viewmodel.MapViewModel

class EntityListFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: EntityListAdapter
    private val viewModel: MapViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_entity_list, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = EntityListAdapter(emptyList())
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        viewModel.entities.observe(viewLifecycleOwner) { entities ->
            adapter.submitList(entities)
        }
        viewModel.fetchEntities()
    }
}
