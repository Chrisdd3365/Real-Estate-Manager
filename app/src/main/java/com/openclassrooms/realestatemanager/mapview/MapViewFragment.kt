package com.openclassrooms.realestatemanager.mapview

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentMapViewBinding

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

            // TODO : Handle position permission requests
            //            googleMap?.isMyLocationEnabled = true // TODO

            googleMap?.addMarker(
                MarkerOptions()
                    .position(LatLng(0.0, 0.0))
                    .title("Test")
            )

            // TODO : Animate camera on current user position
            googleMap?.animateCamera(
                CameraUpdateFactory.newCameraPosition(
                    CameraPosition.Builder().target(LatLng(0.0, 0.0)).zoom(12f)
                        .build()
                )
            )
            mapView?.onResume()
        }

        return binding.root
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

        fun newInstance() : MapViewFragment = MapViewFragment()
    }
}