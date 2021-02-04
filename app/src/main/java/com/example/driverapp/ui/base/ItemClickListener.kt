package com.example.driverapp.ui.base

import android.view.View

class ItemClickListener(private val clickListener: (position: Int, view: View) -> Unit) {
    fun onClick(position: Int, view: View) =
        clickListener(position, view)
}