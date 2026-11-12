package com.example.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PresenceStatus
import com.example.data.model.SocialPlatform
import com.example.data.model.SocialProfile
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
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.StatusEmerald
import com.example.ui.theme.StatusRose
import com.example.ui.theme.StatusSlate

import androidx.compose.runtime.Immutable

@Composable
fun PresencePulseIndicator(
    status: PresenceStatus,
    modifier: Modifier = Modifier,
    sizeDp: Int = 10
) {
    val baseColor = when (status) {
        PresenceStatus.ONLINE -> StatusEmerald
        PresenceStatus.RECENTLY_ACTIVE -> StatusEmerald.copy(alpha = 0.8f)
        PresenceStatus.AWAY -> StatusAmber
        PresenceStatus.OFFLINE -> StatusSlate
    }

    if (status == PresenceStatus.ONLINE) {
        Box(
            modifier = modifier.size(sizeDp.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(sizeDp.dp)
                    .clip(CircleShape)
                    .background(baseColor.copy(alpha = 0.32f))
            )
            Box(
                modifier = Modifier
                    .size((sizeDp - 2).coerceAtLeast(4).dp)
                    .clip(CircleShape)
                    .background(baseColor)
                    .border(1.dp, Color.Black.copy(alpha = 0.4f), CircleShape)
            )
        }
    } else {
        Box(
            modifier = modifier
                .size(sizeDp.dp)
                .clip(CircleShape)
                .background(baseColor)
                .border(1.dp, Color.Black.copy(alpha = 0.4f), CircleShape)
        )
    }
}

@Composable
fun SocialProfileChip(
    profile: SocialProfile,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
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
    val bgTint = brandColor.copy(alpha = 0.15f)

    val clickableMod = if (onClick != null) {
        modifier.clickable { onClick() }
    } else modifier

    Row(
        modifier = clickableMod
            .clip(RoundedCornerShape(6.dp))
            .background(bgTint)
            .border(0.5.dp, brandColor.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = profile.platform.defaultIconSymbol,
            fontSize = 9.sp,
            fontWeight = FontWeight.Black,
            color = brandColor,
            fontFamily = FontFamily.Monospace
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "@${profile.handle.removePrefix("@")}",
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = brandColor
        )
        if (profile.followerCount > 0) {
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "• ${formatCount(profile.followerCount)}",
                fontSize = 10.sp,
                color = brandColor.copy(alpha = 0.75f)
            )
        }
    }
}

fun formatCount(count: Int): String {
    return when {
        count >= 1_000_000 -> "${count / 1_000_000}M"
        count >= 1_000 -> "${count / 1_000}k"
        else -> count.toString()
    }
}

@Immutable
private data class MiniBadgeStyle(
    val brandColor: Color,
    val bgColor: Color,
    val borderColor: Color,
    val label: String
)

private val STYLE_GITHUB = MiniBadgeStyle(Color(0xFFF1F5F9), Color(0xFF1E293B), Color(0xFF475569), "GH")
private val STYLE_X = MiniBadgeStyle(Color(0xFF38BDF8), Color(0xFF0C243C), Color(0xFF0284C7), "𝕏")
private val STYLE_LINKEDIN = MiniBadgeStyle(Color(0xFF38BDF8), Color(0xFF072744), Color(0xFF0369A1), "in")
private val STYLE_INSTAGRAM = MiniBadgeStyle(Color(0xFFFDA4AF), Color(0xFF4C0519), Color(0xFFBE123C), "IG")
private val STYLE_DISCORD = MiniBadgeStyle(Color(0xFFC7D2FE), Color(0xFF1E1B4B), Color(0xFF4338CA), "DC")
private val STYLE_TELEGRAM = MiniBadgeStyle(Color(0xFFBAE6FD), Color(0xFF082F49), Color(0xFF0284C7), "TG")
private val STYLE_YOUTUBE = MiniBadgeStyle(Color(0xFFFECACA), Color(0xFF450A0A), Color(0xFFDC2626), "YT")
private val STYLE_BLUESKY = MiniBadgeStyle(Color(0xFFBAE6FD), Color(0xFF082F49), Color(0xFF0284C7), "BS")
private val STYLE_THREADS = MiniBadgeStyle(Color(0xFFF1F5F9), Color(0xFF1E293B), Color(0xFF475569), "TH")
private val STYLE_MASTODON = MiniBadgeStyle(Color(0xFFDDD6FE), Color(0xFF2E1065), Color(0xFF7C3AED), "MD")
private val STYLE_WHATSAPP = MiniBadgeStyle(Color(0xFFDCFCE7), Color(0xFF052E16), Color(0xFF16A34A), "WA")
private val STYLE_MESSENGER = MiniBadgeStyle(Color(0xFFE0F2FE), Color(0xFF082F49), Color(0xFF0284C7), "MS")

/**
 * Small circular social platform badge indicating a linked network on a contact item.
 */
@Composable
fun SocialPlatformMiniBadge(
    platform: SocialPlatform,
    modifier: Modifier = Modifier,
    sizeDp: Int = 22
) {
    val style = when (platform) {
        SocialPlatform.GITHUB -> STYLE_GITHUB
        SocialPlatform.X_TWITTER -> STYLE_X
        SocialPlatform.LINKEDIN -> STYLE_LINKEDIN
        SocialPlatform.INSTAGRAM -> STYLE_INSTAGRAM
        SocialPlatform.DISCORD -> STYLE_DISCORD
        SocialPlatform.TELEGRAM -> STYLE_TELEGRAM
        SocialPlatform.YOUTUBE -> STYLE_YOUTUBE
        SocialPlatform.BLUESKY -> STYLE_BLUESKY
        SocialPlatform.THREADS -> STYLE_THREADS
        SocialPlatform.MASTODON -> STYLE_MASTODON
        SocialPlatform.WHATSAPP -> STYLE_WHATSAPP
        SocialPlatform.MESSENGER -> STYLE_MESSENGER
    }

    Box(
        modifier = modifier
            .size(sizeDp.dp)
            .clip(CircleShape)
            .background(style.bgColor)
            .border(0.75.dp, style.borderColor, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = style.label,
            color = style.brandColor,
            fontSize = (sizeDp * 0.40f).sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.Monospace,
            letterSpacing = (-0.5).sp
        )
    }
}

/**
 * Horizontal row of linked social platform mini badges for a contact card.
 */
@Composable
fun LinkedPlatformsBadgeRow(
    profiles: List<SocialProfile>,
    modifier: Modifier = Modifier,
    badgeSizeDp: Int = 20
) {
    androidx.compose.foundation.layout.Row(
        modifier = modifier,
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        profiles.distinctBy { it.platform }.forEach { profile ->
            SocialPlatformMiniBadge(
                platform = profile.platform,
                sizeDp = badgeSizeDp
            )
        }
    }
}

