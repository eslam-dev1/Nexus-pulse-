package com.example.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.delay
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.WifiTethering
import com.example.data.model.Contact
import com.example.ui.theme.ThemeMode
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import android.view.HapticFeedbackConstants
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.ContactCard
import com.example.ui.components.LiveActivityTicker
import com.example.ui.dialogs.AddEditContactSheet
import com.example.ui.dialogs.ContactDetailSheet
import com.example.ui.dialogs.ScanContactQrDialog
import com.example.ui.dialogs.ShareContactQrDialog
import com.example.ui.dialogs.SyncControlDialog
import com.example.ui.dialogs.launchEmail
import com.example.ui.dialogs.launchPhone
import com.example.ui.theme.BorderStroke
import com.example.ui.theme.CardSurface
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.ElevatedSurface
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.SlateSurface
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.StatusEmerald
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VioletAccent

@Composable
fun ContactListScreen(
    viewModel: ContactViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val isSystemInDark = androidx.compose.foundation.isSystemInDarkTheme()
    val isDark = when (themeMode) {
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.SYSTEM -> isSystemInDark
    }
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val view = LocalView.current

    val triggerHapticTick = {
        view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
    }
    val triggerHapticConfirm = {
        view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
    }

    var sharingQrContact by remember { mutableStateOf<Contact?>(null) }
    var isQrScannerOpen by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBg),
        containerColor = ObsidianBg,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    triggerHapticTick()
                    viewModel.openAddContact()
                },
                containerColor = CyanAccent,
                contentColor = SlateSurface,
                shape = CircleShape,
                modifier = Modifier
                    .size(56.dp)
                    .testTag("add_contact_fab")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add new contact",
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .windowInsetsPadding(WindowInsets.statusBars)
        ) {
            // Executive Header Bar with real-time sync feedback and QR scanner
            ExecutiveTopBar(
                isSyncing = uiState.syncMetrics.isSyncing,
                lastSyncTime = uiState.syncMetrics.lastSyncTime,
                autoSyncActive = uiState.syncMetrics.autoSyncActive,
                isDarkTheme = isDark,
                onToggleTheme = {
                    triggerHapticTick()
                    viewModel.toggleTheme()
                },
                onScanQrClick = {
                    triggerHapticTick()
                    isQrScannerOpen = true
                },
                onSyncClick = {
                    triggerHapticConfirm()
                    viewModel.triggerSyncNow()
                },
                onSettingsClick = {
                    triggerHapticTick()
                    viewModel.openSyncSettings()
                }
            )

            // Live Activity Stream Banner
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                LiveActivityTicker(
                    events = uiState.liveActivityEvents,
                    isSyncing = uiState.syncMetrics.isSyncing,
                    onTriggerSync = {
                        triggerHapticConfirm()
                        viewModel.triggerSyncNow()
                    }
                )
            }

            // Executive Metrics Snapshot
            MetricsSnapshotRow(
                totalContacts = uiState.contacts.size,
                totalProfiles = uiState.totalProfilesCount,
                activeCount = uiState.activeContactsCount
            )

            // Persistent Animated Search Bar & Sort Dropdown Control
            SearchBarSection(
                query = uiState.searchQuery,
                onQueryChange = { viewModel.onSearchQueryChange(it) },
                resultCount = uiState.filteredContacts.size,
                sortOrder = uiState.sortOrder,
                onSortOrderChange = {
                    triggerHapticTick()
                    viewModel.onSortOrderChange(it)
                }
            )

            // Filter Chips Row
            FilterChipsRow(
                selectedFilter = uiState.selectedFilter,
                onSelectFilter = {
                    triggerHapticTick()
                    viewModel.onFilterSelect(it)
                }
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Main Contact List with Pull-to-Refresh mechanism and haptic feedback
            SwipeRefresh(
                isRefreshing = uiState.syncMetrics.isSyncing,
                onRefresh = {
                    triggerHapticConfirm()
                    viewModel.triggerSyncNow()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                val isLoading = uiState.contacts.isEmpty() && uiState.syncMetrics.isSyncing
                if (uiState.filteredContacts.isEmpty() || isLoading) {
                    EmptyStateView(
                        isLoading = isLoading,
                        isFilterActive = uiState.searchQuery.isNotBlank() || uiState.selectedFilter != "ALL",
                        onRefresh = {
                            triggerHapticConfirm()
                            viewModel.triggerSyncNow()
                        },
                        onAddContact = {
                            triggerHapticTick()
                            viewModel.openAddContact()
                        },
                        onClearFilter = {
                            triggerHapticTick()
                            viewModel.onSearchQueryChange("")
                            viewModel.onFilterSelect("ALL")
                        }
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                            .testTag("contact_list_lazy_column"),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(top = 4.dp, bottom = 88.dp)
                    ) {
                        item(key = "sync_summary_card", contentType = "sync_summary") {
                            SyncSummaryCard(
                                lastSyncTime = uiState.syncMetrics.lastSyncTime,
                                activeConnections = uiState.activeBackgroundConnections,
                                isSyncing = uiState.syncMetrics.isSyncing,
                                autoSyncActive = uiState.syncMetrics.autoSyncActive,
                                onClick = { viewModel.openSyncSettings() }
                            )
                        }

                        if (uiState.favoriteContacts.isNotEmpty() && uiState.selectedFilter != "FAVORITES") {
                            item(key = "favorites_section", contentType = "favorites_strip") {
                                FavoriteContactsSection(
                                    favorites = uiState.favoriteContacts,
                                    onContactClick = {
                                        triggerHapticTick()
                                        viewModel.selectContact(it)
                                    },
                                    onToggleFavorite = {
                                        triggerHapticConfirm()
                                        viewModel.toggleFavorite(it)
                                    },
                                    onShowAllFavorites = {
                                        triggerHapticTick()
                                        viewModel.onFilterSelect("FAVORITES")
                                    }
                                )
                            }
                        }

                        items(
                            items = uiState.filteredContacts,
                            key = { it.id },
                            contentType = { "contact_card" }
                        ) { contact ->
                            ContactCard(
                                contact = contact,
                                onClick = { viewModel.selectContact(contact) },
                                onCallClick = { launchPhone(context, contact.phone) },
                                onEmailClick = { launchEmail(context, contact.email) },
                                onPulseClick = { viewModel.triggerPulseForContact(contact) },
                                onFavoriteClick = { viewModel.toggleFavorite(contact) },
                                onShareQrClick = { sharingQrContact = contact }
                            )
                        }
                    }
                }
            }
        }
    }

    // Detail Dossier Sheet
    uiState.selectedContact?.let { contact ->
        ContactDetailSheet(
            contact = contact,
            onDismiss = { viewModel.selectContact(null) },
            onEditClick = { viewModel.openEditContact(it) },
            onDeleteClick = { viewModel.deleteContact(it) },
            onPulseClick = { viewModel.triggerPulseForContact(it) },
            onShareQrClick = { sharingQrContact = it }
        )
    }

    // Share Contact QR Code Dialog
    sharingQrContact?.let { qrContact ->
        ShareContactQrDialog(
            contact = qrContact,
            onDismiss = { sharingQrContact = null },
            onOpenScanner = { isQrScannerOpen = true }
        )
    }

    // Scan Contact QR Code Dialog
    if (isQrScannerOpen) {
        ScanContactQrDialog(
            onDismiss = { isQrScannerOpen = false },
            onImportContact = { scannedContact ->
                viewModel.importScannedContact(scannedContact)
            }
        )
    }

    // Add / Edit Sheet
    if (uiState.isAddEditSheetOpen) {
        AddEditContactSheet(
            contact = uiState.editingContact,
            onDismiss = { viewModel.closeAddEditSheet() },
            onSave = { name, title, company, email, phone, avatarUrl, tags, profiles, notes ->
                viewModel.saveContact(name, title, company, email, phone, avatarUrl, tags, profiles, notes)
            }
        )
    }

    // Sync Engine Controller Dialog
    if (uiState.isSyncSettingsOpen) {
        SyncControlDialog(
            metrics = uiState.syncMetrics,
            themeMode = themeMode,
            onSetThemeMode = { viewModel.setThemeMode(it) },
            onDismiss = { viewModel.closeSyncSettings() },
            onToggleAutoSync = { viewModel.toggleAutoSync(it) },
            onSetInterval = { viewModel.setPollingInterval(it) },
            onSyncAllNow = { viewModel.triggerSyncNow() }
        )
    }
}

