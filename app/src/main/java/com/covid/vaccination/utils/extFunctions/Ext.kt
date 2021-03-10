package com.covid.vaccination.utils.extFunctions

fun Long.formatToShortNumber(): String {
    return when {
        this >= 1000000000 -> String.format("%.2fB", this / 1000000000.0)
        this >= 1000000 -> String.format("%.2fM", this / 1000000.0)
        this >= 1000 -> String.format("%.2fK", this / 1000.0)
        else -> this.toString()
    }
}