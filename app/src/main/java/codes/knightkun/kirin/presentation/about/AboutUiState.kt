package codes.knightkun.kirin.presentation.about

import codes.knightkun.kirin.domain.model.Profile
import codes.knightkun.kirin.domain.model.Skill
import codes.knightkun.kirin.domain.model.SkillCategory

sealed interface AboutUiState {
    data object Loading : AboutUiState
    data class Success(
        val profile: Profile,
        val skillsByCategory: Map<SkillCategory, List<Skill>>
    ) : AboutUiState
    data class Error(val message: String) : AboutUiState
}
