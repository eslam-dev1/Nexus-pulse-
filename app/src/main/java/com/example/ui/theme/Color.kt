package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

// =========================================================================
// RAW COLOR PALETTES (Dark & Light Foundation Constants)
// =========================================================================

// --- Dark Mode Foundation Raw Tokens ---
val RawDarkObsidianBg = Color(0xFF0B0F19)       // Deep space obsidian background
val RawDarkSlateSurface = Color(0xFF131B2E)     // Elevated slate surface
val RawDarkCardSurface = Color(0xFF192238)      // Rich card container surface
val RawDarkElevatedSurface = Color(0xFF222E4B)  // High elevation popovers
val RawDarkBorderStroke = Color(0xFF2A3A5E)     // Subtle metallic border

val RawDarkCyanAccent = Color(0xFF00E5FF)       // Electric Cyan - Primary brand accent
val RawDarkCyanGlow = Color(0x3300E5FF)         // Soft ambient glow
val RawDarkVioletAccent = Color(0xFF8B5CF6)     // Neon Violet - Secondary accent
val RawDarkHyperBlue = Color(0xFF3B82F6)        // Hyper Blue - Tertiary interactive accent

val RawDarkTextPrimary = Color(0xFFF8FAFC)      // Crisp high-contrast foreground
val RawDarkTextSecondary = Color(0xFF94A3B8)    // Muted slate info
val RawDarkTextMuted = Color(0xFF64748B)        // Tertiary captions

// --- Light Mode Foundation Raw Tokens ---
val RawLightObsidianBg = Color(0xFFF1F5F9)      // Crisp executive luminous background (Slate 100)
val RawLightSlateSurface = Color(0xFFFFFFFF)    // Pure white primary surface
val RawLightCardSurface = Color(0xFFFFFFFF)     // Pure white card container
val RawLightElevatedSurface = Color(0xFFE2E8F0) // Subtle slate elevation (Slate 200)
val RawLightBorderStroke = Color(0xFFCBD5E1)    // Elegant slate border (Slate 300)

val RawLightCyanAccent = Color(0xFF0891B2)      // Deep Rich Cyan / Teal (high contrast AA on light)
val RawLightCyanGlow = Color(0x220891B2)
val RawLightVioletAccent = Color(0xFF7C3AED)    // Deep Royal Violet (high contrast)
val RawLightHyperBlue = Color(0xFF2563EB)       // Royal Blue

val RawLightTextPrimary = Color(0xFF0F172A)     // Deep obsidian charcoal text (high contrast)
val RawLightTextSecondary = Color(0xFF475569)   // Slate body text
val RawLightTextMuted = Color(0xFF64748B)       // Subtle captions

// --- Real-Time Presence & Status Tokens ---
val StatusEmerald = Color(0xFF10B981)        // Active Now / Live Sync Connected
val StatusEmeraldLight = Color(0xFF059669)
val StatusAmber = Color(0xFFF59E0B)          // Away / Polling Paused
val StatusAmberLight = Color(0xFFD97706)
val StatusRose = Color(0xFFEF4444)           // Busy / Error / Destructive
val StatusRoseLight = Color(0xFFDC2626)
val StatusSlate = Color(0xFF64748B)          // Offline / Idle

// --- Social Network Brand Accents (Raw Constants) ---
val RawColorGitHubDark = Color(0xFFE2E8F0)      // Silver white
val RawColorGitHubLight = Color(0xFF1E293B)     // Deep slate
val RawColorX = Color(0xFF38BDF8)              // X / Twitter Sky
val RawColorXLight = Color(0xFF0284C7)
val RawColorLinkedIn = Color(0xFF0EA5E9)       // LinkedIn Corporate Blue
val RawColorLinkedInLight = Color(0xFF0284C7)
val RawColorInstagram = Color(0xFFF43F5E)      // Instagram Rose
val RawColorInstagramLight = Color(0xFFE11D48)

val RawColorDiscord = Color(0xFF5865F2)        // Discord Blurple
val RawColorTelegram = Color(0xFF229ED9)       // Telegram Cyan Blue
val RawColorYouTube = Color(0xFFFF0000)        // YouTube Red
val RawColorBluesky = Color(0xFF1185FE)        // Bluesky Butterfly Blue
val RawColorThreadsDark = Color(0xFFF8FAFC)    // Threads White
val RawColorThreadsLight = Color(0xFF0F172A)   // Threads Black
val RawColorMastodon = Color(0xFF6364FF)       // Mastodon Violet
val RawColorWhatsApp = Color(0xFF25D366)       // WhatsApp Green
val RawColorWhatsAppLight = Color(0xFF16A34A)  // Deep Emerald/Green for Light Mode
val RawColorMessenger = Color(0xFF0084FF)      // Messenger Blue
val RawColorMessengerLight = Color(0xFF0284C7) // Deep Messenger Blue for Light Mode

// =========================================================================
// EXECUTIVE ACCENTS DATA STRUCTURE
// =========================================================================

