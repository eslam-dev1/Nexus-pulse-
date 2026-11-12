package com.example.ui.components

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.WifiTethering
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import android.view.HapticFeedbackConstants
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.Contact
import com.example.ui.theme.BorderStroke
import com.example.ui.theme.CardSurface
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.ElevatedSurface
import com.example.ui.theme.HyperBlue
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VioletAccent

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ContactCard(
    contact: Contact,
    onClick: () -> Unit,
    onCallClick: () -> Unit,
    onEmailClick: () -> Unit,
    onPulseClick: () -> Unit,
    onFavoriteClick: () -> Unit = {},
    onShareQrClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val view = LocalView.current
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("contact_card_${contact.id}")
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(listOf(BorderStroke, ElevatedSurface)),
            width = 1.dp
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Top Row: Avatar + Info + Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar with Presence indicator
                Box(
                    modifier = Modifier.size(52.dp)
                ) {
                    if (!contact.avatarUrl.isNullOrBlank()) {
                        val context = LocalContext.current
                        val imageRequest = remember(contact.avatarUrl) {
                            ImageRequest.Builder(context)
                                .data(contact.avatarUrl)
                                .size(128)
                                .crossfade(false)
                                .build()
                        }
                        AsyncImage(
                            model = imageRequest,
                            contentDescription = "Avatar of ${contact.name}",
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .border(1.dp, CyanAccent.copy(alpha = 0.5f), CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(CyanAccent.copy(alpha = 0.3f), VioletAccent.copy(alpha = 0.5f))
                                    )
                                )
                                .border(1.dp, CyanAccent.copy(alpha = 0.5f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = contact.initials,
                                color = TextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Presence indicator badge anchored to bottom right
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 1.dp, bottom = 1.dp)
                    ) {
                        PresencePulseIndicator(status = contact.presence, sizeDp = 13)
                    }

                    // Primary social platform badge on top-right of avatar
                    contact.socialProfiles.firstOrNull()?.let { primaryProfile ->
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 1.dp, end = 1.dp)
                        ) {
                            SocialPlatformMiniBadge(
                                platform = primaryProfile.platform,
                                sizeDp = 16
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                // Name & Role Info
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = contact.name,
                            style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (contact.socialProfiles.any { it.verified }) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = "Verified profile",
                                tint = CyanAccent,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    if (contact.title.isNotBlank() || contact.company.isNotBlank()) {
                        Text(
                            text = listOf(contact.title, contact.company)
                                .filter { it.isNotBlank() }
                                .joinToString(" • "),
                            fontSize = 12.sp,
                            color = TextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = contact.presence.label,
                            fontSize = 11.sp,
                            color = CyanAccent,
                            fontWeight = FontWeight.Medium
                        )

                        // Small social platform icon badges indicating linked platforms
                        if (contact.socialProfiles.isNotEmpty()) {
                            LinkedPlatformsBadgeRow(
                                profiles = contact.socialProfiles,
                                badgeSizeDp = 18
                            )
                        }
                    }
                }

                // Quick Action Buttons (including Favorite star toggle with haptic feedback)
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onFavoriteClick()
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("favorite_button_${contact.id}")
                    ) {
                        Icon(
                            imageVector = if (contact.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = if (contact.isFavorite) "Remove from favorites" else "Mark as favorite",
                            tint = if (contact.isFavorite) StatusAmber else TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(
                        onClick = {
                            view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                            onPulseClick()
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.WifiTethering,
                            contentDescription = "Simulate live sync pulse",
                            tint = CyanAccent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(
                        onClick = {
                            view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                            onShareQrClick()
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("share_qr_button_${contact.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCode2,
                            contentDescription = "Share contact QR code",
                            tint = CyanAccent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(
                        onClick = {
                            view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                            onCallClick()
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = "Call contact",
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Social Profiles Chips
            if (contact.socialProfiles.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    contact.socialProfiles.forEach { profile ->
                        SocialProfileChip(profile = profile)
                    }
                }
            }

            // Latest Live Activity Snippet (memoized)
            val latestSnippet = remember(contact.socialProfiles) {
                contact.socialProfiles
                    .firstOrNull { !it.latestSnippet.isNullOrBlank() }
                    ?.let { "${it.platform.displayName}: ${it.latestSnippet}" }
            }

            if (!latestSnippet.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(ElevatedSurface.copy(alpha = 0.7f))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(CyanAccent)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = latestSnippet,
                        fontSize = 11.sp,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
