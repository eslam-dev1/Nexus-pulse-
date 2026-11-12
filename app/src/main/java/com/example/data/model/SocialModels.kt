package com.example.data.model

import androidx.compose.runtime.Immutable

enum class SocialPlatform(
    val displayName: String,
    val urlPrefix: String,
    val defaultIconSymbol: String
) {
    GITHUB("GitHub", "https://github.com/", "GH"),
    X_TWITTER("X / Twitter", "https://x.com/", "X"),
    LINKEDIN("LinkedIn", "https://linkedin.com/in/", "IN"),
    INSTAGRAM("Instagram", "https://instagram.com/", "IG"),
    DISCORD("Discord", "https://discord.com/users/", "DC"),
    TELEGRAM("Telegram", "https://t.me/", "TG"),
    YOUTUBE("YouTube", "https://youtube.com/@", "YT"),
    BLUESKY("Bluesky", "https://bsky.app/profile/", "BS"),
    THREADS("Threads", "https://threads.net/@", "TH"),
    MASTODON("Mastodon", "https://mastodon.social/@", "MD"),
    WHATSAPP("WhatsApp", "https://wa.me/", "WA"),
    MESSENGER("Messenger", "https://m.me/", "MS");

    fun fullUrl(handle: String): String {
        var clean = handle.removePrefix("@").trim()
        if (this == WHATSAPP) {
            clean = clean.filter { it.isDigit() }
        }
        return "$urlPrefix$clean"
    }
}

enum class PresenceStatus(val label: String) {
    ONLINE("Active Now"),
    RECENTLY_ACTIVE("Recently Active"),
    AWAY("Away"),
    OFFLINE("Offline")
}

@Immutable
data class SocialProfile(
    val platform: SocialPlatform,
    val handle: String,
    val profileUrl: String = "",
    val followerCount: Int = 0,
    val publicRepos: Int = 0,
    val bio: String? = null,
    val verified: Boolean = false,
    val latestSnippet: String? = null,
    val lastActiveTime: String = "Just now"
)

@Immutable
data class ActivityEvent(
    val id: String,
    val contactId: Long,
    val contactName: String,
    val contactAvatar: String?,
    val platform: SocialPlatform,
    val actionText: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Immutable
data class Contact(
    val id: Long = 0,
    val name: String,
    val title: String,
    val company: String,
    val email: String,
    val phone: String,
    val avatarUrl: String?,
    val tags: List<String> = emptyList(),
    val presence: PresenceStatus = PresenceStatus.ONLINE,
    val socialProfiles: List<SocialProfile> = emptyList(),
    val notes: String = "",
    val lastSyncedTimestamp: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
) {
    fun getProfile(platform: SocialPlatform): SocialProfile? =
        socialProfiles.firstOrNull { it.platform == platform }

    val initials: String = buildString(2) {
        var takeNext = true
        for (i in 0 until name.length) {
            val ch = name[i]
            if (ch.isWhitespace()) {
                takeNext = true
            } else if (takeNext) {
                append(ch.uppercaseChar())
                takeNext = false
                if (length == 2) break
            }
        }
        if (isEmpty()) append('C')
    }
}
