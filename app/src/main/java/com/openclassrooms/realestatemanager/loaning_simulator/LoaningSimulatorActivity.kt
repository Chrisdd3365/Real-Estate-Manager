package com.openclassrooms.realestatemanager.loaning_simulator

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.material.slider.Slider
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.ActivityLoaningSimulatorBinding
import com.openclassrooms.realestatemanager.utils.Utils

class LoaningSimulatorActivity : AppCompatActivity() {

    // Helper classes
    private val viewModel = LoaningSimulatorActivityViewModel()

    // Layout variables
    private var mortgageAmountEt : EditText? = null
    private var bringAmountEt : EditText? = null
    private var mortgageDurationEt : EditText? = null
    private var interestRateSlider : Slider? = null

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            calculateResults()
        }

        override fun afterTextChanged(s: Editable?) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil.setContentView<ActivityLoaningSimulatorBinding>(
            this, R.layout.activity_loaning_simulator
        )

        binding.viewModel = viewModel

        // Setup toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Init layout variables
        mortgageAmountEt = binding.mortgageAmountEditText
        bringAmountEt = binding.bringAmountEditText
        mortgageDurationEt = binding.mortgageDurationEditText
        interestRateSlider = binding.interestRateSlider

        viewModel.hideResults()

        setupChangesListeners()
    }

    /**
     *  Setups [TextWatcher] on [EditText]s, and a [Slider.OnChangeListener] on the [Slider].
     *  We use the same [TextWatcher] for every [EditText], as we only need to know when its text is
     *  changed.
     *  When one of the value is changed, we call for [calculateResults], which will retrieve each
     *  data we need and perform the calculation.
     */
    private fun setupChangesListeners() {
        mortgageAmountEt?.addTextChangedListener(textWatcher)
        bringAmountEt?.addTextChangedListener(textWatcher)
        mortgageDurationEt?.addTextChangedListener(textWatcher)

        interestRateSlider?.addOnChangeListener { _, _, _ ->
            calculateResults()
        }
    }

    /**
     *  Main loaning calculation.
     *  Retrieve data in [EditText]s and calculate the mortgage total cost, and monthly payments.
     */
    private fun calculateResults() {
        val mortgageAmountText = mortgageAmountEt?.text?.toString()
        val bringAmountText = bringAmountEt?.text?.toString()
        val mortgageDurationText = mortgageDurationEt?.text?.toString()
        val interestRate = interestRateSlider?.value

        if (mortgageAmountText.isNullOrEmpty() || mortgageDurationText.isNullOrEmpty()
            || interestRate == null || mortgageDurationText.toInt() == 0)
                return

        val mortgageAmount = mortgageAmountText.toDouble()
        val bringAmount = if (bringAmountText.isNullOrEmpty()) 0.0 else bringAmountText.toDouble()
        val mortgageDuration = mortgageDurationText.toInt()

        val totalLoaned = mortgageAmount.minus(bringAmount)
        val totalInterests = totalLoaned / 100 * interestRate
        val monthlyPayments = (totalLoaned + totalInterests) / mortgageDuration

        displayResults(totalInterests, monthlyPayments)
    }

    /**
     *  Rounds up the results before displaying it with [viewModel].
     *  @param totalCost [Double] - Total cost of the mortgage
     *  @param monthlyPayments [Double] - The amount of monthly payments
     */
    private fun displayResults(totalCost : Double, monthlyPayments : Double) {

        val roundedTotalCost = Utils.getRoundedBigDecimal(totalCost).toPlainString()
        val roundedMonthlyPayments = Utils.getRoundedBigDecimal(monthlyPayments).toPlainString()

        viewModel.updateTotalCostValue(roundedTotalCost)
        viewModel.updateMonthlyPaymentsValue(roundedMonthlyPayments)
        viewModel.setResults()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            finish()
        return super.onOptionsItemSelected(item)
    }

    companion object {

        fun newInstance(context: Context) : Intent {
            return Intent(context, LoaningSimulatorActivity::class.java)
        }
    }
}