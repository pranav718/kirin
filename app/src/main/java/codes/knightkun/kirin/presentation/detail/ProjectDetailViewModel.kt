package codes.knightkun.kirin.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import codes.knightkun.kirin.domain.model.Project
import codes.knightkun.kirin.domain.repository.PortfolioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface ProjectDetailUiState {
    data object Loading : ProjectDetailUiState
    data class Success(val project: Project) : ProjectDetailUiState
    data class Error(val message: String) : ProjectDetailUiState
}

class ProjectDetailViewModel(
    private val repository: PortfolioRepository,
    private val projectId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProjectDetailUiState>(ProjectDetailUiState.Loading)
    val uiState: StateFlow<ProjectDetailUiState> = _uiState.asStateFlow()

    init {
        loadProject()
    }

    fun loadProject() {
        viewModelScope.launch {
            _uiState.value = ProjectDetailUiState.Loading
            val result = repository.getProjectById(projectId)
            if (result.isSuccess) {
                val project = result.getOrNull()
                if (project != null) {
                    _uiState.value = ProjectDetailUiState.Success(project)
                } else {
                    _uiState.value = ProjectDetailUiState.Error("Project not found: $projectId")
                }
            } else {
                _uiState.value = ProjectDetailUiState.Error(
                    result.exceptionOrNull()?.message ?: "Failed to load project details"
                )
            }
        }
    }

    class Factory(
        private val repository: PortfolioRepository,
        private val projectId: String
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ProjectDetailViewModel::class.java)) {
                return ProjectDetailViewModel(repository, projectId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
