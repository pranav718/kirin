package codes.knightkun.kirin.presentation.home

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
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val fakeProfile = Profile(
        name = "Pranav Ray",
        title = "Systems & Distributed Backend",
        bio = "building concurrent architectures",
        aboutFull = "full bio",
        quote = "inside, i'm infinite",
        avatarUrl = "https://example.com/avatar.jpg",
        resumeUrl = "https://example.com/resume.pdf",
        email = "raypranav718@gmail.com"
    )

    private val fakeFeatured = listOf(
        Project(
            id = "tunneru",
            title = "tunneru",
            description = "tunneling engine in Go",
            longDescription = "long desc",
            techStack = listOf("Go", "WebSockets"),
            status = "Live",
            githubUrl = "https://github.com/pranav718/tunneru"
        )
    )

    private class FakeRepo(
        private val profileResult: Result<Profile>,
        private val featuredResult: Result<List<Project>>
    ) : PortfolioRepository {
        override suspend fun getProfile(): Result<Profile> = profileResult
        override suspend fun getProjects(): Result<List<Project>> = Result.success(emptyList())
        override suspend fun getProjectById(id: String): Result<Project?> = Result.success(null)
        override suspend fun getFeaturedProjects(): Result<List<Project>> = featuredResult
        override suspend fun getArticles(): Result<List<Article>> = Result.success(emptyList())
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
    fun `viewModel emits Success state when repository succeeds`() = runTest {
        val repo = FakeRepo(Result.success(fakeProfile), Result.success(fakeFeatured))
        val viewModel = HomeViewModel(repo)

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is HomeUiState.Success)
        val success = state as HomeUiState.Success
        assertEquals("Pranav Ray", success.profile.name)
        assertEquals(1, success.featuredProjects.size)
        assertEquals("tunneru", success.featuredProjects.first().id)
    }

    @Test
    fun `viewModel emits Error state when profile fails to load`() = runTest {
        val repo = FakeRepo(Result.failure(RuntimeException("Network error")), Result.success(fakeFeatured))
        val viewModel = HomeViewModel(repo)

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is HomeUiState.Error)
        assertEquals("Network error", (state as HomeUiState.Error).message)
    }
}
