package codes.knightkun.kirin.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class PortfolioData(
    val profile: Profile,
    val projects: List<Project> = emptyList(),
    val articles: List<Article> = emptyList(),
    val skills: List<Skill> = emptyList()
)
