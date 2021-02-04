package com.example.driverapp.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.example.driverapp.databinding.ItemDestinationListBinding
import com.example.driverapp.datasource.models.PlaceAutoCompleteResponse
import com.example.driverapp.ui.base.BaseRecyclerViewAdapter
import com.example.driverapp.ui.base.BaseViewHolder
import com.example.driverapp.ui.base.ItemClickListener


class SearchDestinationAdapter(

    private val itemClickListener: ItemClickListener
) :
    BaseRecyclerViewAdapter<PlaceAutoCompleteResponse.Prediction, ItemDestinationListBinding>(
        mutableListOf()
    ) {

    var items: ArrayList<PlaceAutoCompleteResponse.Prediction> = ArrayList()
        set(value) {
            field = value
            val diffCallback = SearchDiffUtils(itemsFiltered, items)
            val diffResult = DiffUtil.calculateDiff(diffCallback)
            if (itemsFiltered.size > 0)
                itemsFiltered.clear()
            itemsFiltered.addAll(value)
            setItems(itemsFiltered)
            diffResult.dispatchUpdatesTo(this)
        }

    private var itemsFiltered: ArrayList<PlaceAutoCompleteResponse.Prediction> = ArrayList()
        set(value) {
            field = value
            val diffCallback = SearchDiffUtils(itemsFiltered, items)

            val diffResult = DiffUtil.calculateDiff(diffCallback)
            diffResult.dispatchUpdatesTo(this)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return ProductsViewHolder(
            ItemDestinationListBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ), itemClickListener
        )
    }

    inner class ProductsViewHolder(
        private val binding: ItemDestinationListBinding,
        private val itemClickListener: ItemClickListener
    ) :
        BaseViewHolder(binding.root) {
        override fun onBind(position: Int) {
            listView[position] = binding
            binding.item = itemsFiltered[position]
            binding.position = position
            binding.listener = itemClickListener
            binding.executePendingBindings()
        }

    }
}

class SearchDiffUtils(
    private val oldList: ArrayList<PlaceAutoCompleteResponse.Prediction>,
    private val newList: ArrayList<PlaceAutoCompleteResponse.Prediction>
) :
    DiffUtil.Callback() {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}



