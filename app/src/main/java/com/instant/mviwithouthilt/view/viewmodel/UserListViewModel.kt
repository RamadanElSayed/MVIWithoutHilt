package com.instant.mviwithouthilt.view.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.instant.mviwithouthilt.model.User
import com.instant.mviwithouthilt.repository.UserRepository
import com.instant.mviwithouthilt.repository.UserRepositoryImpl
import com.instant.mviwithouthilt.view.uimodel.SnackbarViewEffect
import com.instant.mviwithouthilt.view.uimodel.UserViewIntent
import com.instant.mviwithouthilt.view.uimodel.UserViewState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

open class UserListViewModel : ViewModel() {
    private val repository: UserRepository = UserRepositoryImpl()


    private val _viewState = MutableStateFlow(UserViewState())
    val viewState: StateFlow<UserViewState> = _viewState

    // Effect channel to handle snackbar events
    private val _effectChannel = Channel<SnackbarViewEffect>()
    val effectFlow: Flow<SnackbarViewEffect> = _effectChannel.receiveAsFlow()

    private var recentlyDeletedUser: User? = null

    init {
        handleIntent(UserViewIntent.LoadUsers)
    }

    fun handleIntent(intent: UserViewIntent) {
        when (intent) {
            is UserViewIntent.LoadUsers -> loadUsers()
            is UserViewIntent.AddUserView -> addUser(intent.name, intent.email)
            is UserViewIntent.DeleteUserView -> deleteUser(intent.user)
            is UserViewIntent.ClearUsers -> clearUsers()
            is UserViewIntent.SearchUserView -> searchUsers(intent.query)
            is UserViewIntent.UpdateName -> updateName(intent.name)
            is UserViewIntent.UpdateEmail -> updateEmail(intent.email)
            is UserViewIntent.UndoDelete -> undoDelete()
        }
    }

    private fun loadUsers() {
        _viewState.value = _viewState.value.copy(isLoading = true)
        viewModelScope.launch {
            try {
                val users = repository.getUsers()
                _viewState.value = _viewState.value.copy(
                    isLoading = false,
                    users = users
                )
                _effectChannel.send(
                    SnackbarViewEffect.ShowSnackbarView(
                        if (users.isEmpty()) "No users available" else "Users loaded"
                    )
                )
            } catch (e: Exception) {
                _viewState.value = _viewState.value.copy(isLoading = false)
                _effectChannel.send(
                    SnackbarViewEffect.ShowSnackbarView(
                        e.message ?: "Unknown error"
                    )
                )
            }
        }
    }

     fun addUser(name: String, email: String) {
        val nameError = name.isBlank()
        val emailError =
            email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

        if (nameError || emailError) {
            _viewState.value = _viewState.value.copy(
                nameError = nameError,
                emailError = emailError
            )
            return
        }

        _viewState.value = _viewState.value.copy(isLoading = true)
        viewModelScope.launch {
            try {
                val newUser = User(id = (0..1000).random(), name = name, email = email)
                val users = repository.addUser(newUser)
                _viewState.value = _viewState.value.copy(
                    isLoading = false,
                    users = users,
                    name = "",
                    email = ""
                )
                _effectChannel.send(SnackbarViewEffect.ShowSnackbarView("User added successfully!"))
            } catch (e: Exception) {
                _viewState.value = _viewState.value.copy(isLoading = false)
                _effectChannel.send(SnackbarViewEffect.ShowSnackbarView("Error adding user: ${e.message}"))
            }
        }
    }

    private fun deleteUser(user: User) {
        _viewState.value = _viewState.value.copy(isLoading = true)
        viewModelScope.launch {
            recentlyDeletedUser = user
            try {
                val users = repository.deleteUser(user)
                _viewState.value = _viewState.value.copy(isLoading = false, users = users)
                _effectChannel.send(SnackbarViewEffect.ShowSnackbarView("User deleted", "Undo"))
            } catch (e: Exception) {
                _viewState.value = _viewState.value.copy(isLoading = false)
                _effectChannel.send(SnackbarViewEffect.ShowSnackbarView("Error deleting user: ${e.message}"))
            }
        }
    }

    private fun undoDelete() {
        recentlyDeletedUser?.let { deletedUser ->
            addUser(deletedUser.name, deletedUser.email)
            recentlyDeletedUser = null
        }
    }

    private fun clearUsers() {
        _viewState.value = _viewState.value.copy(isLoading = true)
        viewModelScope.launch {
            try {
                val users = repository.clearUsers()
                _viewState.value = _viewState.value.copy(isLoading = false, users = users)
                _effectChannel.send(SnackbarViewEffect.ShowSnackbarView("All users cleared!"))
            } catch (e: Exception) {
                _viewState.value = _viewState.value.copy(isLoading = false)
                _effectChannel.send(SnackbarViewEffect.ShowSnackbarView("Error clearing users: ${e.message}"))
            }
        }
    }

    private fun searchUsers(query: String) {
        _viewState.value = _viewState.value.copy(searchQuery = query)

        viewModelScope.launch {
            if (query.isBlank()) {
                // Reload all users when the search query is empty
                val allUsers = repository.getUsers()
                _viewState.value = _viewState.value.copy(users = allUsers)
            } else {
                val filteredUsers = _viewState.value.users.filter {
                    it.name.contains(query, ignoreCase = true) || it.email.contains(
                        query,
                        ignoreCase = true
                    )
                }
                _viewState.value = _viewState.value.copy(users = filteredUsers)
                if (filteredUsers.isEmpty()) {
                    _effectChannel.send(SnackbarViewEffect.ShowSnackbarView("No users found"))
                }
            }
        }
    }


    private fun updateName(name: String) {
        _viewState.value = _viewState.value.copy(name = name, nameError = false)
    }

    private fun updateEmail(email: String) {
        _viewState.value = _viewState.value.copy(email = email, emailError = false)
    }
}