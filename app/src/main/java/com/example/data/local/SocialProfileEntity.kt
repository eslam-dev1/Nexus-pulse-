package com.example.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.data.model.SocialPlatform
import com.example.data.model.SocialProfile

@Entity(
    tableName = "social_profiles",
    foreignKeys = [
        ForeignKey(
            entity = ContactEntity::class,
            parentColumns = ["id"],
            childColumns = ["contactId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["contactId"]),
        Index(value = ["contactId", "platform"], unique = true)
    ]
)
data class SocialProfileEntity(
    @PrimaryKey(autoGenerate = true)
    val profileId: Long = 0,
    val contactId: Long,
    val platform: String,
    val handle: String,
    val profileUrl: String,
    val followerCount: Int = 0,
    val publicRepos: Int = 0,
    val bio: String? = null,
    val verified: Boolean = false,
    val latestSnippet: String? = null,
    val lastActiveTime: String = "Recently",
    val lastSyncedAt: Long = System.currentTimeMillis()
) {
    fun toDomain(): SocialProfile {
        val platformEnum = try {
            SocialPlatform.valueOf(platform)
        } catch (_: Exception) {
            SocialPlatform.GITHUB
        }

        return SocialProfile(
            platform = platformEnum,
            handle = handle,
            profileUrl = profileUrl,
            followerCount = followerCount,
            publicRepos = publicRepos,
            bio = bio,
            verified = verified,
            latestSnippet = latestSnippet,
            lastActiveTime = lastActiveTime
        )
    }

    companion object {
        fun fromDomain(contactId: Long, profile: SocialProfile): SocialProfileEntity {
            return SocialProfileEntity(
                contactId = contactId,
                platform = profile.platform.name,
                handle = profile.handle,
                profileUrl = profile.profileUrl.ifEmpty { profile.platform.fullUrl(profile.handle) },
                followerCount = profile.followerCount,
                publicRepos = profile.publicRepos,
                bio = profile.bio,
                verified = profile.verified,
                latestSnippet = profile.latestSnippet,
                lastActiveTime = profile.lastActiveTime,
                lastSyncedAt = System.currentTimeMillis()
            )
        }
    }
}
