package codes.knightkun.kirin.presentation.about

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
class AboutViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val fakeProfile = Profile(
        name = "Pranav Ray",
        title = "Systems & Distributed Backend",
        bio = "building concurrent architectures",
        aboutFull = "full bio narrative",
        quote = "inside, i'm infinite",
        avatarUrl = "https://example.com/avatar.jpg",
        resumeUrl = "https://example.com/resume.pdf",
        email = "raypranav718@gmail.com"
    )

    private val fakeSkills = listOf(
        Skill(name = "Go", category = SkillCategory.LANGUAGES),
        Skill(name = "Rust", category = SkillCategory.LANGUAGES),
        Skill(name = "Raft Consensus", category = SkillCategory.BACKEND),
        Skill(name = "PostgreSQL", category = SkillCategory.DATABASES),
        Skill(name = "Three.js", category = SkillCategory.FRONTEND),
        Skill(name = "Docker", category = SkillCategory.TOOLS)
    )

    private class FakeRepo(
        private val profileResult: Result<Profile>,
        private val skillsResult: Result<List<Skill>>
    ) : PortfolioRepository {
        override suspend fun getProfile(): Result<Profile> = profileResult
        override suspend fun getProjects(): Result<List<Project>> = Result.success(emptyList())
        override suspend fun getProjectById(id: String): Result<Project?> = Result.success(null)
        override suspend fun getFeaturedProjects(): Result<List<Project>> = Result.success(emptyList())
        override suspend fun getArticles(): Result<List<Article>> = Result.success(emptyList())
        override suspend fun getSkills(): Result<List<Skill>> = skillsResult
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
    fun `viewModel categorizes skills properly into all categories and emits Success`() = runTest {
        val repo = FakeRepo(Result.success(fakeProfile), Result.success(fakeSkills))
        val viewModel = AboutViewModel(repo)

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is AboutUiState.Success)
        val success = state as AboutUiState.Success
        assertEquals("Pranav Ray", success.profile.name)
        assertEquals(2, success.skillsByCategory[SkillCategory.LANGUAGES]?.size)
        assertEquals(1, success.skillsByCategory[SkillCategory.BACKEND]?.size)
        assertEquals(1, success.skillsByCategory[SkillCategory.DATABASES]?.size)
        assertEquals(1, success.skillsByCategory[SkillCategory.FRONTEND]?.size)
        assertEquals(1, success.skillsByCategory[SkillCategory.TOOLS]?.size)
    }

    @Test
    fun `viewModel emits Error when profile repository call fails`() = runTest {
        val repo = FakeRepo(Result.failure(RuntimeException("Database unreachable")), Result.success(fakeSkills))
        val viewModel = AboutViewModel(repo)

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is AboutUiState.Error)
        val error = state as AboutUiState.Error
        assertEquals("Database unreachable", error.message)
    }

    @Test
    fun `viewModel emits Error when skills repository call fails`() = runTest {
        val repo = FakeRepo(Result.success(fakeProfile), Result.failure(RuntimeException("Skills parse error")))
        val viewModel = AboutViewModel(repo)

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is AboutUiState.Error)
        val error = state as AboutUiState.Error
        assertEquals("Skills parse error", error.message)
    }
}
