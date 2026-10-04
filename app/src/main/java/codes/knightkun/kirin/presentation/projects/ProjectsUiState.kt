package codes.knightkun.kirin.presentation.projects

import codes.knightkun.kirin.domain.model.Project

enum class ProjectFilter {
    ALL,
    LIVE,
    IN_PROGRESS
}

sealed interface ProjectsUiState {
    data object Loading : ProjectsUiState

    data class Success(
        val allProjects: List<Project>,
        val filteredProjects: List<Project>,
        val currentFilter: ProjectFilter,
        val searchQuery: String
    ) : ProjectsUiState

    data class Error(val message: String) : ProjectsUiState
}
