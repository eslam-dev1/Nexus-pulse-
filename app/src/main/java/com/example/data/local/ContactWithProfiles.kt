package com.example.data.local

import androidx.room.Embedded
import androidx.room.Relation
import com.example.data.model.Contact
import com.example.data.model.PresenceStatus
import org.json.JSONArray

data class ContactWithProfiles(
    @Embedded val contact: ContactEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "contactId"
    )
    val profiles: List<SocialProfileEntity> = emptyList()
) {
    fun toDomain(): Contact {
        val tagsList = mutableListOf<String>()
        try {
            val jsonArray = JSONArray(contact.tagsJson)
            for (i in 0 until jsonArray.length()) {
                tagsList.add(jsonArray.getString(i))
            }
        } catch (_: Exception) {}

        val presence = try {
            PresenceStatus.valueOf(contact.presenceName)
        } catch (_: Exception) {
            PresenceStatus.ONLINE
        }

        val domainProfiles = if (profiles.isNotEmpty()) {
            profiles.map { it.toDomain() }
        } else {
            contact.toDomain().socialProfiles
        }

        return Contact(
            id = contact.id,
            name = contact.name,
            title = contact.title,
            company = contact.company,
            email = contact.email,
            phone = contact.phone,
            avatarUrl = contact.avatarUrl,
            tags = tagsList,
            presence = presence,
            socialProfiles = domainProfiles,
            notes = contact.notes,
            lastSyncedTimestamp = contact.lastSyncedTimestamp,
            isFavorite = contact.isFavorite
        )
    }
}
