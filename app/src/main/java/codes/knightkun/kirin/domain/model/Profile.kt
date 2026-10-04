package codes.knightkun.kirin.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class SocialLink(
    val id: String,
    val platform: String,
    val username: String,
    val url: String
)

@Serializable
data class Profile(
    val name: String,
    val title: String,
    val bio: String,
    val aboutFull: String,
    val quote: String,
    val avatarUrl: String,
    val resumeUrl: String,
    val email: String,
    val socials: List<SocialLink> = emptyList()
)
