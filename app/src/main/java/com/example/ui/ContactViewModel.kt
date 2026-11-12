package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.ActivityEvent
import com.example.data.model.Contact
import com.example.data.model.PresenceStatus
import com.example.data.model.SocialPlatform
import com.example.data.model.SocialProfile
import com.example.data.remote.GitHubApiService
import com.example.data.repository.ContactRepository
import com.example.data.sync.SocialSyncEngine
import com.example.data.sync.SyncMetrics
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.example.ui.theme.ThemeMode

import androidx.compose.runtime.Immutable
import kotlinx.coroutines.SupervisorJob

/**
 * Sorting options for the contact list.
 */
enum class ContactSortOrder(val label: String) {
    LAST_UPDATED("Last Updated"),
    ALPHABETICAL("Alphabetical")
}

/**
 * Immutable UI State representing the contact directory screen and active sheets.
 */
@Immutable
data class ContactUiState(
    val contacts: List<Contact> = emptyList(),
    val filteredContacts: List<Contact> = emptyList(),
    val favoriteContacts: List<Contact> = emptyList(),
    val searchQuery: String = "",
    val selectedFilter: String = "ALL", // "ALL", "FAVORITES", "ACTIVE", "GITHUB", "X", "LINKEDIN", "INSTAGRAM", etc.
    val sortOrder: ContactSortOrder = ContactSortOrder.LAST_UPDATED,
    val liveActivityEvents: List<ActivityEvent> = emptyList(),
    val selectedContact: Contact? = null,
    val isAddEditSheetOpen: Boolean = false,
    val editingContact: Contact? = null,
    val isSyncSettingsOpen: Boolean = false,
    val syncMetrics: SyncMetrics = SyncMetrics(),
    val totalProfilesCount: Int = 0,
    val activeContactsCount: Int = 0,
    val activeBackgroundConnections: Int = 0
)

private data class FilterCriteria(
    val query: String,
    val filter: String,
    val sortOrder: ContactSortOrder
)

private data class SheetDialogState(
    val selectedContact: Contact?,
    val isAddEditOpen: Boolean,
    val editingContact: Contact?,
    val isSyncSettingsOpen: Boolean
)

/**
 * ContactViewModel coordinates contact persistence through [ContactRepository],
 * manages real-time synchronization states, and exposes reactive [StateFlow] streams
 * to the Jetpack Compose UI layer.
 */
