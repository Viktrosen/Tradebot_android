package ru.bolotov.core.uikit.format

/** Limits decimal precision in displayed prose without changing the stored explanation. */
fun String.formatExplanationNumbers(): String =
    replace(OVERLY_PRECISE_DECIMAL) { match -> match.groupValues[1] }

private val OVERLY_PRECISE_DECIMAL = Regex("(-?\\d+[.,]\\d{2})\\d+")
