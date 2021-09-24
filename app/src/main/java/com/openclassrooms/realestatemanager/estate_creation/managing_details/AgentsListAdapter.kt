package com.openclassrooms.realestatemanager.estate_creation.managing_details

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.AgentsListItemBinding
import com.openclassrooms.realestatemanager.model.Agent

class AgentsListAdapter(val changed : () -> Unit, val isSelecting : Boolean)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val agents = ArrayList<Agent>()
    private val selectedAgents = ArrayList<Int>()

    fun setItems(items : ArrayList<Agent>) {
        agents.clear()
        agents.addAll(items)
        notifyDataSetChanged()
    }

    fun getSelectedAgents() : ArrayList<Agent> {
        val result = ArrayList<Agent>()
        for (agent : Agent in agents) {
            if (selectedAgents.contains(agent.id))
                result.add(agent)
        }
        return result
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AgentsListItemViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.agents_list_item,
                parent,
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

        private val viewModel = AgentsListItemViewModel()

        fun setData(agent: Agent) {
            binding.viewModel = viewModel
            viewModel.setData(agent)

            if (agent.avatar != null)
                binding.avatarImageView.setImageBitmap(agent.avatar)
            else
                binding.avatarImageView.setImageResource(R.drawable.ic_profile)

            if (selectedAgents.contains(agent.id))
                viewModel.selectAgent()
            else
                viewModel.unselectAgent()

            binding.managingSwitch.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked)
                    selectedAgents.add(agent.id!!)
                else
                    selectedAgents.remove(agent.id)
                changed.invoke()
            }
        }
    }

    inner class AgentsListItemViewModel {

        val agentName = ObservableField("")
        val agentMail = ObservableField("")
        val agentPhoneNumber = ObservableField("")

        val isManaging = ObservableField(false)

        val switchVisibility = ObservableInt(View.GONE)

        fun setData(agent : Agent) {
            var name = agent.firstName
            if (!name.isNullOrBlank())
                name += " "
            name += agent.lastName
            agentName.set(name)

            agentMail.set(agent.email)
            agentPhoneNumber.set(agent.phoneNumber)

            if (isSelecting)
                switchVisibility.set(View.VISIBLE)
            else
                switchVisibility.set(View.GONE)
        }

        fun selectAgent() {
            isManaging.set(true)
        }

        fun unselectAgent() {
            isManaging.set(false)
        }
    }

    companion object {

        @Suppress("unused")
        private const val TAG = "AgentsListAdapter"
    }
}