package ru.bolotov.feature.positionDetails.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import java.time.Clock
import java.time.DateTimeException
import java.time.Instant
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import ru.bolotov.core.uikit.format.formatMoney
import ru.bolotov.tradebot.feature.positionDetails.presentation.R

@Composable
internal fun detailsMoney(value: Double?): String =
    value?.let { formatMoney(value = it) } ?: stringResource(R.string.details_no_data)

@Composable
internal fun detailsTime(value: String?): String {
    val locale = LocalConfiguration.current.locales[0]
    val unavailable = stringResource(R.string.details_no_data)
    return formatPositionDateTime(value = value, locale = locale) ?: unavailable
}

/** Uses the same local zone for the displayed date and the current-year comparison. */
internal fun formatPositionDateTime(
    value: String?,
    locale: Locale,
    clock: Clock = Clock.systemDefaultZone(),
): String? {
    if (value == null) return null
    return try {
        val time = Instant.parse(value).atZone(clock.zone)
        val pattern =
            if (time.year == LocalDate.now(clock).year) {
                "dd MMMM HH:mm"
            } else {
                "dd MMMM uuuu HH:mm"
            }
        DateTimeFormatter.ofPattern(pattern, locale).format(time)
    } catch (_: DateTimeException) {
        null
    }
}
