package com.openclassrooms.realestatemanager.mapview

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentMapViewBinding
import com.openclassrooms.realestatemanager.model.Estate
import com.openclassrooms.realestatemanager.utils.Singleton

/**
 *  Handles MapView using Google Maps API.
 *  Note that if you are testing this app on an emulator, the OpenGL API level should be set on 2.0.
 *  @see <a href="https://stackoverflow.com/a/48421364/8286029">StackOverflow answer</a>
 */
class MapViewFragment : Fragment() {

    // Helper classes
    private val viewModel = MapViewFragmentViewModel()
    private var googleMap : GoogleMap? = null

    // Layout variables
    private var mapView : MapView? = null

    private var estatesList: ArrayList<Estate> = ArrayList()
    private var markers = ArrayList<Marker>()
    private var latLngBoundsBuilder = LatLngBounds.Builder()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DataBindingUtil.inflate<FragmentMapViewBinding>(
            inflater, R.layout.fragment_map_view, container, false
        )
        binding.viewModel = viewModel

        if (arguments != null && requireArguments().containsKey(TAG_ESTATES)) {
            val estatesArgs = requireArguments().getSerializable(TAG_ESTATES) as ArrayList<*>
            for (estateArgs in estatesArgs) {
                estatesList.add(estateArgs as Estate)
            }
        }

        mapView = binding.mapView

        mapView?.onCreate(savedInstanceState)

        try {
            MapsInitializer.initialize(requireContext())
        } catch (exception : Exception) {
            Log.e(TAG, "Error : ${exception.message}")
        }

        mapView?.getMapAsync { map ->
            // Called when map is ready
            googleMap = map

            if (ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                googleMap?.isMyLocationEnabled = true
            }

            addMarkers()

            if (markers.isNotEmpty()) {
                val cameraUpdate =
                    CameraUpdateFactory.newLatLngBounds(latLngBoundsBuilder.build(), PADDING)
                googleMap?.animateCamera(cameraUpdate)
            }
            mapView?.onResume()
        }

        return binding.root
    }

    private fun addMarker(marker: Marker?) {
        if (marker != null) {
            markers.add(marker)
            latLngBoundsBuilder.include(marker.position)
        }
    }

    private fun addMarkers() {

        removeMarkers()

        for (estate : Estate in estatesList) {
            addMarker(createMarker(estate))
        }
    }

    private fun createMarker(estate: Estate) : Marker? {
        var marker : Marker? = null

        val latitude = estate.latitude
        val longitude = estate.longitude
        if (latitude != null && longitude != null) {
            val estateType = context?.resources
                ?.getStringArray(R.array.estate_types)?.get(estate.typeIndex!!)
            marker = googleMap?.addMarker(
                MarkerOptions()
                    .position(LatLng(latitude, longitude))
                    .title("$estateType (${estate.getSurface()} ${Singleton.unitSymbol} " +
                            "at ${estate.getPrice()} ${Singleton.currencySymbol})")
            )
        }
        return marker
    }

    /**
     *  Removes every marker on [GoogleMap].
     */
    private fun removeMarkers() {
        for (marker in markers) {
            marker.remove()
        }
    }

    fun updateEstates(estatesList: ArrayList<Estate>) {
        this.estatesList.clear()
        this.estatesList.addAll(estatesList)
        addMarkers()
    }

    fun addNewEstate(estate : Estate) {
        this.estatesList.add(estate)
        addMarker(createMarker(estate))
    }

    /**
     *  Updates the [Marker]s to set the correct currency on their titles.
     */
    fun currencyChanged() {
        addMarkers()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    companion object {

        @Suppress("unused")
        private const val TAG = "MapViewFragment"

        private const val TAG_ESTATES = "estates"

        private const val PADDING = 25

        fun newInstance(estatesList : ArrayList<Estate>): MapViewFragment {
            val fragment = MapViewFragment()
            val bundle = Bundle().apply {
                putSerializable(TAG_ESTATES, estatesList)
            }
            fragment.arguments = bundle
            return fragment
        }
    }
}