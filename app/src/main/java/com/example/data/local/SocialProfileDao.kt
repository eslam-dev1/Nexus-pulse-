package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SocialProfileDao {
    @Query("SELECT * FROM social_profiles WHERE contactId = :contactId")
    fun getProfilesByContact(contactId: Long): Flow<List<SocialProfileEntity>>

    @Query("SELECT * FROM social_profiles WHERE contactId = :contactId")
    suspend fun getProfilesByContactDirect(contactId: Long): List<SocialProfileEntity>

    @Query("SELECT * FROM social_profiles WHERE contactId = :contactId AND platform = :platform LIMIT 1")
    suspend fun getProfileByPlatform(contactId: Long, platform: String): SocialProfileEntity?

    @Query("SELECT * FROM social_profiles WHERE platform = :platform")
    fun getProfilesForPlatform(platform: String): Flow<List<SocialProfileEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(profile: SocialProfileEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateAll(profiles: List<SocialProfileEntity>)

    @Update
    suspend fun update(profile: SocialProfileEntity)

    @Query("""
        UPDATE social_profiles 
        SET followerCount = :followers,
            publicRepos = :repos,
            bio = :bio,
            latestSnippet = :snippet,
            lastActiveTime = :lastActive,
            lastSyncedAt = :syncedAt
        WHERE contactId = :contactId AND platform = :platform
    """)
    suspend fun updateMetadata(
        contactId: Long,
        platform: String,
        followers: Int,
        repos: Int,
        bio: String?,
        snippet: String?,
        lastActive: String,
        syncedAt: Long
    )

    @Query("DELETE FROM social_profiles WHERE contactId = :contactId")
    suspend fun deleteByContact(contactId: Long)

    @Query("DELETE FROM social_profiles WHERE profileId = :profileId")
    suspend fun deleteById(profileId: Long)

    @Query("SELECT COUNT(*) FROM social_profiles")
    suspend fun getCount(): Int
}
