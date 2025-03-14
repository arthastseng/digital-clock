package com.ssc.android.vs_digital_clock.ui.dashboard

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class GridItemDecoration(
    context: Context,
    itemWidth: Int,
    columnCount: Int
) : RecyclerView.ItemDecoration() {
    private var itemOffset: Int = 0

    init {
        val screenWidth = context.resources.displayMetrics.widthPixels
        val itemTotalWidth = itemWidth * columnCount
        itemOffset = (screenWidth-itemTotalWidth)/4
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.set(itemOffset, itemOffset, itemOffset, itemOffset)
    }
}