@Composable
fun ExecutiveTopBar(
    isSyncing: Boolean,
    lastSyncTime: Long,
    autoSyncActive: Boolean,
    isDarkTheme: Boolean = true,
    onToggleTheme: () -> Unit = {},
    onScanQrClick: () -> Unit = {},
    onSyncClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        Brush.linearGradient(listOf(CyanAccent.copy(alpha = 0.25f), VioletAccent.copy(alpha = 0.4f)))
                    )
                    .border(1.dp, CyanAccent.copy(alpha = 0.6f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Sensors,
                    contentDescription = null,
                    tint = CyanAccent,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Nexus",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                    Text(
                        text = "Pulse",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = CyanAccent
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    // Subtle real-time sync status badge (Syncing..., Up to date, Paused)
                    LiveSyncStatusBadge(
                        isSyncing = isSyncing,
                        lastSyncTime = lastSyncTime,
                        autoSyncActive = autoSyncActive,
                        onClick = onSettingsClick
                    )
                }
                Text(
                    text = "REAL-TIME SOCIAL SYNC",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    letterSpacing = 1.2.sp
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            // Scan Contact QR button
            IconButton(
                onClick = onScanQrClick,
                modifier = Modifier
                    .size(38.dp)
                    .testTag("topbar_scan_qr_button")
            ) {
                Icon(
                    imageVector = Icons.Default.QrCodeScanner,
                    contentDescription = "Scan contact QR code",
                    tint = CyanAccent,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Theme toggle button (Dark / Light)
            IconButton(
                onClick = onToggleTheme,
                modifier = Modifier
                    .size(38.dp)
                    .testTag("theme_toggle_button")
            ) {
                Icon(
                    imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = if (isDarkTheme) "Switch to Light Mode" else "Switch to Dark Mode",
                    tint = if (isDarkTheme) CyanAccent else VioletAccent,
                    modifier = Modifier.size(20.dp)
                )
            }

            IconButton(
                onClick = onSyncClick,
                modifier = Modifier
                    .size(38.dp)
                    .testTag("topbar_sync_button")
            ) {
                if (isSyncing) {
                    CircularProgressIndicator(
                        color = CyanAccent,
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Sync all contacts",
                        tint = CyanAccent,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            IconButton(
                onClick = onSettingsClick,
                modifier = Modifier
                    .size(38.dp)
                    .testTag("topbar_settings_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Sync settings",
                    tint = TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

private data class SyncStatusStyle(
    val bg: Color,
    val border: Color,
    val text: Color,
    val label: String
)

/**
 * Subtle pill badge displaying real-time background sync feedback (Syncing..., Up to date, Paused).
 */
@Composable
fun LiveSyncStatusBadge(
    isSyncing: Boolean,
    lastSyncTime: Long,
    autoSyncActive: Boolean,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val style = when {
        isSyncing -> SyncStatusStyle(
            bg = CyanAccent.copy(alpha = 0.15f),
            border = CyanAccent.copy(alpha = 0.5f),
            text = CyanAccent,
            label = "Syncing..."
        )
        !autoSyncActive -> SyncStatusStyle(
            bg = StatusAmber.copy(alpha = 0.15f),
            border = StatusAmber.copy(alpha = 0.5f),
            text = StatusAmber,
            label = "Paused"
        )
        else -> SyncStatusStyle(
            bg = StatusEmerald.copy(alpha = 0.15f),
            border = StatusEmerald.copy(alpha = 0.5f),
            text = StatusEmerald,
            label = "Up to date"
        )
    }

    val clickMod = if (onClick != null) modifier.clickable { onClick() } else modifier

    Row(
        modifier = clickMod
            .clip(RoundedCornerShape(8.dp))
            .background(style.bg)
            .border(0.75.dp, style.border, RoundedCornerShape(8.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isSyncing) {
            val infiniteTransition = rememberInfiniteTransition(label = "syncPulseTransition")
            val rotation by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1200, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = "syncRotation"
            )
            Icon(
                imageVector = Icons.Default.Sync,
                contentDescription = null,
                tint = CyanAccent,
                modifier = Modifier
                    .size(11.dp)
                    .graphicsLayer(rotationZ = rotation)
            )
        } else if (!autoSyncActive) {
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .clip(CircleShape)
                    .background(StatusAmber)
            )
        } else {
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .clip(CircleShape)
                    .background(StatusEmerald)
            )
        }

        Spacer(modifier = Modifier.width(4.dp))

        AnimatedContent(
            targetState = style.label,
            transitionSpec = { fadeIn(tween(150)) togetherWith fadeOut(tween(150)) },
            label = "syncStatusText"
        ) { text ->
            Text(
                text = text,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = style.text
            )
        }
    }
}

@Composable
fun MetricsSnapshotRow(
    totalContacts: Int,
    totalProfiles: Int,
    activeCount: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        MetricTile(
            title = "CONTACTS",
            value = totalContacts.toString(),
            color = TextPrimary,
            modifier = Modifier.weight(1f)
        )
        MetricTile(
            title = "SOCIAL HUBS",
            value = totalProfiles.toString(),
            color = CyanAccent,
            modifier = Modifier.weight(1f)
        )
        MetricTile(
            title = "ACTIVE NOW",
            value = activeCount.toString(),
            color = StatusEmerald,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun MetricTile(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(CardSurface)
            .border(1.dp, BorderStroke, RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        Text(text = title, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextMuted, letterSpacing = 0.5.sp)
        Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Black, color = color)
    }
}

/**
 * Compact 'Sync Summary' card at the top of the contact list displaying the time of the
 * last successful global sync and the number of active background connections.
 */
@Composable
fun SyncSummaryCard(
    lastSyncTime: Long,
    activeConnections: Int,
    isSyncing: Boolean,
    autoSyncActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val formattedSyncTime = remember(lastSyncTime) {
        SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(lastSyncTime))
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        CardSurface,
                        ElevatedSurface.copy(alpha = 0.85f)
                    )
                )
            )
            .border(
                width = 1.dp,
                color = if (isSyncing) CyanAccent.copy(alpha = 0.6f) else BorderStroke,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 10.dp)
            .testTag("sync_summary_card"),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left: Card Title & Icon
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (autoSyncActive) CyanAccent.copy(alpha = 0.15f)
                        else StatusAmber.copy(alpha = 0.15f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.WifiTethering,
                    contentDescription = "Background connections",
                    tint = if (autoSyncActive) CyanAccent else StatusAmber,
                    modifier = Modifier.size(16.dp)
                )
            }

            Column {
                Text(
                    text = "SYNC SUMMARY",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyanAccent,
                    letterSpacing = 0.8.sp
                )
                Text(
                    text = if (isSyncing) "Global sync in progress..." else "Telemetry Engine Ready",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextSecondary
                )
            }
        }

        // Right: Last Global Sync & Active Background Connections
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Column(horizontalAlignment = Alignment.End) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(11.dp)
                    )
                    Text(
                        text = "LAST SYNC",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMuted,
                        letterSpacing = 0.5.sp
                    )
                }
                Text(
                    text = formattedSyncTime,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.testTag("sync_summary_last_sync_time")
                )
            }

            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(24.dp)
                    .background(BorderStroke)
            )

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "CONNECTIONS",
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    letterSpacing = 0.5.sp
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(if (autoSyncActive && activeConnections > 0) StatusEmerald else StatusAmber)
                    )
                    Text(
                        text = "$activeConnections Active",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (autoSyncActive && activeConnections > 0) StatusEmerald else StatusAmber,
                        modifier = Modifier.testTag("sync_summary_active_connections")
                    )
                }
            }
        }
    }
}

