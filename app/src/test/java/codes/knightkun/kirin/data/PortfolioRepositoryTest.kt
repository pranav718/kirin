package codes.knightkun.kirin.data

import codes.knightkun.kirin.data.datasource.PortfolioDataSource
import codes.knightkun.kirin.data.repository.PortfolioRepositoryImpl
import codes.knightkun.kirin.domain.model.Article
import codes.knightkun.kirin.domain.model.PortfolioData
import codes.knightkun.kirin.domain.model.Profile
import codes.knightkun.kirin.domain.model.Project
import codes.knightkun.kirin.domain.model.Skill
import codes.knightkun.kirin.domain.model.SkillCategory
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PortfolioRepositoryTest {

    private val fakeProfile = Profile(
        name = "Pranav Ray",
        title = "Systems & Distributed Backend",
        bio = "building concurrent architectures",
        aboutFull = "full bio",
        quote = "inside, i'm infinite",
        avatarUrl = "https://example.com/avatar.jpg",
        resumeUrl = "https://example.com/resume.pdf",
        email = "test@example.com"
    )

    private val fakeProjects = listOf(
        Project(
            id = "tunneru",
            title = "tunneru",
            description = "tunneling engine in Go",
            longDescription = "long desc",
            techStack = listOf("Go", "WebSockets"),
            status = "Live",
            githubUrl = "https://github.com/pranav718/tunneru"
        ),
        Project(
            id = "nanimo",
            title = "nanimo",
            description = "3D engine",
            longDescription = "long desc",
            techStack = listOf("Three.js"),
            status = "In Progress",
            githubUrl = "https://github.com/pranav718/nanimo"
        )
    )

    private val fakeArticles = listOf(
        Article(
            id = "convex",
            title = "Convex Guide",
            url = "https://medium.com/@knightkun/convex",
            date = "Oct 2025",
            claps = 151
        )
    )

    private val fakeSkills = listOf(
        Skill("Go", SkillCategory.LANGUAGES),
        Skill("PostgreSQL", SkillCategory.DATABASES)
    )

    private val fakeData = PortfolioData(
        profile = fakeProfile,
        projects = fakeProjects,
        articles = fakeArticles,
        skills = fakeSkills
    )

    private class FakeDataSource(private val data: PortfolioData) : PortfolioDataSource {
        var callCount = 0
        override suspend fun getPortfolioData(): PortfolioData {
            callCount++
            return data
        }
    }

    @Test
    fun `repository returns profile successfully`() = runTest {
        val dataSource = FakeDataSource(fakeData)
        val repository = PortfolioRepositoryImpl(dataSource)

        val result = repository.getProfile()
        assertTrue(result.isSuccess)
        assertEquals("Pranav Ray", result.getOrNull()?.name)
    }

    @Test
    fun `repository caches data in memory across calls`() = runTest {
        val dataSource = FakeDataSource(fakeData)
        val repository = PortfolioRepositoryImpl(dataSource)

        repository.getProfile()
        repository.getProjects()
        repository.getArticles()

        // DataSource should only be invoked once due to in-memory caching
        assertEquals(1, dataSource.callCount)
    }

    @Test
    fun `repository finds project by ID`() = runTest {
        val dataSource = FakeDataSource(fakeData)
        val repository = PortfolioRepositoryImpl(dataSource)

        val result = repository.getProjectById("tunneru")
        assertTrue(result.isSuccess)
        assertNotNull(result.getOrNull())
        assertEquals("tunneru", result.getOrNull()?.id)
        assertTrue(result.getOrNull()?.isLive == true)
    }

    @Test
    fun `repository filters skills by category`() = runTest {
        val dataSource = FakeDataSource(fakeData)
        val repository = PortfolioRepositoryImpl(dataSource)

        val langSkills = repository.getSkillsByCategory(SkillCategory.LANGUAGES)
        assertTrue(langSkills.isSuccess)
        assertEquals(1, langSkills.getOrNull()?.size)
        assertEquals("Go", langSkills.getOrNull()?.first()?.name)
    }
}
