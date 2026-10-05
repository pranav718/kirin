package codes.knightkun.kirin.presentation.detail

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
class ProjectDetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val fakeProject = Project(
        id = "tunneru",
        title = "tunneru",
        description = "Lightweight zero-dependency tunneling engine in Go",
        longDescription = "tunneru is a lightweight zero-dependency tunneling engine...",
        techStack = listOf("Go", "WebSockets", "TCP"),
        status = "Live",
        githubUrl = "https://github.com/pranav718/tunneru",
        liveUrl = "https://tunneru.knightkun.codes"
    )

    private class FakeRepo(
        private val project: Project?,
        private val shouldFail: Boolean = false
    ) : PortfolioRepository {
        override suspend fun getProfile(): Result<Profile> = Result.failure(Exception())
        override suspend fun getProjects(): Result<List<Project>> = Result.success(emptyList())
        override suspend fun getProjectById(id: String): Result<Project?> {
            if (shouldFail) return Result.failure(RuntimeException("Network error"))
            return Result.success(if (project?.id == id) project else null)
        }
        override suspend fun getFeaturedProjects(): Result<List<Project>> = Result.success(emptyList())
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
    fun `viewModel loads project successfully when found`() = runTest {
        val repo = FakeRepo(fakeProject)
        val viewModel = ProjectDetailViewModel(repo, "tunneru")

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is ProjectDetailUiState.Success)
        val success = state as ProjectDetailUiState.Success
        assertEquals("tunneru", success.project.id)
        assertEquals("tunneru", success.project.title)
    }

    @Test
    fun `viewModel emits error when project is not found`() = runTest {
        val repo = FakeRepo(fakeProject)
        val viewModel = ProjectDetailViewModel(repo, "unknown_project")

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is ProjectDetailUiState.Error)
        val error = state as ProjectDetailUiState.Error
        assertTrue(error.message.contains("not found"))
    }

    @Test
    fun `viewModel emits error when repository call fails`() = runTest {
        val repo = FakeRepo(null, shouldFail = true)
        val viewModel = ProjectDetailViewModel(repo, "tunneru")

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is ProjectDetailUiState.Error)
        val error = state as ProjectDetailUiState.Error
        assertEquals("Network error", error.message)
    }
}
