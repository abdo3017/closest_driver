package com.example.driverapp.ui.search

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.example.driverapp.R
import com.example.driverapp.databinding.ActivitySearchBinding
import com.example.driverapp.datasource.models.PlaceAutoCompleteResponse
import com.example.driverapp.datasource.models.UserLocation
import com.example.driverapp.ui.SearchViewModel
import com.example.driverapp.ui.base.BaseActivity
import com.example.driverapp.ui.base.ItemClickListener
import com.example.driverapp.utils.ResponseStatus
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class SearchActivity : BaseActivity<ActivitySearchBinding>() {
    private lateinit var viewModel: SearchViewModel
    private lateinit var searchDestinationAdapter: SearchDestinationAdapter
    private lateinit var searchSourceAdapter: SearchSourceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //initial viewModel
        viewModel = ViewModelProvider(this).get(SearchViewModel::class.java)

        getSourceLocations()
        observeData()
        setUpViews()

    }


    override fun getLayoutId(): Int {
        return R.layout.activity_search
    }

    private fun setUpViews() {
        val id: Int? = intent.getIntExtra("textId", 0)
        if (id == R.id.etSourceLocation)
            getViewDataBinding().etSourceLocation.requestFocus()
        else
            getViewDataBinding().etDestinationLocation.requestFocus()

        searchDestinationAdapter = SearchDestinationAdapter(clickDestinationListener())
        searchSourceAdapter =
            SearchSourceAdapter(mutableListOf(), mutableListOf(), clickSourceListener())

        getViewDataBinding().etSourceLocation.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().isNotEmpty()) {
                    searchSourceAdapter.filter.filter(s.toString())
                } else {
                    getViewDataBinding().rvSourceLocations.visibility = View.GONE
                }
            }
        })
        getViewDataBinding().etDestinationLocation.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().length > 3) {
                    getPlacesFromAutocomplete(s.toString())
                } else {
                    getViewDataBinding().rvDestinationLocations.visibility = View.GONE
                }
            }
        })
        getViewDataBinding().etDestinationLocation.setOnClickListener {
            getViewDataBinding().rvDestinationLocations.visibility = View.VISIBLE

        }
        getViewDataBinding().etSourceLocation.setOnClickListener {
            getViewDataBinding().rvSourceLocations.visibility = View.VISIBLE

        }
        getViewDataBinding().rvDestinationLocations.adapter = searchDestinationAdapter
        getViewDataBinding().rvSourceLocations.adapter = searchSourceAdapter

    }

    private fun getSourceLocations() {
        viewModel.getSourceLocations()
    }

    private fun getPlacesFromAutocomplete(s: String) {
        viewModel.getPlacesFromAutocomplete(s)
    }


    private fun observeData() {
        viewModel.dataStateAutocompleteResult.observe(this, {
            it?.let {
                when (it.status) {
                    ResponseStatus.SUCCESS -> {
                        getViewDataBinding().rvDestinationLocations.visibility = View.VISIBLE
                        searchDestinationAdapter.items =
                            it.data?.predictions as ArrayList<PlaceAutoCompleteResponse.Prediction>
                    }
                    ResponseStatus.LOADING -> {
                        Log.d("trtrtrtrt", "heheheheheheh")
                    }
                    ResponseStatus.ERROR -> {

                    }
                }
            }
        })

        viewModel.dataStateUserLocation.observe(this, {
            it?.let {
                when (it.status) {
                    ResponseStatus.ERROR -> {
                    }
                    ResponseStatus.LOADING -> {
                        Log.d("trtrtrtrt", "heheheheheheh")
                    }
                    ResponseStatus.SUCCESS -> {
                        getViewDataBinding().rvSourceLocations.visibility = View.VISIBLE
                        searchSourceAdapter.itemsSearch = it.data as ArrayList<UserLocation>
                    }
                }
            }
        })
    }

    private fun clickSourceListener() = ItemClickListener { position: Int, view: View ->
        val intent = Intent()
        setResult(RESULT_CANCELED, intent)
        finish()
    }

    private fun clickDestinationListener() = ItemClickListener { position: Int, view: View ->
        val intent = Intent()
        intent.putExtra("placeId", searchDestinationAdapter.getItem(position).placeId)
        setResult(RESULT_OK, intent)
        finish()
    }
}