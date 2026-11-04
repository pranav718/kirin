package codes.knightkun.kirin.presentation.about

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
import codes.knightkun.kirin.KirinApplication
import codes.knightkun.kirin.R
import codes.knightkun.kirin.databinding.FragmentAboutBinding
import codes.knightkun.kirin.domain.model.Skill
import codes.knightkun.kirin.domain.model.SkillCategory
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.launch

class AboutFragment : Fragment() {

    private var _binding: FragmentAboutBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AboutViewModel by viewModels {
        val app = requireActivity().application as KirinApplication
        AboutViewModel.Factory(app.container.portfolioRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAboutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvErrorAbout.setOnClickListener {
            viewModel.loadAboutData()
        }
        observeUiState()
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is AboutUiState.Loading -> {
                            binding.progressAbout.visibility = View.VISIBLE
                            binding.layoutContentAbout.visibility = View.GONE
                            binding.tvErrorAbout.visibility = View.GONE
                        }
                        is AboutUiState.Error -> {
                            binding.progressAbout.visibility = View.GONE
                            binding.layoutContentAbout.visibility = View.GONE
                            binding.tvErrorAbout.visibility = View.VISIBLE
                            binding.tvErrorAbout.text = "${state.message}\nTap to retry"
                        }
                        is AboutUiState.Success -> {
                            binding.progressAbout.visibility = View.GONE
                            binding.tvErrorAbout.visibility = View.GONE
                            binding.layoutContentAbout.visibility = View.VISIBLE
                            bindProfile(state)
                        }
                    }
                }
            }
        }
    }

    private fun bindProfile(state: AboutUiState.Success) {
        val profile = state.profile
        if (profile.quote.isNullOrBlank()) {
            binding.tvAboutQuote.visibility = View.GONE
        } else {
            binding.tvAboutQuote.text = profile.quote
            binding.tvAboutQuote.visibility = View.VISIBLE
        }
        binding.tvAboutName.text = profile.name
        binding.tvAboutTitle.text = profile.title
        binding.tvAboutBio.text = profile.bio
        binding.tvAboutFull.text = profile.aboutFull

        // Action Buttons
        binding.btnResume.setOnClickListener {
            val customTabs = CustomTabsIntent.Builder().setShowTitle(true).build()
            customTabs.launchUrl(requireContext(), Uri.parse(profile.resumeUrl))
        }

        binding.btnEmail.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:${profile.email}")
            }
            startActivity(emailIntent)
        }

        binding.btnGithub.setOnClickListener {
            val githubIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/pranav718"))
            startActivity(githubIntent)
        }

        // Skill Matrix by Category
        populateSkillChips(binding.chipGroupLanguages, state.skillsByCategory[SkillCategory.LANGUAGES])
        populateSkillChips(binding.chipGroupBackend, state.skillsByCategory[SkillCategory.BACKEND])
        populateSkillChips(binding.chipGroupDatabases, state.skillsByCategory[SkillCategory.DATABASES])
        populateSkillChips(binding.chipGroupFrontend, state.skillsByCategory[SkillCategory.FRONTEND])
        populateSkillChips(binding.chipGroupTools, state.skillsByCategory[SkillCategory.TOOLS])

        // Social Platforms
        binding.chipGroupSocials.removeAllViews()
        val cornerRadius = resources.getDimension(R.dimen.chip_corner_radius)
        profile.socials.forEach { social ->
            val chip = Chip(requireContext()).apply {
                text = "${social.platform} • ${social.username}"
                isCheckable = false
                isClickable = true
                setEnsureMinTouchTargetSize(false)
                setChipBackgroundColorResource(R.color.brand_card)
                setTextColor(ContextCompat.getColor(context, R.color.brand_text_primary))
                chipStrokeWidth = 1f * resources.displayMetrics.density
                setChipStrokeColorResource(R.color.brand_border_accent)
                shapeAppearanceModel = shapeAppearanceModel.toBuilder().setAllCornerSizes(cornerRadius).build()
                chipMinHeight = 32f * resources.displayMetrics.density
                textSize = 12f
                setOnClickListener {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(social.url)))
                }
            }
            binding.chipGroupSocials.addView(chip)
        }
    }

    private fun populateSkillChips(chipGroup: ChipGroup, skills: List<Skill>?) {
        chipGroup.removeAllViews()
        val cornerRadius = resources.getDimension(R.dimen.chip_corner_radius)
        skills?.forEach { skill ->
            val chip = Chip(requireContext()).apply {
                text = skill.name
                isCheckable = false
                isClickable = false
                setEnsureMinTouchTargetSize(false)
                setChipBackgroundColorResource(R.color.brand_surface_variant)
                setTextColor(ContextCompat.getColor(context, R.color.brand_gold))
                chipStrokeWidth = 1f * resources.displayMetrics.density
                setChipStrokeColorResource(R.color.brand_border)
                shapeAppearanceModel = shapeAppearanceModel.toBuilder().setAllCornerSizes(cornerRadius).build()
                chipMinHeight = 28f * resources.displayMetrics.density
                textSize = 11.5f
            }
            chipGroup.addView(chip)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
