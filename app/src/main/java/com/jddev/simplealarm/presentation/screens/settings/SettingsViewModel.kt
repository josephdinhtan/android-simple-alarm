package com.jddev.simplealarm.presentation.screens.settings

import androidx.lifecycle.ViewModel
import com.jddev.simplealarm.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: SettingsRepository
) : ViewModel() {
    // ...
}