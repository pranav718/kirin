package codes.knightkun.kirin.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import codes.knightkun.kirin.domain.repository.PortfolioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: PortfolioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    fun loadHomeData() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading

            val profileResult = repository.getProfile()
            val featuredResult = repository.getFeaturedProjects()
            val projectsResult = repository.getProjects()
            val articlesResult = repository.getArticles()
            val skillsResult = repository.getSkills()

            if (profileResult.isSuccess && featuredResult.isSuccess) {
                _uiState.value = HomeUiState.Success(
                    profile = profileResult.getOrThrow(),
                    featuredProjects = featuredResult.getOrThrow(),
                    projectCount = projectsResult.getOrDefault(emptyList()).size,
                    articleCount = articlesResult.getOrDefault(emptyList()).size,
                    skillCount = skillsResult.getOrDefault(emptyList()).size
                )
            } else {
                val error = profileResult.exceptionOrNull()?.message
                    ?: featuredResult.exceptionOrNull()?.message
                    ?: "Failed to load portfolio data"
                _uiState.value = HomeUiState.Error(error)
            }
        }
    }

    class Factory(private val repository: PortfolioRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                return HomeViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
