package ru.bolotov.core.uikit.theme.color

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color

@Stable
object ColorPalette {

    @Stable
    object Base {
        val Black = Color(0xFF000000)
        val White = Color(0xFFFFFFFF)
        val White90 = Color(0xe5ffffff)
        val White50 = Color(0x80ffffff)
        val Graphite = Color(0xff22242c)
        val Graphite90 = Color(0xe522242c)
        val Red = Color(0xffff4033)
        val Pink = Color(0xffffc0cb)
        val Blue = Color(0xff0F4CC0)
        val Transparent = Color(0x00000000)
    }

    @Stable
    object Additional {
        val Color = Color(0xff2568ef)
        val Education = Color(0xff6c5cff)
        val ProfCourses = Color(0xff2916d8)
        val Success = Color(0xff6dc615)
        val Plus = Color(0xff5cc84d)
        val Premium = Color(0xfffe4262)
        val Premium2 = Color(0xffdd0a34)
        val PremiumStart = Color(0xffFF4464)
    }

    @Stable
    object GrayTones {
        val Gray25 = Color(0xfff2f2f2)
        val Gray30 = Color(0xffF3F4F6)
        val Gray50 = Color(0xfff6f7f7)
        val Gray55 = Color(0xffE5E7EB)
        val Gray60 = Color(0xffeaecee)
        val Gray100 = Color(0xffeef2f4)
        val Gray190 = Color(0xFFF6F7F7)
        val Gray200 = Color(0xffdedede)
        val Gray225 = Color(0xFFBFBFBF)
        val Gray250 = Color(0xffc0c1c5)
        val Gray280 = Color(0xFF9CA3AF)
        val Gray300 = Color(0xffb0b0b0)
        val Gray350 = Color(0xFF8F9090)
        val Gray400 = Color(0xff888888)
        val Gray500 = Color(0xff6d6d6d)
        val Gray600 = Color(0xff5d5d5d)
        val Gray700 = Color(0xff4f4f4f)
        val Gray800 = Color(0xff454545)
        val Gray900 = Color(0xff333333)
        val Gray950 = Color(0xff262626)
    }

    @Stable
    object RedTones {
        val Red50 = Color(0xfffff2f1)
        val Red100 = Color(0xffffe1df)
        val Red200 = Color(0xffffc9c5)
        val Red300 = Color(0xffffa29c)
        val Red400 = Color(0xffff6e64)
        val Red500 = Color(0xffff4033)
        val Red600 = Color(0xffef3124)
        val Red700 = Color(0xffc8190d)
        val Red800 = Color(0xffa5190f)
        val Red900 = Color(0xff891b13)
        val Red950 = Color(0xff4b0904)
    }

    @Stable
    object Text {
        val Primary: Color = GrayTones.Gray950
        val Secondary: Color = GrayTones.Gray400
        val Disabled: Color = GrayTones.Gray300
        val White: Color = Base.White
        val Accent1: Color = Base.Red
        val Accent2: Color = Base.Graphite
        val InputDefault: Color = InputsText.Default
        val InputDisable: Color = InputsText.Disable
        val InputFocused: Color = InputsText.Focused
        val InputSearch: Color = InputsIcons.Error
        val Error: Color = InputsText.Error
        val SmsDescription: Color = Color(0xff232323)
        val ButtonTextColor: Color = Color(0xff374151)
    }

    @Stable
    object IconColors {
        val Primary: Color = Base.Graphite
        val Secondary: Color = GrayTones.Gray400
        val Disabled: Color = GrayTones.Gray300
        val White: Color = Base.White
        val Accent1: Color = Base.Red
        val Accent2: Color = Base.Red
        val Selected: Color = Base.Pink
        val Unselected: Color = GrayTones.Gray280
    }

    @Stable
    object StrokeColors {
        val Default: Color = GrayTones.Gray200
        val Focus: Color = GrayTones.Gray900
        val Disable: Color = GrayTones.Gray100
        val Error: Color = Base.Red
        val Accent: Color = Base.Graphite
        val Stroke: Color = Base.White
        val Divider: Color = GrayTones.Gray50
        val Premium: Color = Additional.Premium
        val Plus: Color = Additional.Plus
        val InputDefault: Color = InputsIcons.Disable
        val InputDisable: Color = InputsText.Disable
        val InputFocused: Color = InputsText.Default
        val InputError: Color = RedTones.Red600
    }

    @Stable
    object BackgroundColors {
        val Base: Color = GrayTones.Gray50
        val Card: Color = ColorPalette.Base.White
        val NavBar: Color = ColorPalette.Base.White90
        val Category: Color = GrayTones.Gray60
        val Pink: Color = ColorPalette.Base.Pink
        val CardStrokeColor = GrayTones.Gray55
    }

    @Stable
    object ButtonColors {
        val PrimaryDefault: Color = GrayTones.Gray900
        val PrimaryTap: Color = Base.Black
        val PrimaryDisabled: Color = GrayTones.Gray200
        val PrimaryTextable: Color = Base.White
        val SecondaryDefaultBg: Color = Base.White
        val SecondaryDefaultStroke: Color = Base.Graphite
        val SecondaryTapBg: Color = GrayTones.Gray100
        val SecondaryTapStroke: Color = Base.Graphite
        val SecondaryDisabledBg: Color = Base.White
        val SecondaryDisabledStroke: Color = GrayTones.Gray200
        val SecondaryGrayDefault: Color = GrayTones.Gray60
        val SecondaryGrayTap: Color = GrayTones.Gray200
        val SecondaryGrayDisabled: Color = GrayTones.Gray50
        val SecondaryGrayTextable: Color = GrayTones.Gray950
        val TextButtonDefault: Color = Base.Black
        val TextButtonTapBg: Color = GrayTones.Gray100
        val TextButtonTapText: Color = GrayTones.Gray950
        val TextButtonDisabled: Color = GrayTones.Gray400
    }

    @Stable
    object ChipColors {
        val text: Color = Base.Graphite
        val background: Color = Base.Pink
    }

    @Stable
    object InputsIcons {
        val Default = Color(0xFF72778C)
        val Disable = Color(0xFFC6C6CA)
        val Error = Color(0xFF808288)
    }

    @Stable
    object InputsText {
        val Default = Color(0xFF72778C)
        val Disable = Color(0xFFDCDDE2)
        val Placeholder = Color(0xFF888888)
        val Focused = Color(0xFF22242C)
        val Error = Color(0xFFE21E11)
    }

    @Stable
    object Main {
        val Color1 = Color(0xFF2F2E2D)
        val Black20 = Color(0x33000000)
    }

    @Stable
    object SmsInputColors {
        val Pink15 = Color(0x26F54A7B)
        val Purple15 = Color(0x26564DF7)
        val Pink = Color(0xFFF54A7B)
        val Purple = Color(0xFF564DF7)
        val SingleColorBack = Color(0xFFebe4f6)
    }
}