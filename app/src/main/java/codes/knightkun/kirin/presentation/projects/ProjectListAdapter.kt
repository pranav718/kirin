package codes.knightkun.kirin.presentation.projects

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import codes.knightkun.kirin.R
import codes.knightkun.kirin.databinding.ItemProjectCardBinding
import codes.knightkun.kirin.domain.model.Project

class ProjectListAdapter(
    private val onProjectClick: (Project) -> Unit
) : ListAdapter<Project, ProjectListAdapter.ProjectViewHolder>(ProjectDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val binding = ItemProjectCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProjectViewHolder(binding, onProjectClick)
    }

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ProjectViewHolder(
        private val binding: ItemProjectCardBinding,
        private val onProjectClick: (Project) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(project: Project) {
            binding.tvProjectTitle.text = project.title
            binding.tvProjectDescription.text = project.description
            binding.tvProjectTech.text = project.techStack.joinToString(" • ")

            binding.tvProjectStatus.text = project.status.uppercase()
            if (project.isLive) {
                binding.tvProjectStatus.setBackgroundResource(R.drawable.bg_status_live)
                binding.tvProjectStatus.setTextColor(
                    binding.root.context.getColor(R.color.status_live_text)
                )
            } else {
                binding.tvProjectStatus.setBackgroundResource(R.drawable.bg_status_progress)
                binding.tvProjectStatus.setTextColor(
                    binding.root.context.getColor(R.color.status_progress_text)
                )
            }

            if (!project.image.isNullOrEmpty()) {
                binding.ivProjectImage.load(project.image) {
                    crossfade(true)
                }
            }

            binding.root.setOnClickListener {
                onProjectClick(project)
            }
        }
    }

    object ProjectDiffCallback : DiffUtil.ItemCallback<Project>() {
        override fun areItemsTheSame(oldItem: Project, newItem: Project): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Project, newItem: Project): Boolean =
            oldItem == newItem
    }
}
