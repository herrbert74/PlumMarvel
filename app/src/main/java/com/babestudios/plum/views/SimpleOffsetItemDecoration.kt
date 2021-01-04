package com.babestudios.plum.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.babestudios.plum.R

class SimpleOffsetItemDecoration(context: Context?, insetOffset: Int) : ItemDecoration() {
	private val mDivider: Drawable? = ContextCompat.getDrawable(context!!, R.drawable.shape_divider)
	private val offset: Int = insetOffset
	override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
		c.save()
		var left: Int
		val right = parent.width - parent.paddingRight
		val childCount = parent.childCount
		for (i in 0 until childCount) {
			val child = parent.getChildAt(i)
			left = if (i == childCount - 1) {
				parent.paddingLeft
			} else {
				parent.paddingLeft + offset
			}
			val params = child.layoutParams as RecyclerView.LayoutParams
			val top = child.bottom - params.bottomMargin
			val bottom = top + mDivider!!.intrinsicHeight
			mDivider.setBounds(left, top, right, bottom)
			mDivider.draw(c)
		}
		c.restore()
	}

	override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
		val top = 0
		outRect[0, top, 0] = mDivider!!.intrinsicHeight
	}

}