@Composable
fun SearchBarSection(
    query: String,
    onQueryChange: (String) -> Unit,
    resultCount: Int = 0,
    sortOrder: ContactSortOrder = ContactSortOrder.LAST_UPDATED,
    onSortOrderChange: (ContactSortOrder) -> Unit = {}
) {
    var isSortMenuExpanded by remember { mutableStateOf(false) }
    var isSearchFocused by remember { mutableStateOf(false) }
    val isSearchActive = isSearchFocused || query.isNotBlank()

    // Animated cycling placeholder hints for name, platform, and recent activity status
    val placeholders = remember {
        listOf(
            "Search name, platform, or activity status...",
            "Filter by 'GitHub', 'WhatsApp', 'Messenger'...",
            "Filter by 'Active Now' or 'Recently Active'..."
        )
    }
    var placeholderIndex by remember { mutableIntStateOf(0) }
    LaunchedEffect(query) {
        if (query.isBlank()) {
            while (true) {
                delay(3500L)
                placeholderIndex = (placeholderIndex + 1) % placeholders.size
            }
        }
    }

    val animatedBorderWidth by animateDpAsState(
        targetValue = if (isSearchActive) 1.5.dp else 1.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "searchBorderWidth"
    )
    val animatedIconScale by animateFloatAsState(
        targetValue = if (isSearchActive) 1.12f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "searchIconScale"
    )
    val animatedIconTint by animateColorAsState(
        targetValue = if (query.isNotBlank()) StatusEmerald else CyanAccent,
        animationSpec = tween(220),
        label = "searchIconTint"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("persistent_animated_search_section")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = {
                    AnimatedContent(
                        targetState = placeholders[placeholderIndex],
                        transitionSpec = { fadeIn(tween(240)) togetherWith fadeOut(tween(240)) },
                        label = "searchPlaceholderAnim"
                    ) { hintText ->
                        Text(
                            text = hintText,
                            fontSize = 12.sp,
                            color = TextMuted,
                            maxLines = 1
                        )
                    }
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search contacts",
                        tint = animatedIconTint,
                        modifier = Modifier
                            .size(18.dp)
                            .graphicsLayer(
                                scaleX = animatedIconScale,
                                scaleY = animatedIconScale
                            )
                    )
                },
                trailingIcon = {
                    AnimatedVisibility(
                        visible = query.isNotBlank(),
                        enter = fadeIn(tween(180)) + scaleIn(tween(180)),
                        exit = fadeOut(tween(150)) + scaleOut(tween(150))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(end = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(CyanAccent.copy(alpha = 0.15f))
                                    .border(0.75.dp, CyanAccent.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                    .testTag("search_results_count_badge")
                            ) {
                                Text(
                                    text = "$resultCount",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyanAccent
                                )
                            }
                            IconButton(
                                onClick = { onQueryChange("") },
                                modifier = Modifier
                                    .size(32.dp)
                                    .testTag("clear_search_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear search",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyanAccent,
                    unfocusedBorderColor = if (query.isNotBlank()) CyanAccent.copy(alpha = 0.65f) else BorderStroke,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = ElevatedSurface,
                    unfocusedContainerColor = CardSurface
                ),
                modifier = Modifier
                    .weight(1f)
                    .border(
                        width = animatedBorderWidth,
                        color = if (isSearchActive) CyanAccent.copy(alpha = 0.45f) else Color.Transparent,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .onFocusChanged { isSearchFocused = it.isFocused }
                    .testTag("contact_search_bar")
            )

            // Sort Dropdown Button
            Box {
            Row(
                modifier = Modifier
                    .height(54.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(CardSurface)
                    .border(
                        width = 1.dp,
                        color = if (isSortMenuExpanded) CyanAccent else BorderStroke,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { isSortMenuExpanded = true }
                    .padding(horizontal = 12.dp)
                    .testTag("sort_dropdown_button"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Sort,
                    contentDescription = "Sort contacts",
                    tint = CyanAccent,
                    modifier = Modifier.size(17.dp)
                )
                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        text = "SORT BY",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMuted,
                        letterSpacing = 0.6.sp
                    )
                    Text(
                        text = sortOrder.label,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }

            DropdownMenu(
                expanded = isSortMenuExpanded,
                onDismissRequest = { isSortMenuExpanded = false },
                modifier = Modifier
                    .background(ElevatedSurface)
                    .border(1.dp, BorderStroke, RoundedCornerShape(8.dp))
            ) {
                ContactSortOrder.entries.forEach { option ->
                    val isSelected = sortOrder == option
                    val optionTag = when (option) {
                        ContactSortOrder.LAST_UPDATED -> "sort_option_last_updated"
                        ContactSortOrder.ALPHABETICAL -> "sort_option_alphabetical"
                    }
                    val optionIcon = when (option) {
                        ContactSortOrder.LAST_UPDATED -> Icons.Default.Schedule
                        ContactSortOrder.ALPHABETICAL -> Icons.Default.SortByAlpha
                    }

                    DropdownMenuItem(
                        text = {
                            Text(
                                text = option.label,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) CyanAccent else TextPrimary
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = optionIcon,
                                contentDescription = null,
                                tint = if (isSelected) CyanAccent else TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        trailingIcon = {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = CyanAccent,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        },
                        onClick = {
                            onSortOrderChange(option)
                            isSortMenuExpanded = false
                        },
                        modifier = Modifier.testTag(optionTag)
                    )
                }
            }
        }
        }

        // Animated quick-search suggestion bar when focused
        AnimatedVisibility(
            visible = isSearchFocused && query.isBlank(),
            enter = expandVertically(tween(200)) + fadeIn(tween(200)),
            exit = shrinkVertically(tween(180)) + fadeOut(tween(150))
        ) {
            val quickSearchTerms = listOf("Active Now", "Recently Active", "GitHub", "WhatsApp", "Messenger")
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp)
                    .testTag("search_quick_suggestions_row"),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(quickSearchTerms) { term ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(ElevatedSurface)
                            .border(0.75.dp, CyanAccent.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                            .clickable { onQueryChange(term) }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "⌕ $term",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = CyanAccent
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FilterChipsRow(
    selectedFilter: String,
    onSelectFilter: (String) -> Unit
) {
    val filters = listOf(
        "ALL" to "All Contacts",
        "FAVORITES" to "★ Favorites",
        "ACTIVE" to "Active Now",
        "GITHUB" to "GitHub",
        "X" to "X / Twitter",
        "LINKEDIN" to "LinkedIn",
        "INSTAGRAM" to "Instagram",
        "DISCORD" to "Discord",
        "TELEGRAM" to "Telegram",
        "YOUTUBE" to "YouTube",
        "BLUESKY" to "Bluesky",
        "THREADS" to "Threads",
        "MASTODON" to "Mastodon",
        "WHATSAPP" to "WhatsApp",
        "MESSENGER" to "Messenger"
    )

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(filters) { (key, label) ->
            val isSelected = selectedFilter == key
            val isFavoritesChip = key == "FAVORITES"
            val activeColor = if (isFavoritesChip) StatusAmber else CyanAccent
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) activeColor else CardSurface)
                    .border(
                        1.dp,
                        if (isSelected) activeColor else if (isFavoritesChip) StatusAmber.copy(alpha = 0.45f) else BorderStroke,
                        RoundedCornerShape(8.dp)
                    )
                    .clickable { onSelectFilter(key) }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .testTag("filter_chip_$key")
            ) {
                Text(
                    text = label,
                    fontSize = 11.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) SlateSurface else if (isFavoritesChip) StatusAmber else TextSecondary
                )
            }
        }
    }
}

/**
 * Dedicated section at the top of the contact list displaying marked important / favorite contacts.
 */
@Composable
fun FavoriteContactsSection(
    favorites: List<Contact>,
    onContactClick: (Contact) -> Unit,
    onToggleFavorite: (Contact) -> Unit,
    onShowAllFavorites: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CardSurface)
            .border(1.dp, StatusAmber.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
            .padding(12.dp)
            .testTag("favorites_section")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = StatusAmber,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "IMPORTANT CONTACTS (${favorites.size})",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = StatusAmber,
                    letterSpacing = 0.8.sp
                )
            }

            Text(
                text = "Filter Only ★",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = CyanAccent,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .clickable { onShowAllFavorites() }
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                    .testTag("filter_favorites_only_button")
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = favorites,
                key = { "fav_${it.id}" }
            ) { contact ->
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(ElevatedSurface)
                        .border(1.dp, BorderStroke, RoundedCornerShape(10.dp))
                        .clickable { onContactClick(contact) }
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                        .testTag("favorite_pinned_item_${contact.id}"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(StatusAmber.copy(alpha = 0.3f), CyanAccent.copy(alpha = 0.35f))
                                )
                            )
                            .border(1.dp, StatusAmber.copy(alpha = 0.6f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = contact.initials,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    Column {
                        Text(
                            text = contact.name,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        if (contact.company.isNotBlank()) {
                            Text(
                                text = contact.company,
                                fontSize = 10.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    IconButton(
                        onClick = { onToggleFavorite(contact) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Unmark favorite",
                            tint = StatusAmber,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyStateView(
    isLoading: Boolean,
    isFilterActive: Boolean,
    onRefresh: () -> Unit,
    onAddContact: () -> Unit,
    onClearFilter: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 28.dp, vertical = 32.dp)
            .testTag("empty_state_view"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (isLoading) {
            // Loading & Initial Sync Animation
            val infiniteTransition = rememberInfiniteTransition(label = "pulse_radar")
            val pulseScale by infiniteTransition.animateFloat(
                initialValue = 0.85f,
                targetValue = 1.25f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1400, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "pulseScale"
            )
            val pulseAlpha by infiniteTransition.animateFloat(
                initialValue = 0.25f,
                targetValue = 0.75f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1400, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "pulseAlpha"
            )
            val rotationAngle by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(2200, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = "rotationAngle"
            )

            // Animated concentric glowing radar beacon
            Box(
                modifier = Modifier.size(130.dp),
                contentAlignment = Alignment.Center
            ) {
                // Outer glowing halo
                Box(
                    modifier = Modifier
                        .size(120.dp * pulseScale)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    CyanAccent.copy(alpha = 0.35f * pulseAlpha),
                                    VioletAccent.copy(alpha = 0.15f * pulseAlpha),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // Middle glowing ring
                Box(
                    modifier = Modifier
                        .size(86.dp)
                        .clip(CircleShape)
                        .background(SlateSurface)
                        .border(
                            width = 2.dp,
                            brush = Brush.sweepGradient(
                                listOf(CyanAccent, VioletAccent, StatusEmerald, CyanAccent)
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // Rotating sync icon
                    Icon(
                        imageVector = Icons.Default.Sync,
                        contentDescription = "Syncing directory",
                        tint = CyanAccent,
                        modifier = Modifier
                            .size(40.dp)
                            .graphicsLayer { rotationZ = rotationAngle }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Status chip
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(CyanAccent.copy(alpha = 0.12f))
                    .border(1.dp, CyanAccent.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(StatusEmerald)
                    )
                    Text(
                        text = "SYNCING DIRECTORY",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyanAccent,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Connecting Social Feeds",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Establishing secure real-time pipelines to GitHub, X, LinkedIn, and Instagram to retrieve executive profiles...",
                fontSize = 13.sp,
                color = TextMuted,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Indeterminate progress accent bar
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(CyanAccent, VioletAccent, StatusEmerald)
                        )
                    )
            )
        } else {
            // Empty State (Filtered or Directory Empty)
            // Multi-layered illuminated beacon
            Box(
                modifier = Modifier.size(110.dp),
                contentAlignment = Alignment.Center
            ) {
                // Background radial glow
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    if (isFilterActive) VioletAccent.copy(alpha = 0.25f) else CyanAccent.copy(alpha = 0.25f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // Outer border circle
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .clip(CircleShape)
                        .background(ElevatedSurface)
                        .border(
                            width = 1.5.dp,
                            color = if (isFilterActive) VioletAccent.copy(alpha = 0.6f) else CyanAccent.copy(alpha = 0.6f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isFilterActive) Icons.Default.FilterList else Icons.Default.Sensors,
                        contentDescription = null,
                        tint = if (isFilterActive) VioletAccent else CyanAccent,
                        modifier = Modifier.size(34.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Badge chip
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (isFilterActive) VioletAccent.copy(alpha = 0.12f)
                        else CyanAccent.copy(alpha = 0.12f)
                    )
                    .border(
                        width = 1.dp,
                        color = if (isFilterActive) VioletAccent.copy(alpha = 0.35f) else CyanAccent.copy(alpha = 0.35f),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = if (isFilterActive) "FILTERED SEARCH" else "DIRECTORY EMPTY",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isFilterActive) VioletAccent else CyanAccent,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (isFilterActive) "No Matching Contacts" else "No Executive Contacts Yet",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isFilterActive)
                    "No contacts match your current search query or platform filter. Tap below to reset filters or refresh live data."
                else
                    "Your executive directory is empty. Sync with live social platforms or create a new contact to start tracking real-time status and activity.",
                fontSize = 13.sp,
                color = TextMuted,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(26.dp))

            // Primary CTA: Refresh & Sync Data Button (Always prominent)
            Button(
                onClick = onRefresh,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("empty_state_refresh_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CyanAccent,
                    contentColor = SlateSurface
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Refresh & Sync Data",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Secondary CTA: Clear Filter OR Add Contact
            if (isFilterActive) {
                OutlinedButton(
                    onClick = onClearFilter,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("empty_state_clear_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = TextPrimary
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderStroke)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear Filters",
                            tint = VioletAccent,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Clear Filter & Search",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            } else {
                OutlinedButton(
                    onClick = onAddContact,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("empty_state_add_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = TextPrimary
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderStroke)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Contact",
                            tint = CyanAccent,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Add New Contact",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Subtle Hint for Pull-to-Refresh
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(CardSurface)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Sync,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Tip: Swipe down from top anytime to pull-to-refresh",
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeRefresh(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val state = rememberPullToRefreshState()
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        state = state,
        modifier = modifier.testTag("swipe_refresh_layout"),
        indicator = {
            PullToRefreshDefaults.Indicator(
                state = state,
                isRefreshing = isRefreshing,
                modifier = Modifier.align(Alignment.TopCenter),
                containerColor = SlateSurface,
                color = CyanAccent
            )
        }
    ) {
        content()
    }
}
