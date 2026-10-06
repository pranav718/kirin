package codes.knightkun.kirin.presentation.blog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import codes.knightkun.kirin.domain.repository.PortfolioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BlogViewModel(
    private val repository: PortfolioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<BlogUiState>(BlogUiState.Loading)
    val uiState: StateFlow<BlogUiState> = _uiState.asStateFlow()

    init {
        loadArticles()
    }

    fun loadArticles() {
        viewModelScope.launch {
            _uiState.value = BlogUiState.Loading
            val result = repository.getArticles()
            if (result.isSuccess) {
                val articles = result.getOrDefault(emptyList())
                _uiState.value = BlogUiState.Success(articles)
            } else {
                _uiState.value = BlogUiState.Error(
                    result.exceptionOrNull()?.message ?: "Failed to load publications"
                )
            }
        }
    }

    class Factory(
        private val repository: PortfolioRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(BlogViewModel::class.java)) {
                return BlogViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
