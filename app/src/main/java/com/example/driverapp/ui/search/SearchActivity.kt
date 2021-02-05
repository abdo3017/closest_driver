package com.example.driverapp.ui.search

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.example.driverapp.R
import com.example.driverapp.databinding.ActivitySearchBinding
import com.example.driverapp.datasource.models.PlaceAutoCompleteResponse
import com.example.driverapp.datasource.models.UserLocation
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
                getViewDataBinding().rvSourceLocations.visibility = View.VISIBLE
                getViewDataBinding().rvDestinationLocations.visibility = View.GONE
                searchSourceAdapter.filter.filter(s.toString())
            }
        })
        getViewDataBinding().etDestinationLocation.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                getViewDataBinding().rvSourceLocations.visibility = View.GONE
                if (s.toString().length > 3) {
                    getViewDataBinding().rvDestinationLocations.visibility = View.VISIBLE
                    getPlacesFromAutocomplete(s.toString())
                    searchDestinationAdapter.searchString = s.toString()
                } else {
                    getViewDataBinding().rvDestinationLocations.visibility = View.GONE
                }
            }
        })

        getViewDataBinding().imgBack.setOnClickListener {
            overridePendingTransition(0, 0)
            finish()
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
                        searchDestinationAdapter.items =
                            it.data?.predictions as ArrayList<PlaceAutoCompleteResponse.Prediction>
                    }
                    ResponseStatus.LOADING -> {
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
                    }
                    ResponseStatus.SUCCESS -> {
                        searchSourceAdapter.itemsSearch = it.data as ArrayList<UserLocation>
                    }
                }
            }
        })
    }

    private fun clickSourceListener() = ItemClickListener { position: Int, view: View ->
        val intent = Intent()
        val bundle = Bundle()
        bundle.putParcelable("sourceLocation", searchSourceAdapter.getItem(position))
        intent.putExtras(bundle)
        setResult(RESULT_CANCELED, intent)
        overridePendingTransition(0, 0)
        finish()
    }

    private fun clickDestinationListener() = ItemClickListener { position: Int, view: View ->
        val intent = Intent()
        intent.putExtra("placeId", searchDestinationAdapter.getItem(position).placeId)
        intent.putExtra("description", searchDestinationAdapter.getItem(position).description)
        setResult(RESULT_OK, intent)
        overridePendingTransition(0, 0)
        finish()
    }
}