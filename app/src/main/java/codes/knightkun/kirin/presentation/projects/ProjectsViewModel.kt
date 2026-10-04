package codes.knightkun.kirin.presentation.projects

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import codes.knightkun.kirin.domain.model.Project
import codes.knightkun.kirin.domain.repository.PortfolioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProjectsViewModel(
    private val repository: PortfolioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProjectsUiState>(ProjectsUiState.Loading)
    val uiState: StateFlow<ProjectsUiState> = _uiState.asStateFlow()

    private var cachedProjects: List<Project> = emptyList()
    private var currentFilter: ProjectFilter = ProjectFilter.ALL
    private var currentQuery: String = ""

    init {
        loadProjects()
    }

    fun loadProjects() {
        viewModelScope.launch {
            _uiState.value = ProjectsUiState.Loading
            val result = repository.getProjects()
            if (result.isSuccess) {
                cachedProjects = result.getOrDefault(emptyList())
                applyFilters()
            } else {
                _uiState.value = ProjectsUiState.Error(
                    result.exceptionOrNull()?.message ?: "Failed to load projects"
                )
            }
        }
    }

    fun setFilter(filter: ProjectFilter) {
        currentFilter = filter
        applyFilters()
    }

    fun setSearchQuery(query: String) {
        currentQuery = query
        applyFilters()
    }

    private fun applyFilters() {
        var filtered = cachedProjects

        // Apply status filter
        filtered = when (currentFilter) {
            ProjectFilter.ALL -> filtered
            ProjectFilter.LIVE -> filtered.filter { it.isLive }
            ProjectFilter.IN_PROGRESS -> filtered.filter { !it.isLive }
        }

        // Apply search text filter
        if (currentQuery.isNotBlank()) {
            val q = currentQuery.trim().lowercase()
            filtered = filtered.filter { project ->
                project.title.lowercase().contains(q) ||
                    project.description.lowercase().contains(q) ||
                    project.techStack.any { it.lowercase().contains(q) }
            }
        }

        _uiState.value = ProjectsUiState.Success(
            allProjects = cachedProjects,
            filteredProjects = filtered,
            currentFilter = currentFilter,
            searchQuery = currentQuery
        )
    }

    class Factory(private val repository: PortfolioRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ProjectsViewModel::class.java)) {
                return ProjectsViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
