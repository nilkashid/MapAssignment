package com.map.assignment.map.module

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.libraries.places.api.model.Place

interface MapProviderImpl
{
    fun addMapToView(containerId: Int)
    fun removeMap()
    fun setSourceItemOnMap(source:Place)
    fun setDestinationItemOnMap(destination: Place)
    fun getGPlace(requestCode: Int)

    @Throws(MapExceptions::class)
    fun saveSelectedPath()

    fun renderPreviousPath()
    fun getPreviousSource():String?
    fun getPreviousDestination():String?
}