@Immutable
data class ExecutiveAccents(
    val cyanAccent: Color = RawDarkCyanAccent,
    val cyanGlow: Color = RawDarkCyanGlow,
    val violetAccent: Color = RawDarkVioletAccent,
    val hyperBlue: Color = RawDarkHyperBlue,
    val statusEmerald: Color = StatusEmerald,
    val statusAmber: Color = StatusAmber,
    val statusRose: Color = StatusRose,
    val statusSlate: Color = StatusSlate,
    val githubColor: Color = RawColorGitHubDark,
    val xColor: Color = RawColorX,
    val linkedinColor: Color = RawColorLinkedIn,
    val instagramColor: Color = RawColorInstagram,
    val discordColor: Color = RawColorDiscord,
    val telegramColor: Color = RawColorTelegram,
    val youtubeColor: Color = RawColorYouTube,
    val blueskyColor: Color = RawColorBluesky,
    val threadsColor: Color = RawColorThreadsDark,
    val mastodonColor: Color = RawColorMastodon,
    val whatsappColor: Color = RawColorWhatsApp,
    val messengerColor: Color = RawColorMessenger,
    val cardSurface: Color = RawDarkCardSurface,
    val elevatedSurface: Color = RawDarkElevatedSurface,
    val borderStroke: Color = RawDarkBorderStroke,
    val obsidianBg: Color = RawDarkObsidianBg,
    val slateSurface: Color = RawDarkSlateSurface,
    val textPrimary: Color = RawDarkTextPrimary,
    val textSecondary: Color = RawDarkTextSecondary,
    val textMuted: Color = RawDarkTextMuted,
    val isDark: Boolean = true
)

val DarkExecutiveAccents = ExecutiveAccents(
    cyanAccent = RawDarkCyanAccent,
    cyanGlow = RawDarkCyanGlow,
    violetAccent = RawDarkVioletAccent,
    hyperBlue = RawDarkHyperBlue,
    statusEmerald = StatusEmerald,
    statusAmber = StatusAmber,
    statusRose = StatusRose,
    statusSlate = StatusSlate,
    githubColor = RawColorGitHubDark,
    xColor = RawColorX,
    linkedinColor = RawColorLinkedIn,
    instagramColor = RawColorInstagram,
    discordColor = RawColorDiscord,
    telegramColor = RawColorTelegram,
    youtubeColor = RawColorYouTube,
    blueskyColor = RawColorBluesky,
    threadsColor = RawColorThreadsDark,
    mastodonColor = RawColorMastodon,
    whatsappColor = RawColorWhatsApp,
    messengerColor = RawColorMessenger,
    cardSurface = RawDarkCardSurface,
    elevatedSurface = RawDarkElevatedSurface,
    borderStroke = RawDarkBorderStroke,
    obsidianBg = RawDarkObsidianBg,
    slateSurface = RawDarkSlateSurface,
    textPrimary = RawDarkTextPrimary,
    textSecondary = RawDarkTextSecondary,
    textMuted = RawDarkTextMuted,
    isDark = true
)

val LightExecutiveAccents = ExecutiveAccents(
    cyanAccent = RawLightCyanAccent,
    cyanGlow = RawLightCyanGlow,
    violetAccent = RawLightVioletAccent,
    hyperBlue = RawLightHyperBlue,
    statusEmerald = StatusEmeraldLight,
    statusAmber = StatusAmberLight,
    statusRose = StatusRoseLight,
    statusSlate = StatusSlate,
    githubColor = RawColorGitHubLight,
    xColor = RawColorXLight,
    linkedinColor = RawColorLinkedInLight,
    instagramColor = RawColorInstagramLight,
    discordColor = RawColorDiscord,
    telegramColor = RawColorTelegram,
    youtubeColor = RawColorYouTube,
    blueskyColor = RawColorBluesky,
    threadsColor = RawColorThreadsLight,
    mastodonColor = RawColorMastodon,
    whatsappColor = RawColorWhatsAppLight,
    messengerColor = RawColorMessengerLight,
    cardSurface = RawLightCardSurface,
    elevatedSurface = RawLightElevatedSurface,
    borderStroke = RawLightBorderStroke,
    obsidianBg = RawLightObsidianBg,
    slateSurface = RawLightSlateSurface,
    textPrimary = RawLightTextPrimary,
    textSecondary = RawLightTextSecondary,
    textMuted = RawLightTextMuted,
    isDark = false
)

// =========================================================================
// COMPOSABLE DYNAMIC THEME ACCESSORS
// Allows seamless backward compatibility with all existing composables
// =========================================================================

val ObsidianBg: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.executiveAccents.obsidianBg

val SlateSurface: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.executiveAccents.slateSurface

val CardSurface: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.executiveAccents.cardSurface

val ElevatedSurface: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.executiveAccents.elevatedSurface

val BorderStroke: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.executiveAccents.borderStroke

val CyanAccent: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.executiveAccents.cyanAccent

val CyanGlow: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.executiveAccents.cyanGlow

val VioletAccent: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.executiveAccents.violetAccent

val HyperBlue: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.executiveAccents.hyperBlue

val TextPrimary: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.executiveAccents.textPrimary

val TextSecondary: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.executiveAccents.textSecondary

val TextMuted: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.executiveAccents.textMuted

val ColorGitHub: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.executiveAccents.githubColor

val ColorX: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.executiveAccents.xColor

val ColorLinkedIn: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.executiveAccents.linkedinColor

val ColorInstagram: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.executiveAccents.instagramColor

val ColorDiscord: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.executiveAccents.discordColor

val ColorTelegram: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.executiveAccents.telegramColor

val ColorYouTube: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.executiveAccents.youtubeColor

val ColorBluesky: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.executiveAccents.blueskyColor

val ColorThreads: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.executiveAccents.threadsColor

val ColorMastodon: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.executiveAccents.mastodonColor

val ColorWhatsApp: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.executiveAccents.whatsappColor

val ColorMessenger: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.executiveAccents.messengerColor
