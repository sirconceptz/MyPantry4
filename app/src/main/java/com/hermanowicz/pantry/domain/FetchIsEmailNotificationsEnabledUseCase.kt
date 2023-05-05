package com.hermanowicz.pantry.domain

import com.hermanowicz.pantry.di.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FetchIsEmailNotificationsEnabledUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) : () -> Flow<Boolean> {
    override fun invoke(): Flow<Boolean> {
        return settingsRepository.isEmailNotificationsEnabled
    }
}