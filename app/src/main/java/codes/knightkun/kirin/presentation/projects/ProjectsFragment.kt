package codes.knightkun.kirin.presentation.projects

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import codes.knightkun.kirin.KirinApplication
import codes.knightkun.kirin.R
import codes.knightkun.kirin.databinding.FragmentProjectsBinding
import kotlinx.coroutines.launch

class ProjectsFragment : Fragment() {

    private var _binding: FragmentProjectsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProjectsViewModel by viewModels {
        val app = requireActivity().application as KirinApplication
        ProjectsViewModel.Factory(app.container.portfolioRepository)
    }

    private lateinit var projectAdapter: ProjectListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProjectsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearchAndFilters()
        observeUiState()
    }

    private fun setupRecyclerView() {
        projectAdapter = ProjectListAdapter { project ->
            val action = ProjectsFragmentDirections.actionProjectsToProjectDetail(project.id)
            findNavController().navigate(action)
        }
        binding.rvProjects.adapter = projectAdapter
    }

    private fun setupSearchAndFilters() {
        binding.etSearch.doAfterTextChanged { text ->
            val query = text?.toString() ?: ""
            viewModel.setSearchQuery(query)
            binding.ivClearSearch.visibility = if (query.isEmpty()) View.GONE else View.VISIBLE
        }

        binding.ivClearSearch.setOnClickListener {
            binding.etSearch.text?.clear()
        }

        binding.chipGroupFilters.setOnCheckedStateChangeListener { _, checkedIds ->
            val filter = when {
                checkedIds.contains(R.id.chip_live) -> ProjectFilter.LIVE
                checkedIds.contains(R.id.chip_progress) -> ProjectFilter.IN_PROGRESS
                else -> ProjectFilter.ALL
            }
            viewModel.setFilter(filter)
        }

        binding.btnResetFilters.setOnClickListener {
            binding.etSearch.text?.clear()
            binding.chipGroupFilters.check(R.id.chip_all)
            viewModel.setFilter(ProjectFilter.ALL)
        }
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is ProjectsUiState.Loading -> {
                            binding.progressProjects.visibility = View.VISIBLE
                            binding.rvProjects.visibility = View.GONE
                            binding.layoutEmptyProjects.visibility = View.GONE
                        }
                        is ProjectsUiState.Error -> {
                            binding.progressProjects.visibility = View.GONE
                            binding.rvProjects.visibility = View.GONE
                            binding.layoutEmptyProjects.visibility = View.VISIBLE
                            binding.btnResetFilters.visibility = View.GONE
                            binding.tvEmptyProjects.text = state.message
                        }
                        is ProjectsUiState.Success -> {
                            binding.progressProjects.visibility = View.GONE
                            if (state.filteredProjects.isEmpty()) {
                                binding.rvProjects.visibility = View.GONE
                                binding.layoutEmptyProjects.visibility = View.VISIBLE
                                binding.btnResetFilters.visibility = View.VISIBLE
                                binding.tvEmptyProjects.text = if (state.searchQuery.isNotBlank()) {
                                    getString(R.string.search_no_match, state.searchQuery)
                                } else {
                                    getString(R.string.search_empty)
                                }
                            } else {
                                binding.rvProjects.visibility = View.VISIBLE
                                binding.layoutEmptyProjects.visibility = View.GONE
                                projectAdapter.submitList(state.filteredProjects)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
