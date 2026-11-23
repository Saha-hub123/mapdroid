package org.mapdroid

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.maplibre.android.MapLibre
import org.maplibre.android.annotations.Marker
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.location.LocationComponent
import org.maplibre.android.location.LocationComponentActivationOptions
import org.maplibre.android.location.modes.CameraMode
import org.maplibre.android.location.modes.RenderMode
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style

class MainActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private var mapLibreMap: MapLibreMap? = null
    private var currentMarker: Marker? = null
    private var locationComponent: LocationComponent? = null

    private lateinit var tvCurrentStyle: TextView
    private lateinit var tvCoordinates: TextView
    private lateinit var btnStyle: MaterialButton
    private lateinit var fabZoomIn: FloatingActionButton
    private lateinit var fabZoomOut: FloatingActionButton
    private lateinit var fabMyLocation: FloatingActionButton

    data class StyleItem(val name: String, val url: String)

    private val availableStyles = listOf(
        StyleItem("MapLibre Demo", "https://demotiles.maplibre.org/style.json"),
        StyleItem("OSM Bright", "https://tiles.openfreemap.org/styles/bright"),
        StyleItem("OSM Liberty", "https://tiles.openfreemap.org/styles/liberty"),
        StyleItem("OSM Positron", "https://tiles.openfreemap.org/styles/positron")
    )
    private var currentStyleIndex = 0

    companion object {
        private const val PERMISSION_REQUEST_LOCATION = 1001
        private val INDONESIA_CENTER = LatLng(-6.2088, 106.8456)
        private const val DEFAULT_ZOOM = 9.0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MapLibre.getInstance(this)
        setContentView(R.layout.activity_main)

        mapView = findViewById(R.id.mapView)
        tvCurrentStyle = findViewById(R.id.tvCurrentStyle)
        tvCoordinates = findViewById(R.id.tvCoordinates)
        btnStyle = findViewById(R.id.btnStyle)
        fabZoomIn = findViewById(R.id.fabZoomIn)
        fabZoomOut = findViewById(R.id.fabZoomOut)
        fabMyLocation = findViewById(R.id.fabMyLocation)

        mapView.onCreate(savedInstanceState)

        setupControls()

        mapView.getMapAsync { map ->
            mapLibreMap = map
            setupMap()
        }
    }

    private fun setupControls() {
        fabZoomIn.setOnClickListener {
            mapLibreMap?.animateCamera(CameraUpdateFactory.zoomIn())
        }

        fabZoomOut.setOnClickListener {
            mapLibreMap?.animateCamera(CameraUpdateFactory.zoomOut())
        }

        fabMyLocation.setOnClickListener {
            enableUserLocation()
        }

        btnStyle.setOnClickListener {
            switchMapStyle()
        }
    }

    private fun setupMap() {
        val currentStyle = availableStyles[currentStyleIndex]
        loadStyle(currentStyle)

        val initialPosition = CameraPosition.Builder()
            .target(INDONESIA_CENTER)
            .zoom(DEFAULT_ZOOM)
            .build()
        mapLibreMap?.cameraPosition = initialPosition

        mapLibreMap?.addOnMapClickListener { point ->
            onMapClicked(point)
            true
        }
    }

    private fun loadStyle(styleItem: StyleItem) {
        tvCurrentStyle.text = "Style: ${styleItem.name}"
        mapLibreMap?.setStyle(Style.Builder().fromUri(styleItem.url)) { style ->
            if (hasLocationPermission()) {
                activateLocation(style)
            }
        }
    }

    private fun switchMapStyle() {
        currentStyleIndex = (currentStyleIndex + 1) % availableStyles.size
        val nextStyle = availableStyles[currentStyleIndex]
        loadStyle(nextStyle)
        Toast.makeText(this, "Layer diganti ke: ${nextStyle.name}", Toast.LENGTH_SHORT).show()
    }

    private fun onMapClicked(point: LatLng) {
        currentMarker?.let { mapLibreMap?.removeMarker(it) }

        val marker = mapLibreMap?.addMarker(
            MarkerOptions()
                .position(point)
                .title("Titik Terpilih")
                .snippet("Lat: %.5f, Lng: %.5f".format(point.latitude, point.longitude))
        )
        currentMarker = marker

        tvCoordinates.text = "Lat: %.5f | Lng: %.5f".format(point.latitude, point.longitude)
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun enableUserLocation() {
        if (hasLocationPermission()) {
            mapLibreMap?.getStyle { style ->
                activateLocation(style)
            }
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                PERMISSION_REQUEST_LOCATION
            )
        }
    }

    @SuppressLint("MissingPermission")
    private fun activateLocation(style: Style) {
        try {
            val map = mapLibreMap ?: return
            locationComponent = map.locationComponent
            val activationOptions = LocationComponentActivationOptions.builder(this, style)
                .useDefaultLocationEngine(true)
                .build()

            locationComponent?.activateLocationComponent(activationOptions)
            locationComponent?.isLocationComponentEnabled = true
            locationComponent?.cameraMode = CameraMode.TRACKING
            locationComponent?.renderMode = RenderMode.COMPASS

            locationComponent?.lastKnownLocation?.let { loc ->
                val userLatLng = LatLng(loc.latitude, loc.longitude)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 14.0))
                tvCoordinates.text = "Lokasi Saya: %.5f, %.5f".format(loc.latitude, loc.longitude)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mapLibreMap?.getStyle { activateLocation(it) }
            } else {
                Toast.makeText(this, getString(R.string.permission_required), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }
}
