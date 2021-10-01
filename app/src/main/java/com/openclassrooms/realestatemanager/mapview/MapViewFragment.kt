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
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentMapViewBinding
import com.openclassrooms.realestatemanager.main.MainActivity
import com.openclassrooms.realestatemanager.model.Estate

/**
 *  Handles MapView using Google Maps API.
 *  Note that if you are testing this app on an emulator, the OpenGL API level should be set on 2.0.
 *  @see <a href="https://stackoverflow.com/a/48421364/8286029">StackOverflow answer</a>
 */
class MapViewFragment(private val estatesList: ArrayList<Estate>) : Fragment() {

    // Helper classes
    private val viewModel = MapViewFragmentViewModel()
    private var googleMap : GoogleMap? = null

    // Layout variables
    private var mapView : MapView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DataBindingUtil.inflate<FragmentMapViewBinding>(
            inflater, R.layout.fragment_map_view, container, false
        )
        binding.viewModel = viewModel

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

            (activity as? MainActivity)?.getUserLastPosition {
                googleMap?.animateCamera(
                    CameraUpdateFactory.newCameraPosition(
                        CameraPosition.Builder().target(it).zoom(20f)
                            .build()
                    )
                )
            }

            // TODO : Animate camera on current user position
            mapView?.onResume()
        }

        return binding.root
    }

    private fun addMarkers() {

        for (estate : Estate in estatesList) {
            // TODO
        }

        googleMap?.addMarker(
            MarkerOptions()
                .position(LatLng(0.0, 0.0))
                .title("Test")
        )
    }

    fun updateEstates(estatesList: ArrayList<Estate>) {
        this.estatesList.clear()
        this.estatesList.addAll(estatesList)
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

        private const val TAG = "MapViewFragment"

        fun newInstance(estatesList : ArrayList<Estate>): MapViewFragment {
            return MapViewFragment(estatesList)
        }
    }
}