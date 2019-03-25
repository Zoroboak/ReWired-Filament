package com.sazerotwo.rewiredfilament.ui

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mapbox.geojson.Feature
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.geometry.LatLngBounds
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.sazerotwo.rewiredfilament.R
import com.sazerotwo.rewiredfilament.model.MapPoint
import com.sazerotwo.rewiredfilament.network.MapPointProvider

class MapFragment : Fragment() {

    private lateinit var mapView: MapView
    private lateinit var map: MapboxMap
    private val LOCATION_REQUEST_CODE = 1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Mapbox.getInstance(
            context!!,
            getString(R.string.map_token)
        )

        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mapView = view.findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)

        checkPermissions()
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(context!!, android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST_CODE)
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
        mapView.getMapAsync { map ->
            map.setStyle(Style.MAPBOX_STREETS) {
                this.map = map
                enableLocationComponent()
                loadMarkers()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableLocationComponent() {
        val locationComponent = map.locationComponent
        locationComponent.activateLocationComponent(context!!, map.style!!)
        locationComponent.isLocationComponentEnabled = true
        locationComponent.cameraMode = CameraMode.TRACKING
        locationComponent.renderMode = RenderMode.COMPASS
    }

    private fun loadMarkers() {
        var index = 0
        MapPointProvider().getMapPoints { mapPoints ->
            mapPoints.forEach {
                addMarker(it, index++)
            }
            calculateMapBounds(mapPoints)
        }
    }

    private fun calculateMapBounds(mapPoints: List<MapPoint>) {
        val lats = mutableListOf<Double>()
        val lngs = mutableListOf<Double>()
        mapPoints.forEach {
            lats.add(it.lat)
            lngs.add(it.lng)
        }
        val maxLat = lats.max()!!
        val minLat = lats.min()!!
        val maxLng = lngs.max()!!
        val minLng = lngs.min()!!

        val latLngBounds = LatLngBounds.Builder()
            .include(LatLng(maxLat, maxLng))
            .include(LatLng(minLat, minLng))
            .build()
        map.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 200), 1000)
    }

    private fun addMarker(mapPoint: MapPoint, index: Int) {
        map.style?.addImage(
            "marker-icon-id-$index",
            BitmapFactory.decodeResource(
                resources, R.drawable.mapbox_marker_icon_default
            )
        )

        val geoJsonSource = GeoJsonSource(
            "source-id-$index", Feature.fromGeometry(
                Point.fromLngLat(mapPoint.lng, mapPoint.lat)
            )
        )
        map.style?.addSource(geoJsonSource)

        val symbolLayer = SymbolLayer("layer-id-$index", "source-id-$index")
        symbolLayer.withProperties(
            PropertyFactory.iconImage("marker-icon-id-$index"),
            PropertyFactory.textField(mapPoint.name)
        )
        map.style?.addLayer(symbolLayer)
    }

    private fun showAlert(msg: String) {
        val alertBuilder = AlertDialog.Builder(context!!)
            .setTitle("OJO")
            .setMessage(msg)
            .setPositiveButton(
                android.R.string.ok
            ) { dialog, _ -> dialog?.dismiss() }

        alertBuilder.create().show()
    }
}
