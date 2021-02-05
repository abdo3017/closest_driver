package com.example.driverapp.ui.search

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import com.example.driverapp.databinding.ItemSourceListBinding
import com.example.driverapp.datasource.models.UserLocation
import com.example.driverapp.ui.base.BaseRecyclerViewAdapter
import com.example.driverapp.ui.base.BaseViewHolder
import com.example.driverapp.ui.base.ItemClickListener


class SearchSourceAdapter(

    items: MutableList<UserLocation>,
    var itemsSearch: MutableList<UserLocation>,
    private val itemClickListener: ItemClickListener,
) :
    BaseRecyclerViewAdapter<UserLocation, ItemSourceListBinding>(items), Filterable {
    var searchString = ""
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return ProductsViewHolder(
            ItemSourceListBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ), itemClickListener
        )
    }

    inner class ProductsViewHolder(
        private val binding: ItemSourceListBinding,
        private val itemClickListener: ItemClickListener
    ) :
        BaseViewHolder(binding.root) {
        override fun onBind(position: Int) {
            listView[position] = binding
            binding.item = getItem(position)
            binding.position = position
            binding.listener = itemClickListener
            if (searchString.isNotEmpty()) {
                val span: Spannable = SpannableString(getItem(position).name)
                span.setSpan(
                    ForegroundColorSpan(Color.BLACK),
                    0,
                    searchString.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                binding.tvName.setText(span, TextView.BufferType.SPANNABLE)
            }
            binding.executePendingBindings()
        }

    }

    override fun getFilter() = productFilter

    private val productFilter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            searchString = constraint.toString()
            val results = FilterResults()
            if (constraint != null && constraint.isNotEmpty()) {

                val filterPattern = constraint.toString().toLowerCase().trim()
                results.values =
                    itemsSearch.filterNot {
                        !it.name!!.toLowerCase().trim().startsWith(filterPattern)
                    }
                return results
            }
            results.values = listOf<UserLocation>()
            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            setItems(results!!.values as List<UserLocation>)
        }

    }
}


