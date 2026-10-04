package codes.knightkun.kirin.data.datasource

import android.content.Context
import codes.knightkun.kirin.domain.model.PortfolioData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class JsonAssetDataSource(
    private val context: Context,
    private val json: Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }
) : PortfolioDataSource {

    override suspend fun getPortfolioData(): PortfolioData = withContext(Dispatchers.IO) {
        val jsonString = context.assets.open("portfolio.json").bufferedReader().use { it.readText() }
        json.decodeFromString<PortfolioData>(jsonString)
    }
}
