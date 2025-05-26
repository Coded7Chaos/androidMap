package com.tonygnk.maplibredemo.ui.usuario

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tonygnk.maplibredemo.models.User
import com.tonygnk.maplibredemo.repository.UserRepository
import com.tonygnk.maplibredemo.ui.home.HomeUiState
import com.tonygnk.maplibredemo.ui.home.HomeViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class UserViewModel(userRepository: UserRepository): ViewModel() {
    val userUiState: StateFlow<UserUiState> = userRepository.getAllUsersStream().map { UserUiState(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = UserUiState()
        )
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class UserUiState(val userList: List<User> = listOf())