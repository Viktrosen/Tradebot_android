package ru.bolotov.feature.positionDetails.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import ru.bolotov.core.uikit.format.formatMoney
import ru.bolotov.tradebot.feature.positionDetails.presentation.R

@Composable
internal fun detailsMoney(value: Double?): String =
    value?.let { formatMoney(value = it) } ?: stringResource(R.string.details_no_data)

@Composable
internal fun detailsTime(value: String?): String {
    val locale = LocalConfiguration.current.locales[0]
    val unavailable = stringResource(R.string.details_no_data)
    return value?.let {
        runCatching {
                val time = Instant.parse(it).atZone(ZoneId.systemDefault())
                DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT)
                    .withLocale(locale)
                    .format(time) + " " + time.offset.id
            }
            .getOrDefault(unavailable)
    } ?: unavailable
}
