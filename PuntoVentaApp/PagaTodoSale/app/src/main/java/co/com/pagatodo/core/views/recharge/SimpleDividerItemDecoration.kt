package co.com.pagatodo.core.views.recharge

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.RecyclerView
import co.com.pagatodo.core.R

class SimpleDividerItemDecoration(context: Context) : RecyclerView.ItemDecoration() {

    var divider: Drawable? = null

    init {
        divider = context.getDrawable(R.drawable.line_divider)
    }

    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(canvas, parent, state)

        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight

        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)

            val params = child.layoutParams as RecyclerView.LayoutParams

            val top = child.bottom + params.bottomMargin
            val bottom = top + (divider?.getIntrinsicHeight() ?: 0)

            divider?.setBounds(left, top, right, bottom)
            divider?.draw(canvas)
        }
    }
}