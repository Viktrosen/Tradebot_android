package ru.bolotov.core.uikit.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import ru.bolotov.core.uikit.TradebotTheme
import ru.bolotov.core.uikit.dimensions.TradebotDimens

@Composable
fun TradebotIcon(
    @DrawableRes resource: Int,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    tint: Color = TradebotTheme.colors.text,
) {
    Icon(painterResource(resource), contentDescription, modifier.size(TradebotDimens.icon), tint)
}
