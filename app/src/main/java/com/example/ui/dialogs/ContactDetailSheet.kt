package com.example.ui.dialogs

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.WifiTethering
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Contact
import com.example.data.model.SocialPlatform
import com.example.data.model.SocialProfile
import com.example.ui.components.PresencePulseIndicator
import com.example.ui.components.formatCount
import com.example.ui.theme.BorderStroke
import com.example.ui.theme.CardSurface
import com.example.ui.theme.ColorGitHub
import com.example.ui.theme.ColorInstagram
import com.example.ui.theme.ColorLinkedIn
import com.example.ui.theme.ColorX
import com.example.ui.theme.ColorDiscord
import com.example.ui.theme.ColorTelegram
import com.example.ui.theme.ColorYouTube
import com.example.ui.theme.ColorBluesky
import com.example.ui.theme.ColorThreads
import com.example.ui.theme.ColorMastodon
import com.example.ui.theme.ColorWhatsApp
import com.example.ui.theme.ColorMessenger
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.ElevatedSurface
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.SlateSurface
import com.example.ui.theme.StatusRose
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VioletAccent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactDetailSheet(
    contact: Contact,
    onDismiss: () -> Unit,
    onEditClick: (Contact) -> Unit,
    onDeleteClick: (Contact) -> Unit,
    onPulseClick: (Contact) -> Unit,
    onShareQrClick: (Contact) -> Unit = {}
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current
    var showDeleteConfirmation by remember { mutableStateOf(false) }

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
                .padding(bottom = 24.dp)
        ) {
            // Header with Close and Edit
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDismiss, modifier = Modifier.size(40.dp)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close sheet",
                        tint = TextSecondary
                    )
                }

                Row {
                    IconButton(
                        onClick = { onShareQrClick(contact) },
                        modifier = Modifier
                            .size(40.dp)
                            .testTag("detail_share_qr_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCode2,
                            contentDescription = "Share contact QR code",
                            tint = CyanAccent
                        )
                    }
                    IconButton(
                        onClick = { onPulseClick(contact) },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.WifiTethering,
                            contentDescription = "Trigger live pulse",
                            tint = CyanAccent
                        )
                    }
                    IconButton(
                        onClick = { onEditClick(contact) },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit contact",
                            tint = CyanAccent
                        )
                    }
                    IconButton(
                        onClick = { showDeleteConfirmation = true },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete contact",
                            tint = StatusRose
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Profile Header Hero
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.size(72.dp)) {
                    if (!contact.avatarUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = contact.avatarUrl,
                            contentDescription = "Avatar",
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .border(1.5.dp, CyanAccent, CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(CyanAccent.copy(alpha = 0.3f), VioletAccent.copy(alpha = 0.6f))
                                    )
                                )
                                .border(1.5.dp, CyanAccent, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = contact.initials,
                                color = TextPrimary,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 2.dp, bottom = 2.dp)
                    ) {
                        PresencePulseIndicator(status = contact.presence, sizeDp = 16)
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = contact.name,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        if (contact.socialProfiles.any { it.verified }) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = "Verified",
                                tint = CyanAccent,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    if (contact.title.isNotBlank() || contact.company.isNotBlank()) {
                        Text(
                            text = listOf(contact.title, contact.company)
                                .filter { it.isNotBlank() }
                                .joinToString(" • "),
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Status: ${contact.presence.label}",
                        fontSize = 12.sp,
                        color = CyanAccent,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Quick Communication Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(CardSurface)
                    .border(1.dp, BorderStroke, RoundedCornerShape(12.dp))
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                QuickActionButton(
                    icon = Icons.Default.Call,
                    label = "Call",
                    onClick = { launchPhone(context, contact.phone) }
                )
                QuickActionButton(
                    icon = Icons.Default.Message,
                    label = "SMS",
                    onClick = { launchSms(context, contact.phone) }
                )
                QuickActionButton(
                    icon = Icons.Default.Email,
                    label = "Email",
                    onClick = { launchEmail(context, contact.email) }
                )
                QuickActionButton(
                    icon = Icons.Default.QrCode2,
                    label = "Share QR",
                    onClick = { onShareQrClick(contact) }
                )
            }

            // Tags
            if (contact.tags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    contact.tags.forEach { tag ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(ElevatedSurface)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "#$tag",
                                fontSize = 11.sp,
                                color = TextSecondary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Notes Section
            if (contact.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(16.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(CardSurface)
                        .border(1.dp, BorderStroke, RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Text(
                        text = "EXECUTIVE NOTES",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMuted,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = contact.notes,
                        fontSize = 13.sp,
                        color = TextPrimary
                    )
                }
            }

            // Linked Social Media Profiles (Real-Time Synced)
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "REAL-TIME SOCIAL PROFILES (${contact.socialProfiles.size})",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyanAccent,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Live Sync Enabled",
                    fontSize = 10.sp,
                    color = TextSecondary
                )
            }
            Spacer(modifier = Modifier.height(10.dp))

            if (contact.socialProfiles.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(CardSurface)
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No social profiles linked yet. Tap Edit to connect GitHub, X, LinkedIn, or Instagram.",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                }
            } else {
                contact.socialProfiles.forEach { profile ->
                    SocialProfileDetailCard(
                        profile = profile,
                        onOpen = { launchUrl(context, profile.profileUrl.ifEmpty { profile.platform.fullUrl(profile.handle) }) }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Delete Contact", color = TextPrimary) },
            text = {
                Text(
                    "Are you sure you want to delete ${contact.name}? All linked real-time profile telemetry will be removed.",
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirmation = false
                        onDeleteClick(contact)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusRose)
                ) {
                    Text("Delete", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = SlateSurface
        )
    }
}

@Composable
fun QuickActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Icon(imageVector = icon, contentDescription = label, tint = CyanAccent, modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, fontSize = 11.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun SocialProfileDetailCard(
    profile: SocialProfile,
    onOpen: () -> Unit
) {
    val brandColor = when (profile.platform) {
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
    val bgBorder = brandColor.copy(alpha = 0.25f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardSurface)
            .border(1.dp, bgBorder, RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(bgBorder)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = profile.platform.displayName,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = brandColor
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "@${profile.handle.removePrefix("@")}",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                modifier = Modifier.weight(1f)
            )

            IconButton(
                onClick = onOpen,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.OpenInNew,
                    contentDescription = "Open profile",
                    tint = brandColor,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // Stats row
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            if (profile.followerCount > 0) {
                Column {
                    Text(text = "Followers", fontSize = 10.sp, color = TextMuted)
                    Text(
                        text = formatCount(profile.followerCount),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }
            if (profile.publicRepos > 0) {
                Column {
                    Text(text = "Public Repos", fontSize = 10.sp, color = TextMuted)
                    Text(
                        text = profile.publicRepos.toString(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }
            Column {
                Text(text = "Last Synced", fontSize = 10.sp, color = TextMuted)
                Text(
                    text = profile.lastActiveTime,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = CyanAccent
                )
            }
        }

        if (!profile.bio.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = profile.bio,
                fontSize = 12.sp,
                color = TextSecondary
            )
        }

        if (!profile.latestSnippet.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(ElevatedSurface)
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(brandColor)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = profile.latestSnippet,
                    fontSize = 11.sp,
                    color = TextPrimary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

fun launchPhone(context: Context, phone: String) {
    if (phone.isBlank()) {
        Toast.makeText(context, "No phone number available", Toast.LENGTH_SHORT).show()
        return
    }
    try {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${phone.trim()}"))
        context.startActivity(intent)
    } catch (_: Exception) {
        Toast.makeText(context, "Cannot open phone app", Toast.LENGTH_SHORT).show()
    }
}

fun launchSms(context: Context, phone: String) {
    if (phone.isBlank()) {
        Toast.makeText(context, "No phone number available", Toast.LENGTH_SHORT).show()
        return
    }
    try {
        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:${phone.trim()}"))
        context.startActivity(intent)
    } catch (_: Exception) {
        Toast.makeText(context, "Cannot open messaging app", Toast.LENGTH_SHORT).show()
    }
}

fun launchEmail(context: Context, email: String) {
    if (email.isBlank()) {
        Toast.makeText(context, "No email available", Toast.LENGTH_SHORT).show()
        return
    }
    try {
        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:${email.trim()}"))
        context.startActivity(intent)
    } catch (_: Exception) {
        Toast.makeText(context, "Cannot open email client", Toast.LENGTH_SHORT).show()
    }
}

fun launchUrl(context: Context, url: String) {
    if (url.isBlank()) {
        Toast.makeText(context, "No profile link available", Toast.LENGTH_SHORT).show()
        return
    }
    try {
        val validUrl = if (!url.startsWith("http://") && !url.startsWith("https://")) {
            "https://$url"
        } else url
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(validUrl))
        context.startActivity(intent)
    } catch (_: Exception) {
        Toast.makeText(context, "Cannot open browser", Toast.LENGTH_SHORT).show()
    }
}
