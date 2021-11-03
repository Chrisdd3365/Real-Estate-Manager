package com.openclassrooms.realestatemanager.search_dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.appcompat.widget.SwitchCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.google.android.material.slider.RangeSlider
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentFilterDialogBinding
import com.openclassrooms.realestatemanager.utils.CustomDialogInterface
import com.openclassrooms.realestatemanager.utils.Singleton
import java.util.*

class FilterDialogFragment : DialogFragment() {

    var customDialogInterface = object : CustomDialogInterface {
        override fun cancelButtonClicked(dialog : Dialog) {
            dialog.dismiss()
        }

        override fun confirmSearchClicked(
            priceRange: IntArray, surfaceRange: IntArray,
            roomsRange: IntArray, bathroomsRange: IntArray,
            bedroomsRange: IntArray, schoolValue: Boolean,
            playgroundValue: Boolean, shopValue: Boolean,
            busesValue: Boolean, subwayValue: Boolean,
            parkValue: Boolean, fromDate: Long
        ) {

        }

    }

    val viewModel = FilterDialogFragmentViewModel()

    // Layout variables
    private var priceRs : RangeSlider? = null
    private var surfaceRs : RangeSlider? = null
    private var roomsRs : RangeSlider? = null
    private var bathroomsRs : RangeSlider? = null
    private var bedroomsRs : RangeSlider? = null
    private var schoolSwitch : SwitchCompat? = null
    private var playgroundSwitch : SwitchCompat? = null
    private var shopSwitch : SwitchCompat? = null
    private var busesSwitch : SwitchCompat? = null
    private var subwaySwitch : SwitchCompat? = null
    private var parkSwitch : SwitchCompat? = null
    private var onMarketSinceDp : DatePicker? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val dialogBuilder = AlertDialog.Builder(requireContext())
        val binding = DataBindingUtil.inflate<FragmentFilterDialogBinding>(
            layoutInflater, R.layout.fragment_filter_dialog, null, false
        )

        // Init layout variables
        priceRs = binding.priceRangeSlider
        surfaceRs = binding.surfaceRangeSlider
        roomsRs = binding.roomsRangeSlider
        bathroomsRs = binding.bathroomsRangeSlider
        bedroomsRs = binding.bedroomsRangeSlider
        schoolSwitch = binding.schoolSwitch
        playgroundSwitch = binding.playgroundSwitch
        shopSwitch = binding.shopSwitch
        busesSwitch = binding.busesSwitch
        subwaySwitch = binding.subwaySwitch
        parkSwitch = binding.parkSwitch
        onMarketSinceDp = binding.onMarketSinceDatePicker

        onMarketSinceDp?.minDate = Singleton.oldestEstate.time.time
        if (Singleton.oldestEstate == Singleton.mostRecentEstate)
            onMarketSinceDp?.maxDate = Date().time
        else
            onMarketSinceDp?.maxDate = Singleton.mostRecentEstate.time.time

        val calendar = Calendar.getInstance()
        calendar.time = Singleton.oldestEstate.time
        onMarketSinceDp?.init(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH),
            null
        )

        dialogBuilder.setPositiveButton(R.string.button_apply_filters) { _, _ ->
            prepareSearch()
        }

        dialogBuilder.setNegativeButton(R.string.button_cancel) { _, _ ->
            customDialogInterface.cancelButtonClicked(dialog!!)
        }

        dialogBuilder.setView(binding.root)

        return dialogBuilder.create()
    }

    private fun prepareSearch() {
        val priceRange = getRange(priceRs!!)
        val surfaceRange = getRange(surfaceRs!!)
        val roomsRange = getRange(roomsRs!!)
        val bathroomsRange = getRange(bathroomsRs!!)
        val bedroomsRange = getRange(bedroomsRs!!)
        val schoolValue = schoolSwitch?.isChecked!!
        val playgroundValue = playgroundSwitch?.isChecked!!
        val shopValue = shopSwitch?.isChecked!!
        val busesValue = busesSwitch?.isChecked!!
        val subwayValue = subwaySwitch?.isChecked!!
        val parkValue = parkSwitch?.isChecked!!
        val calendar = Calendar.getInstance().apply {
            if (onMarketSinceDp != null) {
                set(Calendar.YEAR, onMarketSinceDp!!.year)
                set(Calendar.MONTH, onMarketSinceDp!!.month)
                set(Calendar.DAY_OF_YEAR, onMarketSinceDp!!.dayOfMonth)
            }
        }

        customDialogInterface.confirmSearchClicked(priceRange, surfaceRange, roomsRange,
            bathroomsRange, bedroomsRange, schoolValue, playgroundValue, shopValue, busesValue,
            subwayValue, parkValue, calendar.timeInMillis)
    }

    private fun getRange(rangeSlider: RangeSlider) : IntArray {
        return intArrayOf(
            rangeSlider.values[0].toInt(),
            rangeSlider.values[1].toInt(),
        )
    }

    companion object {

        @Suppress("unused")
        private const val TAG = "FilterDialogFrag"

        fun newInstance(): FilterDialogFragment {
            return FilterDialogFragment()
        }
    }
}