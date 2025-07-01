import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rounds.imageloader.ImageLoader
import com.rounds.imageloader.RemoteImage
import com.rounds.imageloading.R

class ImageAdapter(
    private val imageLoader: ImageLoader,
    private val placeholderDrawable: Drawable?
) : ListAdapter<RemoteImage, ImageAdapter.ImageViewHolder>(ImageDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageItem = getItem(position)
        holder.bind(imageItem, imageLoader, placeholderDrawable)
    }

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.itemImageView)
        fun bind(imageItem: RemoteImage, loader: ImageLoader, placeholder: Drawable?) {
            loader.load(
                url = imageItem.imageUrl,
                targetView = imageView,
                placeholder = placeholder
            )
            itemView.setOnClickListener {
                Toast.makeText(
                    itemView.context,
                    "Invalidating cache for ${imageItem.imageUrl}",
                    Toast.LENGTH_SHORT
                ).show()
                loader.invalidateCache(imageItem.imageUrl)
                loader.load(
                    url = imageItem.imageUrl,
                    targetView = imageView,
                    placeholder = placeholder
                )
            }
        }
    }
}

class ImageDiffCallback : DiffUtil.ItemCallback<RemoteImage>() {
    override fun areItemsTheSame(oldItem: RemoteImage, newItem: RemoteImage): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: RemoteImage, newItem: RemoteImage): Boolean {
        return oldItem == newItem
    }
}
