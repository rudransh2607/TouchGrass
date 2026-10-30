package com.touchgrass.app

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.touchgrass.app.data.FocusPreferences
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FocusPreferencesPersistenceTest {

    private lateinit var context: Context
    private val testPrefsName = "test_persistence_prefs"

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext<Context>()
        val sharedPrefs = context.getSharedPreferences(testPrefsName, Context.MODE_PRIVATE)
        sharedPrefs.edit().clear().commit()
    }

    @Test
    fun initialOpen_defaultsTo60Minutes() {
        val prefs = FocusPreferences(context, testPrefsName)
        // Requirement 1: Make 1 hour/60 min default focus time when user opens app first time
        assertEquals(60, prefs.getTimerDurationMinutes())
    }

    @Test
    fun afterStartingSession_preservesSessionDurationAsDefault() {
        val prefs = FocusPreferences(context, testPrefsName)

        // User starts first session with 45 minutes
        prefs.startTimer(45)
        assertTrue(prefs.isTimerActive())
        assertEquals(45, prefs.getTimerDurationMinutes())

        // Session completes or is stopped
        prefs.stopTimer()
        assertFalse(prefs.isTimerActive())

        // Requirement 2: After 1st session always keep the time of last session as default when user opens again
        val reopenedPrefs = FocusPreferences(context, testPrefsName)
        assertEquals(45, reopenedPrefs.getTimerDurationMinutes())
    }

    @Test
    fun afterSubsequentSession_updatesDefaultToNewSessionDuration() {
        val prefs = FocusPreferences(context, testPrefsName)

        // Session 1: 30 minutes
        prefs.startTimer(30)
        prefs.stopTimer()
        assertEquals(30, prefs.getTimerDurationMinutes())

        // Session 2: 90 minutes
        prefs.startTimer(90)
        prefs.stopTimer()

        // Verify next launch defaults to 90 min
        val reopenedPrefs = FocusPreferences(context, testPrefsName)
        assertEquals(90, reopenedPrefs.getTimerDurationMinutes())
    }

    @Test
    fun manualDurationAdjustment_isPersisted() {
        val prefs = FocusPreferences(context, testPrefsName)

        // User adjusts duration slider to 50 minutes
        prefs.setTimerDurationMinutes(50)

        // Verify next launch remembers 50 min
        val reopenedPrefs = FocusPreferences(context, testPrefsName)
        assertEquals(50, reopenedPrefs.getTimerDurationMinutes())
    }
}

