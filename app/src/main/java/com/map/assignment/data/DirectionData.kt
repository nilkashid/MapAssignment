package com.map.assignment.data

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polyline
import com.google.maps.model.DirectionsRoute

class DirectionData{
    var direction: DirectionsRoute? = null
    var polyline: Polyline? = null
    var pathLatLng: List<PathDetails.PathLatLng>? = arrayListOf()
}
