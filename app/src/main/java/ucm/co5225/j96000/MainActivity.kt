package ucm.co5225.j96000

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.UiThread
import androidx.core.content.ContextCompat
import kotlinx.coroutines.*
import ucm.co5225.j96000.databinding.ActivityMainBinding
import org.json.JSONObject
import java.net.URL

class MainActivity: AppCompatActivity(), View.OnClickListener {
    private var baseCurrencyOne = "btc"
    private var baseCurrencyTwo = "eth"
    private var conversionRateOne = 0f
    private var conversionRateTwo = 0f
    private var conversionRate = 0f
    private val api = "https://api.coingecko.com/api/v3/exchange_rates"
    private lateinit var buttonConvert : Button
    private lateinit var buttonClear : Button
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        buildSpinner(view)
        buttonConvert = binding.convertButton
        buttonConvert.setOnClickListener(this)
        buttonClear = binding.clearButton
        buttonClear.setOnClickListener(this)
    }
    override fun onClick(view: View?) {
        when(view?.id){
            R.id.convertButton->{
                getApiResult(view)
            }
            R.id.clearButton->{
                clearTextView(view)
            }
        }
    }
    private fun getApiResult(view: View) {
        disableButton(buttonConvert)
        val thread = Thread {
            if (binding.editTextConversionFrom != null && binding.editTextConversionFrom.text.isNotEmpty() && binding.editTextConversionFrom.text.isNotBlank()) {
                //This stops the program crashing when no value is selected
                if (baseCurrencyOne == baseCurrencyTwo) {
                    runOnUiThread {
                        binding.textViewWarning.text = "Please select a currency to convert"
                    }
                    //binding.textViewWarning.setText("Please select a currency to convert")
                } else {
                    //attempts to read from the api
                    val apiResult = URL(api).readText()
                    val jsonObject = JSONObject(apiResult)
                    //pulls the conversion rate from the api call object
                    conversionRateOne =
                        jsonObject.getJSONObject("rates").getJSONObject("$baseCurrencyOne")
                            .getString("value").toFloat()
                    conversionRateTwo =
                        jsonObject.getJSONObject("rates").getJSONObject("$baseCurrencyTwo")
                            .getString("value").toFloat()
                    conversionRate = conversionRateOne / conversionRateTwo * 10
                    //Log.d("Main", "$conversionRateOne")
                    //Log.d("Main", "$conversionRateTwo")
                    //Log.d("Main", "$conversionRate")
                    //Log.d("Main", apiResult)
                    //this Coroutine takes the data from the input boss and multiplies it
                    //by the conversion rate and displays inside the output box
                    runOnUiThread() {
                        if (binding.textViewWarning.text.isNotBlank() || binding.textViewWarning.text.isNotEmpty()) {
                            binding.textViewWarning.text = ""
                        }
                        val text = ((binding.editTextConversionFrom.text.toString()
                            .toFloat()) * conversionRate).toString()
                        binding.editTextConversionTo?.setText(text)

                        if (conversionRate != null) {
                            binding.textView2.text = conversionRate.toString()
                        }

                    }
                }
            }
        }
        thread.start()
        Thread.sleep(500)
        enableButton(buttonConvert)
    }
    private fun clearTextView(view: View) {
        //functionality for the clear button
        disableButton(buttonClear)
        binding.editTextConversionTo.setText("")
        binding.editTextConversionFrom.setText("")
        enableButton(buttonClear)
    }
    private fun disableButton(button: Button) {
        button?.isEnabled = false
        button?.setTextColor(ContextCompat.getColor(button.context, R.color.black))
        button?.setBackgroundColor(ContextCompat.getColor(button.context, R.color.teal_200))
    }
    private fun enableButton(button: Button) {
        button?.isEnabled = true
        button?.setTextColor(ContextCompat.getColor(button.context, R.color.white))
        button?.setBackgroundColor(ContextCompat.getColor(button.context, R.color.purple_700))
    }
    private fun buildSpinner(view: View) {
        val spinnerFrom: Spinner = binding.spinnerConversionFrom
        val spinnerTo: Spinner = binding.spinnerConversionTo
        //creates spinners and populates them with arrays stored in strings.xml page
        ArrayAdapter.createFromResource(this, R.array.CurrenciesOne, android.R.layout.simple_spinner_item).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerFrom.adapter = adapter
        }
        ArrayAdapter.createFromResource(this, R.array.CurrenciesTwo, android.R.layout.simple_spinner_item).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerTo.adapter = adapter
        }
        //when selected assigns the baseCurrency variables with the value selected
        spinnerFrom.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                //This was autogenerated and must remain for spinner to function
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                baseCurrencyOne = parent?.getItemAtPosition(position).toString().substring(1, 4).lowercase()
                Log.d("Main", baseCurrencyOne)
            }
        })
        spinnerTo.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                //This was autogenerated and must remain for spinner to function
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                baseCurrencyTwo = parent?.getItemAtPosition(position).toString().substring(1, 4).lowercase()
                Log.d("Main", baseCurrencyTwo)
            }
        })
    }
}