class ContactViewModel @JvmOverloads constructor(
    application: Application,
    val repository: ContactRepository = createDefaultRepository(application)
) : AndroidViewModel(application) {

    private val syncEngine: SocialSyncEngine = repository.syncEngine

    // Search, filter, and sort state flows
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedFilter = MutableStateFlow("ALL")
    val selectedFilter: StateFlow<String> = _selectedFilter.asStateFlow()

    private val _sortOrder = MutableStateFlow(ContactSortOrder.LAST_UPDATED)
    val sortOrder: StateFlow<ContactSortOrder> = _sortOrder.asStateFlow()

    // Dialog and sheet state flows
    private val _selectedContact = MutableStateFlow<Contact?>(null)
    val selectedContact: StateFlow<Contact?> = _selectedContact.asStateFlow()

    private val _isAddEditSheetOpen = MutableStateFlow(false)
    val isAddEditSheetOpen: StateFlow<Boolean> = _isAddEditSheetOpen.asStateFlow()

    private val _editingContact = MutableStateFlow<Contact?>(null)
    val editingContact: StateFlow<Contact?> = _editingContact.asStateFlow()

    private val _isSyncSettingsOpen = MutableStateFlow(false)
    val isSyncSettingsOpen: StateFlow<Boolean> = _isSyncSettingsOpen.asStateFlow()

    // Theme Mode state flow (Dark, Light, System)
    private val _themeMode = MutableStateFlow(ThemeMode.SYSTEM)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    // Real-time activity events stream
    private val _liveEvents = MutableStateFlow<List<ActivityEvent>>(emptyList())
    val liveActivityEvents: StateFlow<List<ActivityEvent>> = _liveEvents.asStateFlow()

    // Real-time synchronization metrics from sync engine
    val syncMetrics: StateFlow<SyncMetrics> = syncEngine.syncMetrics

    // Filter criteria flow
    private val _filterCriteria = combine(_searchQuery, _selectedFilter, _sortOrder) { query, filter, sort ->
        FilterCriteria(query, filter, sort)
    }

    // Modal/dialog presentation state flow
    private val _sheetDialogState = combine(
        _selectedContact,
        _isAddEditSheetOpen,
        _editingContact,
        _isSyncSettingsOpen
    ) { selected, isAddEdit, editing, isSyncOpen ->
        SheetDialogState(selected, isAddEdit, editing, isSyncOpen)
    }

    /**
     * Unified UI state flow observed by Compose via collectAsStateWithLifecycle.
     */
    val uiState: StateFlow<ContactUiState> = combine(
        repository.allContacts,
        _filterCriteria,
        _sheetDialogState,
        _liveEvents,
        syncEngine.syncMetrics
    ) { contacts: List<Contact>, filterCriteria: FilterCriteria, dialogs: SheetDialogState, events: List<ActivityEvent>, metrics: SyncMetrics ->
        val query = filterCriteria.query
        val filter = filterCriteria.filter
        val sortOrder = filterCriteria.sortOrder

        val filtered = contacts.filter { contact ->
            val trimmedQuery = query.trim()
            val matchesQuery = trimmedQuery.isBlank() ||
                contact.name.contains(trimmedQuery, ignoreCase = true) ||
                contact.company.contains(trimmedQuery, ignoreCase = true) ||
                contact.title.contains(trimmedQuery, ignoreCase = true) ||
                contact.tags.any { it.contains(trimmedQuery, ignoreCase = true) } ||
                contact.presence.label.contains(trimmedQuery, ignoreCase = true) ||
                contact.presence.name.contains(trimmedQuery, ignoreCase = true) ||
                contact.socialProfiles.any { profile ->
                    profile.handle.contains(trimmedQuery, ignoreCase = true) ||
                        profile.platform.displayName.contains(trimmedQuery, ignoreCase = true) ||
                        profile.platform.name.contains(trimmedQuery, ignoreCase = true) ||
                        profile.platform.defaultIconSymbol.equals(trimmedQuery, ignoreCase = true) ||
                        (profile.latestSnippet?.contains(trimmedQuery, ignoreCase = true) == true) ||
                        profile.lastActiveTime.contains(trimmedQuery, ignoreCase = true)
                }

            val matchesFilter = when (filter) {
                "ALL" -> true
                "FAVORITES" -> contact.isFavorite
                "ACTIVE" -> contact.presence == PresenceStatus.ONLINE || contact.presence == PresenceStatus.RECENTLY_ACTIVE
                "GITHUB" -> contact.getProfile(SocialPlatform.GITHUB) != null
                "X" -> contact.getProfile(SocialPlatform.X_TWITTER) != null
                "LINKEDIN" -> contact.getProfile(SocialPlatform.LINKEDIN) != null
                "INSTAGRAM" -> contact.getProfile(SocialPlatform.INSTAGRAM) != null
                "DISCORD" -> contact.getProfile(SocialPlatform.DISCORD) != null
                "TELEGRAM" -> contact.getProfile(SocialPlatform.TELEGRAM) != null
                "YOUTUBE" -> contact.getProfile(SocialPlatform.YOUTUBE) != null
                "BLUESKY" -> contact.getProfile(SocialPlatform.BLUESKY) != null
                "THREADS" -> contact.getProfile(SocialPlatform.THREADS) != null
                "MASTODON" -> contact.getProfile(SocialPlatform.MASTODON) != null
                "WHATSAPP" -> contact.getProfile(SocialPlatform.WHATSAPP) != null
                "MESSENGER" -> contact.getProfile(SocialPlatform.MESSENGER) != null
                else -> true
            }

            matchesQuery && matchesFilter
        }.let { list ->
            when (sortOrder) {
                ContactSortOrder.LAST_UPDATED -> list.sortedWith(
                    compareByDescending<Contact> { it.lastSyncedTimestamp }.thenBy { it.name.lowercase() }
                )
                ContactSortOrder.ALPHABETICAL -> list.sortedBy { it.name.lowercase() }
            }
        }

        val favorites = filtered.filter { it.isFavorite }

        val refreshedSelected = if (dialogs.selectedContact != null) {
            contacts.firstOrNull { it.id == dialogs.selectedContact.id } ?: dialogs.selectedContact
        } else null

        val totalProfiles = contacts.sumOf { it.socialProfiles.size }
        val activeContacts = contacts.count {
            it.presence == PresenceStatus.ONLINE || it.presence == PresenceStatus.RECENTLY_ACTIVE
        }
        val activeConnections = if (metrics.autoSyncActive) totalProfiles else 0

        ContactUiState(
            contacts = contacts,
            filteredContacts = filtered,
            favoriteContacts = favorites,
            searchQuery = query,
            selectedFilter = filter,
            sortOrder = sortOrder,
            liveActivityEvents = events,
            selectedContact = refreshedSelected,
            isAddEditSheetOpen = dialogs.isAddEditOpen,
            editingContact = dialogs.editingContact,
            isSyncSettingsOpen = dialogs.isSyncSettingsOpen,
            syncMetrics = metrics,
            totalProfilesCount = totalProfiles,
            activeContactsCount = activeContacts,
            activeBackgroundConnections = activeConnections
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ContactUiState()
    )

    init {
        // Initialize starter contacts if room db is empty
        viewModelScope.launch {
            repository.seedInitialContactsIfNeeded()
        }

        // Collect incoming live activity pulses from sync engine (bounded to 10 items)
        viewModelScope.launch {
            syncEngine.activityEvents.collect { event ->
                val current = _liveEvents.value
                val next = ArrayList<ActivityEvent>(minOf(current.size + 1, 10))
                next.add(event)
                for (i in 0 until minOf(current.size, 9)) {
                    next.add(current[i])
                }
                _liveEvents.value = next
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        syncEngine.shutdown()
    }

    // --- Search, Filter & Sort Actions ---

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onFilterSelect(filter: String) {
        _selectedFilter.value = filter
    }

    fun onSortOrderChange(sortOrder: ContactSortOrder) {
        _sortOrder.value = sortOrder
    }

    fun toggleSortOrder() {
        _sortOrder.value = when (_sortOrder.value) {
            ContactSortOrder.LAST_UPDATED -> ContactSortOrder.ALPHABETICAL
            ContactSortOrder.ALPHABETICAL -> ContactSortOrder.LAST_UPDATED
        }
    }

    // --- Theme Control Actions ---

    fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
    }

    fun toggleTheme() {
        _themeMode.value = when (_themeMode.value) {
            ThemeMode.DARK -> ThemeMode.LIGHT
            ThemeMode.LIGHT -> ThemeMode.DARK
            ThemeMode.SYSTEM -> ThemeMode.DARK
        }
    }

    // --- Selection & Navigation Actions ---

    fun selectContact(contact: Contact?) {
        _selectedContact.value = contact
    }

    fun openAddContact() {
        _editingContact.value = null
        _isAddEditSheetOpen.value = true
    }

    fun openEditContact(contact: Contact) {
        _editingContact.value = contact
        _isAddEditSheetOpen.value = true
    }

    fun closeAddEditSheet() {
        _isAddEditSheetOpen.value = false
        _editingContact.value = null
    }

    fun openSyncSettings() {
        _isSyncSettingsOpen.value = true
    }

    fun closeSyncSettings() {
        _isSyncSettingsOpen.value = false
    }

    // --- Live Synchronization Controls ---

    fun triggerSyncNow() {
        syncEngine.triggerSyncAll()
    }

    fun triggerPulseForContact(contact: Contact) {
        syncEngine.triggerPulseForContact(contact)
    }

    fun toggleAutoSync(enabled: Boolean) {
        syncEngine.setAutoSyncEnabled(enabled)
    }

    fun setPollingInterval(seconds: Int) {
        syncEngine.setIntervalSeconds(seconds)
    }

    // --- Data Persistence Mutators ---

    fun saveContact(
        name: String,
        title: String,
        company: String,
        email: String,
        phone: String,
        avatarUrl: String?,
        tags: List<String>,
        socialProfiles: List<SocialProfile>,
        notes: String
    ) {
        viewModelScope.launch {
            val existing = _editingContact.value
            if (existing != null) {
                val updated = existing.copy(
                    name = name.trim(),
                    title = title.trim(),
                    company = company.trim(),
                    email = email.trim(),
                    phone = phone.trim(),
                    avatarUrl = avatarUrl?.trim().takeUnless { it.isNullOrBlank() } ?: existing.avatarUrl,
                    tags = tags,
                    socialProfiles = socialProfiles,
                    notes = notes.trim(),
                    lastSyncedTimestamp = System.currentTimeMillis()
                )
                repository.updateContact(updated)
            } else {
                val newContact = Contact(
                    name = name.trim(),
                    title = title.trim(),
                    company = company.trim(),
                    email = email.trim(),
                    phone = phone.trim(),
                    avatarUrl = avatarUrl?.trim().takeUnless { it.isNullOrBlank() },
                    tags = tags,
                    presence = PresenceStatus.ONLINE,
                    socialProfiles = socialProfiles,
                    notes = notes.trim(),
                    lastSyncedTimestamp = System.currentTimeMillis()
                )
                repository.insertContact(newContact)
            }
            closeAddEditSheet()
        }
    }

    fun deleteContact(contact: Contact) {
        viewModelScope.launch {
            repository.deleteContact(contact.id)
            if (_selectedContact.value?.id == contact.id) {
                _selectedContact.value = null
            }
        }
    }

    fun toggleFavorite(contact: Contact) {
        viewModelScope.launch {
            repository.toggleFavorite(contact)
        }
    }

    fun importScannedContact(contact: Contact) {
        viewModelScope.launch {
            repository.insertContact(
                contact.copy(lastSyncedTimestamp = System.currentTimeMillis())
            )
        }
    }

    companion object {
        fun createDefaultRepository(application: Application): ContactRepository {
            val database = AppDatabase.getDatabase(application)
            val gitHubApi = GitHubApiService.create()
            val syncEngine = SocialSyncEngine(
                contactDao = database.contactDao(),
                socialProfileDao = database.socialProfileDao(),
                gitHubApi = gitHubApi,
                externalScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
            )
            return ContactRepository(
                contactDao = database.contactDao(),
                socialProfileDao = database.socialProfileDao(),
                syncEngine = syncEngine
            )
        }
    }
}
