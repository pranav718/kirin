package codes.knightkun.kirin.data.datasource

import codes.knightkun.kirin.domain.model.PortfolioData

interface PortfolioDataSource {
    suspend fun getPortfolioData(): PortfolioData
}
