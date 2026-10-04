package codes.knightkun.kirin.di

import android.content.Context
import codes.knightkun.kirin.data.datasource.JsonAssetDataSource
import codes.knightkun.kirin.data.repository.PortfolioRepositoryImpl
import codes.knightkun.kirin.domain.repository.PortfolioRepository

class DefaultAppContainer(private val context: Context) : AppContainer {

    private val jsonAssetDataSource by lazy {
        JsonAssetDataSource(context)
    }

    override val portfolioRepository: PortfolioRepository by lazy {
        PortfolioRepositoryImpl(jsonAssetDataSource)
    }
}
