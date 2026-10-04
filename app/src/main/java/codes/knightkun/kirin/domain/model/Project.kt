package codes.knightkun.kirin.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Project(
    val id: String,
    val title: String,
    val description: String,
    val longDescription: String,
    val techStack: List<String> = emptyList(),
    val status: String,
    val githubUrl: String,
    val liveUrl: String? = null,
    val postUrl: String? = null,
    val image: String? = null,
    val videoUrl: String? = null
) {
    val isLive: Boolean
        get() = status.equals("Live", ignoreCase = true)
}
