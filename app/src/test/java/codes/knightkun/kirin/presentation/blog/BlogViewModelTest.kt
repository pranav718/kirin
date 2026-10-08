package codes.knightkun.kirin.presentation.blog

import codes.knightkun.kirin.domain.model.Article
import codes.knightkun.kirin.domain.model.Profile
import codes.knightkun.kirin.domain.model.Project
import codes.knightkun.kirin.domain.model.Skill
import codes.knightkun.kirin.domain.model.SkillCategory
import codes.knightkun.kirin.domain.repository.PortfolioRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BlogViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val fakeArticles = listOf(
        Article(
            id = "tunneru-blog",
            title = "Building a Lightweight Zero-Dependency Tunneling Engine in Go",
            url = "https://medium.com/@pranav718/tunneru",
            date = "Oct 2024",
            claps = 151,
            tags = listOf("Go", "Networking", "Distributed Systems")
        ),
        Article(
            id = "hotaru-blog",
            title = "Architecting Hotaru: Distributed Key-Value Store",
            url = "https://medium.com/@pranav718/hotaru",
            date = "Sep 2024",
            claps = 55,
            tags = listOf("Raft", "Go", "Databases")
        )
    )

    private class FakeRepo(
        private val articlesResult: Result<List<Article>>
    ) : PortfolioRepository {
        override suspend fun getProfile(): Result<Profile> = Result.failure(Exception())
        override suspend fun getProjects(): Result<List<Project>> = Result.success(emptyList())
        override suspend fun getProjectById(id: String): Result<Project?> = Result.success(null)
        override suspend fun getFeaturedProjects(): Result<List<Project>> = Result.success(emptyList())
        override suspend fun getArticles(): Result<List<Article>> = articlesResult
        override suspend fun getSkills(): Result<List<Skill>> = Result.success(emptyList())
        override suspend fun getSkillsByCategory(category: SkillCategory): Result<List<Skill>> = Result.success(emptyList())
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `viewModel emits Success state when articles load successfully`() = runTest {
        val repo = FakeRepo(Result.success(fakeArticles))
        val viewModel = BlogViewModel(repo)

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is BlogUiState.Success)
        val success = state as BlogUiState.Success
        assertEquals(2, success.articles.size)
        assertEquals("tunneru-blog", success.articles[0].id)
        assertEquals(151, success.articles[0].claps)
    }

    @Test
    fun `viewModel emits Error state when articles fail to load`() = runTest {
        val repo = FakeRepo(Result.failure(RuntimeException("Medium API timeout")))
        val viewModel = BlogViewModel(repo)

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is BlogUiState.Error)
        val error = state as BlogUiState.Error
        assertEquals("Medium API timeout", error.message)
    }
}
