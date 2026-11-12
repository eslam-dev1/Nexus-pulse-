package com.example.ui.dialogs

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.WifiTethering
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.sync.SyncMetrics
import com.example.ui.theme.BorderStroke
import com.example.ui.theme.CardSurface
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.ElevatedSurface
import com.example.ui.theme.SlateSurface
import com.example.ui.theme.StatusEmerald
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.ThemeMode
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SyncControlDialog(
    metrics: SyncMetrics,
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    onSetThemeMode: (ThemeMode) -> Unit = {},
    onDismiss: () -> Unit,
    onToggleAutoSync: (Boolean) -> Unit,
    onSetInterval: (Int) -> Unit,
    onSyncAllNow: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = SlateSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderStroke),
            modifier = Modifier.fillMaxWidth().testTag("sync_control_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(CyanAccent.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Sensors,
                                contentDescription = null,
                                tint = CyanAccent,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "PREFERENCES & SYNC",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            letterSpacing = 1.sp
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Appearance & Theme Section
                Text(
                    text = "APPEARANCE & THEME",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyanAccent,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val themes = listOf(
                        ThemeMode.DARK to ("Dark" to Icons.Default.DarkMode),
                        ThemeMode.LIGHT to ("Light" to Icons.Default.LightMode),
                        ThemeMode.SYSTEM to ("Auto" to Icons.Default.BrightnessAuto)
                    )
                    themes.forEach { (mode, pair) ->
                        val (label, icon) = pair
                        val isSelected = themeMode == mode
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) CyanAccent.copy(alpha = 0.18f) else CardSurface)
                                .border(
                                    1.dp,
                                    if (isSelected) CyanAccent else BorderStroke,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { onSetThemeMode(mode) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = label,
                                    tint = if (isSelected) CyanAccent else TextSecondary,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = label,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) CyanAccent else TextSecondary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Auto-sync Toggle Card
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(CardSurface)
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Live Background Polling",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Text(
                            text = if (metrics.autoSyncActive) "Syncing in real time across all networks" else "Sync paused by user",
                            fontSize = 11.sp,
                            color = if (metrics.autoSyncActive) StatusEmerald else TextMuted
                        )
                    }
                    Switch(
                        checked = metrics.autoSyncActive,
                        onCheckedChange = onToggleAutoSync,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = CyanAccent,
                            checkedTrackColor = CyanAccent.copy(alpha = 0.3f),
                            uncheckedThumbColor = TextMuted,
                            uncheckedTrackColor = ElevatedSurface
                        )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Polling Cadence Selector
                Text(
                    text = "LIVE POLLING FREQUENCY",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyanAccent,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val intervals = listOf(
                        10 to "10s (Live)",
                        30 to "30s (Norm)",
                        60 to "60s (Slow)"
                    )
                    intervals.forEach { (sec, label) ->
                        val isSelected = metrics.intervalSeconds == sec
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) CyanAccent.copy(alpha = 0.2f) else CardSurface)
                                .border(
                                    1.dp,
                                    if (isSelected) CyanAccent else BorderStroke,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { onSetInterval(sec) }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) CyanAccent else TextSecondary
                            )
                        }
                    }
                }

                // Telemetry Stats
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(ElevatedSurface)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Sync Cycles", fontSize = 10.sp, color = TextMuted)
                        Text(
                            text = metrics.totalSyncedCount.toString(),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Last Cycle", fontSize = 10.sp, color = TextMuted)
                        val timeStr = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(metrics.lastSyncTime))
                        Text(
                            text = timeStr,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyanAccent
                        )
                    }
                }

                // Action Buttons
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onSyncAllNow,
                    enabled = !metrics.isSyncing,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("sync_all_now_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CyanAccent)
                ) {
                    if (metrics.isSyncing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = SlateSurface,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Syncing Profiles...", color = SlateSurface, fontWeight = FontWeight.Bold)
                    } else {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Sync",
                            tint = SlateSurface,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Sync All Profiles Now", color = SlateSurface, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
