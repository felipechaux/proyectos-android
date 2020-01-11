package co.com.pagatodo.core.views.sporting

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SportingSpacingItemDecoration: RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.right = 16
    }
}