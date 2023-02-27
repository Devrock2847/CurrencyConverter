package ucm.co5225.j96000

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ucm.co5225.j96000.databinding.ActivityMainBinding
import org.json.JSONObject
import java.net.URL

class MainActivity : AppCompatActivity() {
    private var baseCurrencyOne = "GBP"
    private var baseCurrencyTwo = "USD"
    private var conversionRate = 0f
    private var apiKey = "325b56c003ec0e19ce02de94"
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        buildSpinner()
    }
    fun getApiResult(view: View) {
        val thread = Thread {
            var API = "https://v6.exchangerate-api.com/v6/$apiKey/pair/$baseCurrencyOne/$baseCurrencyTwo"
            if (binding.editTextConversionFrom != null && binding.editTextConversionFrom.text.isNotEmpty() && binding.editTextConversionFrom.text.isNotBlank()) {
                //This stops the program crashing when no value is selected
                if (baseCurrencyOne == baseCurrencyTwo) {
                    Toast.makeText(
                        applicationContext,
                        "Please pick a currency to convert",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    GlobalScope.launch(Dispatchers.IO) {
                        try {
                            //attempts to read from the api
                            val apiResult = URL(API).readText()
                            val jsonObject = JSONObject(apiResult)

                            conversionRate = jsonObject.getString("conversion_rate").toFloat()

                            if (conversionRate != null) {
                                binding.textView2.text = conversionRate.toString()
                            }
                            //Log.d("Main", "$conversionRate")
                            //Log.d("Main", apiResult)
                            //this Coroutine takes the data from the input boss and multiplies it
                            //by the conversion rate and displays inside the output box
                            withContext(Dispatchers.Main) {
                                val text = ((binding.editTextConversionFrom.text.toString()
                                    .toFloat()) * conversionRate).toString()
                                runOnUiThread {
                                    binding.editTextConversionTo?.setText(text)
                                }
                            }
                            //errors if there is an issue with the api connection
                        } catch (e: Exception) {
                            Log.e("Main", "$e")
                        }
                    }
                }
            }
        }
        thread.start()
    }
    fun clearTextView(view: View) {
        //functionality for the clear button
        binding.editTextConversionTo.setText("")
        binding.editTextConversionFrom.setText("")
    }
    private fun buildSpinner() {
        val spinnerFrom: Spinner = findViewById(R.id.spinnerConversionFrom)
        val spinnerTo: Spinner = findViewById(R.id.spinnerConversionTo)
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
                baseCurrencyOne = parent?.getItemAtPosition(position).toString()
                Log.d("Main", baseCurrencyOne)
            }
        })
        spinnerTo.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                //This was autogenerated and must remain for spinner to function
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                baseCurrencyTwo = parent?.getItemAtPosition(position).toString()
                Log.d("Main", baseCurrencyTwo)
            }
        })
    }
}



