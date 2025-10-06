import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ssu.assu.R
import com.ssu.assu.databinding.ItemSignupPartBinding

class SignUpDropdownAdapter(
    private var items: List<String>
) : RecyclerView.Adapter<SignUpDropdownAdapter.ViewHolder>() {

    var onItemClick: ((String) -> Unit)? = null
    private var selectedItem: String? = null

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newItems: List<String>) {
        items = newItems
        selectedItem = null
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setSelectedItem(item: String?) {
        selectedItem = item
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemSignupPartBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: String) {
            binding.tvItemAdminName.text = item

            if (item == selectedItem) {
                binding.ivItemCheck.visibility = View.VISIBLE
                binding.tvItemAdminName.setTextColor(
                    ContextCompat.getColor(itemView.context, R.color.assu_main)
                )
            } else {
                binding.ivItemCheck.visibility = View.GONE
                binding.tvItemAdminName.setTextColor(
                    ContextCompat.getColor(itemView.context, R.color.assu_font_sub)
                )
            }

            itemView.setOnClickListener {
                onItemClick?.invoke(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSignupPartBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}