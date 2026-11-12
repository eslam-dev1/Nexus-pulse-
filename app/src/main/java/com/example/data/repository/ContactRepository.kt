package com.example.data.repository

import com.example.data.local.ContactDao
import com.example.data.local.ContactEntity
import com.example.data.local.SocialProfileDao
import com.example.data.local.SocialProfileEntity
import com.example.data.model.Contact
import com.example.data.model.PresenceStatus
import com.example.data.model.SocialPlatform
import com.example.data.model.SocialProfile
import com.example.data.sync.SocialSyncEngine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ContactRepository(
    private val contactDao: ContactDao,
    private val socialProfileDao: SocialProfileDao,
    val syncEngine: SocialSyncEngine
) {
    val allContacts: Flow<List<Contact>> = contactDao.getContactsWithProfiles().map { relations ->
        relations.map { it.toDomain() }
    }

    fun getContact(id: Long): Flow<Contact?> = contactDao.getContactWithProfiles(id).map { it?.toDomain() }

    suspend fun insertContact(contact: Contact): Long {
        val contactId = contactDao.insert(ContactEntity.fromDomain(contact))
        if (contact.socialProfiles.isNotEmpty()) {
            val entities = contact.socialProfiles.map { SocialProfileEntity.fromDomain(contactId, it) }
            socialProfileDao.insertOrUpdateAll(entities)
        }
        syncEngine.triggerSyncAll()
        return contactId
    }

    suspend fun updateContact(contact: Contact) {
        contactDao.update(ContactEntity.fromDomain(contact))
        socialProfileDao.deleteByContact(contact.id)
        if (contact.socialProfiles.isNotEmpty()) {
            val entities = contact.socialProfiles.map { SocialProfileEntity.fromDomain(contact.id, it) }
            socialProfileDao.insertOrUpdateAll(entities)
        }
    }

    suspend fun deleteContact(id: Long) {
        // Foreign key CASCADE will also delete related records in social_profiles
        contactDao.deleteById(id)
    }

    suspend fun toggleFavorite(contact: Contact) {
        contactDao.updateFavoriteStatus(contact.id, !contact.isFavorite)
    }

    suspend fun seedInitialContactsIfNeeded() {
        if (contactDao.getCount() == 0) {
            val starterContacts = listOf(
                Contact(
                    name = "Linus Torvalds",
                    title = "Linux Kernel Creator & Fellow",
                    company = "Linux Foundation",
                    email = "torvalds@linux-foundation.org",
                    phone = "+1 (555) 019-2834",
                    avatarUrl = "https://avatars.githubusercontent.com/u/1024025?v=4",
                    tags = listOf("Open Source", "VIP", "Linux"),
                    presence = PresenceStatus.ONLINE,
                    socialProfiles = listOf(
                        SocialProfile(
                            platform = SocialPlatform.GITHUB,
                            handle = "torvalds",
                            profileUrl = "https://github.com/torvalds",
                            followerCount = 230000,
                            publicRepos = 7,
                            bio = "Creator of Linux and Git",
                            verified = true,
                            latestSnippet = "Tagged release v6.14-rc1",
                            lastActiveTime = "Active now"
                        ),
                        SocialProfile(
                            platform = SocialPlatform.LINKEDIN,
                            handle = "linus-torvalds",
                            profileUrl = "https://linkedin.com/in/linus-torvalds",
                            followerCount = 420000,
                            bio = "Linux Foundation Fellow",
                            verified = true,
                            latestSnippet = "Discussed Git branching performance",
                            lastActiveTime = "1h ago"
                        )
                    ),
                    notes = "Pioneer in distributed version control and Unix architecture.",
                    isFavorite = true
                ),
                Contact(
                    name = "Jake Wharton",
                    title = "Principal Android Systems Engineer",
                    company = "Autonomous",
                    email = "jake@wharton.codes",
                    phone = "+1 (555) 082-9943",
                    avatarUrl = "https://avatars.githubusercontent.com/u/66577?v=4",
                    tags = listOf("Android", "Kotlin", "Tech Lead"),
                    presence = PresenceStatus.ONLINE,
                    socialProfiles = listOf(
                        SocialProfile(
                            platform = SocialPlatform.GITHUB,
                            handle = "JakeWharton",
                            profileUrl = "https://github.com/JakeWharton",
                            followerCount = 68000,
                            publicRepos = 140,
                            bio = "Android & Kotlin open source author",
                            verified = true,
                            latestSnippet = "Updated dependency graph in Molecule",
                            lastActiveTime = "Just now"
                        ),
                        SocialProfile(
                            platform = SocialPlatform.X_TWITTER,
                            handle = "JakeWharton",
                            profileUrl = "https://x.com/JakeWharton",
                            followerCount = 115000,
                            verified = true,
                            latestSnippet = "Looking at Kotlin compiler IR transformations",
                            lastActiveTime = "4m ago"
                        ),
                        SocialProfile(
                            platform = SocialPlatform.BLUESKY,
                            handle = "jakewharton.com",
                            profileUrl = "https://bsky.app/profile/jakewharton.com",
                            followerCount = 42000,
                            verified = true,
                            latestSnippet = "Working on Kotlin compiler plugins and IR optimization",
                            lastActiveTime = "12m ago"
                        )
                    ),
                    notes = "Creator of ButterKnife, RxAndroid, and prolific Kotlin developer.",
                    isFavorite = true
                ),
                Contact(
                    name = "Elena Rostova",
                    title = "VP of Product & AI Strategy",
                    company = "Aether Dynamics",
                    email = "elena.rostova@aether.ai",
                    phone = "+1 (415) 890-3412",
                    avatarUrl = "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?w=300",
                    tags = listOf("Executive", "AI Strategy", "Advisor"),
                    presence = PresenceStatus.AWAY,
                    socialProfiles = listOf(
                        SocialProfile(
                            platform = SocialPlatform.LINKEDIN,
                            handle = "elena-rostova-ai",
                            profileUrl = "https://linkedin.com/in/elena-rostova-ai",
                            followerCount = 38400,
                            bio = "Scaling multimodal intelligence for enterprise operations.",
                            verified = true,
                            latestSnippet = "Keynote at AI Enterprise Forum in San Francisco",
                            lastActiveTime = "25m ago"
                        ),
                        SocialProfile(
                            platform = SocialPlatform.X_TWITTER,
                            handle = "ElenaAI_Pulse",
                            profileUrl = "https://x.com/ElenaAI_Pulse",
                            followerCount = 24100,
                            verified = false,
                            latestSnippet = "Real-time edge intelligence is transforming device UX.",
                            lastActiveTime = "1h ago"
                        ),
                        SocialProfile(
                            platform = SocialPlatform.INSTAGRAM,
                            handle = "elena_exec_life",
                            profileUrl = "https://instagram.com/elena_exec_life",
                            followerCount = 12500,
                            verified = false,
                            latestSnippet = "Morning panel discussion in Silicon Valley",
                            lastActiveTime = "3h ago"
                        )
                    ),
                    notes = "Met at Stanford AI summit. Interested in edge synchronization frameworks."
                ),
                Contact(
                    name = "Marcus Vance",
                    title = "Chief Technology Officer",
                    company = "FinPulse Global",
                    email = "mvance@finpulse.io",
                    phone = "+1 (212) 441-9876",
                    avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=300",
                    tags = listOf("Executive", "Fintech", "Board"),
                    presence = PresenceStatus.ONLINE,
                    socialProfiles = listOf(
                        SocialProfile(
                            platform = SocialPlatform.LINKEDIN,
                            handle = "marcus-vance-cto",
                            profileUrl = "https://linkedin.com/in/marcus-vance-cto",
                            followerCount = 52000,
                            bio = "Architecting zero-latency financial networks",
                            verified = true,
                            latestSnippet = "Announced Series B closing of $45M",
                            lastActiveTime = "Just now"
                        ),
                        SocialProfile(
                            platform = SocialPlatform.GITHUB,
                            handle = "mvance",
                            profileUrl = "https://github.com/mvance",
                            followerCount = 1200,
                            publicRepos = 24,
                            bio = "Systems engineer & distributed ledger research",
                            verified = false,
                            latestSnippet = "Merged PR on zero-copy network buffers",
                            lastActiveTime = "12m ago"
                        )
                    ),
                    notes = "Leading the technical advisory committee."
                ),
                Contact(
                    name = "Aria Chen",
                    title = "Design Systems & Creative Lead",
                    company = "NeoStudio Tokyo",
                    email = "aria.chen@neostudio.jp",
                    phone = "+81 90-1234-5678",
                    avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=300",
                    tags = listOf("Design", "Creative", "Tokyo"),
                    presence = PresenceStatus.RECENTLY_ACTIVE,
                    socialProfiles = listOf(
                        SocialProfile(
                            platform = SocialPlatform.INSTAGRAM,
                            handle = "aria_designs",
                            profileUrl = "https://instagram.com/aria_designs",
                            followerCount = 48000,
                            bio = "Crafting dark-mode interfaces & motion typography",
                            verified = true,
                            latestSnippet = "Tokyo Design Week sneak preview",
                            lastActiveTime = "18m ago"
                        ),
                        SocialProfile(
                            platform = SocialPlatform.X_TWITTER,
                            handle = "AriaVisuals",
                            profileUrl = "https://x.com/AriaVisuals",
                            followerCount = 31200,
                            verified = false,
                            latestSnippet = "Asymmetry and contrast are the future of mobile UI.",
                            lastActiveTime = "45m ago"
                        ),
                        SocialProfile(
                            platform = SocialPlatform.GITHUB,
                            handle = "ariachen",
                            profileUrl = "https://github.com/ariachen",
                            followerCount = 940,
                            publicRepos = 18,
                            bio = "Front-end creative developer",
                            verified = false,
                            latestSnippet = "Published Jetpack Compose dark theme tokens",
                            lastActiveTime = "2h ago"
                        )
                    ),
                    notes = "Collaborated on Tokyo typography guidelines."
                )
            )

            starterContacts.forEach { starter ->
                val id = contactDao.insert(ContactEntity.fromDomain(starter))
                val profileEntities = starter.socialProfiles.map {
                    SocialProfileEntity.fromDomain(id, it)
                }
                socialProfileDao.insertOrUpdateAll(profileEntities)
            }
            syncEngine.triggerSyncAll()
        }
    }
}
