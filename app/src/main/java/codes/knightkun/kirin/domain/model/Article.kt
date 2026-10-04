package codes.knightkun.kirin.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Article(
    val id: String,
    val title: String,
    val url: String,
    val date: String,
    val claps: Int,
    val tags: List<String> = emptyList()
) {
    val formattedClaps: String
        get() = "$claps claps"
}
