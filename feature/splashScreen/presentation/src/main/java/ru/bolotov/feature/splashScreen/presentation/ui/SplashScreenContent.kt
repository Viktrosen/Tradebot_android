package ru.bolotov.feature.splashScreen.presentation.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.bolotov.core.uikit.TradebotTheme
import ru.bolotov.core.uikit.theme.color.ColorPalette
import ru.bolotov.tradebot.core.uikit.R


@Composable
internal fun SplashScreenContent() {
    // Состояния для анимации
    val logoScale = remember { Animatable(0.3f) }
    val logoAlpha = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }

    // Запускаем анимации при первом появлении
    LaunchedEffect(Unit) {
        // Анимация логотипа: появление и масштабирование одновременно
        launch {
            logoAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 600)
            )
        }
        launch {
            logoScale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }
        // Текст появляется чуть позже
        delay(400)
        textAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 500)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorPalette.Base.Graphite),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Логотип с анимацией
        Image(
            painter = painterResource(id = R.drawable.logo_image),
            contentDescription = "TradeBot Logo",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .aspectRatio(1f)
                .scale(logoScale.value)
                .alpha(logoAlpha.value)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Название с анимацией
        Text(
            text = "SmartTrade",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = ColorPalette.Base.White,
            modifier = Modifier.alpha(textAlpha.value)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Слоган с анимацией
        Text(
            text = "Автоматическая торговля",
            fontSize = 14.sp,
            color = ColorPalette.GrayTones.Gray400,
            modifier = Modifier.alpha(textAlpha.value)
        )
    }
}

@Preview
@Composable
fun PreviewSplash() {
    TradebotTheme {
        SplashScreenContent()
    }
}