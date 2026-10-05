package codes.knightkun.kirin.presentation.blog

import codes.knightkun.kirin.domain.model.Article

sealed interface BlogUiState {
    data object Loading : BlogUiState
    data class Success(val articles: List<Article>) : BlogUiState
    data class Error(val message: String) : BlogUiState
}
