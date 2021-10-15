package com.openclassrooms.realestatemanager.main

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
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
import com.openclassrooms.realestatemanager.utils.*

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
    private var permissionRequestLauncher : ActivityResultLauncher<String>? = null
    private var fusedLocationClient : FusedLocationProviderClient? = null
    private var actionBarDrawerToggle : ActionBarDrawerToggle? = null

    // Child fragments
    private var propertiesListFragment : PropertiesListFragment? = null
    private var mapViewFragment : MapViewFragment? = null

    // Layout variables
    private var drawerLayout : DrawerLayout? = null
    private var navigationView : NavigationView? = null
    private var tabLayout : TabLayout? = null
    private var viewPager : ViewPager2? = null
    private var mainFab : FloatingActionButton? = null
    private var addPropertyFab : FloatingActionButton? = null
    private var addAgentFab : FloatingActionButton? = null

    private var areMiniFabEnabled = false

    private var estateList = ArrayList<Estate>()
    private var lastKnownPosition : LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get the saved currency
        // TODO : Move this in the splashscreen
        Utils.changeCurrency(this, SharedPreferencesManager.getCurrency(this))
        Utils.changeUnit(this, SharedPreferencesManager.getUnit(this))

        // Setup location service client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(
            this, R.layout.activity_main
        )
        binding.viewModel = viewModel

        // Init layout variables
        drawerLayout = binding.drawerLayout
        navigationView = binding.navigationView
        tabLayout = binding.tabLayout
        viewPager = binding.viewPager
        mainFab = binding.mainFab
        addPropertyFab = binding.addPropertyFab
        addAgentFab = binding.addAgentFab

        viewModel.setLoading()

        // Setup NavigationDrawer
        setupNavigationDrawer()

        // Init child fragments
        propertiesListFragment = PropertiesListFragment.newInstance(estateList)
        mapViewFragment = MapViewFragment.newInstance(estateList)

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

        viewPager?.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                // This disable swipe when the user is on the MapView, in order to allow him to
                //  navigate through the map.
                viewPager?.isUserInputEnabled = (position == 0)
            }
        })

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
            resultLauncher?.launch(
                EstateCreationActivity.newInstance(this, null, true,
                    lastKnownPosition)
            )
        }

        addAgentFab?.setOnClickListener {
            startActivity(AgentCreationActivity.newInstance(this))
        }

        // Ask for location permissions
        checkAndAskLocationPermissions()

        // Setup estate list
        DatabaseManager(this).getEstates({
            // TODO : If the list is empty, show "No items" messages instead
            estateList = if (it.isEmpty()) getStaticEstateList() else it
            propertiesListFragment?.setEstateList(it)
            viewModel.setFragments()
            mapViewFragment?.updateEstates(it)
        }, {
            // TODO
        })
    }

    private fun setupNavigationDrawer() {
        actionBarDrawerToggle = ActionBarDrawerToggle(
            this, drawerLayout, R.string.open_nav_bar, R.string.close_nav_bar
        )
        drawerLayout?.addDrawerListener(actionBarDrawerToggle!!)
        actionBarDrawerToggle?.syncState()

        // Make the Navigation drawer icon always appear on the action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navigationView?.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_switch_units -> {
                    Log.d(TAG, "Switch units !")
                    switchUnit()
                }
                R.id.nav_switch_currency -> {
                    Log.d(TAG, "Switch currency !")
                    switchCurrency()
                }
                R.id.nav_agents -> {
                    Log.d(TAG, "Show agents !")
                }
                R.id.nav_loaning_simulator -> {
                    Log.d(TAG, "Loaning simulator !")
                }
                else -> {
                    Log.d(TAG, "OTHER")
                }
            }
            true
        }
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
                mapViewFragment?.updateEstates(estateList)
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
        resultLauncher?.launch(EstateCreationActivity.newInstance(this, estateClicked,
            false, null))
    }

    private fun getStaticEstateList() : ArrayList<Estate> {
        return StaticData.staticEstatesList
    }

    fun getUserLastPosition(onLastLocationSuccess : ((LatLng) -> Unit)?) {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient?.lastLocation?.addOnSuccessListener {
                lastKnownPosition = LatLng(it.latitude, it.longitude)
                onLastLocationSuccess?.invoke(LatLng(it.latitude, it.longitude))
            }
        }
    }

    private fun checkAndAskLocationPermissions() {
        val locationPermission = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        )

        if (locationPermission == PackageManager.PERMISSION_GRANTED) {
            getUserLastPosition(null)
            return
        }

        permissionRequestLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {
            if (!it) {
                // TODO
            }
        }

        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            permissionRequestLauncher?.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    /**
     *  Changes the app currency display and closes the [DrawerLayout].
     */
    private fun switchCurrency() {
        Utils.switchCurrency(this)
        drawerLayout?.closeDrawer(GravityCompat.START)
        propertiesListFragment?.currencyChanged()
        mapViewFragment?.currencyChanged()
    }

    /**
     *  Changes the app unit display and closes the [DrawerLayout].
     */
    private fun switchUnit() {
        Utils.switchUnits(this)
        drawerLayout?.closeDrawer(GravityCompat.START)
//        propertiesListFragment?.unitChanged()
//        mapViewFragment?.unitChanged()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (actionBarDrawerToggle?.onOptionsItemSelected(item) == true) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {

        @Suppress("unused")
        private const val TAG = "MainActivity"

        private const val TAG_ESTATE = "estate"
        private const val TAG_NEW_ESTATE = "is_new_estate"
        private const val TAG_TO_DELETE = "to_delete"
    }
}