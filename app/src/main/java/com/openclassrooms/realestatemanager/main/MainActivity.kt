package com.openclassrooms.realestatemanager.main

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
import com.openclassrooms.realestatemanager.BaseActivity
import com.openclassrooms.realestatemanager.DatabaseManager
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.agent_creation.AgentCreationActivity
import com.openclassrooms.realestatemanager.databinding.ActivityMainBinding
import com.openclassrooms.realestatemanager.estate_creation.EstateCreationActivity
import com.openclassrooms.realestatemanager.loaning_simulator.LoaningSimulatorActivity
import com.openclassrooms.realestatemanager.mapview.MapViewFragment
import com.openclassrooms.realestatemanager.model.Estate
import com.openclassrooms.realestatemanager.properties_list.PropertiesListFragment
import com.openclassrooms.realestatemanager.utils.SharedPreferencesManager
import com.openclassrooms.realestatemanager.utils.StaticData
import com.openclassrooms.realestatemanager.utils.Utils

// TODO : Add a splash screen before this activity

/**
 *  This [AppCompatActivity] handles the main view, with 2 tabs : a list of properties, and a map.
 *  It links a [ViewPager] with a [TabLayout] : Every time we swipe the [ViewPager], it also changes
 *  the tab name on [TabLayout], and reciprocally.
 */
class MainActivity : BaseActivity() {

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
    private var orientation = Configuration.ORIENTATION_UNDEFINED
    private var isAskingForPermissions = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        permissionRequestLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {
            if (!it) {
                Utils.showPermissionsDeniedDialog(
                    this,
                    getString(R.string.location_permissions_not_granted_dialog),
                    goToSettings = {
                        isAskingForPermissions = true
                    },
                    reAskPermissions = {
                        checkAndAskLocationPermissions()
                    }
                )
            }
        }

        if (savedInstanceState != null) {
            val savedEstates = savedInstanceState.getSerializable("test") as ArrayList<*>
            if (savedEstates.isNotEmpty())
                estateList.clear()
            for (savedEstate in savedEstates) {
                estateList.add(savedEstate as Estate)
            }
        }

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
            getUserLastPosition()
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

        // Setup orientation
        orientation = resources.configuration.orientation

        // Setup estate list
        if (savedInstanceState == null) {
            setupChildFragments()
            DatabaseManager(this).getEstates({
                estateList = it
                propertiesListFragment?.setEstateList(it)
                viewModel.setFragments()
                mapViewFragment?.updateEstates(it)
            }, {
                Toast.makeText(this, getString(R.string.dumb_error), Toast.LENGTH_LONG)
                    .show()
            })
        } else {
            setupChildFragments()
        }
    }

    private fun setupChildFragments() {
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
                tab.setIcon(R.drawable.ic_list)
            else
                tab.setIcon(R.drawable.ic_building_map)
        }.attach()

        viewPager?.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                // This disable swipe when the user is on the MapView, in order to allow him to
                //  navigate through the map, or when the user is in landscape mode, for smoother
                //  navigation.
                viewPager?.isUserInputEnabled =
                    (position == 0 && orientation != Configuration.ORIENTATION_LANDSCAPE)
            }
        })

        viewModel.setFragments()
//        propertiesListFragment?.setEstateList(estateList)
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
                    launchLoaningSimulator()
                }
                R.id.nav_generate_estates -> {
                    Log.d(TAG, "Generate estates !")
                    generateTestingEstates()
                }
                else -> {
                    Log.d(TAG, "OTHER")
                }
            }
            drawerLayout?.closeDrawers()
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
                mapViewFragment?.addNewEstate(resultEstate)
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

    private fun getUserLastPosition() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient?.lastLocation?.addOnSuccessListener {
                if (it != null) {
                    lastKnownPosition = LatLng(it.latitude, it.longitude)
                }
            }
        }
    }

    private fun checkAndAskLocationPermissions() {
        val locationPermission = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        )

        if (locationPermission == PackageManager.PERMISSION_GRANTED) {
            getUserLastPosition()
            return
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
        propertiesListFragment?.unitChanged()
//        mapViewFragment?.unitChanged()
    }

    private fun launchLoaningSimulator() {
        startActivity(LoaningSimulatorActivity.newInstance(this))
    }

    private fun generateTestingEstates() {
        estateList.addAll(StaticData.staticEstatesList)
        propertiesListFragment?.addTestingEstates(StaticData.staticEstatesList)
    }

    override fun deleteEstate(estateToDelete : Estate) {
        DatabaseManager(this).deleteEstate(
            estateToDelete.id!!,
            onSuccess = {
                val index = estateList.indexOf(estateToDelete)
                estateList.removeAt(index)
                propertiesListFragment?.removeEstateAtPosition(index)
            },
            onFailure = {
                Toast.makeText(this, getString(R.string.dumb_error), Toast.LENGTH_LONG)
                    .show()
            }
        )
    }

    fun filterEstates(priceRange: IntArray, surfaceRange: IntArray, roomsRange: IntArray,
                      bathroomsRange: IntArray, bedroomsRange: IntArray, schoolValue: Boolean,
                      playgroundValue: Boolean, shopValue: Boolean, busesValue: Boolean,
                      subwayValue: Boolean, parkValue: Boolean, fromDate: Long, sold: Boolean) {
        DatabaseManager(this).filterEstates(
            priceRange, surfaceRange, roomsRange, bathroomsRange, bedroomsRange, schoolValue,
            playgroundValue, shopValue, busesValue, subwayValue, parkValue, fromDate, sold,
            onSuccess = {
                propertiesListFragment?.displaySearchResults(it)
            },
            onFailure = {

            }
        )
    }

    override fun handleCompleteEstateCreationCancelled(estateToEdit: Estate) {
        estateClicked(estateToEdit)
    }

    override fun estateSoldStateChanged(estateToEdit: Estate) {
        val editedIndex = estateList.indexOf(estateToEdit)
        if (editedIndex != -1) {
            estateList[editedIndex] = estateToEdit
            propertiesListFragment?.editEstateAtPosition(editedIndex, estateToEdit)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        orientation = newConfig.orientation
        propertiesListFragment?.setupOrientation(orientation)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (actionBarDrawerToggle?.onOptionsItemSelected(item) == true) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable("test", estateList)
        super.onSaveInstanceState(outState)
    }

    override fun onResume() {
        setupChildFragments()
        super.onResume()
        if (isAskingForPermissions) {
            isAskingForPermissions = false
            checkAndAskLocationPermissions()
        }
    }

    companion object {

        @Suppress("unused")
        private const val TAG = "MainActivity"

        private const val TAG_ESTATE = "estate"
        private const val TAG_NEW_ESTATE = "is_new_estate"
        private const val TAG_TO_DELETE = "to_delete"
    }
}