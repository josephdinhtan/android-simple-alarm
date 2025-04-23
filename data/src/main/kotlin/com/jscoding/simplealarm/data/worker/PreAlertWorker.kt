package com.jscoding.simplealarm.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.jscoding.simplealarm.domain.repository.AlarmRepository
import com.jscoding.simplealarm.domain.repository.SettingsRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltWorker
class PreAlertWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters
) : CoroutineWorker(context, params) {

    @Inject
    lateinit var alarmRepository: AlarmRepository

    @Inject
    lateinit var settingsRepository: SettingsRepository

    override suspend fun doWork(): Result {
        val alarmId = inputData.getLong(EXTRA_ALARM_ID, -1L)
        Timber.d("PreAlertWorker: $alarmId")
        if (alarmId == -1L) {
            Timber.e("Invalid alarm id")
            return Result.failure()
        }

        val alarm = alarmRepository.getAlarmById(alarmId)
        if (alarm != null) {
            val is24Hour = settingsRepository.getIs24HourFormat()
//            notificationHelper.showAlarmAlertNotification(alarm.toStringNotification(is24Hour), alarmId)
            return Result.success()
        }

        return Result.failure()
    }

    companion object {
        const val EXTRA_ALARM_ID = "alarm_id"

        fun buildRequest(alarmId: Long, delayMillis: Long): OneTimeWorkRequest {
            val data = workDataOf(EXTRA_ALARM_ID to alarmId)

            return OneTimeWorkRequestBuilder<PreAlertWorker>()
                .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
                .setInputData(data)
                .addTag("pre_alert_$alarmId")
                .build()
        }
    }
}