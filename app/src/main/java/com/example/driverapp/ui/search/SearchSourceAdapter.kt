package com.example.driverapp.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
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
            binding.executePendingBindings()
        }

    }

    override fun getFilter() = productFilter

    private val productFilter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val results = FilterResults()
            if (constraint != null && constraint.isNotEmpty()) {
                val filterPattern = constraint.toString().toLowerCase().trim()
                results.values =
                    itemsSearch.filterNot {
                        !it.name!!.toLowerCase().trim().contains(filterPattern)
                    }
                return results
            }
            results.values = listOf<UserLocation>()
            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            getItems().clear()
            getItems().addAll(results!!.values as List<UserLocation>)
            notifyDataSetChanged()
        }

    }
}


