package com.example.taller2_santiagoaviles

import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.os.StrictMode
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.taller2_santiagoaviles.databinding.ActivityMapsBinding
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.tasks.Task
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.util.GeoPoint
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    //location permission
    val locationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
        ActivityResultCallback {
            if(it) {
                Log.i("Permisos", "Hay permiso: " + it.toString())
                locationSettings()
            }
            else{
                Log.i("Permisos", "Permiso denegado")
            }
        }
    )

    //LocationSettings
    val locationSettings = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult(),
        ActivityResultCallback {
            if(it.resultCode == RESULT_OK){
                startLocationUpdates()
            }else{
                Log.i("Permisos - Settings", "GPS is off")
            }
        })

    //location
    val RADIUS_OF_EARTH_KM = 6400
    private lateinit var locationClient : FusedLocationProviderClient
    private lateinit var locationRequest : LocationRequest
    private lateinit var locationCallback : LocationCallback
    private lateinit var UserLoc : Location
    data class JsonLoc(val Lat: Double, val Long : Double, val Time : String)
    private var FirstAnimationLoc = false
    private lateinit var UserLocMarker : Marker
    private lateinit var SearchMarkerGeo : Marker
    private lateinit var LongClickMarker : Marker

    //Sensor de luminosidad
    private lateinit var sensorManager : SensorManager
    private lateinit var lightSensor : Sensor
    private lateinit var lightSensorListener : SensorEventListener


    //Geocoder Limits [Colombia]
    private val lowerLeftLatitude = 1.396967
    private val lowerLeftLongitude = -78.903968
    private val upperRightLatitude = 11.983639
    private val upperRightLongitude = -71.869905

    //OSRM
    private lateinit var roadManager: RoadManager

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //Inicializar gestor OSRM y politica sincornas 'Para pruebas'
        roadManager = OSRMRoadManager(this, null)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        //Inicializar sensor de luminosidad
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        lightSensorListener = object : SensorEventListener{
            override fun onSensorChanged(p0: SensorEvent?) {
                if(mMap != null && p0 != null){
                    if(p0.values[0] < 5000){
                        Log.i("Skin_Change", "Se cambio al tema oscuro")
                            //mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this@MapsActivity,R.raw.night_style)
                    }
                    else{
                        Log.i("Skin_Change", "Se cambio al tema claro")
                        //mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this@MapsActivity,R.raw.light_style))
                    }
                }
            }
            override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
                //Ignorar
            }
        }
        sensorManager.registerListener(lightSensorListener,lightSensor,SensorManager.SENSOR_DELAY_NORMAL)


        //Administracion de localizacion
        locationPermission.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        locationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = createLocationRequest()
        locationCallback = object : LocationCallback(){
            override fun onLocationResult(result: LocationResult) {
                if(result !== null){
                    val location = result.lastLocation
                    if (location != null) {
                        updateWriteLoc(location)
                        //Crear el fragmento del mapa
                        createMapFragment()
                    }
                }
            }
        }

        binding.inputusuario.setOnEditorActionListener { textView, i, keyEvent ->
            if (i == EditorInfo.IME_ACTION_SEND) {
                val location = binding.inputusuario.text.toString()
                val geocoder = Geocoder(this)
                try {
                    val addresses = geocoder.getFromLocationName(location,1,lowerLeftLatitude,lowerLeftLongitude,upperRightLatitude,upperRightLongitude)
                    if (addresses != null) {
                        if (addresses.isNotEmpty()) {
                            val address = addresses[0]
                            val latLng = LatLng(address.latitude, address.longitude)
                            Log.i("Geocoder", "Se encontro la localizacion $latLng")
                            if(!::SearchMarkerGeo.isInitialized){
                                this.SearchMarkerGeo = mMap.addMarker(MarkerOptions().position(latLng))!!
                            }
                            else{
                                this.SearchMarkerGeo.position = latLng
                            }
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                            var distanceUM = distance(UserLoc.latitude,UserLoc.longitude, latLng.latitude, latLng.longitude)
                            if(distanceUM >= 1000){
                                distanceUM /= 1000
                                Toast.makeText(baseContext, "Distance : $distanceUM KM",Toast.LENGTH_LONG).show()
                            }
                            else{
                                Toast.makeText(baseContext, "Distance : $distanceUM M",Toast.LENGTH_LONG).show()
                            }
                        }
                        else {
                            Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.i("Error", "Error: " + e.message)
                }
                return@setOnEditorActionListener true
            }
            false
        }
    }


    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
        sensorManager.registerListener(lightSensorListener,lightSensor,SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        //mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.night_style))
        //Log.i("Style", "Se aplico el diseño por defecto")
        createMarker()
        mMap.setOnMapLongClickListener(object: GoogleMap.OnMapLongClickListener{
            override fun onMapLongClick(p0: LatLng) {

                if(!::LongClickMarker.isInitialized){
                    LongClickMarker = mMap.addMarker(MarkerOptions().position(p0))!!
                }
                else{
                    LongClickMarker.position = p0
                }
                val geocoder = Geocoder(baseContext)
                try{
                    val addresses = geocoder.getFromLocation(p0.latitude,p0.longitude,1)
                    if(addresses != null){
                        if(addresses.isNotEmpty()){
                            val address = addresses[0]
                            LongClickMarker.title = address.getAddressLine(0)
                        }
                        else{
                            LongClickMarker.title = "Your LongClick Event"
                        }
                    }
                }
                catch (e : Exception){
                    e.printStackTrace()
                    Log.i("Error", "Error: " + e.message)
                }
                //Pintar ruta
                val polylineOptions = PolylineOptions().apply{
                    addAll(CreateRouteOSM(UserLoc.latitude,UserLoc.longitude,p0.latitude,p0.longitude))
                    width(5F)
                    color(Color.BLUE)
                }
                mMap.addPolyline(polylineOptions)
                //Toast con distancia
                var distanceUM = distance(UserLoc.latitude,UserLoc.longitude, p0.latitude, p0.longitude)
                if(distanceUM >= 1000){
                    distanceUM /= 1000
                    Toast.makeText(baseContext, "Distance : $distanceUM KM",Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(baseContext, "Distance : $distanceUM M",Toast.LENGTH_LONG).show()
                }
            }

        })
    }


    private fun createMarker(){
        val MarkerI = LatLng(this.UserLoc.latitude,this.UserLoc.longitude)
        this.UserLocMarker = mMap.addMarker(MarkerOptions().position(MarkerI).title("Your Location!"))!!
        mMap.uiSettings.setAllGesturesEnabled(true)
        mMap.uiSettings.isZoomControlsEnabled = true
        if(!this.FirstAnimationLoc){
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(MarkerI, 15F), 1000, null)
            this.FirstAnimationLoc = true
        }
    }

    private fun createMapFragment(){
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    private fun locationSettings(){
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener { locationSettingsResponse ->
            // All location settings are satisfied. The client can initialize
            // location requests here.
            // ...
            startLocationUpdates()
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException){
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    val isr : IntentSenderRequest = IntentSenderRequest.Builder(exception.resolution).build()
                    locationSettings.launch(isr)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }

    private fun updateWriteLoc(it : Location){
        if(!::UserLoc.isInitialized){
            this.UserLoc = it
        }
        else
        {
            var Dist = distance(this.UserLoc.latitude, this.UserLoc.longitude, it.latitude, it.longitude)
            if(Dist >= 30){
                UpdateApp(it)
            }
            this.UserLoc = it
        }
    }

    private fun UpdateApp(it: Location) {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd|HH-mm-ss")
        val date = LocalDateTime.now().format(formatter)
        val register = JsonLoc(it.latitude,it.longitude,date)
        val ListReg = ReadJSON(baseContext,"Locations.json")
        if(ListReg != null){
            addRegisterToFile(baseContext,"Locations.json",register)
        }
        else{
            WriteJson(baseContext,"Locations.json", arrayListOf(register))
        }
    }

    private fun ReadJSON(context: Context, filename: String): List<JsonLoc>? {
        val gson = Gson()
        var JsonLocList: List<JsonLoc>? = null
        try
        {
            val inputStream = context.openFileInput(filename)
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))
            val jsonStr = bufferedReader.use { it.readText() }
            val listType = object : TypeToken<List<JsonLoc>>() {}.type
            JsonLocList = gson.fromJson(jsonStr, listType)
            inputStream.close()
        }
        catch (e : Exception){
            Log.i("Exception", "No se pudo leer el archivo")
            e.printStackTrace()
        }

        return JsonLocList
    }

    private fun WriteJson(context: Context, filename: String, obList : List<JsonLoc>){

        val gson = Gson()
        var jsonStr = gson.toJson(obList)

        try{
            val outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE)
            outputStream.write(jsonStr.toByteArray())
            outputStream.close()
        }
        catch (e:Exception){
            Log.i("Exception", "No se pudo escribir en archivo")
            e.printStackTrace()
        }
    }

    private fun addRegisterToFile(context: Context, filename: String, register: JsonLoc) {
        val regList = ReadJSON(baseContext, "Locations.json")?.toMutableList()
        if (regList != null) {
            regList.add(register)
            WriteJson(baseContext,"Locations.json", regList)
        }
    }


    private fun startLocationUpdates(){
        if( ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            locationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    private fun stopLocationUpdates(){
        locationClient.removeLocationUpdates(locationCallback)
    }

    private fun createLocationRequest() : LocationRequest{
        var locationRequest = LocationRequest.create()
            .setInterval(1000)
            .setFastestInterval(5000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        return locationRequest
    }


    private fun distance(lat1 : Double, long1: Double, lat2:Double, long2:Double) : Double{
        val latDistance = Math.toRadians(lat1 - lat2)
        val lngDistance = Math.toRadians(long1 - long2)
        val a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)+ Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        val result = RADIUS_OF_EARTH_KM * c;
        return result *(1000);
    }

    private fun CreateRouteOSM(lat1 : Double, long1: Double, lat2:Double, long2:Double): List<LatLng> {
        val RoutePoints = ArrayList<GeoPoint>()
        RoutePoints.add(GeoPoint(lat1,long1))
        RoutePoints.add(GeoPoint(lat2,long2))
        val road = roadManager.getRoad(RoutePoints)
        val latLngList = road.routeLow.map{LatLng(it.latitude,it.longitude)}
        return latLngList

    }
}