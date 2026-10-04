package codes.knightkun.kirin.data.repository

import codes.knightkun.kirin.data.datasource.PortfolioDataSource
import codes.knightkun.kirin.domain.model.Article
import codes.knightkun.kirin.domain.model.PortfolioData
import codes.knightkun.kirin.domain.model.Profile
import codes.knightkun.kirin.domain.model.Project
import codes.knightkun.kirin.domain.model.Skill
import codes.knightkun.kirin.domain.model.SkillCategory
import codes.knightkun.kirin.domain.repository.PortfolioRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class PortfolioRepositoryImpl(
    private val dataSource: PortfolioDataSource
) : PortfolioRepository {

    private val mutex = Mutex()
    private var cachedData: PortfolioData? = null

    private suspend fun loadData(): PortfolioData {
        return mutex.withLock {
            cachedData ?: dataSource.getPortfolioData().also {
                cachedData = it
            }
        }
    }

    override suspend fun getProfile(): Result<Profile> = runCatching {
        loadData().profile
    }

    override suspend fun getProjects(): Result<List<Project>> = runCatching {
        loadData().projects
    }

    override suspend fun getProjectById(id: String): Result<Project?> = runCatching {
        loadData().projects.find { it.id.equals(id, ignoreCase = true) }
    }

    override suspend fun getFeaturedProjects(): Result<List<Project>> = runCatching {
        // Return top 3 systems: tunneru, hotaru, tsuna
        val all = loadData().projects
        val featuredIds = listOf("tunneru", "hotaru", "tsuna")
        all.filter { it.id in featuredIds }.ifEmpty { all.take(3) }
    }

    override suspend fun getArticles(): Result<List<Article>> = runCatching {
        loadData().articles
    }

    override suspend fun getSkills(): Result<List<Skill>> = runCatching {
        loadData().skills
    }

    override suspend fun getSkillsByCategory(category: SkillCategory): Result<List<Skill>> = runCatching {
        loadData().skills.filter { it.category == category }
    }
}
