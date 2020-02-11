package com.map.assignment.map.module

import android.content.Context
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.model.Place
import com.google.maps.model.PlaceDetails
import com.map.assignment.R
import com.map.assignment.data.PlaceData

class MarkerPolyLineUtil
{
    companion object{
        fun addMarkerToMap(googleMap: GoogleMap, place: PlaceData): Marker?{
            var marker: Marker?
            with(googleMap) {
                marker = addMarker(getMarkerOptions(place))
                marker?.showInfoWindow()
                animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(place.latitude!!, place.longitude!!), 15f))
            }
            return marker
        }
        fun getMarkerOptions(place: PlaceData):MarkerOptions{
            return MarkerOptions()
                .position(LatLng(place.latitude!!, place.longitude!!))
                .title(place?.name)
        }

        fun addPolylineToMap(context: Context, googleMap: GoogleMap,
                             mapsLatLongs: List<com.google.android.gms.maps.model.LatLng>):Polyline{

            var polyline = googleMap.addPolyline(PolylineOptions().addAll(mapsLatLongs).geodesic(true))
            polyline?.isClickable = true
            setNonSelectedPolyline(context, polyline)
            return polyline
        }

        fun setSelectedPolyline(context: Context, polyline: Polyline?){
            polyline?.color = ContextCompat.getColor(context, R.color.polyline_color)
            polyline?.zIndex = 1f
            polyline?.width = context.resources.getDimension(R.dimen.selected_polyline_width)
        }
        fun setNonSelectedPolyline(context: Context, polyline: Polyline?){
            polyline?.color = ContextCompat.getColor(context, R.color.disabled_polyline_color)
            polyline?.zIndex = 0f
            polyline?.width = context.resources.getDimension(R.dimen.non_selected_polyline_width)
        }
    }
}