package com.instant.mviwithouthilt.view.uimodel

// SnackbarEvent for one-time snackbar events
sealed class SnackbarViewEffect {
    data class ShowSnackbarView(val message: String, val actionLabel: String? = null) : SnackbarViewEffect()
}


