package com.sazerotwo.rewiredfilament

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import com.mapbox.geojson.Feature
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource

class MapActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var map: MapboxMap
    private val LOCATION_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(
            this,
            "pk.eyJ1Ijoic2FuY2hlemVnaWRvIiwiYSI6ImNqdGthcTIzajA5aWE0YXEzaXE0aWxteDgifQ.kSRTDPSqTZv3jGahAnF66w"
        )
        setContentView(R.layout.activity_map)

        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)

        checkPermissions()
    }

    public override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    public override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    public override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    public override fun onStop() {
        mapView.onStop()
        super.onStop()
    }

    override fun onLowMemory() {
        mapView.onLowMemory()
        super.onLowMemory()
    }

    override fun onDestroy() {
        mapView.onDestroy()
        super.onDestroy()
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_REQUEST_CODE
            )
        } else {
            loadMap()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (permissions.isNotEmpty() && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadMap()
            } else {
                showAlert("Necesitas dar permiso de ubicación para poder localizarte en el mapa")
            }
        }
    }

    private fun loadMap() {
        mapView.getMapAsync { mapboxMap ->
            mapboxMap.setStyle(Style.MAPBOX_STREETS) {
                map = mapboxMap
                enableLocationComponent()
                loadMarkers()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableLocationComponent() {
        // Get an instance of the component
        val locationComponent = map.locationComponent

        // Activate with options
        locationComponent.activateLocationComponent(this, map.style!!)

        // Enable to make component visible
        locationComponent.isLocationComponentEnabled = true

        // Set the component's camera mode
        locationComponent.cameraMode = CameraMode.TRACKING

        // Set the component's render mode
        locationComponent.renderMode = RenderMode.COMPASS

        locationComponent.isLocationComponentEnabled = true
    }

    private fun loadMarkers() {
        map.style?.addImage(
            "marker-icon-id",
            BitmapFactory.decodeResource(
                resources, R.drawable.mapbox_marker_icon_default
            )
        )

        val geoJsonSource = GeoJsonSource(
            "source-id", Feature.fromGeometry(
                Point.fromLngLat(-5.663812, 40.965075)
            )
        )
        map.style?.addSource(geoJsonSource)

        val symbolLayer = SymbolLayer("layer-id", "source-id")
        symbolLayer.withProperties(
            PropertyFactory.iconImage("marker-icon-id"),
            PropertyFactory.textField("hola")
        )
        map.style?.addLayer(symbolLayer)
    }

    private fun showAlert(msg: String) {
        val alertBuilder = AlertDialog.Builder(this)
            .setTitle("OJO")
            .setMessage(msg)
            .setPositiveButton(
                android.R.string.ok
            ) { dialog, _ -> dialog?.dismiss() }

        alertBuilder.create().show()
    }
}
