package codes.knightkun.kirin.presentation.home

import codes.knightkun.kirin.domain.model.Profile
import codes.knightkun.kirin.domain.model.Project

sealed interface HomeUiState {
    data object Loading : HomeUiState

    data class Success(
        val profile: Profile,
        val featuredProjects: List<Project>,
        val projectCount: Int,
        val articleCount: Int,
        val skillCount: Int
    ) : HomeUiState

    data class Error(val message: String) : HomeUiState
}
