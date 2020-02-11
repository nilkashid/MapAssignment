package com.map.assignment.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.map.assignment.R
import com.map.assignment.databinding.ActivityMainBinding
import com.map.assignment.map.module.MapProvider
import com.map.assignment.map.module.MapProviderImpl

class MapViewActivity : BaseActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var viewModel: MapViewModel
    var mapProviderImpl: MapProviderImpl = MapProvider(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        initViewModel()
        binding.viewModel = viewModel
        mapProviderImpl.addMapToView(binding.mapFragmentContainer.id)
        initObservers()
    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this).get(MapViewModel::class.java)
        viewModel.init(this, mapProviderImpl)
    }

    private fun initObservers() {
        viewModel.networkCheckFailed?.observe(this, networkEventObseerver)
        viewModel.errorState?.observe(this, errorStateObserver)
        viewModel.previousSource?.observe(this, previousSourceObserver)
        viewModel.previousDestination?.observe(this, previousDestinationObserver)
    }

    val errorStateObserver = Observer<String>{message ->
        displayErrorToast(message)
    }
    val networkEventObseerver = Observer<Boolean> { vt ->
        displayNetworkErrorDialog()
    }
    val previousSourceObserver = Observer<String> { value -> binding.sourceEt.setText(value) }
    val previousDestinationObserver = Observer<String> { value -> binding.destinationEt.setText(value) }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (resultCode == Activity.RESULT_OK) {
            val place = Autocomplete.getPlaceFromIntent(data!!)
            if (requestCode == viewModel.SOURCE_AUTOCOMPLETE_REQUEST_CODE) {
                mapProviderImpl.setSourceItemOnMap(place)
                binding.sourceEt.setText(place.name)
            } else if (requestCode == viewModel.DESTINATION_AUTOCOMPLETE_REQUEST_CODE) {
                mapProviderImpl.setDestinationItemOnMap(place)
                binding.destinationEt.setText(place.name)
            }
        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            displayErrorToast(getString(R.string.error_string))
            Toast.makeText(this, "Some error occurred", Toast.LENGTH_SHORT).show()
        } else if (resultCode == Activity.RESULT_CANCELED) {

        }
        super.onActivityResult(requestCode, resultCode, data)
    }

}
