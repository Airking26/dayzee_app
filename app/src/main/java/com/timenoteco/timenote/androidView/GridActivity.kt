package com.timenoteco.timenote.androidView

import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.annotation.Px
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.timenoteco.timenote.R
import com.timenoteco.timenote.databinding.ActivityGridBinding
import com.timenoteco.timenote.model.CarouselImage
import com.timenoteco.timenote.model.CarouselImageModel

/** Works only with a [GridLayoutManager] */
class GridSpacingDecoration(
    @Px private val spacing: Int
) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        child: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val itemPosition = parent.getChildAdapterPosition(child)
        val itemCount = parent.adapter?.itemCount ?: 0

        val lm = parent.layoutManager as GridLayoutManager
        val spanCount = lm.spanCount
        val spanIndex = lm.spanSizeLookup.getSpanIndex(itemPosition, spanCount)

        val isTopRow = itemPosition < spanCount

        val bottomRowIsComplete = itemCount % spanCount == 0
        val isBottomRow =
            if (bottomRowIsComplete) itemPosition >= itemCount - spanCount else itemPosition >= itemCount - (itemCount % spanCount)

        outRect.top = if (isTopRow) spacing else spacing / 2
        outRect.bottom = if (isBottomRow) spacing else spacing / 2
        outRect.left = (spacing * ((spanCount - spanIndex) / spanCount.toFloat())).toInt()
        outRect.right = (spacing * ((spanIndex + 1) / spanCount.toFloat())).toInt()
    }
}

class GridAdapter(
    private val images: List<CarouselImageModel>,
    private val onImageClicked: (CarouselImageModel, Int) -> Unit
) : RecyclerView.Adapter<GridAdapter.VH>() {
    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH = VH(parent)

    override fun onBindViewHolder(holder: VH, position: Int) {
        val image = images[position]

        Glide.with(holder.imageView)
            .load(image.url)
            .transition(withCrossFade())
            .into(holder.imageView)

        holder.imageView.setOnClickListener {
            onImageClicked(image, position)
        }
    }

    override fun getItemCount(): Int = images.size

    override fun getItemId(position: Int): Long {
        return images[position].url.hashCode().toLong()
    }

    class VH(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_grid_image, parent, false)
    ) {
        val imageView: ImageView = itemView.findViewById(R.id.image_view)
    }
}

data class CarouselImplementation(
    val label: String,
    val clazz: Class<out Activity>
)