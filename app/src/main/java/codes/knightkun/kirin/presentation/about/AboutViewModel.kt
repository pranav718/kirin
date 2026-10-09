package codes.knightkun.kirin.presentation.about

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import codes.knightkun.kirin.domain.model.SkillCategory
import codes.knightkun.kirin.domain.repository.PortfolioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AboutViewModel(
    private val repository: PortfolioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AboutUiState>(AboutUiState.Loading)
    val uiState: StateFlow<AboutUiState> = _uiState.asStateFlow()

    init {
        loadAboutData()
    }

    fun loadAboutData() {
        viewModelScope.launch {
            _uiState.value = AboutUiState.Loading
            val profileResult = repository.getProfile()
            val skillsResult = repository.getSkills()

            if (profileResult.isSuccess && skillsResult.isSuccess) {
                val profile = profileResult.getOrThrow()
                val skills = skillsResult.getOrThrow()

                val skillsByCategory = SkillCategory.entries.associateWith { category ->
                    skills.filter { it.category == category }
                }

                _uiState.value = AboutUiState.Success(
                    profile = profile,
                    skillsByCategory = skillsByCategory
                )
            } else {
                val errorMsg = profileResult.exceptionOrNull()?.message
                    ?: skillsResult.exceptionOrNull()?.message
                    ?: "Failed to load developer profile"
                _uiState.value = AboutUiState.Error(errorMsg)
            }
        }
    }

    class Factory(
        private val repository: PortfolioRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AboutViewModel::class.java)) {
                return AboutViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
