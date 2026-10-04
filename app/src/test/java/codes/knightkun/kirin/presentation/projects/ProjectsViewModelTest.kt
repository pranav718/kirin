package codes.knightkun.kirin.presentation.projects

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
class ProjectsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

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
            techStack = listOf("Three.js", "TypeScript"),
            status = "In Progress",
            githubUrl = "https://github.com/pranav718/nanimo"
        )
    )

    private class FakeRepo(private val projects: List<Project>) : PortfolioRepository {
        override suspend fun getProfile(): Result<Profile> = Result.failure(Exception())
        override suspend fun getProjects(): Result<List<Project>> = Result.success(projects)
        override suspend fun getProjectById(id: String): Result<Project?> = Result.success(projects.find { it.id == id })
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
    fun `viewModel loads and emits all projects initially`() = runTest {
        val repo = FakeRepo(fakeProjects)
        val viewModel = ProjectsViewModel(repo)

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is ProjectsUiState.Success)
        val success = state as ProjectsUiState.Success
        assertEquals(2, success.filteredProjects.size)
    }

    @Test
    fun `viewModel filters live projects correctly`() = runTest {
        val repo = FakeRepo(fakeProjects)
        val viewModel = ProjectsViewModel(repo)

        advanceUntilIdle()
        viewModel.setFilter(ProjectFilter.LIVE)

        val state = viewModel.uiState.value as ProjectsUiState.Success
        assertEquals(1, state.filteredProjects.size)
        assertEquals("tunneru", state.filteredProjects.first().id)
    }

    @Test
    fun `viewModel filters by search query`() = runTest {
        val repo = FakeRepo(fakeProjects)
        val viewModel = ProjectsViewModel(repo)

        advanceUntilIdle()
        viewModel.setSearchQuery("WebSockets")

        val state = viewModel.uiState.value as ProjectsUiState.Success
        assertEquals(1, state.filteredProjects.size)
        assertEquals("tunneru", state.filteredProjects.first().id)
    }
}
