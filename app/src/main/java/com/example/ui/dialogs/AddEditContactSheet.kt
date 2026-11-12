package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Contact
import com.example.data.model.SocialPlatform
import com.example.data.model.SocialProfile
import com.example.ui.theme.BorderStroke
import com.example.ui.theme.CardSurface
import com.example.ui.theme.ColorBluesky
import com.example.ui.theme.ColorDiscord
import com.example.ui.theme.ColorGitHub
import com.example.ui.theme.ColorInstagram
import com.example.ui.theme.ColorLinkedIn
import com.example.ui.theme.ColorMastodon
import com.example.ui.theme.ColorTelegram
import com.example.ui.theme.ColorThreads
import com.example.ui.theme.ColorWhatsApp
import com.example.ui.theme.ColorMessenger
import com.example.ui.theme.ColorX
import com.example.ui.theme.ColorYouTube
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.SlateSurface
import com.example.ui.theme.StatusRose
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VioletAccent

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditContactSheet(
    contact: Contact?,
    onDismiss: () -> Unit,
    onSave: (
        name: String,
        title: String,
        company: String,
        email: String,
        phone: String,
        avatarUrl: String?,
        tags: List<String>,
        socialProfiles: List<SocialProfile>,
        notes: String
    ) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var name by remember { mutableStateOf(contact?.name ?: "") }
    var title by remember { mutableStateOf(contact?.title ?: "") }
    var company by remember { mutableStateOf(contact?.company ?: "") }
    var email by remember { mutableStateOf(contact?.email ?: "") }
    var phone by remember { mutableStateOf(contact?.phone ?: "") }
    var avatarUrl by remember { mutableStateOf(contact?.avatarUrl ?: "") }
    var tagsInput by remember { mutableStateOf(contact?.tags?.joinToString(", ") ?: "") }
    var notes by remember { mutableStateOf(contact?.notes ?: "") }

    // Dynamic map of linked social platform handles
    val linkedHandles = remember {
        mutableStateMapOf<SocialPlatform, String>().apply {
            if (contact != null) {
                contact.socialProfiles.forEach { profile ->
                    put(profile.platform, profile.handle)
                }
            } else {
                // By default for new contacts, show GitHub, X, and LinkedIn
                put(SocialPlatform.GITHUB, "")
                put(SocialPlatform.X_TWITTER, "")
                put(SocialPlatform.LINKEDIN, "")
            }
        }
    }

    var isAddMenuExpanded by remember { mutableStateOf(false) }
    var nameError by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SlateSurface,
        scrimColor = Color.Black.copy(alpha = 0.65f),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .size(width = 38.dp, height = 4.dp)
                    .clip(CircleShape)
                    .background(BorderStroke)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .navigationBarsPadding()
                .padding(bottom = 28.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (contact == null) "NEW EXECUTIVE CONTACT" else "EDIT EXECUTIVE CONTACT",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyanAccent,
                    letterSpacing = 1.sp
                )
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Primary Contact Info
            ExecutiveTextField(
                value = name,
                onValueChange = {
                    name = it
                    if (it.isNotBlank()) nameError = false
                },
                label = "Full Name *",
                isError = nameError,
                testTag = "contact_name_input"
            )
            if (nameError) {
                Text(
                    text = "Name is required",
                    fontSize = 11.sp,
                    color = StatusRose,
                    modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            ExecutiveTextField(
                value = title,
                onValueChange = { title = it },
                label = "Title / Role (e.g. Chief Technology Officer)",
                testTag = "contact_title_input"
            )

            Spacer(modifier = Modifier.height(10.dp))
            ExecutiveTextField(
                value = company,
                onValueChange = { company = it },
                label = "Company / Organization (e.g. Nexus Innovations)",
                testTag = "contact_company_input"
            )

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    ExecutiveTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "Email Address",
                        testTag = "contact_email_input"
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    ExecutiveTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = "Phone Number",
                        testTag = "contact_phone_input"
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            ExecutiveTextField(
                value = avatarUrl,
                onValueChange = { avatarUrl = it },
                label = "Avatar Image URL (Optional)"
            )

            Spacer(modifier = Modifier.height(10.dp))
            ExecutiveTextField(
                value = tagsInput,
                onValueChange = { tagsInput = it },
                label = "Tags (comma separated, e.g. Executive, AI, Tech)"
            )

            // Real-Time Social Handles Section
            Spacer(modifier = Modifier.height(22.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "LINKED SOCIAL PROFILES",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyanAccent,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Connect profiles to enable real-time telemetry and feeds.",
                        fontSize = 11.sp,
                        color = TextMuted,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                // Add Platform Button
                Box {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(CyanAccent.copy(alpha = 0.12f))
                            .border(1.dp, CyanAccent.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .clickable { isAddMenuExpanded = true }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("add_platform_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Platform",
                                tint = CyanAccent,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Add Platform",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyanAccent
                            )
                        }
                    }

                    // Dropdown menu for selecting any unadded platform
                    DropdownMenu(
                        expanded = isAddMenuExpanded,
                        onDismissRequest = { isAddMenuExpanded = false },
                        modifier = Modifier.background(CardSurface)
                    ) {
                        SocialPlatform.entries.forEach { platform ->
                            val isAlreadyAdded = linkedHandles.containsKey(platform)
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(getPlatformBrandColor(platform))
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = platform.displayName,
                                            color = if (isAlreadyAdded) TextMuted else TextPrimary,
                                            fontWeight = if (isAlreadyAdded) FontWeight.Normal else FontWeight.Medium
                                        )
                                        if (isAlreadyAdded) {
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "(Added)",
                                                fontSize = 10.sp,
                                                color = TextMuted
                                            )
                                        }
                                    }
                                },
                                onClick = {
                                    if (!isAlreadyAdded) {
                                        linkedHandles[platform] = ""
                                    }
                                    isAddMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Active Platform Inputs List
            linkedHandles.keys.sortedBy { it.ordinal }.forEach { platform ->
                val handleValue = linkedHandles[platform] ?: ""
                ExecutiveSocialTextField(
                    value = handleValue,
                    onValueChange = { raw ->
                        linkedHandles[platform] = extractHandle(raw, platform)
                    },
                    platform = platform,
                    onRemove = {
                        linkedHandles.remove(platform)
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Quick-add platform suggestions chips
            val unaddedPlatforms = SocialPlatform.entries.filter { !linkedHandles.containsKey(it) }
            if (unaddedPlatforms.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Quick add other platforms:",
                    fontSize = 10.sp,
                    color = TextMuted,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    unaddedPlatforms.forEach { platform ->
                        val brandColor = getPlatformBrandColor(platform)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(brandColor.copy(alpha = 0.12f))
                                .border(0.75.dp, brandColor.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                .clickable {
                                    linkedHandles[platform] = ""
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "+",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = brandColor
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = platform.displayName,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = brandColor
                                )
                            }
                        }
                    }
                }
            }

            // Notes
            Spacer(modifier = Modifier.height(16.dp))
            ExecutiveTextField(
                value = notes,
                onValueChange = { notes = it },
                label = "Executive Notes & Context",
                singleLine = false,
                maxLines = 3
            )

            // Action Button
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    if (name.isBlank()) {
                        nameError = true
                        return@Button
                    }

                    val tagsList = tagsInput.split(",")
                        .map { it.trim().removePrefix("#") }
                        .filter { it.isNotBlank() }

                    val profilesList = mutableListOf<SocialProfile>()
                    linkedHandles.forEach { (platform, rawHandle) ->
                        val cleanHandle = extractHandle(rawHandle, platform)
                        if (cleanHandle.isNotBlank()) {
                            val existing = contact?.getProfile(platform)
                            profilesList.add(
                                existing?.copy(
                                    handle = cleanHandle,
                                    profileUrl = platform.fullUrl(cleanHandle)
                                ) ?: SocialProfile(
                                    platform = platform,
                                    handle = cleanHandle,
                                    profileUrl = platform.fullUrl(cleanHandle)
                                )
                            )
                        }
                    }

                    onSave(
                        name,
                        title,
                        company,
                        email,
                        phone,
                        avatarUrl.ifBlank { null },
                        tagsList,
                        profilesList,
                        notes
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("save_contact_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CyanAccent,
                    contentColor = SlateSurface
                )
            ) {
                Text(
                    text = if (contact == null) "Create Executive Contact" else "Save Changes",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlateSurface
                )
            }
        }
    }
}

/**
 * Extracts a clean handle from either a username or a full pasted profile URL.
 */
fun extractHandle(raw: String, platform: SocialPlatform): String {
    var text = raw.trim()
    if (platform == SocialPlatform.WHATSAPP) {
        if (text.startsWith("http://") || text.startsWith("https://")) {
            text = text.removePrefix("http://").removePrefix("https://")
            if (text.contains("send?phone=")) {
                text = text.substringAfter("send?phone=").substringBefore("&")
            } else if (text.startsWith("wa.me/")) {
                text = text.removePrefix("wa.me/").substringBefore("?").substringBefore("/")
            }
        }
        val digitsOnly = text.filter { it.isDigit() }
        return if (digitsOnly.isNotEmpty()) digitsOnly else text.trim()
    }
    if (text.startsWith("http://") || text.startsWith("https://")) {
        text = text.removePrefix("http://").removePrefix("https://")
        val domain = platform.urlPrefix.removePrefix("https://").removePrefix("http://").substringBefore("/")
        if (text.startsWith(domain)) {
            text = text.substringAfter("/").trim()
            if (platform == SocialPlatform.LINKEDIN && text.startsWith("in/")) {
                text = text.removePrefix("in/")
            }
            if (platform == SocialPlatform.DISCORD && text.startsWith("users/")) {
                text = text.removePrefix("users/")
            }
            if (platform == SocialPlatform.BLUESKY && text.startsWith("profile/")) {
                text = text.removePrefix("profile/")
            }
            if (platform == SocialPlatform.MESSENGER && text.startsWith("t/")) {
                text = text.removePrefix("t/")
            }
        }
    }
    return text.removePrefix("@").trim().trimEnd('/')
}

@Composable
fun getPlatformBrandColor(platform: SocialPlatform): Color {
    return when (platform) {
        SocialPlatform.GITHUB -> ColorGitHub
        SocialPlatform.X_TWITTER -> ColorX
        SocialPlatform.LINKEDIN -> ColorLinkedIn
        SocialPlatform.INSTAGRAM -> ColorInstagram
        SocialPlatform.DISCORD -> ColorDiscord
        SocialPlatform.TELEGRAM -> ColorTelegram
        SocialPlatform.YOUTUBE -> ColorYouTube
        SocialPlatform.BLUESKY -> ColorBluesky
        SocialPlatform.THREADS -> ColorThreads
        SocialPlatform.MASTODON -> ColorMastodon
        SocialPlatform.WHATSAPP -> ColorWhatsApp
        SocialPlatform.MESSENGER -> ColorMessenger
    }
}

fun getPlatformPlaceholder(platform: SocialPlatform): String {
    return when (platform) {
        SocialPlatform.GITHUB -> "username (e.g. torvalds)"
        SocialPlatform.X_TWITTER -> "handle (e.g. techleader)"
        SocialPlatform.LINKEDIN -> "vanity-name (e.g. jane-doe)"
        SocialPlatform.INSTAGRAM -> "handle (e.g. photourban)"
        SocialPlatform.DISCORD -> "user#0000 or username"
        SocialPlatform.TELEGRAM -> "username (e.g. alex_dev)"
        SocialPlatform.YOUTUBE -> "channel handle (e.g. @AndroidDev)"
        SocialPlatform.BLUESKY -> "handle (e.g. name.bsky.social)"
        SocialPlatform.THREADS -> "handle (e.g. @zuck)"
        SocialPlatform.MASTODON -> "user@instance (e.g. dev@mastodon.social)"
        SocialPlatform.WHATSAPP -> "phone with country code (e.g. +1 555 123 4567)"
        SocialPlatform.MESSENGER -> "username or page (e.g. alex.rivera)"
    }
}

@Composable
fun ExecutiveTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isError: Boolean = false,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    testTag: String? = null
) {
    val tagMod = if (testTag != null) Modifier.testTag(testTag) else Modifier
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 12.sp) },
        isError = isError,
        singleLine = singleLine,
        maxLines = maxLines,
        shape = RoundedCornerShape(10.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = CyanAccent,
            unfocusedBorderColor = BorderStroke,
            errorBorderColor = StatusRose,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            focusedLabelColor = CyanAccent,
            unfocusedLabelColor = TextSecondary,
            focusedContainerColor = CardSurface,
            unfocusedContainerColor = CardSurface
        ),
        modifier = tagMod.fillMaxWidth()
    )
}

@Composable
fun ExecutiveSocialTextField(
    value: String,
    onValueChange: (String) -> Unit,
    platform: SocialPlatform,
    onRemove: (() -> Unit)? = null
) {
    val brandColor = getPlatformBrandColor(platform)
    val placeholder = getPlatformPlaceholder(platform)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(CardSurface)
            .border(1.dp, BorderStroke, RoundedCornerShape(10.dp))
            .padding(start = 10.dp, end = 4.dp, top = 2.dp, bottom = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(brandColor.copy(alpha = 0.15f))
                .border(0.5.dp, brandColor.copy(alpha = 0.35f), RoundedCornerShape(6.dp))
                .padding(horizontal = 7.dp, vertical = 4.dp)
        ) {
            Text(
                text = platform.displayName,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = brandColor
            )
        }

        Spacer(modifier = Modifier.width(6.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, fontSize = 11.sp, color = TextMuted) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            ),
            modifier = Modifier.weight(1f)
        )

        if (onRemove != null) {
            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove ${platform.displayName}",
                    tint = TextMuted,
                    modifier = Modifier.size(15.dp)
                )
            }
        }
    }
}
