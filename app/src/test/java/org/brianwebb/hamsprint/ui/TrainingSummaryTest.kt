package org.brianwebb.hamsprint.ui

import android.os.Looper
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.brianwebb.hamsprint.AppContainer
import org.brianwebb.hamsprint.domain.CardKind
import org.brianwebb.hamsprint.domain.LicenseClass
import org.brianwebb.hamsprint.ui.training.CardPhase
import org.brianwebb.hamsprint.ui.training.TrainingUiState
import org.brianwebb.hamsprint.ui.training.TrainingViewModel
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Shadows.shadowOf
import kotlin.random.Random

/** A first perfect round must show visible progress, even though pass chance is still near zero. */
@RunWith(AndroidJUnit4::class)
class TrainingSummaryTest {
    private val container = AppContainer(ApplicationProvider.getApplicationContext(), random = Random(7), progressDbName = null)
    private val store = ViewModelStore()

    @After
    fun tearDown() {
        store.clear()
        container.progress.close()
    }

    @Test
    fun `a perfect first round raises the expected score`() {
        container.settings.update { it.copy(license = LicenseClass.TECHNICIAN, autoAdvance = false) }
        val vm = ViewModelProvider.create(store, viewModelFactory { initializer { TrainingViewModel(container, null) } })[TrainingViewModel::class]

        val summary = playPerfectRound(vm)

        assertEquals(1.0, summary.summary.correct.toDouble() / summary.summary.answered, 0.0)
        assertTrue(
            "expected score ${summary.before.expectedScore} -> ${summary.after.expectedScore}",
            summary.after.expectedScore > summary.before.expectedScore,
        )
        assertFalse("pass chance is still too small to show", summary.after.showsPassChance)
    }

    private fun playPerfectRound(vm: TrainingViewModel): TrainingUiState.Summary {
        val deadline = System.currentTimeMillis() + 20_000
        while (System.currentTimeMillis() < deadline) {
            shadowOf(Looper.getMainLooper()).idle()
            when (val s = vm.state.value) {
                is TrainingUiState.Summary -> return s
                is TrainingUiState.Lesson -> vm.startLesson()
                is TrainingUiState.Studying -> when {
                    s.phase is CardPhase.Revealed -> vm.next()
                    s.card.kind == CardKind.PREVIEW -> vm.acknowledgePreview()
                    else -> vm.select(s.card.correctPosition)
                }
                is TrainingUiState.Error -> error(s.message)
                else -> Thread.sleep(10)
            }
        }
        error("round never finished: ${vm.state.value}")
    }
}
