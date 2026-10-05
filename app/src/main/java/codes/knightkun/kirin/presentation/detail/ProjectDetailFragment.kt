package codes.knightkun.kirin.presentation.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import coil.load
import codes.knightkun.kirin.KirinApplication
import codes.knightkun.kirin.R
import codes.knightkun.kirin.databinding.FragmentProjectDetailBinding
import codes.knightkun.kirin.domain.model.Project
import com.google.android.material.chip.Chip
import kotlinx.coroutines.launch

class ProjectDetailFragment : Fragment() {

    private var _binding: FragmentProjectDetailBinding? = null
    private val binding get() = _binding!!

    private val args: ProjectDetailFragmentArgs by navArgs()

    private val viewModel: ProjectDetailViewModel by viewModels {
        val app = requireActivity().application as KirinApplication
        ProjectDetailViewModel.Factory(
            repository = app.container.portfolioRepository,
            projectId = args.projectId
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProjectDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeUiState()
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is ProjectDetailUiState.Loading -> {
                            binding.progressDetail.visibility = View.VISIBLE
                            binding.layoutContentDetail.visibility = View.GONE
                            binding.tvErrorDetail.visibility = View.GONE
                        }
                        is ProjectDetailUiState.Error -> {
                            binding.progressDetail.visibility = View.GONE
                            binding.layoutContentDetail.visibility = View.GONE
                            binding.tvErrorDetail.visibility = View.VISIBLE
                            binding.tvErrorDetail.text = state.message
                        }
                        is ProjectDetailUiState.Success -> {
                            binding.progressDetail.visibility = View.GONE
                            binding.tvErrorDetail.visibility = View.GONE
                            binding.layoutContentDetail.visibility = View.VISIBLE
                            bindProjectData(state.project)
                        }
                    }
                }
            }
        }
    }

    private fun bindProjectData(project: Project) {
        binding.tvDetailTitle.text = project.title
        binding.tvDetailNarrative.text = project.longDescription.ifBlank { project.description }

        // Status badge
        val isLive = project.isLive
        binding.tvDetailStatus.text = if (isLive) {
            getString(R.string.status_live)
        } else {
            getString(R.string.status_progress)
        }
        binding.tvDetailStatus.setBackgroundResource(
            if (isLive) R.drawable.bg_status_live else R.drawable.bg_status_progress
        )
        binding.tvDetailStatus.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (isLive) R.color.status_live_text else R.color.status_progress_text
            )
        )

        // Banner Image
        if (!project.image.isNullOrBlank()) {
            binding.ivDetailImage.load(project.image) {
                crossfade(true)
            }
        }

        // Tech stack chips
        binding.chipGroupTech.removeAllViews()
        project.techStack.forEach { tech ->
            val chip = Chip(requireContext()).apply {
                text = tech
                isCheckable = false
                isClickable = false
                setChipBackgroundColorResource(R.color.brand_surface)
                setTextColor(ContextCompat.getColor(context, R.color.brand_gold))
                chipStrokeWidth = 1f
                setChipStrokeColorResource(R.color.brand_border)
                textSize = 12f
            }
            binding.chipGroupTech.addView(chip)
        }

        // Action Buttons
        binding.btnGithubCode.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(project.githubUrl))
            startActivity(intent)
        }

        if (!project.liveUrl.isNullOrBlank()) {
            binding.btnLiveDemo.visibility = View.VISIBLE
            binding.btnLiveDemo.setOnClickListener {
                val customTabsIntent = CustomTabsIntent.Builder().build()
                customTabsIntent.launchUrl(requireContext(), Uri.parse(project.liveUrl))
            }
        } else {
            binding.btnLiveDemo.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
