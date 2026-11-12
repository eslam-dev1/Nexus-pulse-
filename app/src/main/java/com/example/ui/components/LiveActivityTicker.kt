package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ActivityEvent
import com.example.ui.theme.BorderStroke
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.ElevatedSurface
import com.example.ui.theme.SlateSurface
import com.example.ui.theme.StatusEmerald
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VioletAccent
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LiveActivityTicker(
    events: List<ActivityEvent>,
    isSyncing: Boolean,
    onTriggerSync: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    val latestEvent = events.firstOrNull()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(SlateSurface, ElevatedSurface.copy(alpha = 0.8f))
                )
            )
            .border(1.dp, BorderStroke, RoundedCornerShape(12.dp))
            .clickable { isExpanded = !isExpanded }
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Live broadcast badge
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(CyanAccent.copy(alpha = 0.15f))
                    .border(0.5.dp, CyanAccent.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 6.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Sensors,
                    contentDescription = null,
                    tint = if (isSyncing) CyanAccent else StatusEmerald,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isSyncing) "SYNCING..." else "LIVE PULSE",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSyncing) CyanAccent else StatusEmerald
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Latest ticker message
            Text(
                text = latestEvent?.let { "${it.contactName}: ${it.actionText}" }
                    ?: "Monitoring real-time social profiles & presence...",
                fontSize = 12.sp,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(6.dp))

            Icon(
                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = "Toggle recent event history",
                tint = TextSecondary,
                modifier = Modifier.size(18.dp)
            )
        }

        // Expanded Recent Live Stream
        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            ) {
                Text(
                    text = "RECENT REAL-TIME ACTIVITY (${events.size})",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyanAccent,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                if (events.isEmpty()) {
                    Text(
                        text = "No real-time events received yet. Live engine polls automatically.",
                        fontSize = 11.sp,
                        color = TextMuted,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                } else {
                    events.take(6).forEach { ev ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(5.dp)
                                    .clip(CircleShape)
                                    .background(VioletAccent)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${ev.contactName} (${ev.platform.displayName})",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = ev.actionText,
                                    fontSize = 10.sp,
                                    color = TextSecondary,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            val timeStr = remember(ev.timestamp) {
                                SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(ev.timestamp))
                            }
                            Text(
                                text = timeStr,
                                fontSize = 9.sp,
                                color = TextMuted
                            )
                        }
                    }
                }
            }
        }
    }
}
