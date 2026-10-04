package codes.knightkun.kirin.di

import codes.knightkun.kirin.domain.repository.PortfolioRepository

interface AppContainer {
    val portfolioRepository: PortfolioRepository
}
