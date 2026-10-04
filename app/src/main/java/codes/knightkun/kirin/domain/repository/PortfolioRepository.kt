package codes.knightkun.kirin.domain.repository

import codes.knightkun.kirin.domain.model.Article
import codes.knightkun.kirin.domain.model.Profile
import codes.knightkun.kirin.domain.model.Project
import codes.knightkun.kirin.domain.model.Skill
import codes.knightkun.kirin.domain.model.SkillCategory

interface PortfolioRepository {
    suspend fun getProfile(): Result<Profile>
    suspend fun getProjects(): Result<List<Project>>
    suspend fun getProjectById(id: String): Result<Project?>
    suspend fun getFeaturedProjects(): Result<List<Project>>
    suspend fun getArticles(): Result<List<Article>>
    suspend fun getSkills(): Result<List<Skill>>
    suspend fun getSkillsByCategory(category: SkillCategory): Result<List<Skill>>
}
