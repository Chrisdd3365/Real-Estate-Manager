package com.openclassrooms.realestatemanager.estate_creation.managing_details

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.DatabaseManager
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentManagingBinding
import com.openclassrooms.realestatemanager.model.Agent

class ManagingFragment(private val agentSelected : (ArrayList<Agent>) -> Unit) : Fragment() {

    // Helper classes
    private val viewModel = ManagingFragmentViewModel()
    private val agentsListAdapter = AgentsListAdapter(
        { notifyEstateCreationActivity() },
        true
    )

    // Layout variables
    private var agentsRv : RecyclerView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        val binding = DataBindingUtil.inflate<FragmentManagingBinding>(
            inflater, R.layout.fragment_managing, container, false
        )
        binding.viewModel = viewModel

        agentsRv = binding.agentsRv
        agentsRv?.layoutManager = LinearLayoutManager(context)
        agentsRv?.adapter = agentsListAdapter

        viewModel.setLoading()
        getAgents()

        return binding.root
    }

    private fun getAgents() {
        DatabaseManager(requireContext()).getAgents(
            {
                if (it.isNotEmpty()) {
                    agentsRv?.post { agentsListAdapter.setItems(it) }
                    viewModel.setAgents()
                } else
                    viewModel.setNoAgents()
            }, {
                Log.e(TAG, "An error occurred.")
                Toast.makeText(context, getText(R.string.dumb_error), Toast.LENGTH_LONG)
                    .show()
                viewModel.setNoAgents()
            }
        )
    }

    private fun notifyEstateCreationActivity() {
        agentSelected.invoke(agentsListAdapter.getSelectedAgents())
    }

    companion object {

        private const val TAG = "ManagingFragment"

        fun newInstance(callback : (ArrayList<Agent>) -> Unit) : ManagingFragment {
            return ManagingFragment(callback)
        }

    }
}