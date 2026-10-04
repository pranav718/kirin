package codes.knightkun.kirin.presentation.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import coil.load
import codes.knightkun.kirin.KirinApplication
import codes.knightkun.kirin.R
import codes.knightkun.kirin.databinding.FragmentHomeBinding
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels {
        val app = requireActivity().application as KirinApplication
        HomeViewModel.Factory(app.container.portfolioRepository)
    }

    private lateinit var featuredAdapter: FeaturedProjectsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupListeners()
        observeUiState()
    }

    private fun setupRecyclerView() {
        featuredAdapter = FeaturedProjectsAdapter { project ->
            val action = HomeFragmentDirections.actionHomeToProjectDetail(project.id)
            findNavController().navigate(action)
        }
        binding.rvFeaturedProjects.adapter = featuredAdapter
    }

    private fun setupListeners() {
        binding.tvViewAllProjects.setOnClickListener {
            findNavController().navigate(R.id.projects_dest)
        }
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is HomeUiState.Loading -> {
                            binding.progressHome.visibility = View.VISIBLE
                            binding.tvErrorHome.visibility = View.GONE
                        }
                        is HomeUiState.Error -> {
                            binding.progressHome.visibility = View.GONE
                            binding.tvErrorHome.visibility = View.VISIBLE
                            binding.tvErrorHome.text = state.message
                        }
                        is HomeUiState.Success -> {
                            binding.progressHome.visibility = View.GONE
                            binding.tvErrorHome.visibility = View.GONE
                            bindSuccess(state)
                        }
                    }
                }
            }
        }
    }

    private fun bindSuccess(state: HomeUiState.Success) {
        val profile = state.profile

        binding.tvName.text = profile.name
        binding.tvTitle.text = profile.title
        binding.tvBio.text = profile.bio

        if (profile.avatarUrl.isNotEmpty()) {
            binding.ivAvatar.load(profile.avatarUrl) {
                crossfade(true)
            }
        }

        binding.tvStatProjects.text = state.projectCount.toString()
        binding.tvStatArticles.text = state.articleCount.toString()
        binding.tvStatSkills.text = state.skillCount.toString()

        featuredAdapter.submitList(state.featuredProjects)

        binding.btnResume.setOnClickListener {
            openUrl(profile.resumeUrl)
        }

        binding.btnEmail.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:${profile.email}")
                putExtra(Intent.EXTRA_SUBJECT, "Engineering Inquiry / Opportunity")
            }
            startActivity(Intent.createChooser(intent, "Send Email"))
        }

        binding.btnGithub.setOnClickListener {
            val githubUrl = profile.socials.find { it.id == "github" }?.url ?: "https://github.com/pranav718"
            openUrl(githubUrl)
        }
    }

    private fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
