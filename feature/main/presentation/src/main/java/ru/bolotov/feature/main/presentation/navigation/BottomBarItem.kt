package ru.bolotov.feature.main.presentation.navigation

enum class BottomBarTag {
    Dashboard, Positions, Strategy, Risk, More
}

data class BottomBarItem(
    val route: String,
    val icon: Int,
    val text: Int,
    val tag: BottomBarTag,
    val graphRoute: Any? = null  // Добавляем для типизированной навигации
)