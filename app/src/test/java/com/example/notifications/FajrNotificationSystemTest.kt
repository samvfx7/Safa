package com.example.notifications

import android.Manifest
import android.app.AlarmManager
import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.entity.PrayerEntity
import com.example.data.repository.SettingsRepository
import com.example.data.util.SolarPrayerCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowAlarmManager
import org.robolectric.shadows.ShadowNotificationManager
import java.util.Calendar
import java.util.TimeZone

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class FajrNotificationSystemTest {

    private lateinit var context: Context
    private lateinit var settingsRepository: SettingsRepository
    private lateinit var notificationManager: PrayerNotificationManager
    private lateinit var sysNotificationManager: NotificationManager
    private lateinit var shadowAlarmManager: ShadowAlarmManager
    private lateinit var shadowNotificationManager: ShadowNotificationManager

    @Before
    fun setup() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        shadowOf(app).grantPermissions(
            Manifest.permission.POST_NOTIFICATIONS,
            Manifest.permission.SCHEDULE_EXACT_ALARM
        )
        context = app
        settingsRepository = SettingsRepository(context)
        notificationManager = PrayerNotificationManager(context, settingsRepository)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        sysNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        shadowAlarmManager = shadowOf(alarmManager)
        shadowNotificationManager = shadowOf(sysNotificationManager)
    }

    @Test
    fun solarPrayerCalculator_calculatesValidFajrTimeForVariousLocations() {
        val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            set(2026, Calendar.SEPTEMBER, 5, 12, 0, 0)
        }

        // Test Makkah (lat: 21.4225, lng: 39.8262)
        val makkahFajr = SolarPrayerCalculator.calculateFajrTime(
            calendar = cal,
            latitude = 21.4225,
            longitude = 39.8262,
            methodId = 4, // Umm Al-Qura
            timeZoneId = "Asia/Riyadh"
        )
        assertNotNull(makkahFajr)
        assertTrue(makkahFajr.matches(Regex("^\\d{2}:\\d{2}$")))

        // Test New York (lat: 40.7128, lng: -74.0060)
        val nyFajr = SolarPrayerCalculator.calculateFajrTime(
            calendar = cal,
            latitude = 40.7128,
            longitude = -74.0060,
            methodId = 2, // ISNA
            timeZoneId = "America/New_York"
        )
        assertNotNull(nyFajr)
        assertTrue(nyFajr.matches(Regex("^\\d{2}:\\d{2}$")))

        // Test full day calculations
        val times = SolarPrayerCalculator.calculatePrayerTimes(
            calendar = cal,
            latitude = 21.4225,
            longitude = 39.8262,
            methodId = 4,
            isHanafi = false,
            timeZoneId = "Asia/Riyadh"
        )
        assertTrue(times.fajr.isNotEmpty())
        assertTrue(times.sunrise.isNotEmpty())
        assertTrue(times.dhuhr.isNotEmpty())
        assertTrue(times.asr.isNotEmpty())
        assertTrue(times.maghrib.isNotEmpty())
        assertTrue(times.isha.isNotEmpty())
    }

    @Test
    fun prayerNotificationManager_createsHighPriorityFajrChannel() {
        notificationManager.createNotificationChannels()

        val channel = sysNotificationManager.getNotificationChannel(PrayerNotificationManager.FAJR_CHANNEL_ID)
        assertNotNull("Fajr channel should be created", channel)
        assertEquals(NotificationManager.IMPORTANCE_HIGH, channel.importance)
        assertTrue(channel.shouldVibrate())
    }

    @Test
    fun prayerNotificationManager_schedulesFajrAlarmInAlarmManager() {
        val dummyEntity = PrayerEntity(
            date = "2026-09-05",
            fajr = "05:15",
            sunrise = "06:30",
            dhuhr = "12:30",
            asr = "15:45",
            maghrib = "18:15",
            isha = "19:45",
            locationName = "Makkah",
            latitude = 21.4225,
            longitude = 39.8262,
            calculationMethod = "Umm Al-Qura",
            hijriDate = "14 Safar 1448 AH",
            timezone = "UTC"
        )

        val scheduled = notificationManager.scheduleFajrAlarm(dummyEntity)
        assertNotNull("Fajr alarm should be successfully scheduled", scheduled)

        val nextScheduledAlarm = shadowAlarmManager.nextScheduledAlarm
        assertNotNull("An alarm must be scheduled in AlarmManager", nextScheduledAlarm)
    }

    @Test
    fun prayerAlarmReceiver_handlesDismissActionSuccessfully() {
        val receiver = PrayerAlarmReceiver()

        // 1. Post a notification first
        val notifIntent = Intent(context, PrayerAlarmReceiver::class.java).apply {
            action = PrayerAlarmReceiver.ACTION_FAJR_ALARM
            putExtra(PrayerAlarmReceiver.EXTRA_PRAYER_NAME, "Fajr")
            putExtra(PrayerAlarmReceiver.EXTRA_PRAYER_TIME_STRING, "05:15")
        }
        receiver.onReceive(context, notifIntent)

        val activeCountBefore = shadowNotificationManager.allNotifications.size
        assertTrue("Notification should be posted", activeCountBefore > 0)

        // 2. Trigger Dismiss action
        val dismissIntent = Intent(context, PrayerAlarmReceiver::class.java).apply {
            action = PrayerAlarmReceiver.ACTION_DISMISS_NOTIFICATION
            putExtra(PrayerAlarmReceiver.EXTRA_NOTIFICATION_ID, PrayerNotificationManager.NOTIFICATION_ID_FAJR)
        }
        receiver.onReceive(context, dismissIntent)

        val activeCountAfter = shadowNotificationManager.allNotifications.size
        assertEquals("Notification should be dismissed and removed", 0, activeCountAfter)
    }

    @Test
    fun prayerAlarmReceiver_displaysCorrectFajrNotificationMetadata() {
        val receiver = PrayerAlarmReceiver()
        val notifIntent = Intent(context, PrayerAlarmReceiver::class.java).apply {
            action = PrayerAlarmReceiver.ACTION_FAJR_ALARM
            putExtra(PrayerAlarmReceiver.EXTRA_PRAYER_NAME, "Fajr")
            putExtra(PrayerAlarmReceiver.EXTRA_PRAYER_TIME_STRING, "05:15")
        }
        receiver.onReceive(context, notifIntent)

        val notifications = shadowNotificationManager.allNotifications
        assertTrue(notifications.isNotEmpty())
        val notif = notifications[0]

        val title = shadowOf(notif).contentTitle.toString()
        val text = shadowOf(notif).contentText.toString()

        assertEquals("Fajr — It's time to pray", title)
        assertTrue("Notification should include Fajr time", text.contains("05:15"))
        assertTrue("Notification should include الصَّلَاةُ خَيْرٌ مِنَ النَّوْمِ", text.contains("الصَّلَاةُ خَيْرٌ مِنَ النَّوْمِ"))

        // Verify actions
        val actions = notif.actions
        assertNotNull("Actions should be present", actions)
        val actionTitles = actions.map { it.title.toString() }
        assertTrue("Must have Dismiss action", actionTitles.contains("Dismiss"))
        assertTrue("Must have Open Safa action", actionTitles.contains("Open Safa"))
        assertTrue("Must have Make Wudu action", actionTitles.contains("Make Wudu"))
        assertTrue("Must have Remind Later action", actionTitles.contains("Remind Later (10m)"))

        // Verify fullScreenIntent
        assertNotNull("Fajr notification must have fullScreenIntent set", notif.fullScreenIntent)
    }
}
