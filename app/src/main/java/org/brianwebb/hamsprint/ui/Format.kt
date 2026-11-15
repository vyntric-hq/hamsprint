package org.brianwebb.hamsprint.ui

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

fun formatDuration(duration: Duration): String {
    val totalMinutes = duration.inWholeMinutes
    return when {
        totalMinutes < 1 -> if (duration.isPositive()) "<1m" else "0m"
        totalMinutes < 60 -> "${totalMinutes}m"
        else -> {
            val h = totalMinutes / 60
            val m = totalMinutes % 60
            if (m == 0L) "${h}h" else "${h}h ${m}m"
        }
    }
}

fun formatMillis(millis: Long): String = formatDuration(millis.milliseconds)

fun formatPercent(p: Double): String = "${(p * 100).roundToInt()}%"

/** Like [formatPercent], but a real possibility never reads as a flat 0% or 100%. */
fun formatChance(p: Double): String {
    val rounded = (p * 100).roundToInt()
    return when {
        rounded == 0 && p > 0 -> "<1%"
        rounded == 100 && p < 1 -> ">99%"
        else -> "$rounded%"
    }
}

/** One decimal, so a round's gain in expected exam score is visible. */
fun formatScore(score: Double): String = String.format(Locale.US, "%.1f", score)

fun formatDate(date: LocalDate): String =
    date.format(DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.US))
