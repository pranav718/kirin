package codes.knightkun.kirin.presentation.blog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import codes.knightkun.kirin.R
import codes.knightkun.kirin.databinding.ItemBlogCardBinding
import codes.knightkun.kirin.domain.model.Article
import com.google.android.material.chip.Chip

class BlogListAdapter(
    private val onArticleClick: (Article) -> Unit
) : ListAdapter<Article, BlogListAdapter.BlogViewHolder>(ArticleDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogViewHolder {
        val binding = ItemBlogCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BlogViewHolder(binding, onArticleClick)
    }

    override fun onBindViewHolder(holder: BlogViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class BlogViewHolder(
        private val binding: ItemBlogCardBinding,
        private val onArticleClick: (Article) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(article: Article) {
            binding.tvBlogDate.text = article.date.uppercase()
            binding.tvBlogClaps.text = "${article.claps} CLAPS"
            binding.tvBlogTitle.text = article.title

            binding.chipGroupBlogTags.removeAllViews()
            article.tags.forEach { tag ->
                val chip = Chip(binding.root.context).apply {
                    text = tag
                    isCheckable = false
                    isClickable = false
                    setChipBackgroundColorResource(R.color.brand_surface)
                    setTextColor(ContextCompat.getColor(context, R.color.brand_gold))
                    chipStrokeWidth = 1f
                    setChipStrokeColorResource(R.color.brand_border)
                    textSize = 11f
                }
                binding.chipGroupBlogTags.addView(chip)
            }

            binding.root.setOnClickListener {
                onArticleClick(article)
            }
        }
    }

    object ArticleDiffCallback : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean =
            oldItem == newItem
    }
}
