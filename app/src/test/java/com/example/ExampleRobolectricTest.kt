package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.ContactEntity
import com.example.data.local.ContactWithProfiles
import com.example.data.local.SocialProfileEntity
import com.example.data.model.Contact
import com.example.data.model.PresenceStatus
import com.example.data.model.SocialPlatform
import com.example.data.model.SocialProfile
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("NexusPulse", appName)
    }

    @Test
    fun `contact entity domain conversion integrity`() {
        val contact = Contact(
            id = 101L,
            name = "Linus Torvalds",
            title = "Fellow",
            company = "Linux Foundation",
            email = "torvalds@kernel.org",
            phone = "12345",
            avatarUrl = "https://example.com/avatar.jpg",
            tags = listOf("Kernel", "VIP"),
            presence = PresenceStatus.ONLINE,
            socialProfiles = listOf(
                SocialProfile(
                    platform = SocialPlatform.GITHUB,
                    handle = "torvalds",
                    followerCount = 200000,
                    publicRepos = 7
                )
            ),
            notes = "Creator of Git"
        )

        val entity = ContactEntity.fromDomain(contact)
        val domain = entity.toDomain()

        assertEquals("Linus Torvalds", domain.name)
        assertEquals("Fellow", domain.title)
        assertEquals(1, domain.socialProfiles.size)
        assertEquals(SocialPlatform.GITHUB, domain.socialProfiles[0].platform)
        assertEquals("torvalds", domain.socialProfiles[0].handle)
        assertEquals("https://github.com/torvalds", SocialPlatform.GITHUB.fullUrl("torvalds"))
    }

    @Test
    fun `relational ContactWithProfiles mapping integrity`() {
        val contactEntity = ContactEntity(
            id = 42L,
            name = "Sarah Lin",
            title = "Principal Engineer",
            company = "Nexus Systems",
            email = "sarah@nexus.io",
            phone = "+1-555-4321",
            avatarUrl = null,
            tagsJson = "[\"Mobile\",\"Staff\"]",
            presenceName = PresenceStatus.ONLINE.name,
            socialProfilesJson = "[]",
            notes = "Offline sync test",
            lastSyncedTimestamp = 123456789L
        )

        val profileEntities = listOf(
            SocialProfileEntity(
                profileId = 1L,
                contactId = 42L,
                platform = SocialPlatform.GITHUB.name,
                handle = "sarahlin",
                profileUrl = "https://github.com/sarahlin",
                followerCount = 1420,
                publicRepos = 45,
                bio = "Kotlin & Compose enthusiast",
                verified = true,
                latestSnippet = "Pushed to nexus-core",
                lastActiveTime = "5m ago"
            ),
            SocialProfileEntity(
                profileId = 2L,
                contactId = 42L,
                platform = SocialPlatform.X_TWITTER.name,
                handle = "sarah_codes",
                profileUrl = "https://x.com/sarah_codes",
                followerCount = 5300,
                verified = false,
                latestSnippet = "Excited about Room offline sync!",
                lastActiveTime = "1h ago"
            )
        )

        val contactWithProfiles = ContactWithProfiles(
            contact = contactEntity,
            profiles = profileEntities
        )

        val domain = contactWithProfiles.toDomain()
        assertEquals(42L, domain.id)
        assertEquals("Sarah Lin", domain.name)
        assertEquals(2, domain.socialProfiles.size)
        assertEquals("sarahlin", domain.getProfile(SocialPlatform.GITHUB)?.handle)
        assertEquals(1420, domain.getProfile(SocialPlatform.GITHUB)?.followerCount)
        assertEquals(45, domain.getProfile(SocialPlatform.GITHUB)?.publicRepos)
        assertEquals("sarah_codes", domain.getProfile(SocialPlatform.X_TWITTER)?.handle)
        assertEquals(PresenceStatus.ONLINE, domain.presence)
    }

    @Test
    fun `main activity builds and launches without error`() {
        val controller = org.robolectric.Robolectric.buildActivity(MainActivity::class.java).setup()
        val activity = controller.get()
        org.junit.Assert.assertNotNull(activity)
        controller.destroy()
    }

    @Test
    fun `expanded social platforms fullUrl and domain integrity`() {
        val discordUrl = SocialPlatform.DISCORD.fullUrl("alex_dev")
        val telegramUrl = SocialPlatform.TELEGRAM.fullUrl("alex_dev")
        val youtubeUrl = SocialPlatform.YOUTUBE.fullUrl("@AndroidDev")
        val blueskyUrl = SocialPlatform.BLUESKY.fullUrl("dev.bsky.social")

        assertEquals("https://discord.com/users/alex_dev", discordUrl)
        assertEquals("https://t.me/alex_dev", telegramUrl)
        assertEquals("https://youtube.com/@AndroidDev", youtubeUrl)
        assertEquals("https://bsky.app/profile/dev.bsky.social", blueskyUrl)

        val contact = Contact(
            id = 200L,
            name = "Alex Rivera",
            title = "Staff Platform Architect",
            company = "GlobalMesh",
            email = "alex@mesh.io",
            phone = "+1-555-9000",
            avatarUrl = null,
            socialProfiles = listOf(
                SocialProfile(SocialPlatform.DISCORD, "alex#1234"),
                SocialProfile(SocialPlatform.TELEGRAM, "alex_rivera"),
                SocialProfile(SocialPlatform.YOUTUBE, "AlexTechLive"),
                SocialProfile(SocialPlatform.BLUESKY, "alex.bsky.social")
            )
        )

        assertEquals(4, contact.socialProfiles.size)
        assertEquals("alex#1234", contact.getProfile(SocialPlatform.DISCORD)?.handle)
        assertEquals("alex_rivera", contact.getProfile(SocialPlatform.TELEGRAM)?.handle)
        assertEquals("AlexTechLive", contact.getProfile(SocialPlatform.YOUTUBE)?.handle)
        assertEquals("alex.bsky.social", contact.getProfile(SocialPlatform.BLUESKY)?.handle)
    }

    @Test
    fun `theme modes verification`() {
        val dark = com.example.ui.theme.ThemeMode.DARK
        val light = com.example.ui.theme.ThemeMode.LIGHT
        val system = com.example.ui.theme.ThemeMode.SYSTEM

        assertEquals("Dark", dark.displayName)
        assertEquals("Light", light.displayName)
        assertEquals("System Default", system.displayName)
    }

    @Test
    fun `whatsapp and messenger platform integration verification`() {
        val whatsappUrl = SocialPlatform.WHATSAPP.fullUrl("+1 (555) 019-2834")
        val messengerUrl = SocialPlatform.MESSENGER.fullUrl("alex.rivera")

        assertEquals("https://wa.me/15550192834", whatsappUrl)
        assertEquals("https://m.me/alex.rivera", messengerUrl)
        assertEquals("WA", SocialPlatform.WHATSAPP.defaultIconSymbol)
        assertEquals("MS", SocialPlatform.MESSENGER.defaultIconSymbol)

        val contact = Contact(
            id = 301L,
            name = "Jordan Blake",
            title = "Chief Operations Officer",
            company = "Apex Ventures",
            email = "jordan@apex.com",
            phone = "+1-555-019-2834",
            avatarUrl = null,
            socialProfiles = listOf(
                SocialProfile(SocialPlatform.WHATSAPP, "15550192834"),
                SocialProfile(SocialPlatform.MESSENGER, "jordan.blake.apex")
            )
        )

        assertEquals(2, contact.socialProfiles.size)
        assertEquals("15550192834", contact.getProfile(SocialPlatform.WHATSAPP)?.handle)
        assertEquals("jordan.blake.apex", contact.getProfile(SocialPlatform.MESSENGER)?.handle)
    }

    @Test
    fun `contact sort order last updated and alphabetical verification`() {
        assertEquals("Last Updated", com.example.ui.ContactSortOrder.LAST_UPDATED.label)
        assertEquals("Alphabetical", com.example.ui.ContactSortOrder.ALPHABETICAL.label)

        val contacts = listOf(
            Contact(
                id = 1L,
                name = "Zoe Vance",
                title = "VP Design",
                company = "Aura",
                email = "zoe@aura.io",
                phone = "111",
                avatarUrl = null,
                lastSyncedTimestamp = 5000L
            ),
            Contact(
                id = 2L,
                name = "Aaron Chen",
                title = "CTO",
                company = "Core",
                email = "aaron@core.io",
                phone = "222",
                avatarUrl = null,
                lastSyncedTimestamp = 1000L
            ),
            Contact(
                id = 3L,
                name = "Maya Patel",
                title = "Lead AI",
                company = "Synth",
                email = "maya@synth.io",
                phone = "333",
                avatarUrl = null,
                lastSyncedTimestamp = 9000L
            )
        )

        val sortedByLastUpdated = contacts.sortedWith(
            compareByDescending<Contact> { it.lastSyncedTimestamp }.thenBy { it.name.lowercase() }
        )
        assertEquals("Maya Patel", sortedByLastUpdated[0].name)
        assertEquals("Zoe Vance", sortedByLastUpdated[1].name)
        assertEquals("Aaron Chen", sortedByLastUpdated[2].name)

        val sortedAlphabetical = contacts.sortedBy { it.name.lowercase() }
        assertEquals("Aaron Chen", sortedAlphabetical[0].name)
        assertEquals("Maya Patel", sortedAlphabetical[1].name)
        assertEquals("Zoe Vance", sortedAlphabetical[2].name)
    }

    @Test
    fun `share contact qr payload encode and decode integrity`() {
        val original = Contact(
            id = 505L,
            name = "Sundar Pichai",
            title = "CEO",
            company = "Google",
            email = "sundar@google.com",
            phone = "+1-650-253-0000",
            avatarUrl = null,
            tags = listOf("Executive", "VIP"),
            socialProfiles = listOf(
                SocialProfile(SocialPlatform.X_TWITTER, "sundarpichai"),
                SocialProfile(SocialPlatform.WHATSAPP, "16502530000")
            )
        )

        val payload = com.example.ui.dialogs.encodeContactQrPayload(original)
        org.junit.Assert.assertTrue(payload.startsWith("NEXUSPULSE:"))

        val matrix = com.example.ui.dialogs.generateQrMatrix(payload, 25)
        assertEquals(625, matrix.size)

        val decoded = com.example.ui.dialogs.decodeContactQrPayload(payload)
        org.junit.Assert.assertNotNull(decoded)
        assertEquals("Sundar Pichai", decoded?.name)
        assertEquals("CEO", decoded?.title)
        assertEquals("Google", decoded?.company)
        assertEquals(2, decoded?.socialProfiles?.size)
        assertEquals("sundarpichai", decoded?.getProfile(SocialPlatform.X_TWITTER)?.handle)
    }
}
