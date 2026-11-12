package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.ContactListScreen
import com.example.ui.ContactViewModel
import com.example.ui.theme.NexusPulseTheme
import com.example.ui.theme.ThemeMode

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val app = application
            val viewModel: ContactViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        val repo = ContactViewModel.createDefaultRepository(app)
                        return ContactViewModel(app, repo) as T
                    }
                }
            )

            val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
            val systemDark = isSystemInDarkTheme()
            val isDark = when (themeMode) {
                ThemeMode.DARK -> true
                ThemeMode.LIGHT -> false
                ThemeMode.SYSTEM -> systemDark
            }

            NexusPulseTheme(darkTheme = isDark) {
                ContactListScreen(viewModel = viewModel)
            }
        }
    }
}
