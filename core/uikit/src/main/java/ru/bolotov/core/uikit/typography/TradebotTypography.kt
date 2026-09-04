package ru.bolotov.core.uikit.typography

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import ru.bolotov.tradebot.core.uikit.R

private val interFont = FontFamily(
    Font(R.font.inter),
)

private val sfProFont = FontFamily(
    Font(R.font.sf_ui_display_regular),
)

@Immutable
class TradebotTypography(

    var title: TextStyle = TextStyle(
        fontSize = 50.sp,
        lineHeight = 30.sp,
        fontFamily = interFont,
        fontWeight = FontWeight(700),
        letterSpacing = 0.5.sp,
    ),
    // Heading
    val h1Bold: TextStyle = TextStyle(
        fontSize = 24.sp,
        lineHeight = 30.sp,
        fontFamily = interFont,
        fontWeight = FontWeight(700),
        letterSpacing = 0.5.sp,
    ),

    val h1BoldLink: TextStyle = TextStyle(
        fontSize = 24.sp,
        lineHeight = 30.sp,
        fontFamily = interFont,
        fontWeight = FontWeight(700),
        letterSpacing = 0.5.sp,
        textDecoration = TextDecoration.Underline,
    ),

    val h2SemiBld: TextStyle = TextStyle(
        fontSize = 22.sp,
        lineHeight = 27.5.sp,
        fontFamily = interFont,
        fontWeight = FontWeight(600),
    ),

    val h2MedLink: TextStyle = TextStyle(
        fontSize = 22.sp,
        lineHeight = 27.5.sp,
        fontFamily = interFont,
        fontWeight = FontWeight(500),
        textDecoration = TextDecoration.Underline,
    ),

    val h2Med: TextStyle = TextStyle(
        fontSize = 22.sp,
        lineHeight = 27.5.sp,
        fontFamily = interFont,
        fontWeight = FontWeight(500),
    ),

    val h3Med: TextStyle = TextStyle(
        fontSize = 20.sp,
        lineHeight = 25.sp,
        fontFamily = interFont,
        fontWeight = FontWeight(500),
    ),

    val h3Bold: TextStyle = TextStyle(
        fontSize = 20.sp,
        lineHeight = 25.sp,
        fontFamily = interFont,
        fontWeight = FontWeight(600),
    ),

    // Subtitle
    val sub1Reg: TextStyle = TextStyle(
        fontSize = 18.sp,
        lineHeight = 24.3.sp,
        fontFamily = interFont,
        fontWeight = FontWeight(400),
    ),

    val sub2Med: TextStyle = TextStyle(
        fontSize = 18.sp,
        lineHeight = 24.3.sp,
        fontFamily = interFont,
        fontWeight = FontWeight(500),
    ),

    // Body
    val body1Bold: TextStyle = TextStyle(
        fontSize = 16.sp,
        lineHeight = 21.6.sp,
        fontFamily = interFont,
        fontWeight = FontWeight(700),
    ),

    val body1SemiBld: TextStyle = TextStyle(
        fontSize = 16.sp,
        lineHeight = 21.6.sp,
        fontFamily = interFont,
        fontWeight = FontWeight(600),
    ),

    val body1Med: TextStyle = TextStyle(
        fontSize = 16.sp,
        lineHeight = 21.6.sp,
        fontFamily = interFont,
        fontWeight = FontWeight(500),
    ),

    val body1Reg: TextStyle = TextStyle(
        fontSize = 16.sp,
        lineHeight = 21.6.sp,
        fontFamily = interFont,
        fontWeight = FontWeight(400),
    ),

    val body1RegLink: TextStyle = TextStyle(
        fontSize = 16.sp,
        lineHeight = 21.6.sp,
        fontFamily = interFont,
        fontWeight = FontWeight(400),
        textDecoration = TextDecoration.Underline,
    ),

    // Caption
    val capMed: TextStyle = TextStyle(
        fontSize = 14.sp,
        lineHeight = 18.9.sp,
        fontFamily = interFont,
        fontWeight = FontWeight(500),
        letterSpacing = 0.14.sp,
    ),

    val capReg: TextStyle = TextStyle(
        fontSize = 14.sp,
        lineHeight = 18.9.sp,
        fontFamily = interFont,
        fontWeight = FontWeight(400),
        letterSpacing = 0.14.sp,
    ),

    val capRegLink: TextStyle = TextStyle(
        fontSize = 14.sp,
        lineHeight = 18.9.sp,
        fontFamily = interFont,
        fontWeight = FontWeight(400),
        letterSpacing = 0.14.sp,
        textDecoration = TextDecoration.Underline,
    ),

    val helperReg: TextStyle = TextStyle(
        fontSize = 12.sp,
        lineHeight = 16.8.sp,
        fontFamily = interFont,
        fontWeight = FontWeight(400),
    ),

    val helperRegLink: TextStyle = TextStyle(
        fontSize = 12.sp,
        lineHeight = 16.8.sp,
        fontFamily = interFont,
        fontWeight = FontWeight(400),
        textDecoration = TextDecoration.Underline,
    ),

    val helperMed: TextStyle = TextStyle(
        fontSize = 12.sp,
        lineHeight = 16.8.sp,
        fontFamily = interFont,
        fontWeight = FontWeight(500),
    ),

    // Tooltip
    val tpBold: TextStyle = TextStyle(
        fontSize = 14.sp,
        lineHeight = 18.2.sp,
        fontFamily = interFont,
        fontWeight = FontWeight(700),
    ),

    val tpMed: TextStyle = TextStyle(
        fontSize = 14.sp,
        lineHeight = 18.2.sp,
        fontFamily = interFont,
        fontWeight = FontWeight(500),
    ),

    val tpReg: TextStyle = TextStyle(
        fontSize = 14.sp,
        lineHeight = 18.2.sp,
        fontFamily = interFont,
        fontWeight = FontWeight(400),
    ),

    // Buttons
    val btn1Bold: TextStyle = TextStyle(
        fontSize = 16.sp,
        lineHeight = 18.4.sp,
        fontFamily = interFont,
        fontWeight = FontWeight(700),
    ),

    val btn1Med: TextStyle = TextStyle(
        fontSize = 16.sp,
        lineHeight = 18.4.sp,
        fontFamily = interFont,
        fontWeight = FontWeight(500),
    ),

    val btn2Reg: TextStyle = TextStyle(
        fontSize = 14.sp,
        lineHeight = 16.1.sp,
        fontFamily = interFont,
        fontWeight = FontWeight(400),
    ),

    // Input
    val inputReg: TextStyle = TextStyle(
        fontSize = 16.sp,
        lineHeight = 21.6.sp,
        fontFamily = interFont,
        fontWeight = FontWeight(400),
    ),

    // Missing from the design

    val smsInput: TextStyle = TextStyle(
        fontSize = 28.sp,
        lineHeight = (28 * 1.25).sp,
        fontFamily = interFont,
        fontWeight = FontWeight(400),
    ),

    // Button
    val sfPro: TextStyle = TextStyle(
        fontSize = 17.sp,
        lineHeight = 22.sp,
        fontFamily = sfProFont,
        fontWeight = FontWeight(600),
    )
)
