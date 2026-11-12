package ru.bolotov.core.uikit.format

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import java.text.NumberFormat
import ru.bolotov.tradebot.core.uikit.R

/** Display formatting only; never used in sizing or trading calculations. */
@Composable
fun formatMoney(value: Double, signed: Boolean = false): String {
    val locale = LocalConfiguration.current.locales[0]
    val number =
        NumberFormat.getNumberInstance(locale)
            .apply {
                minimumFractionDigits = 2
                maximumFractionDigits = 2
            }
            .format(value)
    return stringResource(R.string.tb_money, (if (signed && value > 0) "+" else "") + number)
}

@Composable
fun formatPercent(value: Double, digits: Int = 2, signed: Boolean = false): String {
    val locale = LocalConfiguration.current.locales[0]
    val number =
        NumberFormat.getNumberInstance(locale)
            .apply {
                minimumFractionDigits = digits
                maximumFractionDigits = digits
            }
            .format(value)
    return stringResource(R.string.tb_percent, (if (signed && value > 0) "+" else "") + number)
}
