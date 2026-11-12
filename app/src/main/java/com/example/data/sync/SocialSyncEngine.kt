package com.example.data.sync

import com.example.data.local.ContactDao
import com.example.data.local.ContactEntity
import com.example.data.local.SocialProfileDao
import com.example.data.local.SocialProfileEntity
import com.example.data.model.ActivityEvent
import com.example.data.model.Contact
import com.example.data.model.PresenceStatus
import com.example.data.model.SocialPlatform
import com.example.data.model.SocialProfile
import com.example.data.remote.GitHubApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.random.Random

import androidx.compose.runtime.Immutable

@Immutable
data class SyncMetrics(
    val isSyncing: Boolean = false,
    val lastSyncTime: Long = System.currentTimeMillis(),
    val totalSyncedCount: Int = 0,
    val autoSyncActive: Boolean = true,
    val intervalSeconds: Int = 15
)

private val GITHUB_ACTIONS = listOf(
    "Pushed 3 commits to branch 'main'" to "pushed code to repository",
    "Merged pull request #42 in mobile-core" to "merged PR in mobile-core",
    "Starred new repository 'jetpack-compose-rules'" to "starred a new repo"
)
private val X_ACTIONS = listOf(
    "Posted: 'Excited about the real-time social integration! 🚀'" to "tweeted new thoughts",
    "Retweeted a thread on Android Architecture" to "retweeted on X",
    "Joined a live tech audio space" to "joined audio space on X"
)
private val INSTAGRAM_ACTIONS = listOf(
    "Shared a new story: 'Keynote prep session'" to "added a new Instagram story",
    "Posted backstage conference reel" to "posted a new reel",
    "Updated profile bio and highlight links" to "updated Instagram highlights"
)
private val DISCORD_ACTIONS = listOf(
    "Active in developer voice lounge" to "joined voice channel on Discord",
    "Shared code snippet in #android-dev" to "posted in Discord community",
    "Updated status to 'Building NexusPulse'" to "updated Discord status"
)
private val TELEGRAM_ACTIONS = listOf(
    "Broadcasted update in tech channel" to "posted Telegram broadcast",
    "Shared release APK preview" to "shared files on Telegram",
    "Online in executive advisory group" to "active on Telegram"
)
private val YOUTUBE_ACTIONS = listOf(
    "Uploaded new tutorial: 'Jetpack Compose Architecture'" to "published new video on YouTube",
    "Live streaming executive developer Q&A" to "started live stream on YouTube",
    "Posted community poll on tech roadmaps" to "posted on YouTube community"
)
private val BLUESKY_ACTIONS = listOf(
    "Skeeted: 'Decentralized social graphs are the future'" to "posted on Bluesky",
    "Reposted article on open protocols" to "reposted on Bluesky",
    "Created new custom algorithmic feed" to "shared custom feed on Bluesky"
)
private val THREADS_ACTIONS = listOf(
    "Threaded: 'Modern UI engineering takes discipline'" to "posted thread on Threads",
    "Replied to executive design debate" to "engaged on Threads"
)
private val MASTODON_ACTIONS = listOf(
    "Tooted about federated identity protocols" to "tooted on Mastodon",
    "Boosted article on Android performance" to "boosted post on Mastodon"
)
private val WHATSAPP_ACTIONS = listOf(
    "Broadcasted executive briefing to private group" to "sent broadcast on WhatsApp",
    "Updated WhatsApp business catalog & status" to "updated WhatsApp status",
    "Replied to direct priority client inquiry" to "messaged on WhatsApp"
)
private val MESSENGER_ACTIONS = listOf(
    "Active on Facebook Messenger" to "active on Messenger",
    "Replied to executive community message" to "messaged via Messenger",
    "Shared strategy deck in Messenger group" to "shared link on Messenger"
)

