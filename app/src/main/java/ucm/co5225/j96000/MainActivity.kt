package ucm.co5225.j96000

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import ucm.co5225.j96000.databinding.ActivityMainBinding
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class MainActivity : AppCompatActivity() {
    var baseCurrencyOne = "GBP"
    var baseCurrencyTwo = "USD"
    var conversionRate = 0f
    var apiKey = "325b56c003ec0e19ce02de94"
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        spinnerSetup()
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
                            val apiResult = URL(API).readText()
                            val jsonObject = JSONObject(apiResult)

                            conversionRate = jsonObject.getString("conversion_rate").toFloat()

                            if (conversionRate != null) {
                                binding.textView2.text = conversionRate.toString()
                            }
                            Log.d("Main", "$conversionRate")
                            Log.d("Main", apiResult)

                            withContext(Dispatchers.Main) {
                                val text = ((binding.editTextConversionFrom.text.toString()
                                    .toFloat()) * conversionRate).toString()
                                runOnUiThread {
                                    binding.editTextConversionTo?.setText(text)
                                }
                            }
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
        binding.editTextConversionTo.setText("")
        binding.editTextConversionFrom.setText("")
    }
    private fun spinnerSetup() {
        val spinnerFrom: Spinner = findViewById(R.id.spinnerConversionFrom)
        val spinnerTo: Spinner = findViewById(R.id.spinnerConversionTo)

        ArrayAdapter.createFromResource(this, R.array.CurrenciesOne, android.R.layout.simple_spinner_item).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerFrom.adapter = adapter
        }
        ArrayAdapter.createFromResource(this, R.array.CurrenciesTwo, android.R.layout.simple_spinner_item).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerTo.adapter = adapter
        }
        spinnerFrom.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                baseCurrencyOne = parent?.getItemAtPosition(position).toString()
                Log.d("Main", baseCurrencyOne)
            }
        })
        spinnerTo.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                baseCurrencyTwo = parent?.getItemAtPosition(position).toString()
                Log.d("Main", baseCurrencyTwo)
            }
        })
    }
}



