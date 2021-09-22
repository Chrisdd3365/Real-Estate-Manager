package com.openclassrooms.realestatemanager.estate_creation.managing_details

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.AgentsListItemBinding
import com.openclassrooms.realestatemanager.model.Agent

class AgentsListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val agents = ArrayList<Agent>()

    fun setItems(items : ArrayList<Agent>) {
        agents.clear()
        agents.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AgentsListItemViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.agents_list_item,
                null,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? AgentsListItemViewHolder)?.setData(agents[position])
    }

    override fun getItemCount(): Int = agents.size

    inner class AgentsListItemViewHolder(private val binding : AgentsListItemBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun setData(agent: Agent) {
            binding.viewModel = AgentsListItemViewModel()
            binding.viewModel?.setData(agent)
        }
    }

    inner class AgentsListItemViewModel {

        val agentName = ObservableField("")

        fun setData(agent : Agent) {
            var name = agent.firstName
            if (!name.isNullOrBlank())
                name += " "
            name += agent.lastName
            agentName.set(name)
        }
    }
}