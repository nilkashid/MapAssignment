package com.map.assignment.map.module

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.model.Place
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.maps.internal.PolylineEncoding
import com.google.maps.model.DirectionsResult
import com.google.maps.model.DirectionsRoute
import com.google.maps.model.LatLng
import com.map.assignment.data.DirectionData
import com.map.assignment.R
import com.map.assignment.data.PathDetails
import com.map.assignment.data.PlaceData
import com.map.assignment.data.PlacesDao
import com.map.assignment.map.module.MapConstants.rootNode
import com.map.assignment.map.module.MapConstants.routeInfoNode
import com.map.assignment.map.module.MapExceptions.Companion.insufficientPathDataToSave

class MapProvider(context: Context) : MapProviderImpl, OnMapReadyCallback,
    GoogleMap.OnPolylineClickListener {

    lateinit var googleMap: GoogleMap
    var source: PlaceData? = null
    var destination: PlaceData? = null
    var sourceMarker: Marker? = null
    var destinationMarker: Marker? = null
    var directionsDataList = arrayListOf<DirectionData>()
    var selectedDirectionData: DirectionData? = null
    var previousData: DataSnapshot? = null
    var context: Context
    var valueChangeListener: ValueEventListener? = null
    var placesDao = PlacesDao()
    var mapRepository = MapRepository()

    init {
        this.context = context
        initValueChangeListener()
        var databaseRef = FirebaseDatabase.getInstance().getReference(rootNode)
        databaseRef.addValueEventListener(valueChangeListener!!)
    }

    fun initValueChangeListener() {
        valueChangeListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                previousData = dataSnapshot
            }
        }
    }

    override fun addMapToView(containerId: Int) {

        val mTransaction =
            (context as? AppCompatActivity)?.supportFragmentManager?.beginTransaction()
        val mapFragment = SupportMapFragment.newInstance()
        mTransaction?.add(containerId, mapFragment)
        mTransaction?.commit()

        MapsInitializer.initialize(context)
        mapFragment?.getMapAsync(this)
    }

    override fun removeMap() {

    }

    fun setSourceItem(source: PlaceData) {
        this.source = source
    }

    override fun setSourceItemOnMap(source: Place) {
        var placeData = placesDao.getPlaceData(source)
        setSourceItem(placeData)
        sourceMarker?.remove()
        setupSourceMarker(placeData)
        if (destination != null) {
            handleDirectionDrawing()
        }

    }

    fun setDestinationItem(destination: PlaceData) {
        this.destination = destination
    }

    override fun setDestinationItemOnMap(destination: Place) {
        var placeData = placesDao.getPlaceData(destination)
        setDestinationItem(placeData)
        destinationMarker?.remove()
        setupDestinationMarker(placeData)

        if (source != null) {
            handleDirectionDrawing()
        }
    }


    fun handleDirectionDrawing() {
        removeOlderRouteInfo()
        var directionResult = mapRepository.getDirections(
            source, destination, context.getString(R.string.google_maps_key)
        )
        drawDirectionsOnMap(directionResult)
        zoomToRoute(source!!, destination!!)
    }


    fun drawDirectionsOnMap(directionResult: DirectionsResult) {
        for (route in directionResult.routes) {
            var mapsLatLongs = getPathLatLngFromEncodedPath(route.overviewPolyline.encodedPath)
            var polyline = drawPolyLine(mapsLatLongs)
            var mapLatLngCords = getPathLatLngCords(route.overviewPolyline.encodedPath)
            addDirectionTodirectionList(route, polyline, mapLatLngCords)
        }
    }

    fun addDirectionTodirectionList(
        route: DirectionsRoute?,
        polyline: Polyline?,
        mapLatLngCords: List<PathDetails.PathLatLng>?
    ) {

        var directionData = DirectionData()
        directionData.direction = route
        directionData.polyline = polyline
        directionData.pathLatLng = mapLatLngCords
        directionsDataList.add(directionData)
    }

    fun getPathLatLngFromEncodedPath(encodedPath: String): List<com.google.android.gms.maps.model.LatLng> {
        var pathsLatLong = PolylineEncoding.decode(encodedPath)

        var mapsLatLongs = arrayListOf<com.google.android.gms.maps.model.LatLng>()

        for (latLong in pathsLatLong) mapsLatLongs.add(
            com.google.android.gms.maps.model.LatLng(
                latLong.lat,
                latLong.lng
            )
        )
        return mapsLatLongs
    }

    fun getPolyPathWithCords(pathCords: List<PathDetails.PathLatLng>): List<com.google.android.gms.maps.model.LatLng> {

        var mapsLatLongs = arrayListOf<com.google.android.gms.maps.model.LatLng>()
        for (latLong in pathCords) mapsLatLongs.add(
            com.google.android.gms.maps.model.LatLng(
                latLong.latitude!!,
                latLong.longitude!!
            )
        )
        return mapsLatLongs
    }

    fun getPathLatLngCords(encodedPath: String): List<PathDetails.PathLatLng> {
        var pathsLatLong = PolylineEncoding.decode(encodedPath)
        var mapsLatLongs = arrayListOf<PathDetails.PathLatLng>()
        for (latLong in pathsLatLong) {
            var pathLatLng = PathDetails.PathLatLng()
            pathLatLng.latitude = latLong.lat
            pathLatLng.longitude = latLong.lng
            mapsLatLongs.add(pathLatLng)
        }
        return mapsLatLongs
    }


    fun drawPolyLine(mapsLatLongs: List<com.google.android.gms.maps.model.LatLng>): Polyline {
        return MarkerPolyLineUtil.addPolylineToMap(context, googleMap, mapsLatLongs)
    }

    private fun setupSourceMarker(place: PlaceData) {
        sourceMarker = MarkerPolyLineUtil.addMarkerToMap(googleMap, place)
    }

    private fun setupDestinationMarker(place: PlaceData) {
        destinationMarker = MarkerPolyLineUtil.addMarkerToMap(googleMap, place)
    }

    override fun saveSelectedPath() {
        if (source != null && destination != null && selectedDirectionData != null) {
            mapRepository.saveDirectionInfo(source!!, destination!!, selectedDirectionData!!)
        } else {
            throw MapExceptions(insufficientPathDataToSave)
        }
    }

    override fun renderPreviousPath() {
        if (previousData != null) {
            sourceMarker?.remove()
            destinationMarker?.remove()

            removeOlderRouteInfo()

            var rootNode = previousData!!.child(routeInfoNode).getValue(PathDetails::class.java)!!
            source = rootNode.source
            setupSourceMarker(source!!)

            destination = rootNode.destination
            setupDestinationMarker(destination!!)

            var polylineCords = getPolyPathWithCords(rootNode.route!!)
            var polyLine = drawPolyLine(polylineCords)
            MarkerPolyLineUtil.setSelectedPolyline(context, polyLine)
            zoomToRoute(source!!, destination!!)

            var directionData = DirectionData()
            directionData.polyline = polyLine
            directionsDataList.add(directionData)

            sourceMarker?.snippet =
                "Distance ${rootNode.distance} Time ${rootNode.duration}"
            sourceMarker?.showInfoWindow()
        }
    }

    override fun getPreviousSource(): String? {
        return previousData?.child(routeInfoNode)?.getValue(PathDetails::class.java)?.source?.name
    }

    override fun getPreviousDestination(): String? {
        return previousData?.child(routeInfoNode)?.getValue(PathDetails::class.java)?.destination?.name
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        this.googleMap = googleMap ?: return
        this.googleMap.setOnPolylineClickListener(this)
    }

    override fun onPolylineClick(polyLine: Polyline?) {
        for (directionData in directionsDataList) {
            if (directionData.polyline?.id == polyLine?.id) {
                selectedDirectionData = directionData
                MarkerPolyLineUtil.setSelectedPolyline(context, polyLine)
                sourceMarker?.snippet =
                    "Distance ${directionData.direction?.legs?.get(0)?.distance} Time ${directionData.direction?.legs?.get(
                        0
                    )?.duration}"
                sourceMarker?.showInfoWindow()
            } else {
                MarkerPolyLineUtil.setNonSelectedPolyline(context, directionData.polyline)
            }
        }
    }

    override fun getGPlace(requestCode: Int) {
        PlacesUtil.getGPlace(context, requestCode)
    }

    fun removeOlderRouteInfo() {
        for (directionData in directionsDataList) {
            directionData.polyline?.remove()
        }
        directionsDataList.clear()
        selectedDirectionData = null
    }

    fun zoomToRoute(source: PlaceData, destination: PlaceData) {

        var sourceLatLng =
            com.google.android.gms.maps.model.LatLng(source?.latitude!!, source?.longitude!!)
        var destinationLatLng = com.google.android.gms.maps.model.LatLng(
            destination?.latitude!!, destination?.longitude!!
        )

        var bounds = LatLngBounds.Builder()
        bounds.include(sourceLatLng)
        bounds.include(destinationLatLng)
        var latlongBounds = bounds.build()
        var routeArea = 120

        googleMap.animateCamera(
            CameraUpdateFactory.newLatLngBounds(latlongBounds, routeArea),
            600,
            null
        )
    }
}