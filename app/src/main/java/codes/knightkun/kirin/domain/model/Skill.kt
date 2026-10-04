package codes.knightkun.kirin.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class SkillCategory(val displayName: String) {
    LANGUAGES("Languages"),
    BACKEND("Backend & Distributed"),
    DATABASES("Databases & Caching"),
    FRONTEND("Frontend & Graphics"),
    TOOLS("Tools & DevOps")
}

@Serializable
data class Skill(
    val name: String,
    val category: SkillCategory
)
