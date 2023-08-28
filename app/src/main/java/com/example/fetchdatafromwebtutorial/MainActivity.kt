package com.example.fetchdatafromwebtutorial

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.fetchdatafromwebtutorial.databinding.ActivityMainBinding
import com.google.gson.Gson
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import org.json.JSONObject
import java.math.RoundingMode
import kotlin.math.round

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fetchCurrencyData().start()
    }

    private fun colorChange(key: String, value: Double) {

        if (key == "uv") {
            if (value < 3) {
                binding.UVValue.setBackgroundResource(R.drawable.rounded_square_green)
            } else if (value < 6) {
                binding.UVValue.setBackgroundResource(R.drawable.rounded_square_yellow)
            } else if (value < 8) {
                binding.UVValue.setBackgroundResource(R.drawable.rounded_square_orange)
            } else {
                binding.UVValue.setBackgroundResource(R.drawable.rounded_square_red)
            }
        }
        else if (key == "temperature") {
            if (value < 80) {
                binding.tempValue.setBackgroundResource(R.drawable.rounded_square_green)
            } else if (value < 86) {
                binding.tempValue.setBackgroundResource(R.drawable.rounded_square_yellow)
            } else if (value < 90) {
                binding.tempValue.setBackgroundResource(R.drawable.rounded_square_orange)
            } else {
                binding.tempValue.setBackgroundResource(R.drawable.rounded_square_red)
            }
        }
        else if (key == "wave_height") {
            if (value < 3) {
                binding.waveheightValue.setBackgroundResource(R.drawable.rounded_square_green)
            } else if (value < 4) {
                binding.waveheightValue.setBackgroundResource(R.drawable.rounded_square_yellow)
            } else if (value < 5) {
                binding.waveheightValue.setBackgroundResource(R.drawable.rounded_square_orange)
            } else {
                binding.waveheightValue.setBackgroundResource(R.drawable.rounded_square_red)
            }
        }

        else if (key == "wave_period") {
            if (value > 20) {
                binding.wavePeriodValue.setBackgroundResource(R.drawable.rounded_square_green)
            } else if (value > 15) {
                binding.wavePeriodValue.setBackgroundResource(R.drawable.rounded_square_yellow)
            } else if (value < 10) {
                binding.wavePeriodValue.setBackgroundResource(R.drawable.rounded_square_orange)
            } else {
                binding.wavePeriodValue.setBackgroundResource(R.drawable.rounded_square_red)
            }
        }

        else if (key == "score") {
            if (value > 7.5) {
                binding.oCondition.setBackgroundResource(R.drawable.rounded_square_green)
                binding.oCondition.text = "GOOD"
            } else if (value > 5.5) {
                binding.oCondition.setBackgroundResource(R.drawable.rounded_square_yellow)
                binding.oCondition.text = "FAIR"
            } else if (value < 3.5) {
                binding.oCondition.setBackgroundResource(R.drawable.rounded_square_orange)
                binding.oCondition.text = "POOR"
            } else {
                binding.oCondition.setBackgroundResource(R.drawable.rounded_square_red)
                binding.oCondition.text = "DANGER"
            }
        }

    }

    private fun calculateScore(wave_height: Double, wave_period: Double, temp: Double, uv: Double) {
        //Normalizing Data

        val n_wave_height = 4 * ((7 - wave_height) / 7)
        val n_uv = 3 * ((10 - uv) / 10)
        val n_wave_period = 2 * (wave_period / 25)
        val n_temp = 1 * ((100 - (temp - 50)) / (100 - 50))

        val n_total = n_temp + n_uv + n_wave_height + n_wave_period

        var f_total = 0.0

        if (n_total > 0 && n_total < 2) {
            f_total = (n_total / 2) * 2.5
        }
        else if (n_total > 2 && n_total < 5) {
            f_total = 2.5 + ((n_total - 2) /3) * 2.5
        }
        else if (n_total > 5 && n_total < 8) {
            f_total = 5 + ((n_total - 5) / 3) * 2.5
        }
        else if (n_total > 8 && n_total < 10) {
            f_total = 7.5 + ((n_total - 8) / 2) * 2.5
        }
        else {
            binding.score.text = "Error"
            return
        }

        val rounded = f_total.toBigDecimal().setScale(1, RoundingMode.UP).toDouble()

        binding.score.text = rounded.toString()
        colorChange("score", rounded)


    }



    @SuppressLint("SetTextI18n")
    private fun fetchCurrencyData(): Thread {
        return Thread {
            val url =
                URL("https://marine-api.open-meteo.com/v1/marine?latitude=33.25&longitude=-117.5&hourly=wave_height,wave_period")

            val url_weather =
                URL("https://api.open-meteo.com/v1/forecast?latitude=33.23&longitude=-117.5&hourly=temperature_2m&daily=uv_index_max&temperature_unit=fahrenheit&timezone=America%2FLos_Angeles")
            val connection = url.openConnection() as HttpsURLConnection
            val connection_2 = url_weather.openConnection() as HttpsURLConnection

            // Values used to calculate score
            var waveHeight = 0.0
            var uv = 0.0
            var temperature = 0.0
            var wavePeriod = 0.0

            if (connection.responseCode == 200) {
                val inputSystem = connection.inputStream
                val inputStreamReader = InputStreamReader(inputSystem, "UTF-8")
                val jsonResponse =
                    inputStreamReader.readText() // Read the JSON response as a string

                val jsonObject = JSONObject(jsonResponse)

                val waveHeights = jsonObject.getJSONObject("hourly").getJSONArray("wave_height")
                waveHeight = waveHeights.getDouble(0)


                val wavePeriods = jsonObject.getJSONObject("hourly").getJSONArray("wave_period")
                wavePeriod = wavePeriods.getDouble(0)


                runOnUiThread {
                    binding.waveheightValue.text = waveHeight.toString()
                    binding.wavePeriodValue.text = wavePeriod.toString()
                    colorChange("wave_height", waveHeight)
                    colorChange("wave_period", wavePeriod)

                }

                inputStreamReader.close()
                inputSystem.close()

            } else {
                binding.waveheightValue.text = "Failed Connection"
            }

            if (connection_2.responseCode == 200) {
                val inputSystem = connection_2.inputStream
                val inputStreamReader = InputStreamReader(inputSystem, "UTF-8")
                val jsonResponse =
                    inputStreamReader.readText() // Read the JSON response as a string

                val jsonObject = JSONObject(jsonResponse)
                val temperatures = jsonObject.getJSONObject("hourly").getJSONArray("temperature_2m")
                val uvs = jsonObject.getJSONObject("daily").getJSONArray("uv_index_max")


                temperature = temperatures.getDouble(0)
                uv = uvs.getDouble(0)



                runOnUiThread {
                    binding.tempValue.text = temperature.toString()
                    binding.UVValue.text = uv.toString()

                    colorChange("temperature", temperature)
                    colorChange("uv", uv)

                    calculateScore(waveHeight, wavePeriod, temperature, uv)


                }

                inputStreamReader.close()
                inputSystem.close()

            } else {

                binding.tempValue.text = "Failed Connection"
                binding.UVValue.text = "Failed Connection"
            }


        }
    }


}