package com.map.assignment.map.module

import com.google.android.libraries.places.api.model.Place
import com.google.firebase.database.FirebaseDatabase
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.model.DirectionsResult
import com.google.maps.model.DirectionsRoute
import com.google.maps.model.LatLng
import com.map.assignment.R
import com.map.assignment.data.DirectionData
import com.map.assignment.data.PathDetails
import com.map.assignment.data.PlaceData
import com.map.assignment.map.module.MapConstants.destinationNode
import com.map.assignment.map.module.MapConstants.distanceNode
import com.map.assignment.map.module.MapConstants.durationNode
import com.map.assignment.map.module.MapConstants.rootNode
import com.map.assignment.map.module.MapConstants.routeInfoNode
import com.map.assignment.map.module.MapConstants.routeNode
import com.map.assignment.map.module.MapConstants.sourceNode


class MapRepository
{
    var geoApiContext: GeoApiContext? = null
    fun saveDirectionInfo(source: PlaceData, destination: PlaceData, directionData: DirectionData){
        var databaseRef = FirebaseDatabase.getInstance().getReference(rootNode)

        var pathDetails = PathDetails()
        pathDetails.source = source
        pathDetails.destination = destination
        pathDetails.duration = directionData.direction?.legs?.get(0)?.duration?.humanReadable
        pathDetails.distance = directionData.direction?.legs?.get(0)?.distance?.humanReadable
        pathDetails.route = directionData.pathLatLng
        databaseRef.child(routeInfoNode).setValue(pathDetails)
    }
    fun getDirections(source: PlaceData?, destination: PlaceData?, apiKey: String?): DirectionsResult {
        prepareGeoApiContext(apiKey!!)
        var directionResult = getDirectionResult(source!!, destination!!)
        return directionResult
    }

    private fun prepareGeoApiContext(apiKey: String) {
        if (geoApiContext == null) {
            geoApiContext = GeoApiContext.Builder()
                .apiKey(apiKey)
                .build()
        }
    }

    private fun getDirectionResult(source: PlaceData, destination: PlaceData): DirectionsResult {
        return DirectionsApi.newRequest(geoApiContext)
            .destination(
                LatLng(
                    destination?.latitude ?: 0.0, destination?.longitude ?: 0.0
                )
            )
            .origin(LatLng(source?.latitude ?: 0.0, source?.longitude ?: 0.0))
            .alternatives(true)
            .await()
    }
}