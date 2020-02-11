package com.map.assignment.data

import com.google.android.libraries.places.api.model.Place

class PlacesDao
{
    fun getPlaceData(source: Place): PlaceData{
        var placeData = PlaceData()
        placeData.name = source.name
        placeData.latitude = source.latLng?.latitude
        placeData.longitude = source.latLng?.longitude
        placeData.id = source.id
        return placeData

    }
}