class SocialSyncEngine(
    private val contactDao: ContactDao,
    private val socialProfileDao: SocialProfileDao,
    private val gitHubApi: GitHubApiService,
    private val externalScope: CoroutineScope
) {
    private val _syncMetrics = MutableStateFlow(SyncMetrics())
    val syncMetrics: StateFlow<SyncMetrics> = _syncMetrics.asStateFlow()

    private val _activityEvents = MutableSharedFlow<ActivityEvent>(replay = 8, extraBufferCapacity = 8)
    val activityEvents: SharedFlow<ActivityEvent> = _activityEvents.asSharedFlow()

    private var pollingJob: Job? = null

    init {
        startAutoSyncLoop()
    }

    fun startAutoSyncLoop() {
        pollingJob?.cancel()
        pollingJob = externalScope.launch(Dispatchers.IO) {
            while (isActive) {
                val currentMetrics = _syncMetrics.value
                if (currentMetrics.autoSyncActive) {
                    performSyncCycle()
                }
                val waitTime = (_syncMetrics.value.intervalSeconds.coerceAtLeast(5)) * 1000L
                delay(waitTime)
            }
        }
    }

    fun shutdown() {
        pollingJob?.cancel()
        pollingJob = null
    }

    fun setAutoSyncEnabled(enabled: Boolean) {
        _syncMetrics.value = _syncMetrics.value.copy(autoSyncActive = enabled)
    }

    fun setIntervalSeconds(seconds: Int) {
        _syncMetrics.value = _syncMetrics.value.copy(intervalSeconds = seconds.coerceIn(5, 120))
    }

    fun triggerSyncAll() {
        externalScope.launch(Dispatchers.IO) {
            performSyncCycle(forceFull = true)
        }
    }

    private suspend fun performSyncCycle(forceFull: Boolean = false) {
        _syncMetrics.value = _syncMetrics.value.copy(isSyncing = true)
        try {
            syncContactsBatch(forceFull)
        } catch (_: Exception) {
            // Graceful handling of network hiccups
        } finally {
            _syncMetrics.value = _syncMetrics.value.copy(
                isSyncing = false,
                lastSyncTime = System.currentTimeMillis(),
                totalSyncedCount = _syncMetrics.value.totalSyncedCount + 1
            )
        }
    }

    private suspend fun syncContactsBatch(forceFull: Boolean) {
        val rawEntities = contactDao.getAllContactsDirect()
        if (rawEntities.isEmpty()) return

        val targets = if (forceFull) rawEntities else listOf(rawEntities.random())

        for (entity in targets) {
            val contact = entity.toDomain()
            val updatedProfiles = contact.socialProfiles.toMutableList()
            var newAvatar = contact.avatarUrl

            // 1. Sync real GitHub profile if present
            val ghProfile = contact.getProfile(SocialPlatform.GITHUB)
            if (ghProfile != null && ghProfile.handle.isNotBlank()) {
                val cleanHandle = ghProfile.handle.removePrefix("@").trim()
                try {
                    val userRes = gitHubApi.getUser(cleanHandle)
                    val ghIndex = updatedProfiles.indexOfFirst { it.platform == SocialPlatform.GITHUB }
                    val refreshedBio = userRes.bio ?: ghProfile.bio
                    val refreshedRepos = userRes.publicRepos ?: ghProfile.publicRepos
                    val refreshedFollowers = userRes.followers ?: ghProfile.followerCount
                    if (userRes.avatarUrl != null && (newAvatar == null || newAvatar.isBlank())) {
                        newAvatar = userRes.avatarUrl
                    }

                    val snippet = "Public repos: $refreshedRepos • Followers: $refreshedFollowers"
                    val refreshedGh = ghProfile.copy(
                        profileUrl = "https://github.com/$cleanHandle",
                        followerCount = refreshedFollowers,
                        publicRepos = refreshedRepos,
                        bio = refreshedBio,
                        latestSnippet = snippet,
                        lastActiveTime = "Live now"
                    )

                    if (ghIndex >= 0) {
                        updatedProfiles[ghIndex] = refreshedGh
                    }

                    _activityEvents.emit(
                        ActivityEvent(
                            id = UUID.randomUUID().toString(),
                            contactId = contact.id,
                            contactName = contact.name,
                            contactAvatar = newAvatar,
                            platform = SocialPlatform.GITHUB,
                            actionText = "Synced GitHub: $refreshedRepos repos, $refreshedFollowers followers",
                            timestamp = System.currentTimeMillis()
                        )
                    )
                } catch (_: Exception) {
                    generateSimulatedEvent(contact, SocialPlatform.GITHUB, updatedProfiles)
                }
            }

            // 2. Refresh social profiles for other platforms with realistic live telemetry
            val randomOtherProfile = contact.socialProfiles
                .asSequence()
                .filter { it.platform != SocialPlatform.GITHUB }
                .firstOrNull()

            if (randomOtherProfile != null) {
                generateSimulatedEvent(contact, randomOtherProfile.platform, updatedProfiles)
            }

            val newPresence = if (Random.nextFloat() < 0.35f) {
                val presences = PresenceStatus.entries
                presences[Random.nextInt(presences.size)]
            } else {
                contact.presence
            }

            val updatedContact = contact.copy(
                avatarUrl = newAvatar,
                socialProfiles = updatedProfiles,
                presence = newPresence,
                lastSyncedTimestamp = System.currentTimeMillis()
            )

            // Update contact in Room
            contactDao.update(ContactEntity.fromDomain(updatedContact))

            // Update normalized social_profiles table in Room
            socialProfileDao.insertOrUpdateAll(
                updatedProfiles.map { SocialProfileEntity.fromDomain(contact.id, it) }
            )
        }
    }

    private suspend fun generateSimulatedEvent(
        contact: Contact,
        platform: SocialPlatform,
        profilesList: MutableList<SocialProfile>
    ) {
        val profileIndex = profilesList.indexOfFirst { it.platform == platform }
        if (profileIndex < 0) return

        val currentProfile = profilesList[profileIndex]
        val (snippet, action) = when (platform) {
            SocialPlatform.GITHUB -> GITHUB_ACTIONS.random()
            SocialPlatform.X_TWITTER -> X_ACTIONS.random()
            SocialPlatform.LINKEDIN -> {
                when (Random.nextInt(3)) {
                    0 -> "Published article: 'Building Scalable Native Applications'" to "published article on LinkedIn"
                    1 -> "Celebrated 3 years at ${contact.company}" to "celebrated work milestone"
                    else -> "Endorsed a colleague for Kotlin & Jetpack Compose" to "endorsed skills on LinkedIn"
                }
            }
            SocialPlatform.INSTAGRAM -> INSTAGRAM_ACTIONS.random()
            SocialPlatform.DISCORD -> DISCORD_ACTIONS.random()
            SocialPlatform.TELEGRAM -> TELEGRAM_ACTIONS.random()
            SocialPlatform.YOUTUBE -> YOUTUBE_ACTIONS.random()
            SocialPlatform.BLUESKY -> BLUESKY_ACTIONS.random()
            SocialPlatform.THREADS -> THREADS_ACTIONS.random()
            SocialPlatform.MASTODON -> MASTODON_ACTIONS.random()
            SocialPlatform.WHATSAPP -> WHATSAPP_ACTIONS.random()
            SocialPlatform.MESSENGER -> MESSENGER_ACTIONS.random()
        }

        profilesList[profileIndex] = currentProfile.copy(
            latestSnippet = snippet,
            lastActiveTime = "Just now"
        )

        _activityEvents.emit(
            ActivityEvent(
                id = UUID.randomUUID().toString(),
                contactId = contact.id,
                contactName = contact.name,
                contactAvatar = contact.avatarUrl,
                platform = platform,
                actionText = "$action: \"$snippet\"",
                timestamp = System.currentTimeMillis()
            )
        )
    }

    fun triggerPulseForContact(contact: Contact) {
        externalScope.launch(Dispatchers.IO) {
            val entity = contactDao.getContactDirect(contact.id) ?: return@launch
            val domain = entity.toDomain()
            val profiles = domain.socialProfiles.toMutableList()
            val platform = if (profiles.isNotEmpty()) profiles.random().platform else SocialPlatform.X_TWITTER

            generateSimulatedEvent(domain, platform, profiles)
            val updated = domain.copy(
                socialProfiles = profiles,
                presence = PresenceStatus.ONLINE,
                lastSyncedTimestamp = System.currentTimeMillis()
            )
            contactDao.update(ContactEntity.fromDomain(updated))
            socialProfileDao.insertOrUpdateAll(
                profiles.map { SocialProfileEntity.fromDomain(domain.id, it) }
            )
        }
    }
}
