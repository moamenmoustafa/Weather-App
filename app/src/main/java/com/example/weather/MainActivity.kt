package com.example.weather

import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.Manifest
import android.content.pm.PackageManager
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
class MainActivity : AppCompatActivity() {
   lateinit var fustedLocationClient : FusedLocationProviderClient
   val api : String = "ef67d9bee51c2d32d7c360c89406d763"

   override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
       fustedLocationClient = LocationServices.getFusedLocationProviderClient(this)
       if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
           ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
       }else{
                getLocation()
       }


    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun getLocation(){
        fustedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null){
                val lat = location.latitude
                val long = location.longitude
                weatherTask(lat, long).execute()
            }
        }
    }
    inner class weatherTask(private val lat: Double, private val lon: Double) : AsyncTask<String , Void , String>(){
        override fun onPreExecute() {
            super.onPreExecute()
        findViewById<ProgressBar>(R.id.loader).visibility = View.VISIBLE
        findViewById<RelativeLayout>(R.id.main).visibility= View.GONE
        findViewById<TextView>(R.id.errorText).visibility =View.GONE

        }

        override fun doInBackground(vararg params: String?): String? {
            return try {
                URL("https://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$lon&units=metric&appid=$api")
                    .readText(Charsets.UTF_8)
            } catch (e: Exception) {
                null
            }
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
           try {
               val jsonObject = JSONObject(result)
               val main = jsonObject.getJSONObject("main")
               val sys = jsonObject.getJSONObject("sys")
               val wind = jsonObject.getJSONObject("wind")
               val weather = jsonObject.getJSONArray("weather").getJSONObject(0)

               val updateAt : Long = jsonObject.getLong("dt")
               val updatedAtText = "Updated at : "+ SimpleDateFormat("dd/MM/yyyy hh:mm a").format(Date(updateAt * 1000))

               val temp = main.getString("temp")
               val minTemp = main.getString("temp_min") + "°C"
               val maxTemp = main.getString("temp_max") + "°C"

               val pressure = main.getString("pressure")
               val humidity = main.getString("humidity")

               val sunrise : Long = sys.getLong("sunrise")
               val sunset : Long = sys.getLong("sunset")

               val weatherDescription = weather.getString("description")
               val windSpeed = wind.getString("speed")

               val address = jsonObject.getString("name")+", "+sys.getString("country")


               findViewById<TextView>(R.id.address).text = address
               findViewById<TextView>(R.id.updata_at).text= updatedAtText
               findViewById<TextView>(R.id.status).text = weatherDescription.capitalize()
               findViewById<TextView>(R.id.temp).text = temp
               findViewById<TextView>(R.id.minTemp).text = minTemp
               findViewById<TextView>(R.id.maxTemp).text = maxTemp
               findViewById<TextView>(R.id.sunrise).text = SimpleDateFormat("hh:mm a" , Locale.ENGLISH).format(Date(sunrise * 1000))
               findViewById<TextView>(R.id.sunset).text = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunset*1000))
               findViewById<TextView>(R.id.wind).text = windSpeed
               findViewById<TextView>(R.id.pressure).text = pressure
               findViewById<TextView>(R.id.humidity).text = humidity

               findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
               findViewById<RelativeLayout>(R.id.main).visibility = View.VISIBLE
           }catch (e :Exception){
               findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
               findViewById<TextView>(R.id.errorText).visibility = View.VISIBLE
           }
        }

    }
}