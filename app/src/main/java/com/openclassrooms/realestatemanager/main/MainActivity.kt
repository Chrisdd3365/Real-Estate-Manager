package com.openclassrooms.realestatemanager.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.openclassrooms.realestatemanager.DatabaseManager
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.agent_creation.AgentCreationActivity
import com.openclassrooms.realestatemanager.databinding.ActivityMainBinding
import com.openclassrooms.realestatemanager.estate_creation.EstateCreationActivity
import com.openclassrooms.realestatemanager.mapview.MapViewFragment
import com.openclassrooms.realestatemanager.model.Estate
import com.openclassrooms.realestatemanager.properties_list.PropertiesListFragment
import com.openclassrooms.realestatemanager.utils.StaticData

// TODO : Add a splash screen before this activity

/**
 *  This [AppCompatActivity] handles the main view, with 2 tabs : a list of properties, and a map.
 *  It links a [ViewPager] with a [TabLayout] : Every time we swipe the [ViewPager], it also changes
 *  the tab name on [TabLayout], and reciprocally.
 */
class MainActivity : AppCompatActivity() {

    // Helper classes
    private var mainViewPagerAdapter : MainViewPagerAdapter? = null
    private var viewModel = MainActivityViewModel()
    private var resultLauncher : ActivityResultLauncher<Intent>? = null

    // Child fragments
    private var propertiesListFragment : PropertiesListFragment? = null
    private var mapViewFragment : MapViewFragment? = null

    // Layout variables
    private var tabLayout : TabLayout? = null
    private var viewPager : ViewPager2? = null
    private var mainFab : FloatingActionButton? = null
    private var addPropertyFab : FloatingActionButton? = null
    private var addAgentFab : FloatingActionButton? = null

    private var areMiniFabEnabled = false

    private var estateList = ArrayList<Estate>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(
            this, R.layout.activity_main
        )
        binding.viewModel = viewModel

        // Init layout variables
        tabLayout = binding.tabLayout
        viewPager = binding.viewPager
        mainFab = binding.mainFab
        addPropertyFab = binding.addPropertyFab
        addAgentFab = binding.addAgentFab

        viewModel.setLoading()

        // Init child fragments
        propertiesListFragment = PropertiesListFragment.newInstance(estateList)
        mapViewFragment = MapViewFragment.newInstance()

        // Create adapter
        mainViewPagerAdapter = MainViewPagerAdapter(this, propertiesListFragment!!,
            mapViewFragment!!)

        // Configure [ViewPager]
        viewPager?.adapter = mainViewPagerAdapter

        // Configure [TabLayout]
        TabLayoutMediator(tabLayout!!, viewPager!!) { tab, position ->
            if (position == 0)
                tab.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_list, theme)
            else
                tab.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_building_map, theme)
        }.attach()

        resultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            result ->
            if (result.resultCode == Activity.RESULT_OK) handleEstateChanges(result.data)
        }

        // Setup buttons
        mainFab?.setOnClickListener {
            areMiniFabEnabled = if (areMiniFabEnabled) {
                viewModel.setMiniFabVisibility(false)
                false
            } else {
                viewModel.setMiniFabVisibility(true)
                true
            }
        }

        addPropertyFab?.setOnClickListener {
            resultLauncher?.launch(EstateCreationActivity.newInstance(this, null, true))
        }

        addAgentFab?.setOnClickListener {
            startActivity(AgentCreationActivity.newInstance(this))
        }

        // Setup estate list
        DatabaseManager(this).getEstates({
            // TODO : If the list is empty, show "No items" messages instead
            estateList = if (it.isEmpty()) getStaticEstateList() else it
            propertiesListFragment?.setEstateList(it)
            viewModel.setFragments()
        }, {

        })
    }

    private fun handleEstateChanges(resultIntent : Intent?) {
        if (resultIntent == null)
            return
        val toDelete = resultIntent.getBooleanExtra(TAG_TO_DELETE, false)
        val isNewEstate = resultIntent.getBooleanExtra(TAG_NEW_ESTATE, false)
        val resultEstate = resultIntent.extras?.get(TAG_ESTATE) as? Estate

        if (toDelete && resultEstate != null) {
            val index = estateList.indexOf(resultEstate)
            if (index != -1) {
                propertiesListFragment?.removeEstateAtPosition(index)
                estateList.remove(resultEstate)
            }
        } else if (resultEstate != null) {
            if (isNewEstate) {
                estateList.add(0, resultEstate)
                propertiesListFragment?.addNewEstate(resultEstate)
            } else {
                val editedIndex = estateList.indexOf(resultEstate)
                if (editedIndex != -1) {
                    estateList[editedIndex] = resultEstate
                    propertiesListFragment?.editEstateAtPosition(editedIndex, resultEstate)
                }
            }
        }
    }

    fun estateClicked(estateClicked : Estate) {
        resultLauncher?.launch(EstateCreationActivity.newInstance(this, estateClicked, false))
    }

    private fun getStaticEstateList() : ArrayList<Estate> {
        return StaticData.staticEstatesList
    }

    companion object {

        @Suppress("unused")
        private const val TAG = "MainActivity"

        private const val TAG_ESTATE = "estate"
        private const val TAG_NEW_ESTATE = "is_new_estate"
        private const val TAG_TO_DELETE = "to_delete"
    }
}