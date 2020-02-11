package com.map.assignment.data

import com.google.maps.model.EncodedPolyline

class PathDetails
{
    var source: PlaceData? = null
    var destination: PlaceData? = null
    var distance: String? = null
    var duration: String? = null
    var route: List<PathLatLng>? = null

    class PathLatLng{
        var latitude: Double? = null
        var longitude: Double? = null
    }
}

