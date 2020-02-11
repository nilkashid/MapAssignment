package com.map.assignment.map.module

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.map.assignment.R
import java.util.*

class PlacesUtil
{
    companion object{
        fun getGPlace(context: Context, requestCode: Int) {
            initialisePlacesIfNot(context)
            var fields = getPlaceFields()
            var intent = Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY, fields
            ).build(context)
            (context as? AppCompatActivity)?.startActivityForResult(intent, requestCode)
        }

        private fun getPlaceFields() =
            Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)

        fun initialisePlacesIfNot(context: Context){
            if (!Places.isInitialized()) {
                Places.initialize(context.getApplicationContext(), context.getString(R.string.google_maps_key))
            }
        }
    }
}