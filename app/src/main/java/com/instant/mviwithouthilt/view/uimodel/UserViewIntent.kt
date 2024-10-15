package com.instant.mviwithouthilt.view.uimodel

import com.instant.mviwithouthilt.model.User

// Intent represents user interactions
sealed class UserViewIntent {
    data object LoadUsers : UserViewIntent()
    data class AddUserView(val name: String, val email: String) : UserViewIntent()
    data class DeleteUserView(val user: User) : UserViewIntent()
    data object ClearUsers : UserViewIntent()
    data class SearchUserView(val query: String) : UserViewIntent()
    data class UpdateName(val name: String) : UserViewIntent()
    data class UpdateEmail(val email: String) : UserViewIntent()
    data object UndoDelete : UserViewIntent()
}

