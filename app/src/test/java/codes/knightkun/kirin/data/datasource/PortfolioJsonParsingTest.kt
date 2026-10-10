package codes.knightkun.kirin.data.datasource

import codes.knightkun.kirin.domain.model.PortfolioData
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class PortfolioJsonParsingTest {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    @Test
    fun `portfolio json parses successfully and contains all 11 projects, 3 articles, and 31 skills`() {
        val fileCandidates = listOf(
            File("src/main/assets/portfolio.json"),
            File("app/src/main/assets/portfolio.json"),
            File("../app/src/main/assets/portfolio.json")
        )
        val file = fileCandidates.firstOrNull { it.exists() }
        assertNotNull("Could not find portfolio.json in expected asset paths", file)

        val jsonString = file!!.readText()
        val portfolioData = json.decodeFromString<PortfolioData>(jsonString)

        // Profile assertions
        assertEquals("Pranav Ray", portfolioData.profile.name)
        assertEquals("inside, i'm infinite", portfolioData.profile.quote)
        assertTrue(portfolioData.profile.socials.isNotEmpty())

        // Projects assertions: 11 projects total (9 live, 2 in progress)
        assertEquals(11, portfolioData.projects.size)
        val liveCount = portfolioData.projects.count { it.isLive }
        val progressCount = portfolioData.projects.count { !it.isLive }
        assertEquals(9, liveCount)
        assertEquals(2, progressCount)

        // Articles assertions: 3 technical articles
        assertEquals(3, portfolioData.articles.size)

        // Skills assertions: 31 categorized skills
        assertEquals(31, portfolioData.skills.size)
    }
}
