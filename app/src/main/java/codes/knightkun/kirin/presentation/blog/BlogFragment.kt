package codes.knightkun.kirin.presentation.blog

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import codes.knightkun.kirin.KirinApplication
import codes.knightkun.kirin.databinding.FragmentBlogBinding
import kotlinx.coroutines.launch

class BlogFragment : Fragment() {

    private var _binding: FragmentBlogBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BlogViewModel by viewModels {
        val app = requireActivity().application as KirinApplication
        BlogViewModel.Factory(app.container.portfolioRepository)
    }

    private lateinit var blogAdapter: BlogListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBlogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeUiState()
    }

    private fun setupRecyclerView() {
        blogAdapter = BlogListAdapter { article ->
            val customTabsIntent = CustomTabsIntent.Builder()
                .setShowTitle(true)
                .build()
            customTabsIntent.launchUrl(requireContext(), Uri.parse(article.url))
        }
        binding.rvBlog.adapter = blogAdapter
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is BlogUiState.Loading -> {
                            binding.progressBlog.visibility = View.VISIBLE
                            binding.rvBlog.visibility = View.GONE
                            binding.tvEmptyBlog.visibility = View.GONE
                        }
                        is BlogUiState.Error -> {
                            binding.progressBlog.visibility = View.GONE
                            binding.rvBlog.visibility = View.GONE
                            binding.tvEmptyBlog.visibility = View.VISIBLE
                            binding.tvEmptyBlog.text = state.message
                        }
                        is BlogUiState.Success -> {
                            binding.progressBlog.visibility = View.GONE
                            if (state.articles.isEmpty()) {
                                binding.rvBlog.visibility = View.GONE
                                binding.tvEmptyBlog.visibility = View.VISIBLE
                            } else {
                                binding.rvBlog.visibility = View.VISIBLE
                                binding.tvEmptyBlog.visibility = View.GONE
                                blogAdapter.submitList(state.articles)
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
