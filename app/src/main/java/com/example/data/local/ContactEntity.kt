package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.Contact
import com.example.data.model.PresenceStatus
import com.example.data.model.SocialPlatform
import com.example.data.model.SocialProfile
import org.json.JSONArray
import org.json.JSONObject

@Entity(tableName = "contacts")
data class ContactEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val title: String,
    val company: String,
    val email: String,
    val phone: String,
    val avatarUrl: String?,
    val tagsJson: String,
    val presenceName: String,
    val socialProfilesJson: String,
    val notes: String,
    val lastSyncedTimestamp: Long,
    val isFavorite: Boolean = false
) {
    fun toDomain(): Contact {
        val tagsList = mutableListOf<String>()
        try {
            val jsonArray = JSONArray(tagsJson)
            for (i in 0 until jsonArray.length()) {
                tagsList.add(jsonArray.getString(i))
            }
        } catch (_: Exception) {}

        val profilesList = mutableListOf<SocialProfile>()
        try {
            val jsonArray = JSONArray(socialProfilesJson)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val platformName = obj.optString("platform", SocialPlatform.GITHUB.name)
                val platform = try {
                    SocialPlatform.valueOf(platformName)
                } catch (_: Exception) {
                    SocialPlatform.GITHUB
                }
                profilesList.add(
                    SocialProfile(
                        platform = platform,
                        handle = obj.optString("handle", ""),
                        profileUrl = obj.optString("profileUrl", ""),
                        followerCount = obj.optInt("followerCount", 0),
                        publicRepos = obj.optInt("publicRepos", 0),
                        bio = obj.optString("bio", "").ifEmpty { null },
                        verified = obj.optBoolean("verified", false),
                        latestSnippet = obj.optString("latestSnippet", "").ifEmpty { null },
                        lastActiveTime = obj.optString("lastActiveTime", "Recently")
                    )
                )
            }
        } catch (_: Exception) {}

        val presence = try {
            PresenceStatus.valueOf(presenceName)
        } catch (_: Exception) {
            PresenceStatus.ONLINE
        }

        return Contact(
            id = id,
            name = name,
            title = title,
            company = company,
            email = email,
            phone = phone,
            avatarUrl = avatarUrl,
            tags = tagsList,
            presence = presence,
            socialProfiles = profilesList,
            notes = notes,
            lastSyncedTimestamp = lastSyncedTimestamp,
            isFavorite = isFavorite
        )
    }

    companion object {
        fun fromDomain(contact: Contact): ContactEntity {
            val tagsArr = JSONArray()
            contact.tags.forEach { tagsArr.put(it) }

            val profilesArr = JSONArray()
            contact.socialProfiles.forEach { profile ->
                val obj = JSONObject()
                obj.put("platform", profile.platform.name)
                obj.put("handle", profile.handle)
                obj.put("profileUrl", profile.profileUrl)
                obj.put("followerCount", profile.followerCount)
                obj.put("publicRepos", profile.publicRepos)
                obj.put("bio", profile.bio ?: "")
                obj.put("verified", profile.verified)
                obj.put("latestSnippet", profile.latestSnippet ?: "")
                obj.put("lastActiveTime", profile.lastActiveTime)
                profilesArr.put(obj)
            }

            return ContactEntity(
                id = contact.id,
                name = contact.name,
                title = contact.title,
                company = contact.company,
                email = contact.email,
                phone = contact.phone,
                avatarUrl = contact.avatarUrl,
                tagsJson = tagsArr.toString(),
                presenceName = contact.presence.name,
                socialProfilesJson = profilesArr.toString(),
                notes = contact.notes,
                lastSyncedTimestamp = contact.lastSyncedTimestamp,
                isFavorite = contact.isFavorite
            )
        }
    }
}
